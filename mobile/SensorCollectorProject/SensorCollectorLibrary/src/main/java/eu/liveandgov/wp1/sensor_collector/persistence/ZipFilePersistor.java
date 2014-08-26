package eu.liveandgov.wp1.sensor_collector.persistence;

import android.os.Handler;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.serialization.Serialization;
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

    public final Function<Item, String> serialization;

    public ZipFilePersistor(File logFile,Function<Item, String> serialization) {
        this.logFile = logFile;
        this.serialization=serialization;

        openLogFileAppend();
    }

    @Override
    public synchronized void push(Item item) {
        if (fileWriter == null) {
            log.info("Blocked write event");
            return;
        }

        try {
            fileWriter.write(serialization.apply(item) + "\n");
            sampleCount++;
        } catch (IOException e) {
            log.error("Cannot write file.", e);
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        boolean suc;

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
        if (!logFile.delete())
            log.error("Could not delete log file");

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

            sanityCheck();

            fileWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(logFile, true)), "UTF8"));
        } catch (IOException e) {
            log.error("Error opening log file for appending", e);
            return false;
        }
        return true;
    }

    private void sanityCheck() throws IOException {
        // Create sanity file
        File f = File.createTempFile("sanity", "." + Files.getFileExtension(logFile.getName()), logFile.getParentFile());

        // Create input stream on file to read and sanity file
        MultiMemberGZIPInputStream gis = new MultiMemberGZIPInputStream(new FileInputStream(logFile));
        GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(f));

        // Copy as much as possible
        try {
            // Do a byte-wise copy, retrieve as much as possible before running into an IO exception
            int d;
            while ((d = gis.read()) != -1)
                gos.write(d);
        } catch (EOFException e) {
            // This is due to someone not regarding specifications
        } catch (IOException e) {
            log.error("An error occurred during the sanity transfer", e);
        } finally {
            gis.close();
            gos.close();
        }

        if (f.length() != logFile.length()) {
            // Number of lines to output to the user
            final int n = 5;

            // Obtain a reader
            BufferedReader r = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f)), "UTF8"));

            // Read all lines to a buffer
            String s;
            Deque<String> buffer = new LinkedList<String>();
            while ((s = r.readLine()) != null) {
                buffer.offer(s);

                // Limit the buffer size
                while (buffer.size() > n)
                    buffer.poll();
            }

            // Close the cascade
            r.close();

            // Output as debug
            log.debug("Recovered insanity error, " + f.length() + " of " + logFile.length() + " were recovered, last " + buffer.size() + " lines are: " + Joiner.on("\r\n").join(buffer));
        }

        // Replace file with sanity file
        if (logFile.delete()) {
            if (!f.renameTo(logFile))
                log.error("Could not complete sanity transfer");
        } else
            log.error("Could not initiate sanity transfer");
    }

    private boolean openLogFileOverwrite() {
        try {
            fileWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(logFile, false)), "UTF8"));
        } catch (IOException e) {
            log.error("Error opening log file for overwriting", e);
            return false;
        }

        return true;
    }

    private boolean closeLogFile() {
        if (fileWriter == null) return true;

        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            log.error("Error closing log file", e);
            return false;
        }
        fileWriter = null;
        return true;
    }
}
