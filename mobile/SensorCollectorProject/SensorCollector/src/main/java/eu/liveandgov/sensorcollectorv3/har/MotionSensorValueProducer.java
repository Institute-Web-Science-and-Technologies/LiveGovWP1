package eu.liveandgov.sensorcollectorv3.har;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.connector.Producer;
import eu.liveandgov.sensorcollectorv3.sensors.SensorParser;
import eu.liveandgov.sensorcollectorv3.sensors.MotionSensorValue;

/**
 * Created by cehlen on 10/19/13.
 */
public class MotionSensorValueProducer extends Producer<MotionSensorValue> implements Consumer<String> {

    @Override
    public void push(String m) {
        MotionSensorValue msv = SensorParser.parseEvent(m);
        consumer.push(msv);
    }

}