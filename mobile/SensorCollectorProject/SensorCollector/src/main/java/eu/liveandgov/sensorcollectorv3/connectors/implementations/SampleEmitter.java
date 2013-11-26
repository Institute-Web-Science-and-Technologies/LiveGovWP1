package eu.liveandgov.sensorcollectorv3.connectors.implementations;

import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;

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
