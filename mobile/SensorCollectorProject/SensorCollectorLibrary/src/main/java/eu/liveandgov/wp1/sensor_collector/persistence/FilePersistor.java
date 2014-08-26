package eu.liveandgov.wp1.sensor_collector.persistence;

import com.google.common.base.Function;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.serialization.Serialization;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * Persistor class that writes samples into a log file.
 * <p/>
 * Created by hartmann on 9/20/13.
 */
public class FilePersistor implements Persistor {
    private final Logger log = LogPrincipal.get();

    private File logFile;
    protected BufferedWriter fileWriter;
    private long sampleCount = 0L;

    protected boolean disabled = false;

    // TODO: Protect from filling up all memory: Max Sampels? Set fixed file size?


    public final Function<Item, String> serialization;

    public FilePersistor(File logFile, Function<Item, String> serialization) {
        this.logFile = logFile;
        this.serialization = serialization;
        try {
            openLogFileAppend();
        } catch (IOException e) {
            log.error("Could not open file. Disabling Persistor.");
            disabled = true;
        }
    }

    @Override
    public synchronized void push(Item item) {
        if (disabled) return;
        try {
            if (fileWriter == null) {
                log.info("Blocked write event");
                return;
            }

            fileWriter.write(serialization.apply(item) + "\n");
            sampleCount++;

        } catch (IOException e) {
            log.error("Cannot write file.", e);
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        if (disabled) return false;

        try {
            log.info("Exporting samples.");

            if (stageFile.exists()) {
                log.error("Stage file exists.");
                return false;
            }

            closeLogFile();

            boolean suc = logFile.renameTo(stageFile);
            if (!suc) {
                log.error("Staging Failed");
                return false;
            }

            openLogFileOverwrite();

            sampleCount = 0;
        } catch (IOException e) {
            log.error("Error exporting samples", e);
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
            log.error("Error deleting samples", e);
        }
    }

    @Override
    public void close() {
        try {
            closeLogFile();
        } catch (IOException e) {
            log.error("Error closing file persistor", e);
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
        log.info("Opening Log File to Append: " + logFile);
        fileWriter = new BufferedWriter(new FileWriter(logFile, true));
    }

    private void openLogFileOverwrite() throws IOException {
        log.info("Overwrining Log File: " + logFile);
        fileWriter = new BufferedWriter(new FileWriter(logFile, false));
    }

    private void closeLogFile() throws IOException {
        log.info("Closing Log File");

        if (fileWriter == null) return;

        fileWriter.close();
        fileWriter = null;
    }

    @Override
    public String toString() {
        return "File persistor";
    }
}
