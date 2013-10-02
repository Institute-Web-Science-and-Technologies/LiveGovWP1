package eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import eu.liveandgov.sensorcollectorv3.Sensors.MessageQueue;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorParser;

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityIntentService extends IntentService {

    public ActivityIntentService() {
        super("ActivityIntentService");
    }

    public ActivityIntentService(String name) {
        super(name);
        Log.d("AIS", "Constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("AIS", "HandleIntent");
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            MessageQueue.push(SensorParser.parse(result));
        }
    }


}
