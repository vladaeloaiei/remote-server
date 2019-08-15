package remote;

import main.ApplicationCore;
import utils.file.FileStoreHandler;
import utils.image.ScreenShotHandler;
import utils.jni.VolumeControl;
import utils.log.Log;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class Control implements IControl
{
    /* Log purpose */
    private static final String TAG           = "Control";
    /* Only height will be used. The width will be computed using the aspect ratio of the image */
    private static final int    HEIGHT        = 720;
    private static final String MEDIA_PATH    = "Media";
    public static final  int    INVALID_TOKEN = - 1;

    private int               token             = INVALID_TOKEN;
    private boolean           pinged            = false;
    private Robot             robot             = null;
    private ScreenShotHandler screenShotHandler = null;
    private FileStoreHandler  fileStoreHandler  = null;

    public Control()
            throws AWTException
    {
        this.robot             = new Robot();
        this.screenShotHandler = new ScreenShotHandler(HEIGHT);
        this.fileStoreHandler  = new FileStoreHandler(MEDIA_PATH);
    }

    /**
     * The method checks the state of the connection and remove the client if the connection is lost
     */
    private void heartbeat()
    {
        Log.d(TAG, "heartbeat: thread started");

        while (INVALID_TOKEN != token)
        {
            try
            {
                pinged = false;
                Thread.sleep(5000);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            if (! pinged)
            {
                Log.d(TAG, "heartbeat: Client disconnected");
                this.disconnect(token);
            }
        }

        Log.d(TAG, "heartbeat: thread finished");
    }

    @Override
    public Integer connect(short password)
    {
        if (INVALID_TOKEN != this.token)
        {
            Log.d(TAG, "connect: Client already connected");
            return INVALID_TOKEN;
        }
        else
        {
            if (! ApplicationCore.getInstance().clientConnect(password))
            {
                Log.e(TAG, "connect: Invalid password");
                return INVALID_TOKEN;
            }
            else
            {
                Log.d(TAG, "connect: Client connected");
                this.token = new Random().nextInt(Integer.MAX_VALUE);

                while (0 == this.token)
                {
                    this.token = new Random().nextInt(Integer.MAX_VALUE);
                }

                new Thread(this::heartbeat).start();
                Log.d(TAG, "connect: token generated: " + this.token);
                return this.token;
            }
        }
    }

    @Override
    public Boolean disconnect(int token)
    {
        if (INVALID_TOKEN == token)
        {
            Log.e(TAG, "disconnect: Invalid token received: " + token);
            return false;
        }
        else
        {
            Log.d(TAG, "disconnect: Client disconnected");
            this.token = INVALID_TOKEN;
            this.fileStoreHandler.reset();
            ApplicationCore.getInstance().clientDisconnect();
            return true;
        }
    }

    @Override
    public byte[] getScreenShot(int token)
    {
        if (token != this.token)
        {
            Log.e(TAG, "getScreenShot: Invalid token received: " + token);
            return null;
        }
        else
        {
            Log.d(TAG, "getScreenShot: Send screenshot");

            if (! this.screenShotHandler.isStarted())
            {
                this.screenShotHandler.start();
            }

            return this.screenShotHandler.get();
        }
    }

    @Override
    public void keyPress(int token, int keyValue)
    {
        if (token != this.token)
        {
            Log.e(TAG, "keyPress: Invalid token received: " + token);
        }
        else
        {
            try
            {
                Log.d(TAG, "keyPress: Key received: " + keyValue);
                this.robot.keyPress(keyValue);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void keyRelease(int token, int keyValue)
    {
        if (token != this.token)
        {
            Log.e(TAG, "keyRelease: Invalid token received: " + token);
        }
        else
        {
            try
            {
                Log.d(TAG, "keyRelease: Key received: " + keyValue);
                this.robot.keyRelease(keyValue);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void mousePress(int token, int mouseButton)
    {
        if (token != this.token)
        {
            Log.e(TAG, "mousePress: Invalid token received: " + token);
        }
        else
        {
            try
            {
                Log.d(TAG, "mousePress: Mouse button received: " + mouseButton);
                this.robot.mousePress(mouseButton);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void mouseRelease(int token, int mouseButton)
    {
        if (token != this.token)
        {
            Log.e(TAG, "mouseRelease: Invalid token received: " + token);
        }
        else
        {
            try
            {
                Log.d(TAG, "mouseRelease: Mouse button received: " + mouseButton);
                this.robot.mouseRelease(mouseButton);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void mouseMove(int token, float pX, float pY, boolean isAbsPos)
    {
        if (token != this.token)
        {
            Log.e(TAG, "mouseMove: Invalid token received: " + token);
        }
        else
        {
            try
            {
                Log.d(TAG, "mouseMove: Position: [" + pX + ", " + pY + "] absolute: " + isAbsPos);

                if (isAbsPos)
                {
                    int x = (int)(pX * Toolkit.getDefaultToolkit().getScreenSize().width);
                    int y = (int)(pY * Toolkit.getDefaultToolkit().getScreenSize().height);

                    this.robot.mouseMove(x, y);
                }
                else
                {
                    Point currPos = MouseInfo.getPointerInfo().getLocation();

                    this.robot.mouseMove(currPos.x + (int)pX, currPos.y + (int)pY);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void mouseScroll(int token, short direction)
    {
        if (token != this.token)
        {
            Log.e(TAG, "mouseScroll: Invalid token received: " + token);
        }
        else
        {
            Log.d(TAG, "mouseScroll: direction: " + direction);
            this.robot.mouseWheel(direction);
        }
    }

    @Override
    public void changeVolume(int token, float delta)
    {
        if (token != this.token)
        {
            Log.e(TAG, "changeVolume: Invalid token received: " + token);
        }
        else
        {
            Log.d(TAG, "changeVolume: Delta volume: " + delta);
            VolumeControl.changeVolume(delta);
        }
    }

    @Override
    public Boolean sendFile(int token, String fileName, byte[] fileChunk)
    {
        if (token != this.token)
        {
            Log.e(TAG, "sendFile: Invalid token received: " + token);
            return false;
        }
        else
        {
            try
            {
                Log.d(TAG, "sendFile: Received chunk for: " + fileName);
                this.fileStoreHandler.add(fileName, fileChunk);

                return true;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public Boolean ping(int token)
    {
        if (token != this.token)
        {
            Log.e(TAG, "ping: Invalid token received: " + token);
            return false;
        }
        else
        {
            this.pinged = true;
            Log.d(TAG, "ping: received");
            return true;
        }
    }

    @Override
    public Boolean shutdown(int token)
    {
        if (token != this.token)
        {
            Log.e(TAG, "shutdown: Invalid token received: " + token);
            return false;
        }
        else
        {
            try
            {
                Runtime.getRuntime().exec("shutdown -s -t 0");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            Log.d(TAG, "shutdown: received");
            return true;
        }
    }

    @Override
    public Boolean restart(int token)
    {
        if (token != this.token)
        {
            Log.e(TAG, "restart: Invalid token received: " + token);
            return false;
        }
        else
        {
            try
            {
                Runtime.getRuntime().exec("shutdown -r -t 0");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            Log.d(TAG, "restart: received");
            return true;
        }
    }
}
