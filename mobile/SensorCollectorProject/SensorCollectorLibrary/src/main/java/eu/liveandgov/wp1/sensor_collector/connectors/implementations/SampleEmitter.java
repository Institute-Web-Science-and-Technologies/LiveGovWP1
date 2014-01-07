package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by hartmann on 11/12/13.
 */
public class SampleEmitter implements Consumer<String> {
    private SensorQueue sensorQueue = GlobalContext.getSensorQueue();
    private String prefix = "";

    public SampleEmitter(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void push(String value) {
        sensorQueue.push(SensorSerializer.fillDefaults(prefix, value));
    }

}
