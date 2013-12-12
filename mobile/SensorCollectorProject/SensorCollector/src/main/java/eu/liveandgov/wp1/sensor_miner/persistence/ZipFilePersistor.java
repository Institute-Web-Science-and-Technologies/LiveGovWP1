package eu.liveandgov.wp1.sensor_miner.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPOutputStream;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;

/**
 * Persistor class that writes samples into a zipped log file.
 *
 * Created by hartmann on 9/20/13.
 */
public class ZipFilePersistor implements Persistor {
    public static final String LOG_TAG = "ZFP";
    public static final String FILENAME = "sensor.log.gz";

    private static final String SHARED_PREFS_NAME = "ZipFilePersistorPrefs";
    private static final String PREF_VALID_LENGTH = "validLength";

    // Set this flag to write validation point after every push
    private static final boolean DEFENSIVE_VALIDATION_POINTS = true;

    private File logFile;
    private BufferedWriter fileWriter;
    private long sampleCount = 0L;

    public ZipFilePersistor(File logFile) {
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

            // We defensive now, so store the new valid length of the GZIP stream
            if(DEFENSIVE_VALIDATION_POINTS)
            {
                putValidLength(logFile.length());
            }

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

        // Renamed, the valid length is now zero
        putValidLength(0);
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
    public void deleteSamples() {
        closeLogFile();

        // Deleted, the valid length is now zero
        putValidLength(0);
        logFile.delete();
    }

    @Override
    public String getStatus() {
        return "File size: " + logFile.length()/1024 + "kb. Samples written: " + sampleCount;
    }

    private boolean openLogFileAppend() {
        try {
            // Compare actual length to valid length
            final long validLength = getValidLength();
            final long actualLength = logFile.length();

            Log.d(LOG_TAG, "Valid zipfile length: " + validLength + ", actual length " + actualLength);

            if(actualLength > validLength)
            {
                Log.w(LOG_TAG, "Erronous file size, truncating");

                // Truncate if mismatching
                final FileChannel channel = new FileOutputStream(logFile, true).getChannel();

                channel.truncate(validLength);
                channel.close();
            }

            fileWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(logFile,true)), "UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Gets the valid length
    private long getValidLength() {
        SharedPreferences prefs = GlobalContext.context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        return prefs.getLong(PREF_VALID_LENGTH, 0L);
    }

    // Store last value to keep access to the shared prefs to a minimum
    private long lastPutValidLength = -1;

    // Puts the next valid length if it differs from the last put value
    private void putValidLength(long value)
    {
        if(lastPutValidLength == value)
        {
            return;
        }

        lastPutValidLength = value;

        Log.d(LOG_TAG, "New valid length: " + value);

        SharedPreferences prefs = GlobalContext.context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(PREF_VALID_LENGTH, value);

        editor.commit();
    }

    private boolean openLogFileOverwrite() {
        try {
            fileWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(logFile,false)), "UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean closeLogFile() {
        try {
            putValidLength(logFile.length());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        fileWriter = null;
        return true;
    }
}
