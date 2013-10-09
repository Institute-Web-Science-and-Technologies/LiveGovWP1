package eu.liveandgov.sensorcollectorv3.Persistence;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import eu.liveandgov.sensorcollectorv3.Monitor.MonitorThread;
import eu.liveandgov.sensorcollectorv3.GlobalContext;

/**
 * Created by hartmann on 9/20/13.
 */
public class FilePersistor implements Persistor {
    public static final String LOG_TAG = "FP";
    public static final String FILENAME = "sensor.log";

    private File logFile;
    private BufferedWriter fileWriter;

    public FilePersistor(File logFile) {
        this.logFile = logFile;
        openFileWriter();
    }

    @Override
    public synchronized void push(String s){
        if (fileWriter == null) {
            Log.i(LOG_TAG, "Blocked write event");
            return;
        }

        try {
            fileWriter.write(s + "\n");
        } catch (IOException e) {
            Log.i(LOG_TAG,"Cannot write file.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        boolean del = false;
        boolean ren = false;

        if (stageFile.exists()) {
            Log.i(LOG_TAG, "Found staged file.");
            del = stageFile.delete();
        }

        Log.i(LOG_TAG, "Exporting samples.");
        close();

        ren = logFile.renameTo(stageFile);

        reset();
        unblockPush();

        return ren && del;
    }

    @Override
    public String getStatus() {
        return "File size: " + logFile.length();
    }


    private void openFileWriter() {
        try {
            fileWriter = new BufferedWriter(new FileWriter(logFile,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void flush() {
        try {
            fileWriter.flush();
        } catch (IOException e) {
            Log.i(LOG_TAG,"Cannot flush file.");
            e.printStackTrace();
        }
    }

    public synchronized void blockPush() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileWriter = null;
    }

    public synchronized void unblockPush() {
        openFileWriter();
    }

    public File getFile() {
        return logFile;
    }

    public synchronized void reset() {
        try {
            fileWriter = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MonitorThread.sampleCount = 0;
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
