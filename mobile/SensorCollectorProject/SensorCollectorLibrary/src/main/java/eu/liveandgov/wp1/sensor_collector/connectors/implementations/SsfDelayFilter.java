package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_value_objects.GpsSensorValue;

/**
 * Filter Pipeline that lets only samples pass that are a certain time appart.
 */
public class SsfDelayFilter extends Pipeline<String, String> {

    private static final String LOG_TAG = "DF";
    private int delayMs = 0;
    private long lastTs = 0;

    public SsfDelayFilter(int delayMs) {
        this.delayMs = delayMs;
    }

    @Override
    public void push(String s) {
        long ts = parseTs(s);
        if (Math.abs(lastTs - ts) > delayMs) {
            // delay big enough
            consumer.push(s);
            lastTs = ts;
        } else {
            // delay too small: Discard
            Log.d(LOG_TAG, "Discarding Message. Delay too short: " + Math.abs(lastTs - ts) + "ms");
        }
    }

    private long parseTs(String s) {
        GpsSensorValue gpsSensorValue = SensorSerializer.parseGpsEvent(s);
        return gpsSensorValue.time;
    }
}
