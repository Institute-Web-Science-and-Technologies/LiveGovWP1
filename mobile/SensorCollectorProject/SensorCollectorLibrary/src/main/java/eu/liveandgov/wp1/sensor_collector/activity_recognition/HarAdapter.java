package eu.liveandgov.wp1.sensor_collector.activity_recognition;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.human_activity_recognition.HarPipeline;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.StartsWith;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.IntentEmitter;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;
import eu.liveandgov.wp1.serialization.impl.ActivitySerialization;

/**
 * <p>Pipeline class that consumes accelerometer values and produces an activity stream.
 * </p>
 * Todo: Adapt the codebase of HAR to use new pipeline commons
 * Created by hartmann on 10/20/13.
 */
public class HARAdapter implements Consumer<String> {

    private final StartsWith filter;
    private final MotionSensorValueProducer parseProd;
    private final Pipeline<MotionSensorValue, String> harPipeline;

    public HARAdapter() {
        // ACC filter
        filter = new StartsWith();
        filter.addPrefix(DataCommons.TYPE_ACCELEROMETER);

        // Parser
        parseProd = new MotionSensorValueProducer();
        filter.setConsumer(parseProd);

        // HAR
        harPipeline = new HarPipeline(1000);
        parseProd.setConsumer(new Consumer<MotionSensorValue>() {
            @Override
            public void push(MotionSensorValue motionSensorValue) {
                harPipeline.push(motionSensorValue);
            }
        });

        final IntentEmitter intentEmitter = new IntentEmitter(IntentAPI.RETURN_ACTIVITY, IntentAPI.FIELD_ACTIVITY);
        final SensorEmitter sensorEmitter = new SensorEmitter();

        harPipeline.setConsumer(new eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer<String>() {
            @Override
            public void push(String s) {
                intentEmitter.push(s);
                sensorEmitter.push(ActivitySerialization.ACTIVITY_SERIALIZATION.serialize(new Activity(System.currentTimeMillis(), GlobalContext.getUserId(), s)));
            }
        });
    }

    @Override
    public void push(String m) {
        filter.push(m);
    }

}
