package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import android.util.Log;

import java.util.ArrayList;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.human_activity_recognition.helper.TimedQueue;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_value_objects.GpsSensorValue;

/**
 * Created by hartmann on 11/14/13.
 */
public class GpsCache implements Consumer<String> {

    PrefixFilter filter;
    GpsCacheImpl cacheImpl;
    Multiplexer<String> mpx;
    IntentEmitter gpsBroadcast;
    Pipeline<String, String> delayFilter;

    public GpsCache() {
        cacheImpl = new GpsCacheImpl();
        mpx       = new Multiplexer<String>();
        gpsBroadcast = new IntentEmitter(ExtendedIntentAPI.RETURN_GPS_SAMPLE, ExtendedIntentAPI.FIELD_GPS_ENTRY);
        delayFilter = new SsfDelayFilter(30000);

        filter = new PrefixFilter();
        filter.addFilter(SsfFileFormat.SSF_GPS);
        filter.setConsumer(mpx);
        mpx.addConsumer(delayFilter);
        mpx.addConsumer(gpsBroadcast);

        delayFilter.setConsumer(cacheImpl);
    }

    @Override
    public void push(String message) {
        filter.push(message);
    }

    public ArrayList<String> getSamples() {
        return cacheImpl.getSamples();
    }

    public String getEntryString() {
        StringBuilder out = new StringBuilder(10000);

        for (String line : getSamples()){
            out.append(line + "\n");
        }

        return out.toString();
    }

    /**
     * Queues sensor values in a given time frame.
     *
     * The queued messages can be received by another tread using the getSamples() method.
     */
    private static class GpsCacheImpl implements Consumer<String> {
        private static int LENGTH_IN_MINUTES = 5;
        private static long LENGTH_IN_MS = LENGTH_IN_MINUTES * 60 * 1000;

        TimedQueue<String> Q = new TimedQueue<String>(LENGTH_IN_MS);

        @Override
        public synchronized void push(String message) {
            Log.d("GCI", "Cached value " + message);
            GpsSensorValue value = SensorSerializer.parseGpsEvent(message);
            Q.push(value.time, message);
        }

        public synchronized ArrayList<String> getSamples(){
            return Q.toArrayList();
        }
    }

}
