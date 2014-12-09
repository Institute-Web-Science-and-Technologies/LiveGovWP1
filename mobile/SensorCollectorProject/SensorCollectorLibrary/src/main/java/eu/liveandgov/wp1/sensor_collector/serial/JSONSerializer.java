package eu.liveandgov.wp1.sensor_collector.serial;

import com.google.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * <p>
 * Serializes an item to the common JSON forma
 * </p>
 * <p>
 * Created on 09.12.2014.
 * </p>
 *
 * @author lukashaertel
 * @author cehlen
 */
@Singleton
public class JSONSerializer implements ItemSerializer {
    @Override
    public String serialize(Item item) {
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
}
