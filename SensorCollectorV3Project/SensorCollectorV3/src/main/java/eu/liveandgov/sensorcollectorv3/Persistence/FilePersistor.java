package eu.liveandgov.sensorcollectorv3.Persistence;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import eu.liveandgov.sensorcollectorv3.Monitor.MonitorThread;

/**
 * Created by hartmann on 9/20/13.
 */
public class FilePersistor implements Persistor {
    public static final String LOG_TAG = "FPers";
    public static final String FILENAME = "sensor.log";

    private File logFile;
    private BufferedWriter fileWriter;

    public FilePersistor(Context context) {
        logFile = new File(context.getFilesDir(), FILENAME);
        openFileWriter();
    }

    private void openFileWriter() {
        try {
            fileWriter = new BufferedWriter(new FileWriter(logFile,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void push(String s){
        if (fileWriter == null) {
            Log.i(LOG_TAG, "Blocked write event");
            return;
        }

        try {
            fileWriter.write(s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void blockPush() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileWriter = null;
    }

    @Override
    public synchronized void unblockPush() {
        openFileWriter();
    }

    @Override
    public File getFile() {
        return logFile;
    }

    @Override
    public synchronized void reset() {
        try {
            fileWriter = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MonitorThread.sampleCount = 0;
    }

    @Override
    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
