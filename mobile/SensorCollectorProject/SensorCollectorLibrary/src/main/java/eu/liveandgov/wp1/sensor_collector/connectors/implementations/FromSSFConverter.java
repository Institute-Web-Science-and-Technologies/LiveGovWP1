package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 04.02.14.
 */
public class FromSSFConverter<T> extends Pipeline<String, T>  {
    private final SensorSerializer.Conversion<T> conversion;

    public FromSSFConverter(SensorSerializer.Conversion<T> conversion) {
        this.conversion = conversion;
    }

    @Override
    public void push(String value) {
        if(consumer != null)
        {
            consumer.push(conversion.fromSSF(value).data);
        }
    }
}
