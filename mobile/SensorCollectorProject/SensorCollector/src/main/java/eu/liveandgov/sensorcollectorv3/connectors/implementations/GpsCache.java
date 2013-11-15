package eu.liveandgov.sensorcollectorv3.connectors.implementations;

import java.util.ArrayList;

import eu.liveandgov.sensorcollectorv3.configuration.ExtendedIntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.SsfFileFormat;
import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.human_activity_recognition.TimedQueue;
import eu.liveandgov.sensorcollectorv3.sensors.GpsSensorValue;
import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;

/**
 * Created by hartmann on 11/14/13.
 */
public class GpsCache implements Consumer<String> {

    PrefixFilter filter;
    GpsCacheImpl cacheImpl;
    Multiplexer mpx;
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

    private class GpsCacheImpl implements Consumer<String> {

        TimedQueue<String> Q = new TimedQueue<String>(5 * 1000 * 60); // 1 Minutes time

        @Override
        public void push(String message) {
            GpsSensorValue value = SensorSerializer.parseGpsEvent(message);
            Q.push(value.time, message);
        }

        public ArrayList<String> getSamples(){
            return Q.toArrayList();
        }
    }

}
