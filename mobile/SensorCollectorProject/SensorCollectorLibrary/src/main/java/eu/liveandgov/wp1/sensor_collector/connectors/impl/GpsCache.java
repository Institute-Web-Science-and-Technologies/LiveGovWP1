package eu.liveandgov.wp1.sensor_collector.connectors.impl;

import java.util.ArrayList;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.helper.TimedQueue;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ClassFilter;
import eu.liveandgov.wp1.pipeline.impl.ItemSerializer;
import eu.liveandgov.wp1.pipeline.impl.Multiplexer;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;

/**
 * Created by hartmann on 11/14/13.
 */
public class GpsCache implements Consumer<Item> {

    ClassFilter<GPS> filter;
    GpsCacheImpl cacheImpl;
    Multiplexer<GPS> mpx;
    ItemSerializer itemSerializer;
    IntentEmitter gpsBroadcast;

    public GpsCache() {

        filter = new ClassFilter<GPS>(GPS.class);

        mpx = new Multiplexer<GPS>();
        filter.setConsumer(mpx);

        cacheImpl = new GpsCacheImpl();
        mpx.addConsumer(cacheImpl);

        itemSerializer = new ItemSerializer();
        mpx.addConsumer(itemSerializer);

        gpsBroadcast = new IntentEmitter(ExtendedIntentAPI.RETURN_GPS_SAMPLE, ExtendedIntentAPI.FIELD_GPS_ENTRY);
        itemSerializer.setConsumer(gpsBroadcast);
    }

    @Override
    public void push(Item item) {
        filter.push(item);
    }

    public ArrayList<GPS> getSamples() {
        return cacheImpl.getSamples();
    }

    public String getEntryString() {
        StringBuilder out = new StringBuilder(10000);

        for (GPS line : getSamples()) {
            out.append(line.toSerializedForm() + "\n");
        }

        return out.toString();
    }

    /**
     * Queues sensor values in a given time frame.
     * <p/>
     * The queued messages can be received by another tread using the getSamples() method.
     */
    private static class GpsCacheImpl implements Consumer<GPS> {
        private static int LENGTH_IN_MINUTES = 5;
        private static long LENGTH_IN_MS = LENGTH_IN_MINUTES * 60 * 1000;

        TimedQueue<GPS> Q = new TimedQueue<GPS>(LENGTH_IN_MS);

        @Override
        public synchronized void push(GPS gps) {
            Q.push(gps.getTimestamp(), gps);
        }

        public synchronized ArrayList<GPS> getSamples() {
            return Q.toArrayList();
        }
    }

}
