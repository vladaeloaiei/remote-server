package utils.log;

/**
 * Class used for log
 */
public class Log
{
    /**
     * Debug log
     *
     * @param tag     The tag
     * @param message The message
     */
    public static void d(String tag, String message)
    {
        System.out.println(tag + ": debug:" + tag + ":" + message);
    }

    /**
     * Error log
     *
     * @param tag     The tag
     * @param message The message
     */
    public static void e(String tag, String message)
    {
        System.err.println("error:" + tag + ": " + message);
    }
}
