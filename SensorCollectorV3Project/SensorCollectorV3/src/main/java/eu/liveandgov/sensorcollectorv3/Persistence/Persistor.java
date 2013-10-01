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
     * FLush buffers.
     */
    void flush();


    /**
     * Drop all incoming messages
     */
    void blockPush();

    /**
     * Resume accepting incoming messages
     */
    void unblockPush();
    void reset();
    void close();

    long getSize();

    File getFile();
}
