package utils.image;

import utils.log.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class used to handle the screen shot process
 */
public class ScreenShotHandler
{
    /* Log purpose */
    private static final String TAG = "ScreenShotHandler";

    private static final int NUMBER_OF_THREADS  = 3; /* screenshot/resize/convert */
    private static final int MAX_IGNORED_FRAMES = 100;

    private final ReentrantLock screenShotLock             = new ReentrantLock();
    private final ReentrantLock resizedScreenShotLock      = new ReentrantLock();
    private final Condition     screenShotAvailable        = this.screenShotLock.newCondition();
    private final Condition     resizedScreenShotAvailable = this.resizedScreenShotLock.newCondition();

    private int             counter           = 0;
    private int             height            = 0;
    private AtomicBoolean   isRunning         = null;
    private ExecutorService executor          = null;
    private BufferedImage   screenShot        = null;
    private BufferedImage   resizedScreenShot = null;

    /* These will be used by the screenShot worker
     * to interrupt the other 2 workers */
    private Future resizeScreenShotTask         = null;
    private Future convertResizedScreenShotTask = null;


    private AtomicReference<byte[]> resizedScreenShotBytes = null;

    public ScreenShotHandler(int preferredHeight)
    {
        this.height                 = preferredHeight;
        this.executor               = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        this.resizedScreenShotBytes = new AtomicReference<>();
        this.isRunning              = new AtomicBoolean(false);
    }

    /**
     * Start the screen shot handler
     */
    public void start()
    {
        if (! this.isRunning.get())
        {
            this.isRunning.set(true);
            this.executor.submit(this::takeScreenShot);
            this.resizeScreenShotTask         = this.executor.submit(this::resizeScreenShot);
            this.convertResizedScreenShotTask = this.executor.submit(this::convertResizedScreenShotToBytes);
        }
    }

    /**
     * Destroys the screen shot handler and frees all the resources
     */
    public void destroy()
    {
        this.isRunning.set(false);
        this.executor.shutdown();

        try
        {
            /* Wait a while for existing tasks to terminate */
            if (! this.executor.awaitTermination(500, TimeUnit.MILLISECONDS))
            {
                this.executor.shutdownNow(); /* Cancel currently executing tasks */
            }
        }
        catch (InterruptedException ex)
        {
            /* (Re-)Cancel if current thread also interrupted */
            this.executor.shutdownNow();
            /* Preserve interrupt status */
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Return the last screen shot processed
     *
     * @return the last screen shot
     */
    public byte[] get()
    {
        this.counter = 0;
        return this.resizedScreenShotBytes.getAndSet(null);
    }

    /**
     * Checks if the screen shot handler is started
     *
     * @return true if the screen shot is started, or false otherwise
     */
    public boolean isStarted()
    {
        return this.isRunning.get();
    }

    /**
     * Set a newly created screen shot
     *
     * @param image The image
     */
    private void setScreenShot(BufferedImage image)
    {
        /* lock access to screenShot */
        this.screenShotLock.lock();
        /* save screen shot */
        this.screenShot = image;
        /* signal resizeScreenShot thread */
        this.screenShotAvailable.signal();
        /* unlock access to screenShot */
        this.screenShotLock.unlock();
    }

    /**
     * Get the last screen shot created
     *
     * @return The last screen shot
     * @throws InterruptedException In case that the calling thread is interrupted
     */
    private BufferedImage getScreenShot()
            throws InterruptedException
    {
        BufferedImage image = null;

        try
        {
            /* lock access to screenShot */
            this.screenShotLock.lock();

            /* wait until a screen shot is available */
            while (null == this.screenShot)
            {
                this.screenShotAvailable.await();
            }

            image           = this.screenShot;
            this.screenShot = null;
        }
        finally
        {
            /* unlock access to screenShot */
            this.screenShotLock.unlock();
        }

        return image;
    }

    /**
     * Set a resized screen shot
     *
     * @param image The resized image
     */
    private void setResizedScreenShot(BufferedImage image)
    {
        /* lock access to resizedScreenShot */
        this.resizedScreenShotLock.lock();
        /* save resized screen shot */
        this.resizedScreenShot = image;
        /* signal convertResizedScreenShotToBytes thread */
        this.resizedScreenShotAvailable.signal();
        /* unlock access to resizedScreenShot */
        this.resizedScreenShotLock.unlock();
    }

    /**
     * Get the last resized screen shot created
     *
     * @return The last screen shot
     * @throws InterruptedException In case that the calling thread is interrupted
     */
    private BufferedImage getResizedScreenShot()
            throws InterruptedException
    {
        BufferedImage image = null;

        try
        {
            /* lock access to resizedScreenShot */
            this.resizedScreenShotLock.lock();

            /* wait until a resized screen shot is available */
            while (null == this.resizedScreenShot)
            {
                this.resizedScreenShotAvailable.await();
            }

            image                  = this.resizedScreenShot;
            this.resizedScreenShot = null;
        }
        finally
        {
            /* unlock access to resizedScreenShot */
            this.resizedScreenShotLock.unlock();
        }

        return image;
    }

    /**
     * The task which takes screen shots
     */
    private void takeScreenShot()
    {
        Log.d(TAG, "takeScreenShot: started");
        try
        {
            Rectangle screenFrame = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            Robot     robot       = new Robot();

            while (this.isRunning.get())
            {
                this.setScreenShot(robot.createScreenCapture(screenFrame));

                if (++ this.counter > MAX_IGNORED_FRAMES)
                {
                    /* Mark pipe as not running */
                    this.isRunning.set(false);
                    /* Interrupt the other 2 working threads */
                    this.resizeScreenShotTask.cancel(true);
                    this.convertResizedScreenShotTask.cancel(true);
                }
            }
        }
        catch (AWTException ex)
        {
            ex.printStackTrace();
        }

        Log.d(TAG, "takeScreenShot: finished");
    }

    /**
     * The task which resize the screen shot
     */
    private void resizeScreenShot()
    {
        Log.d(TAG, "resizeScreenShot: started");

        BufferedImage original    = null;
        BufferedImage resized     = null;
        Graphics2D    graph2D     = null;
        float         aspectRatio = 1;

        try
        {
            while (this.isRunning.get())
            {
                original    = this.getScreenShot();
                aspectRatio = (float)original.getWidth() / original.getHeight();
                resized     = new BufferedImage((int)(aspectRatio * this.height), this.height, original.getType());
                graph2D     = resized.createGraphics();

                graph2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graph2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graph2D.drawImage(original, 0, 0, (int)(aspectRatio * this.height), this.height, null);
                graph2D.dispose();

                this.setResizedScreenShot(resized);
            }
        }
        catch (InterruptedException ex)
        {
            /* Preserve interrupt status */
            Thread.currentThread().interrupt();
        }

        Log.d(TAG, "resizeScreenShot: finished");

    }

    /**
     * The task which convert the resized screen shot into byte[]
     */
    private void convertResizedScreenShotToBytes()
    {
        Log.d(TAG, "convertResizedScreenShotToBytes: started");

        BufferedImage         image       = null;
        ByteArrayOutputStream imageStream = null;

        try
        {
            while (this.isRunning.get())
            {
                try
                {
                    image       = this.getResizedScreenShot();
                    imageStream = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", imageStream);
                    this.resizedScreenShotBytes.set(imageStream.toByteArray());
                    imageStream.reset();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        catch (InterruptedException ex)
        {
            /* Preserve interrupt status */
            Thread.currentThread().interrupt();
        }

        Log.d(TAG, "convertResizedScreenShotToBytes: finished");

    }
}
