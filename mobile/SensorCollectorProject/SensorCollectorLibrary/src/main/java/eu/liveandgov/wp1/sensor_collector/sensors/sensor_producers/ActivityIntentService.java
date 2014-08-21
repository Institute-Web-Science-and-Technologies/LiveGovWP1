package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.data.impl.GoogleActivity;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityIntentService extends IntentService {
    private final Logger log = LogPrincipal.get();

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
        this.sensorQueue = GlobalContext.getSensorQueue();
    }

    public ActivityIntentService(String name) {
        super(name);
       log.debug( "Constructor");
        this.sensorQueue = GlobalContext.getSensorQueue();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        log.debug("HandleIntent");
        if (ActivityRecognitionResult.hasResult(intent)) {

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            sensorQueue.push(new GoogleActivity(
                    System.currentTimeMillis(),
                    GlobalContext.getUserId(),
                    getActivityNameFromType(result.getMostProbableActivity().getType()),
                    result.getMostProbableActivity().getConfidence()
            ));
        }
    }
}
