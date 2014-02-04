package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 04.02.14.
 */
public class ToSSFConverter<T> extends Pipeline<T, String>  {
    private final SensorSerializer.Conversion<T> conversion;

    public ToSSFConverter(SensorSerializer.Conversion<T> conversion) {
        this.conversion = conversion;
    }

    @Override
    public void push(T value) {
        if(consumer != null)
        {
            consumer.push(conversion.toSSFDefault(value));
        }
    }

}
