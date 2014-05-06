package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityIntentService extends IntentService {

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
        if(ActivityRecognitionResult.hasResult(intent)) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        sensorQueue.push(SensorSerializer.activityRecognitionResult.toSSFDefault(result));
    }
}


}
