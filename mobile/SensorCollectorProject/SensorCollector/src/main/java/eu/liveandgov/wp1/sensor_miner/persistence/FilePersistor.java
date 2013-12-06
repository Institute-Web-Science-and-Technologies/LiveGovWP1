package eu.liveandgov.wp1.sensor_miner.persistence;

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

    private File logFile;
    protected BufferedWriter fileWriter;
    private long sampleCount = 0L;

    protected boolean disabled = false;

    public FilePersistor(File logFile) {
        this.logFile = logFile;
        try {
            openLogFileAppend();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not open file. Disabling Persistor.");
            disabled = true;
        }
    }

    @Override
    public synchronized void push(String s) {
        if (disabled) return;
        try {
            if (fileWriter == null) {
                Log.v(LOG_TAG, "Blocked write event");
                return;
            }

            fileWriter.write(s + "\n");
            sampleCount ++;

        } catch (IOException e) {
            Log.e(LOG_TAG,"Cannot write file.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        if (disabled) return false;

        try {
            Log.i(LOG_TAG, "Exporting samples.");

            if (stageFile.exists()) { Log.e(LOG_TAG, "Stage file exists."); return false; }

            closeLogFile();

            boolean suc = logFile.renameTo(stageFile);
            if (!suc) { Log.e(LOG_TAG, "Staging Failed"); return false; }

            openLogFileOverwrite();

            sampleCount = 0;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error exporting samples", e);
            return false;
        }
        return true;
    }

    @Override
    public void deleteSamples() {
        if (disabled) return;
        try {
            closeLogFile();
            if (logFile.exists()) logFile.delete();
            openLogFileOverwrite();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error deleting samples", e);
        }
    }

    @Override
    public boolean hasSamples() {
        return logFile.length() > 0;
    }


    @Override
    public String getStatus() {
        return "File size: " + logFile.length()/1024 + "kb. Samples written: " + sampleCount;
    }

    private void openLogFileAppend() throws IOException {
        fileWriter = new BufferedWriter(new FileWriter(logFile,true));
    }

    private void openLogFileOverwrite() throws IOException {
        fileWriter = new BufferedWriter(new FileWriter(logFile,false));
    }

    private void closeLogFile() throws IOException {
        if (fileWriter == null) return;

        fileWriter.close();
        fileWriter = null;
    }

}
