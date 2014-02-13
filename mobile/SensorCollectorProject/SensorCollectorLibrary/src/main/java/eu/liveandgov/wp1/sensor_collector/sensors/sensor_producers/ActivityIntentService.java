package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.data.impl.GoogleActivity;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.serialization.impl.ActivitySerialization;
import eu.liveandgov.wp1.serialization.impl.GoogleActivitySerialization;

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityIntentService extends IntentService {

    /**
     * Utility method for converting the activity type into a readable string
     */
    private static String getActivityNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";

            default:
                return "unknown";
        }
    }

    private final SensorQueue sensorQueue;

    public ActivityIntentService() {
        super("ActivityIntentService");
        this.sensorQueue = GlobalContext.context.sensorQueue;
    }

    public ActivityIntentService(String name) {
        super(name);
        Log.d("AIS", "Constructor");
        this.sensorQueue = GlobalContext.context.sensorQueue;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("AIS", "HandleIntent");
        if (ActivityRecognitionResult.hasResult(intent)) {

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            final String message = GoogleActivitySerialization.GOOGLE_ACTIVITY_SERIALIZATION.serialize(new GoogleActivity(
                    System.currentTimeMillis(),
                    GlobalContext.getUserId(),
                    getActivityNameFromType(result.getMostProbableActivity().getType()),
                    result.getMostProbableActivity().getConfidence()
            ));


            sensorQueue.push(message);
        }
    }
}
