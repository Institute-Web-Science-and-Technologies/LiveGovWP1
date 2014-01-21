package eu.liveandgov.wp1.sensor_collector.pps;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_value_objects.GpsSensorValue;

/**
 * Created by cehlen on 10/19/13.
 */
class GpsSensorValueProducer extends Producer<GpsSensorValue> implements Consumer<String> {

    @Override
    public void push(String m) {
        GpsSensorValue gpssv = SensorSerializer.parseGpsEvent(m);
        consumer.push(gpssv);
    }

}