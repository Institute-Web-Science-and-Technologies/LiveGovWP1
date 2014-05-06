package eu.liveandgov.wp1.sensor_collector.activity_recognition;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by cehlen on 10/19/13.
 */
class MotionSensorValueProducer extends Producer<MotionSensorValue> implements Consumer<String> {

    @Override
    public void push(String m) {
        MotionSensorValue msv = SensorSerializer.parseMotionEvent(m);
        consumer.push(msv);
    }

}