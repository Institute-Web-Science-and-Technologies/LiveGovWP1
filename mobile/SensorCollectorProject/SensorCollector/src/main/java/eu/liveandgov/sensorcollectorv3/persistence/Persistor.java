package eu.liveandgov.sensorcollectorv3.persistence;

import java.io.File;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.monitor.Monitorable;

/**
 * Created by hartmann on 9/20/13.
 */
public interface Persistor extends Monitorable, Consumer {
    /**
     * Persist message m to storage unless blockPush() was called in
     * which case all messages are dropped.
     * Buffering inside the Persistor is allowed.
     * @param m
     */
    //void push(String m);


    /**
     * Exports all stored samples into a given File in the ssf format.
     *
     * Implementations may return null in case, they do not want to export samples.
     *
     * @return successFlag
     */
    boolean exportSamples(File stageFile);
}
