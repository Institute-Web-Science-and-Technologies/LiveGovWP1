package eu.liveandgov.wp1.sensor_collector.pps;

import android.util.Log;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityType;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityService;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_value_objects.GpsSensorValue;

/**
 * Created by lukashaertel on 18.01.14.
 */
public class PPSPipeline extends Pipeline<GpsSensorValue, ProximityEvent>  {
    private static final String LOG_TAG = "PPSP";

    private final String key;

    private final ProximityService proximityService;

    public PPSPipeline(String key, ProximityService proximityService) {
        this.key = key;
        this.proximityService = proximityService;
    }

    @Override
    public void push(final GpsSensorValue gpsSensorValue) {
        final Proximity prox = proximityService.calculate(gpsSensorValue.lat, gpsSensorValue.lon);

        if (prox != null) {
            consumer.push(new ProximityEvent(gpsSensorValue.time, key, prox));

            Log.d(LOG_TAG, "Now ProximityType of " + prox + ", at lon: " + gpsSensorValue.lon + ", lat: " + gpsSensorValue.lat);
        }
    }
}
