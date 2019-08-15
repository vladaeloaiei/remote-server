package remote;

import byteremote.generation.annotation.ByteRemote;

@ByteRemote(name = "RemoteControl")
public interface IControl
{
    /**
     * Connect to the remote desktop server
     *
     * @param password requested password
     * @return a valid token (greater than 0 for successful connections) or -1 otherwise
     * @throws Exception not used
     */
    public Integer connect(short password)
            throws Exception;

    /**
     * Disconnect from the remote desktop server
     *
     * @param token received token
     * @return true for success and false otherwise
     * @throws Exception not used
     */
    public Boolean disconnect(int token)
            throws Exception;

    /**
     * Get the current screenshot in JPG format.
     * <p>Info: Starts the screenShot handler if is needed</p>
     *
     * @param token received token
     * @return The image bytes serialized in byte[] format
     * @throws Exception not used
     */
    public byte[] getScreenShot(int token)
            throws Exception;

    /**
     * Press a key
     *
     * @param token    received token
     * @param keyValue The key code wanted to be pressed
     * @throws Exception not used
     */
    public void keyPress(int token, int keyValue)
            throws Exception;

    /**
     * Release a key
     *
     * @param token    received token
     * @param keyValue The key code wanted to be released
     * @throws Exception not used
     */
    public void keyRelease(int token, int keyValue)
            throws Exception;

    /**
     * Mouse left click
     *
     * @param token       received token
     * @param mouseButton The button number clicked
     * @throws Exception not used
     */
    public void mousePress(int token, int mouseButton)
            throws Exception;

    /**
     * Mouse left click
     *
     * @param token       received token
     * @param mouseButton The button number clicked
     * @throws Exception not used
     */
    public void mouseRelease(int token, int mouseButton)
            throws Exception;

    /**
     * Set the mouse position
     *
     * @param token    received token
     * @param pX       The position of the mouse on Ox calculated in percents
     * @param pY       The position of the mouse on Oy calculated in percents
     * @param isAbsPos true if the inserted position is absolute
     *                 or false if it is relative
     * @throws Exception not used
     */
    public void mouseMove(int token, float pX, float pY, boolean isAbsPos)
            throws Exception;

    /**
     * Scroll up/down
     *
     * @param token     received token
     * @param direction -1 for up / 1 for down
     * @throws Exception not used
     */
    public void mouseScroll(int token, short direction)
            throws Exception;

    /**
     * Change volume adding delta to the current value
     *
     * @param token received token
     * @param delta volume difference (between [-1. 1])
     * @throws Exception not used
     */
    public void changeVolume(int token, float delta)
            throws Exception;

    /**
     * Send a chunk of a file
     *
     * @param token     received token
     * @param fileName  Name of the file
     * @param fileChunk The chunk that will be sent
     * @return true for operation successfully or false/null otherwise
     * @throws Exception not used
     */
    public Boolean sendFile(int token, String fileName, byte[] fileChunk)
            throws Exception;

    /**
     * Method used to check the connection with the server
     *
     * @param token received token
     * @return true in case of success or false otherwise
     * @throws Exception not used
     */
    public Boolean ping(int token)
            throws Exception;

    /**
     * Method used to shutdown the current machine on which server is running
     *
     * @param token received token
     * @return true in case of success or false otherwise
     * @throws Exception not used
     */
    public Boolean shutdown(int token)
            throws Exception;

    /**
     * Method used to restart the current machine on which server is running
     *
     * @param token received token
     * @return true in case of success or false otherwise
     * @throws Exception not used
     */
    public Boolean restart(int token)
            throws Exception;
}
