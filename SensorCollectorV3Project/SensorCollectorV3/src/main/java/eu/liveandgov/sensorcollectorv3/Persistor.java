package eu.liveandgov.sensorcollectorv3;

import java.io.File;

/**
 * Created by hartmann on 9/20/13.
 */
public interface Persistor {
    void push(String s);
    void blockPush();
    void unblockPush();
    File getFile();
    void reset();
    void close();
}
