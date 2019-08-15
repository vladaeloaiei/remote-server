package utils.jni;

/**
 * Class used to handle the audio volume control
 */
public class VolumeControl
{
    static
    {
        try
        {
            System.loadLibrary("volume-control-win64");
        }
        catch (Error | Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Change the current volume
     *
     * @param delta The delta
     */
    public static native void changeVolume(float delta);
}
