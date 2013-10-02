package eu.liveandgov.sensorcollectorv3.Persistence;

import java.io.File;
import java.io.IOException;

import eu.liveandgov.sensorcollectorv3.Monitor.Monitorable;

/**
 * Created by hartmann on 9/20/13.
 */
public interface Persistor extends Monitorable {
    /**
     * Persist message m to storage unless blockPush() was called in
     * which case all messages are dropped.
     * Buffering inside the Persistor is allowed.
     * @param m
     */
    void push(String m);


    /**
     * Exports all stored samples into a given File in the ssf format.
     *
     * Implementations may return null in case, they do not want to export samples.
     *
     * @return successFlag
     */
    boolean exportSamples(File stageFile);
}
