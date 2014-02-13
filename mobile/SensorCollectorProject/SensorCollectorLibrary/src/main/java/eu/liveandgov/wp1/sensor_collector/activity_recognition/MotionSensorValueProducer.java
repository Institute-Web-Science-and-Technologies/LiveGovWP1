package eu.liveandgov.wp1.sensor_collector.activity_recognition;

import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.serialization.impl.MotionSerialization;

/**
 * Created by cehlen on 10/19/13.
 */
class MotionSensorValueProducer extends Pipeline<String, MotionSensorValue> {

    @Override
    public void push(String item) {
        final Motion motion = MotionSerialization.MOTION_SERIALIZATION.deSerialize(item);

        final MotionSensorValue msv = new MotionSensorValue();
        msv.type = motion.getType();
        msv.time = motion.getTimestamp();
        msv.id = motion.getDevice();

        msv.x = motion.values[0];
        msv.y = motion.values[1];
        msv.z = motion.values[2];

        produce(msv);
    }

}