package eu.liveandgov.wp1.sensor_collector.persistence;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonWriter;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.data.impl.Bluetooth;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.GSM;
import eu.liveandgov.wp1.data.impl.GoogleActivity;
import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.data.impl.Tag;
import eu.liveandgov.wp1.data.impl.Velocity;
import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.data.impl.WiFi;
import eu.liveandgov.wp1.sensor_collector.logging.LP;
import eu.liveandgov.wp1.util.LocalBuilder;


/**
 * Created by cehlen on 18/08/14.
 */
public class JSONPersistor implements Persistor {
    private final Logger log = LP.get();

    private File logFile;
    private JsonWriter jsonWriter;
    protected RandomAccessFile raf;
    private long sampleCount = 0L;

    protected boolean disabled = false;
    private boolean appened = false;

    public JSONPersistor(File logFile) {
        this.logFile = logFile;
        try {
            openLogFileAppend();
        } catch (IOException e) {
            log.error("Could not open file. Disabling persistor");
            disabled = true;
        }
    }

    @Override
    public boolean exportSamples(File stageFile) {
        if (disabled) return false;

        try {
            log.info("Exporting samples");

            if (stageFile.exists()) {
                log.error("Stage file exists.");
                return false;
            }

            closeLogFile();

            boolean suc = logFile.renameTo(stageFile);
            if (!suc) {
                log.error("Staging failed");
                return false;
            }

            openLogFileOverwrite();
            sampleCount = 0l;
        } catch (IOException e) {
            log.error("Error exporting Samples", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean hasSamples() {
        return logFile.length() > 0;
    }

    @Override
    public void deleteSamples() {
        if (disabled) return;
        try {
            closeLogFile();

            if (logFile.exists()) {
                boolean suc = logFile.delete();
                if (!suc) throw new IOException("logFile.delete failed!");
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
            log.error("Error closing json file persistor", e);
        }
    }

    @Override
    public synchronized void push(Item item) {
        if (disabled) return;
        try {
            if (raf == null) {
                log.info("Blocked write event");
                return;
            }

            if (logFile.length() > 1) {
                raf.writeBytes(",");
            }

            System.out.println(serialize(item));

            raf.writeBytes(serialize(item));
            sampleCount++;

        } catch (IOException e) {
            log.error("Cannot write file.", e);
        }
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
        log.info("Opening JSON Log File to Append: " + logFile);
        boolean fileExists = logFile.exists();

        raf = new RandomAccessFile(logFile, "rw");
        if (fileExists && logFile.length() > 0) { // we want to append before the last bracket `]`
            raf.seek(logFile.length() - 1);
            appened = true;
        } else { // start array
            raf.writeBytes("[");
            raf.seek(logFile.length());
        }
    }

    private void openLogFileOverwrite() throws IOException {
        log.info("Overwriting JSON Log File: " + logFile);
        raf = new RandomAccessFile(logFile, "rw");
        raf.setLength(0);
        raf.writeBytes("[");
        raf.seek(logFile.length());
    }

    private void closeLogFile() throws IOException {
        log.info("Closing JSON Log File");

        if (raf == null) return;

        if (!appened) {
            raf.writeBytes("]");
        }
        raf.close();
        raf = null;
    }

    private String serialize(Item item) throws IllegalArgumentException {
        JSONArray json = new JSONArray();
        json.put(item.getType());
        json.put(item.getTimestamp());
        json.put(item.getDevice());
        if (item instanceof Motion) {
            Motion m = (Motion) item;
            json.put(m.values);
        } else if (item instanceof Activity) {
            Activity a = (Activity) item;
            JSONArray act = new JSONArray();
            act.put(a.activity);
            json.put(act);
        } else if (item instanceof Bluetooth) {
            throw new UnsupportedOperationException();
        } else if (item instanceof GoogleActivity) {
            GoogleActivity ga = (GoogleActivity) item;
            JSONArray act = new JSONArray();
            act.put(ga.activity);
            act.put(ga.confidence);
            json.put(act);
        } else if (item instanceof GPS) {
            GPS g = (GPS) item;
            JSONArray gps = new JSONArray();
            try {
                gps.put(g.lat);
                gps.put(g.lon);
                gps.put(g.alt);
            } catch (JSONException e) {
                // Why is this exception here and nowhere else...
                e.printStackTrace();
            }
            json.put(gps);
        } else if (item instanceof GSM) {
            throw  new UnsupportedOperationException();
        } else if (item instanceof WiFi) {
            throw new UnsupportedOperationException();
        } else if (item instanceof Tag) {
            Tag t = (Tag) item;
            JSONArray tag = new JSONArray();
            tag.put(t.tag);
            json.put(tag);
        } else if (item instanceof Proximity) {
            Proximity p = (Proximity) item;
            JSONArray proximity = new JSONArray();
            proximity.put(p.key);
            proximity.put(p.in);
            proximity.put(p.of);
            json.put(proximity);
        } else if (item instanceof Waiting) {
            Waiting w = (Waiting) item;
            JSONArray waiting = new JSONArray();
            waiting.put(w.key);
            waiting.put(w.duration);
            waiting.put(w.at);
            json.put(waiting);
        } else if (item instanceof Velocity) {
            Velocity v = (Velocity) item;
            JSONArray velocity = new JSONArray();
            try {
                velocity.put(v.velocity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json.put(velocity);
        } else {
            throw new IllegalArgumentException();
        }
        return json.toString();
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
