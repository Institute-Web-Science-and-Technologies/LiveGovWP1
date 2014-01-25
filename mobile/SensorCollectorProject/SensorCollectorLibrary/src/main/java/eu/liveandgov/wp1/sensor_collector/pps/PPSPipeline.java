package eu.liveandgov.wp1.sensor_collector.pps;

import android.util.Log;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityService;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_value_objects.GpsSensorValue;

/**
 * Created by lukashaertel on 18.01.14.
 */
public class PPSPipeline extends Pipeline<GpsSensorValue, ProximityEvent> implements Monitorable {
    private static final String LOG_TAG = "PPSP";

    private String key;

    private ProximityService proximityService;

    private Proximity lastProximity;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ProximityService getProximityService() {
        return proximityService;
    }

    public void setProximityService(ProximityService proximityService) {
        this.proximityService = proximityService;
    }

    @Override
    public void push(final GpsSensorValue gpsSensorValue) {
        final Proximity prox = proximityService.calculate(gpsSensorValue.lat, gpsSensorValue.lon);

        if (!prox.equals(lastProximity)) {
            lastProximity = prox;

            consumer.push(new ProximityEvent(gpsSensorValue.time, key, prox));

            Log.d(LOG_TAG, "Now Proximity of " + prox + ", at lon: " + gpsSensorValue.lon + ", lat: " + gpsSensorValue.lat);
        }
    }

    @Override
    public String getStatus() {
        return lastProximity == null ? Proximity.NO_DECISION.toString() : lastProximity.toString();
    }
}
