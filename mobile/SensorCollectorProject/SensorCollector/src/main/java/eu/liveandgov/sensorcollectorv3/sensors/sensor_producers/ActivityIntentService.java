package eu.liveandgov.sensorcollectorv3.sensors.sensor_producers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;

import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;

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
            try {
                GlobalContext.context.sensorQueue.push(SensorSerializer.fromGoogleActivity(result));
            } catch (NullPointerException e) {
                // Context Not initialized
            }
        }
    }


}
