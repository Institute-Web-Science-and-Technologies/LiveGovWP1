package eu.liveandgov.sensorcollectorv3.persistence;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Persistor class that writes samples into a log file.
 *
 * Created by hartmann on 9/20/13.
 */
public class FilePersistor implements Persistor {
    public static final String LOG_TAG = "FP";
    public static final String FILENAME = "sensor.log";

    private File logFile;
    private BufferedWriter fileWriter;
    private long sampleCount = 0L;

    public FilePersistor(File logFile) {
        this.logFile = logFile;
        openLogFileAppend();
    }

    @Override
    public synchronized void push(String s) {
        if (fileWriter == null) {
            Log.v(LOG_TAG, "Blocked write event");
            return;
        }

        try {
            fileWriter.write(s + "\n");
            sampleCount ++;
        } catch (IOException e) {
            Log.e(LOG_TAG,"Cannot write file.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        boolean suc = true;

        Log.i(LOG_TAG, "Exporting samples.");
        if (stageFile.exists()) { Log.e(LOG_TAG, "Stage file exists."); return false; }

        suc = closeLogFile();
        if (!suc) { Log.e(LOG_TAG, "Cosing LogFile failed."); return false; }

        suc = logFile.renameTo(stageFile);
        if (!suc) { Log.e(LOG_TAG, "Renaming failed."); return false; }

        suc = openLogFileOverwrite();
        if (!suc) { Log.e(LOG_TAG, "Opening new Log File failed."); return false; }

        sampleCount = 0;
        return true;
    }

    @Override
    public boolean hasSamples() {
        return logFile.length() > 0;
    }

    @Override
    public String getStatus() {
        return "File size: " + logFile.length()/1024 + "kb. Samples written: " + sampleCount;
    }

    private boolean openLogFileAppend() {
        try {
            fileWriter = new BufferedWriter(new FileWriter(logFile,true));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean openLogFileOverwrite() {
        try {
            fileWriter = new BufferedWriter(new FileWriter(logFile,false));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean closeLogFile() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        fileWriter = null;
        return true;
    }
}
