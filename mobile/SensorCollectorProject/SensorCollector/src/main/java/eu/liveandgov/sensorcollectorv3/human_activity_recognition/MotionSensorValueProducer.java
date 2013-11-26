package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Producer;
import eu.liveandgov.wp1.feature_pipeline.containers.MotionSensorValue;

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