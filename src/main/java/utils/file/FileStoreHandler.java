package utils.file;

import utils.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class used to handle the file transfer on server side
 */
public class FileStoreHandler
{
    /* Log purpose */
    private static final String TAG = "FileStoreHandler";

    private ConcurrentHashMap<String, ArrayList<byte[]>> filesContent = null;

    private String targetDirPath = "";

    public FileStoreHandler(String dir)
    {
        this.filesContent  = new ConcurrentHashMap<>();
        this.targetDirPath = dir;
    }

    /**
     * Add a new chunk
     *
     * @param fileName  Name of the file
     * @param fileChunk The chunk
     * @throws IOException If any error occurs
     */
    public void add(String fileName, byte[] fileChunk)
            throws IOException
    {
        if (! this.filesContent.containsKey(fileName))
        {
            this.addFile(fileName);
        }

        this.addFileChunk(fileName, fileChunk);
    }

    /**
     * Reset this handler to it's default state
     */
    public void reset()
    {
        this.filesContent = new ConcurrentHashMap<>();
    }

    /**
     * Add a new file in the list
     *
     * @param fileName Name of the file
     */
    private void addFile(String fileName)
    {
        this.filesContent.put(fileName, new ArrayList<>());
    }

    /**
     * Add a new chunk of a file
     *
     * @param fileName Name of the file
     * @param chunk    The chunk
     * @throws IOException If an IO error occurs
     */
    private void addFileChunk(String fileName, byte[] chunk)
            throws IOException
    {
        if (null != chunk)
        {
            this.filesContent.get(fileName).add(chunk);
        }
        else
        {
            this.writeFileToDisk(fileName);
            this.filesContent.remove(fileName);
        }
    }

    /**
     * Write all the content of a file to disk
     *
     * @param fileName Name of the file
     * @throws IOException If an IO error occurs
     */
    private void writeFileToDisk(String fileName)
            throws IOException
    {
        FileOutputStream  outputStream = null;
        ArrayList<byte[]> fileContent  = this.filesContent.get(fileName);

        this.createTargetDirectory();
        outputStream = new FileOutputStream(new File(this.targetDirPath + "/" + fileName));

        for (byte[] chunkOfData : fileContent)
        {
            outputStream.write(chunkOfData);
        }

        outputStream.flush();
        outputStream.close();
    }

    /**
     * Create a new directory in which the files will be stored.
     * The directory name was provided at instantiation.
     *
     * @throws IOException If an IO error occurs
     */
    private void createTargetDirectory()
            throws IOException
    {
        File directory = new File(this.targetDirPath);

        if (directory.exists() && directory.isDirectory())
        {
            /* Nothing to do here */
            return;
        }

        this.findValidDirectoryName();

        if (! new File(this.targetDirPath).mkdirs())
        {
            Log.d(TAG, "Can not create directory: " + this.targetDirPath);
            throw new IOException("Can not create directory" + this.targetDirPath);
        }
    }

    /**
     * Find a valid directory name and store it into the private member
     */
    private void findValidDirectoryName()
    {
        Random random           = new Random();
        String newTargetDirPath = this.targetDirPath;

        while (new File(newTargetDirPath).exists())
        {
            newTargetDirPath = this.targetDirPath + random.nextInt();
        }

        this.targetDirPath = newTargetDirPath;
    }

}
