package eu.liveandgov.sensorcollectorv3.connectors.implementations;

import java.util.ArrayList;

import eu.liveandgov.sensorcollectorv3.configuration.ExtendedIntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.SsfFileFormat;
import eu.liveandgov.sensorcollectorv3.sensors.sensor_value_objects.GpsSensorValue;
import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;
import eu.liveandgov.wp1.feature_pipeline.helper.TimedQueue;

/**
 * Created by hartmann on 11/14/13.
 */
public class GpsCache implements Consumer<String> {

    PrefixFilter filter;
    GpsCacheImpl cacheImpl;
    Multiplexer<String> mpx;
    IntentEmitter gpsBroadcast;

    public GpsCache() {
        cacheImpl = new GpsCacheImpl();
        mpx       = new Multiplexer<String>();
        gpsBroadcast = new IntentEmitter(ExtendedIntentAPI.RETURN_GPS_SAMPLE, ExtendedIntentAPI.FIELD_GPS_ENTRY);

        filter = new PrefixFilter();
        filter.addFilter(SsfFileFormat.SSF_GPS);
        filter.setConsumer(mpx);
        mpx.addConsumer(cacheImpl);
        mpx.addConsumer(gpsBroadcast);
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
            GpsSensorValue value = SensorSerializer.parseGpsEvent(message);
            Q.push(value.time, message);
        }

        public synchronized ArrayList<String> getSamples(){
            return Q.toArrayList();
        }
    }

}
