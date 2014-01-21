package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by hartmann on 11/12/13.
 */
public class ConversionEmitter<T> implements Consumer<T> {
    private final SensorQueue sensorQueue = GlobalContext.getSensorQueue();
    private final SensorSerializer.Conversion<T> conversion;

    public ConversionEmitter(SensorSerializer.Conversion<T> conversion) {
        this.conversion = conversion;
    }

    @Override
    public void push(T value) {
        sensorQueue.push(conversion.toSSFDefault(value));
    }

}
