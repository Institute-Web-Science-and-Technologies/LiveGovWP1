package eu.liveandgov.wp1.sensor_collector.connectors.impl;

import java.util.ArrayList;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.human_activity_recognition.helper.TimedQueue;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ClassFilter;
import eu.liveandgov.wp1.pipeline.impl.Multiplexer;
import eu.liveandgov.wp1.pipeline.impl.Serializer;
import eu.liveandgov.wp1.pipeline.impl.StartsWith;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.serialization.impl.GPSSerialization;

/**
 * Created by hartmann on 11/14/13.
 */
public class GpsCache implements Consumer<Item> {

    ClassFilter<GPS> filter;
    Serializer<GPS> serializer;
    GpsCacheImpl cacheImpl;
    Multiplexer<String> mpx;
    IntentEmitter gpsBroadcast;

    public GpsCache() {

        filter = new ClassFilter<GPS>(GPS.class);

        serializer = new Serializer<GPS>(GPSSerialization.GPS_SERIALIZATION);
        filter.setConsumer(serializer);

        mpx = new Multiplexer<String>();
        serializer.setConsumer(mpx);

        cacheImpl = new GpsCacheImpl();
        mpx.addConsumer(cacheImpl);
        
        gpsBroadcast = new IntentEmitter(ExtendedIntentAPI.RETURN_GPS_SAMPLE, ExtendedIntentAPI.FIELD_GPS_ENTRY);
        mpx.addConsumer(gpsBroadcast);
    }

    @Override
    public void push(Item item) {
        filter.push(item);
    }

    public ArrayList<String> getSamples() {
        return cacheImpl.getSamples();
    }

    public String getEntryString() {
        StringBuilder out = new StringBuilder(10000);

        for (String line : getSamples()) {
            out.append(line + "\n");
        }

        return out.toString();
    }

    /**
     * Queues sensor values in a given time frame.
     * <p/>
     * The queued messages can be received by another tread using the getSamples() method.
     */
    private static class GpsCacheImpl implements Consumer<String> {
        private static int LENGTH_IN_MINUTES = 5;
        private static long LENGTH_IN_MS = LENGTH_IN_MINUTES * 60 * 1000;

        TimedQueue<String> Q = new TimedQueue<String>(LENGTH_IN_MS);

        @Override
        public synchronized void push(String message) {
            final GPS gps = GPSSerialization.GPS_SERIALIZATION.deSerialize(message);

            Q.push(gps.getTimestamp(), message);
        }

        public synchronized ArrayList<String> getSamples() {
            return Q.toArrayList();
        }
    }

}
