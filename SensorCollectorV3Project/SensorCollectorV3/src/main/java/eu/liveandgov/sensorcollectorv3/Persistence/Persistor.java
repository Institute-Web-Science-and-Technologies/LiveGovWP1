package eu.liveandgov.sensorcollectorv3.Persistence;

import java.io.File;
import java.io.IOException;

/**
 * Created by hartmann on 9/20/13.
 */
public interface Persistor {
    /**
     * Persist message m to storage unless blockPush() was called in
     * which case all messages are dropped.
     * Buffering inside the Persistor is allowed.
     * @param m
     */
    void push(String m);

    /**
     * Returns space used by persistence object on the SDCard in bytes.
     * @return size
     */
    long size();

    /**
     * Flush all buffers and close file handlers.
     * After this method is called all pushed messages will be dropped.
     */
    void close();

    /**
     * Exports all stored samples into a given File in the ssf format.
     *
     * Implementations may return null in case, they do not want to export samples.
     *
     * @return successFlag
     */
    boolean exportSamples(File stageFile);
}
