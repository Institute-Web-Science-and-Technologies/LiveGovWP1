package eu.liveandgov.wp1.sensor_collector.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPOutputStream;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * Persistor class that writes samples into a zipped log file.
 * <p/>
 * Created by hartmann on 9/20/13.
 */
public class ZipFilePersistor implements Persistor {
    private final Logger log = LogPrincipal.get();

    public static final String FILENAME = "sensor.log.gz";

    private static final String SHARED_PREFS_NAME = "ZipFilePersistorPrefs";
    private static final String PREF_VALID_LENGTH = "validLength";

    private File logFile;
    private Handler handler;

    private BufferedWriter fileWriter;
    private long sampleCount = 0L;

    public ZipFilePersistor(File logFile) {
        this.logFile = logFile;

        openLogFileAppend();
    }

    @Override
    public synchronized void push(Item item) {
        if (fileWriter == null) {
            log.info("Blocked write event");
            return;
        }

        try {
            fileWriter.write(item.toSerializedForm() + "\n");
            sampleCount++;
        } catch (IOException e) {
            log.error("Cannot write file.", e);
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        boolean suc = true;

        log.info("Exporting samples.");
        if (stageFile.exists()) {
            log.error("Stage file exists.");
            return false;
        }

        suc = closeLogFile();
        if (!suc) {
            log.error("Cosing LogFile failed.");
            return false;
        }

        // Renamed, the valid length is now zero
        suc = logFile.renameTo(stageFile);
        putValidLength(0);

        if (!suc) {
            log.error("Renaming failed, tried to rename " + logFile + " to " + stageFile);
            return false;
        }

        suc = openLogFileOverwrite();
        if (!suc) {
            log.error("Opening new Log File failed.");
            return false;
        }

        sampleCount = 0;
        return true;
    }

    @Override
    public boolean hasSamples() {
        return sampleCount > 0;
    }

    @Override
    public void deleteSamples() {
        final boolean wasOpen = fileWriter != null;

        closeLogFile();

        // Deleted, the valid length is now zero
        logFile.delete();
        putValidLength(0);

        if (wasOpen) {
            // We can override here because we do in fact want to delete the samples
            openLogFileOverwrite();
        }
    }

    @Override
    public void close() {
        closeLogFile();
    }

    @Override
    public String getStatus() {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
        stringBuilder.append("ZIP File size: ");
        stringBuilder.append(Math.round(logFile.length() / 1024.0));
        stringBuilder.append("kb. Samples written: ");
        stringBuilder.append(sampleCount);

        return stringBuilder.toString();
    }

    private boolean openLogFileAppend() {
        try {

            truncateFileIfCorupted();

            fileWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(logFile, true)), "UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void truncateFileIfCorupted() throws IOException {
        // Compare actual length to valid length
        final long validLength = getValidLength();
        final long actualLength = logFile.length();

        log.debug("Valid zipfile length: " + validLength + ", actual length " + actualLength);

        if (actualLength > validLength) {
            log.warn("Erronous file size, truncating");

            // Truncate if mismatching
            final FileChannel channel = new FileOutputStream(logFile, true).getChannel();

            channel.truncate(validLength);
            channel.close();
        }
    }

    // Gets the valid length
    private long getValidLength() {
        SharedPreferences prefs = GlobalContext.context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        return prefs.getLong(PREF_VALID_LENGTH, 0L);
    }

    // Store last value to keep access to the shared prefs to a minimum
    private long lastPutValidLength = -1;

    // Puts the next valid length if it differs from the last put value
    private void putValidLength(long value) {
        if (lastPutValidLength == value) {
            return;
        }

        lastPutValidLength = value;

        log.debug("New valid length: " + value);

        SharedPreferences prefs = GlobalContext.context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(PREF_VALID_LENGTH, value);

        editor.commit();
    }

    private boolean openLogFileOverwrite() {
        try {
            fileWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(logFile, false)), "UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean closeLogFile() {
        if (fileWriter == null) return true;

        try {
            fileWriter.flush();
            fileWriter.close();

            putValidLength(logFile.length());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        fileWriter = null;
        return true;
    }
}
