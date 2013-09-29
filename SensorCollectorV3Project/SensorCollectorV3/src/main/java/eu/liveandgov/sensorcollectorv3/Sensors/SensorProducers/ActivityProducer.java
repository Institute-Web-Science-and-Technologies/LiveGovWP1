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

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityProducer
        implements
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, SensorEventListener {

    public static final String LOG_TAG = "AP";
    private Context context;
    private PendingIntent activityRecognitionPendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;


    public ActivityProducer(Integer PORT) {
    }

    private boolean playServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
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
        activityRecognitionClient = new ActivityRecognitionClient(context, this, this);
        Intent intent = new Intent(context, ActivityIntentService.class);
        activityRecognitionPendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        activityRecognitionClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {
        activityRecognitionClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setContext(Context context) {
        this.context = context;
        init();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // DUMMY
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // DUMMY
    }
}
