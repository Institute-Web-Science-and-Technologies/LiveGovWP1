package eu.liveandgov.wp1.sensor_collector.activity_recognition;

import com.google.common.base.Function;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.pipeline.ActivityPipeline;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.FeaturePipeline;
import eu.liveandgov.wp1.pipeline.InterpolationPipeline;
import eu.liveandgov.wp1.pipeline.QualityPipeline;
import eu.liveandgov.wp1.pipeline.WindowPipeline;
import eu.liveandgov.wp1.pipeline.impl.ClassFilter;
import eu.liveandgov.wp1.pipeline.impl.Transformation;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.IntentEmitter;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;

/**
 * Pipeline class that consumes accelerometer values and produces an activity stream.
 * Created by hartmann on 10/20/13.
 */
public class HARAdapter implements Consumer<Item> {
    private static final String LOG_TAG = "HARA";
    private final ClassFilter<Acceleration> filter;
    private final Transformation<Acceleration, Tuple<Long, Acceleration>> tupleTransformation;
    private final WindowPipeline windowPipeline;
    private final QualityPipeline qualityPipeline;
    private final InterpolationPipeline interpolationPipeline;
    private final FeaturePipeline featurePipeline;
    private final ActivityPipeline activityPipeline;


    public HARAdapter() {

        // Type Filter
        filter = new ClassFilter<Acceleration>(Acceleration.class);

        // Trip-ID annotation
        tupleTransformation = new Transformation<Acceleration, Tuple<Long, Acceleration>>(new Function<Acceleration, Tuple<Long, Acceleration>>() {
            @Override
            public Tuple<Long, Acceleration> apply(Acceleration acceleration) {
                return Tuple.create(-1L, acceleration);
            }
        });
        filter.setConsumer(tupleTransformation);

        // Windowing
        windowPipeline = new WindowPipeline(5000, 4500);
        tupleTransformation.setConsumer(windowPipeline);

        // Quality check
        qualityPipeline = new QualityPipeline(40);
        windowPipeline.setConsumer(qualityPipeline);


        // Interpolation
        interpolationPipeline = new InterpolationPipeline(50);
        qualityPipeline.setConsumer(interpolationPipeline);

        // Feature extraction
        featurePipeline = new FeaturePipeline();
        interpolationPipeline.setConsumer(featurePipeline);


        // Activity recognition
        activityPipeline = new ActivityPipeline(0);
        featurePipeline.setConsumer(activityPipeline);

        // Emission
        final SensorEmitter sensorEmitter = new SensorEmitter();
        final IntentEmitter intentEmitter = new IntentEmitter(IntentAPI.RETURN_ACTIVITY, IntentAPI.FIELD_ACTIVITY);

        activityPipeline.setConsumer(new Consumer<Tuple<Long, String>>() {
            @Override
            public void push(Tuple<Long, String> longStringTuple) {
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
