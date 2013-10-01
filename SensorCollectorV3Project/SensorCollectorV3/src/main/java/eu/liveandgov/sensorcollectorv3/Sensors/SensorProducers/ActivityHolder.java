package eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

import eu.liveandgov.sensorcollectorv3.Sensors.GlobalContext;

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityHolder
        implements
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, SensorHolder {

    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL_SECONDS = 20;
    public static final int DETECTION_INTERVAL = MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

    public static final String LOG_TAG = "ACTH";
    private PendingIntent activityRecognitionPendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;

    private boolean connected = false;
    private boolean startImmediate = false;


    public ActivityHolder() {
        init();
    }

    private boolean playServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(GlobalContext.context);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LOG_TAG, "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            Log.d(LOG_TAG, "Google Play services is not available.");
            return false;
        }
    }

    private void init() {
        if(!playServicesAvailable())
            return;

        activityRecognitionClient = new ActivityRecognitionClient(GlobalContext.context, this, this);
        Intent intent = new Intent(GlobalContext.context, ActivityIntentService.class);
        activityRecognitionPendingIntent = PendingIntent.getService(GlobalContext.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        activityRecognitionClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        connected = true;
        if(startImmediate) {
            Log.i(LOG_TAG, "Start recording activities");
            activityRecognitionClient.requestActivityUpdates(DETECTION_INTERVAL, activityRecognitionPendingIntent);
            startImmediate = false;
        }
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        connected = false;
    }

    @Override
    public void startRecording() {
        if(!connected) {
            startImmediate = true;
        } else {
            Log.i(LOG_TAG, "Start recording activities 2");
            activityRecognitionClient.requestActivityUpdates(DETECTION_INTERVAL, activityRecognitionPendingIntent);
        }
    }

    @Override
    public void stopRecording() {
        startImmediate = false;
        if(activityRecognitionClient.isConnected()) {
            activityRecognitionClient.removeActivityUpdates(activityRecognitionPendingIntent);
        }
    }
}
