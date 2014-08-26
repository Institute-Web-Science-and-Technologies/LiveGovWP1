package eu.liveandgov.wp1.sensor_collector.persistence;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonWriter;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;

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
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.util.LocalBuilder;


/**
 * Created by cehlen on 18/08/14.
 */
public class JSONPersistor implements Persistor {
    private static final Logger log = LogPrincipal.get();

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
                raf.writeBytes(",\n");
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

    public static String serialize(Item item) throws IllegalArgumentException {
        try {
            JSONObject json = new JSONObject();

            json.put("type", item.getType());
            json.put("ts", item.getTimestamp());
            json.put("user", item.getDevice());
            if (item instanceof Motion) {
                Motion m = (Motion) item;
                JSONArray motion = new JSONArray();
                for (float v : m.values)
                    motion.put(v);

                json.put("value", motion);
            } else if (item instanceof Activity) {
                Activity a = (Activity) item;
                json.put("value", a.activity);
            } else if (item instanceof Bluetooth) {
                Bluetooth b = (Bluetooth) item;
                JSONArray bluetooth = new JSONArray();
                for (Bluetooth.Item i : b.items) {
                    JSONObject obj = new JSONObject();
                    obj.put("address", i.address);
                    obj.put("bondState", i.bondState);
                    obj.put("deviceMajorClass", i.deviceMajorClass);
                    obj.put("deviceMinorClass", i.deviceMinorClass);
                    obj.put("name", i.name);
                    obj.put("rssi", i.rssi);
                    bluetooth.put(obj);
                }
                json.put("value", bluetooth);
            } else if (item instanceof GoogleActivity) {
                GoogleActivity ga = (GoogleActivity) item;
                JSONObject act = new JSONObject();
                act.put("activity", ga.activity);
                act.put("confidence", ga.confidence);
                json.put("value", act);
            } else if (item instanceof GPS) {
                GPS g = (GPS) item;
                JSONArray gps = new JSONArray();

                gps.put(g.lat);
                gps.put(g.lon);
                gps.put(g.alt);

                json.put("value", gps);
            } else if (item instanceof GSM) {
                GSM g = (GSM) item;
                JSONArray gsm = new JSONArray();
                for (GSM.Item i : g.items) {
                    JSONObject obj = new JSONObject();
                    obj.put("rssi", i.rssi);
                    obj.put("cellIdentity", i.cellIdentity);
                    obj.put("cellType", i.cellType);
                    gsm.put(obj);
                }
                json.put("value", gsm);
            } else if (item instanceof WiFi) {
                WiFi w = (WiFi) item;
                JSONArray wifi = new JSONArray();
                for (WiFi.Item i : w.items) {
                    JSONObject obj = new JSONObject();
                    obj.put("bssid", i.bssid);
                    obj.put("frequenct", i.frequency);
                    obj.put("level", i.level);
                    obj.put("ssid", i.ssid);
                    wifi.put(obj);
                }
                json.put("value", wifi);
            } else if (item instanceof Tag) {
                Tag t = (Tag) item;
                json.put("value", t.tag);
            } else if (item instanceof Proximity) {
                Proximity p = (Proximity) item;
                JSONObject proximity = new JSONObject();
                proximity.put("key", p.key);
                proximity.put("in", p.in);
                proximity.put("of", p.of);
                json.put("value", proximity);
            } else if (item instanceof Waiting) {
                Waiting w = (Waiting) item;
                JSONObject waiting = new JSONObject();
                waiting.put("key", w.key);
                waiting.put("duration", w.duration);
                waiting.put("at", w.at);
                json.put("value", waiting);
            } else if (item instanceof Velocity) {
                Velocity v = (Velocity) item;
                json.put("value", v.velocity);
            } else {
                throw new IllegalArgumentException();
            }
            return json.toString();
        } catch (JSONException e) {
            // Non-recoverable exception, serialization must convert, delegate item filtering before that
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
