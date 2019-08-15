package main;

import byteremote.common.socket.ProtocolType;
import byteremote.server.ByteServer;
import byteremote.server.tcp.ByteTCPServer;
import byteremote.server.udp.ByteUDPServer;
import gui.RemoteFrame;
import remote.Control;
import utils.log.Log;

import javax.swing.*;

/**
 * This class is the core of the application. It manages the entire flow
 */
public class ApplicationCore
{
    /* Log purpose */
    private static final String TAG = "ApplicationCore";

    private static final int[] PORT_LIST = {40000,
                                            40001,
                                            40002,
                                            40003};

    private ByteServer  byteServer = null;
    private RemoteFrame mainFrame  = null;

    private static final ApplicationCore instance = new ApplicationCore();

    /**
     * Singleton private constructor
     */
    private ApplicationCore()
    {
        mainFrame = new RemoteFrame("Remote");
    }

    /**
     * Get the singleton instance
     *
     * @return The instance
     */
    public static ApplicationCore getInstance()
    {
        return instance;
    }

    /**
     * Method used to connect a client
     * @param password The client's password
     * @return true for a successful connection, or false otherwise
     */
    public boolean clientConnect(int password)
    {
        if (password != mainFrame.getPassword())
        {
            Log.e(TAG, "clientConnect: invalid password");
            return false;
        }
        else
        {
            mainFrame.clientConnected();
            return true;
        }
    }

    /**
     * Method used to disconnect a client
     */
    public void clientDisconnect()
    {
        mainFrame.clientDisconnected();
    }

    /**
     * Method used to start the server
     * @param type The wanted protocol type
     * @return true for a successful operation, or false otherwise
     */
    public boolean startServer(ProtocolType type)
    {
        Control control = null;
        try
        {
            control = new Control();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (null == byteServer || null == control)
        {
            if (ProtocolType.TCP == type)
            {
                Log.d(TAG, "startServer: TCP");
                byteServer = new ByteTCPServer(control, true);
            }
            else
            {
                Log.d(TAG, "startServer: UDP");
                byteServer = new ByteUDPServer(control, true);
                /* Set timeout to 200 ms */
                ((ByteUDPServer)byteServer).setDataTimeOut(200);
            }

            for (int port : PORT_LIST)
            {
                try
                {
                    byteServer.start(port);
                    Log.d(TAG, "startServer: Server started on port: " + port);
                    JOptionPane.showMessageDialog(mainFrame, "Server started.");
                    mainFrame.serverStarted(type);
                    return true;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        Log.d(TAG, "startServer: Failed to start server");
        JOptionPane.showMessageDialog(mainFrame, "Failed to start server.");
        return false;
    }

    /**
     * Method used to close the server
     */
    public void stopServer()
    {
        if (null != byteServer)
        {
            try
            {
                byteServer.stop();
                byteServer = null;
                mainFrame.serverClosed();
                Log.d(TAG, "stopServer: Server stopped");
                JOptionPane.showMessageDialog(mainFrame, "Server stopped.");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Checks if the server is running
     * @return true if the server is running, or false otherwise
     */
    public boolean isServerStarted()
    {
        return null != byteServer;
    }

    public static void main(String[] args)
            throws Exception
    {
        ApplicationCore.getInstance().mainFrame.setVisible(true);
    }
}
