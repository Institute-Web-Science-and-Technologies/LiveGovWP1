package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.connectors.Producer;
import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;
import eu.liveandgov.sensorcollectorv3.sensors.MotionSensorValue;

/**
 * Created by cehlen on 10/19/13.
 */
class MotionSensorValueProducer extends Producer<MotionSensorValue> implements Consumer<String> {

    @Override
    public void push(String m) {
        MotionSensorValue msv = SensorSerializer.parseEvent(m);
        consumer.push(msv);
    }

}