package eu.liveandgov.sensorcollectorv3.Persistence;

import android.util.Log;

import org.jeromq.ZMQ;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import eu.liveandgov.sensorcollectorv3.Sensors.GlobalContext;
import eu.liveandgov.sensorcollectorv3.Sensors.MessageQueue;

/**
 * Created by hartmann on 9/15/13.
 */
public class PersistorThread implements Runnable {
    private static final String LOG_TAG = "P_ZMQ";
    public static final String STAGE_FILENAME = "sensor.stage.ssf";

    private final Persistor persistor;
    private final Thread thread;
    private static PersistorThread instance;
    private final File stageFile;

    /* Signleton Pattern */
    private PersistorThread(Persistor persistor){
        this.persistor = persistor;
        this.thread = new Thread(this);
        this.stageFile = new File(GlobalContext.context.getFilesDir(), STAGE_FILENAME);
    }

    public static void setup(Persistor persistor) {
        instance = new PersistorThread(persistor);
    }

    public static PersistorThread getInstance(){
        if (instance == null) instance = new PersistorThread(new FilePersistor());
        return instance;
    }


    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Persist Loop");
        String msg;
        while (true) {
            msg = MessageQueue.blockingPull();
            persistor.push(msg);
        }
    }

    public void start() {
        thread.start();
    }

    public long getSize() {
        return persistor.getSize();
    }

    public Persistor getPersistor() {
        return persistor;
    }

    public File stageFile() {
        if (stageFile.exists()) {
            Log.i(LOG_TAG, "Found staged file.");
            return stageFile;
        } else {
            Log.i(LOG_TAG, "Staging File.");
            persistor.close();
            boolean success = persistor.getFile().renameTo(stageFile);
            if (!success) { Log.i(LOG_TAG, "Cannot stage file."); return null; }

            persistor.reset();
            persistor.unblockPush();
        }
        return stageFile;
    }
}
