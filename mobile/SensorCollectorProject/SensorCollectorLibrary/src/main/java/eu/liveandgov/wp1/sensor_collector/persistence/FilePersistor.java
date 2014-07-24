package eu.liveandgov.wp1.sensor_collector.persistence;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * Persistor class that writes samples into a log file.
 * <p/>
 * Created by hartmann on 9/20/13.
 */
public class FilePersistor implements Persistor {
    public static final String LOG_TAG = "FP";

    private File logFile;
    protected BufferedWriter fileWriter;
    private long sampleCount = 0L;

    protected boolean disabled = false;

    // TODO: Protect from filling up all memory: Max Sampels? Set fixed file size?

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
    public synchronized void push(Item item) {
        if (disabled) return;
        try {
            if (fileWriter == null) {
                Log.v(LOG_TAG, "Blocked write event");
                return;
            }

            fileWriter.write(item.toSerializedForm() + "\n");
            sampleCount++;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Cannot write file.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        if (disabled) return false;

        try {
            Log.i(LOG_TAG, "Exporting samples.");

            if (stageFile.exists()) {
                Log.e(LOG_TAG, "Stage file exists.");
                return false;
            }

            closeLogFile();

            boolean suc = logFile.renameTo(stageFile);
            if (!suc) {
                Log.e(LOG_TAG, "Staging Failed");
                return false;
            }

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

            if (logFile.exists()) {
                boolean suc = logFile.delete();
                if (!suc) throw new IOException("logFile.delete failed");
            }

            openLogFileOverwrite();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error deleting samples", e);
        }
    }

    @Override
    public void close() {
        try {
            closeLogFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasSamples() {
        return logFile.length() > 0;
    }


    @Override
    public String getStatus() {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
        stringBuilder.append("File size: ");
        stringBuilder.append(Math.round(logFile.length() / 1024.0));
        stringBuilder.append("kb. Samples written: ");
        stringBuilder.append(sampleCount);

        return stringBuilder.toString();
    }

    private void openLogFileAppend() throws IOException {
        Log.i(LOG_TAG, "Opening Log File to Append: " + logFile);
        fileWriter = new BufferedWriter(new FileWriter(logFile, true));
    }

    private void openLogFileOverwrite() throws IOException {
        Log.i(LOG_TAG, "Overwrining Log File: " + logFile);
        fileWriter = new BufferedWriter(new FileWriter(logFile, false));
    }

    private void closeLogFile() throws IOException {
        Log.i(LOG_TAG, "Closing Log File");

        if (fileWriter == null) return;

        fileWriter.close();
        fileWriter = null;
    }

    @Override
    public String toString() {
        return "File persistor";
    }
}
