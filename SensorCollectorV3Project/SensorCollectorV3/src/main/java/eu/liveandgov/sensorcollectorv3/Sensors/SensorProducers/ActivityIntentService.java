package eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityIntentService extends IntentService {

    public ActivityIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("AIS", "HandleIntent");
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActiviy = result.getMostProbableActivity();
            int confidence = mostProbableActiviy.getConfidence();
            int activityType = mostProbableActiviy.getType();
            Log.i("ACTIVITY", "Activity : " + activityType + " Confidence: " + confidence);
        }
    }

}
