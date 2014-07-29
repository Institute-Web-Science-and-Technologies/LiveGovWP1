package eu.liveandgov.wp1.sensor_collector.activity_recognition;

import eu.liveandgov.wp1.HARPipeline;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ClassFilter;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.IntentEmitter;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;

/**
 * Pipeline class that consumes accelerometer values and produces an activity stream.
 * Created by hartmann on 10/20/13.
 */
public class    HARAdapter implements Consumer<Item> {
    private static final String LOG_TAG = "HARA";
    private final ClassFilter<Acceleration> filter;
    private final HARPipeline harPipeline;


    public HARAdapter() {

        // Type Filter
        filter = new ClassFilter<Acceleration>(Acceleration.class);

        harPipeline = new HARPipeline(1000);

        filter.setConsumer(harPipeline);

        // Emission
        final SensorEmitter sensorEmitter = new SensorEmitter();
        final IntentEmitter intentEmitter = new IntentEmitter(IntentAPI.RETURN_ACTIVITY, IntentAPI.FIELD_ACTIVITY);

        harPipeline.setConsumer(new Consumer<Triple<Long, Long, String>>() {
            @Override
            public void push(Triple<Long, Long, String> longStringTuple) {
                final Activity activity = new Activity(System.currentTimeMillis(), GlobalContext.getUserId(), longStringTuple.right);

                sensorEmitter.push(activity);
                intentEmitter.push(activity.activity);
            }
        });
    }

    @Override
    public void push(Item item) {
        filter.push(item);
    }

    @Override
    public String toString() {
        return "HAR Adapter";
    }
}
