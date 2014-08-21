package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * Created by cehlen on 9/26/13.
 */
public class ActivityHolder implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, SensorHolder {

    private final Logger log = LogPrincipal.get();
    public static final int DETECTION_INTERVAL_SECONDS = 20;

    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL = MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

    private PendingIntent activityRecognitionPendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;

    private boolean connected = false;
    private boolean available = false;
    private boolean startImmediate = false;

    public ActivityHolder() {
        init();
    }

    private boolean playServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(GlobalContext.context);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            log.debug("Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            log.debug("Google Play services is not available.");
            return false;
        }
    }

    private void init() {
        if (!playServicesAvailable()) return;
        available = true;
        activityRecognitionClient = new ActivityRecognitionClient(GlobalContext.context, this, this);
        Intent intent = new Intent(GlobalContext.context, ActivityIntentService.class);
        activityRecognitionPendingIntent = PendingIntent.getService(GlobalContext.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        activityRecognitionClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        connected = true;
        if (startImmediate) {
            activityRecognitionClient.requestActivityUpdates(DETECTION_INTERVAL, activityRecognitionPendingIntent);
            startImmediate = false;
        }
    }

    @Override
    public void onDisconnected() {
        connected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        connected = false;
        available = false;
    }

    @Override
    public void startRecording() {
        if (!available) {
            return;
        }
        if (!connected) {
            startImmediate = true;
        } else {
            activityRecognitionClient.requestActivityUpdates(DETECTION_INTERVAL, activityRecognitionPendingIntent);
        }
    }

    @Override
    public void stopRecording() {
        if (!available) {
            return;
        }
        startImmediate = false;
        if (activityRecognitionClient.isConnected()) {
            activityRecognitionClient.removeActivityUpdates(activityRecognitionPendingIntent);
        }
    }
}
