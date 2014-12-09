package eu.liveandgov.wp1.sensor_collector.components;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.data.impl.GoogleActivity;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.MoraConstants;
import roboguice.service.RoboIntentService;

/**
 * <p>Sample source for Google activity recognition</p>
 * Created by lukashaertel on 05.12.2014.
 */
@Singleton
public class GActSource extends RegularSampleSource {
    private static final Logger log = LogPrincipal.get();

    /**
     * <p>The service context</p>
     */
    private Context context;

    private final ActivityRecognitionClient activityRecognitionClient;

    private final PendingIntent pendingIntent;

    private boolean available;

    private boolean connected;

    private boolean startImmediate;

    /**
     * <p>Constructs the Google Activity Recognition source</p>
     *
     * @param context The context, injected by guice
     */
    @Inject
    public GActSource(Configurator configurator, Context context) {
        super(configurator);

        this.context = context;

        // Check if play services are available and initialize fields
        available = isPlayServicesAvailable();
        connected = false;
        startImmediate = false;

        // TODO: Use dummy fallback provider like fallback location source provider instead of carrying availability
        if (available) {
            // Make the activity recognition client with the callbacks in this class
            activityRecognitionClient = new ActivityRecognitionClient(context, connectionCallbacks, onConnectionFailedListener);

            // Create the pending intent receiving the activities
            Intent intent = new Intent(context, GActSourceReceiver.class);
            pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Connect the client
            activityRecognitionClient.connect();
        } else {
            activityRecognitionClient = null;
            pendingIntent = null;
        }
    }

    /**
     * <p>Checks if the play services are available in the context</p>
     *
     * @return Returns true if the play services are available
     */
    private boolean isPlayServicesAvailable() {
        // Get availability
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        // If Google Play services is not available, log a detailed warning
        if (code != ConnectionResult.SUCCESS) {
            // In debug mode, log the status
            log.warn("Google Play services is not available, status: "
                    + MoraConstants.toConnectionResultString(code));
            return false;
        }

        return false;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    protected Integer getDelay(MoraConfig config) {
        return config.googleActivity;
    }

    @Override
    protected void handleActivation() {
        // If connected, we may start immediately, else set a flag starting on successful connection
        if (connected)
            activityRecognitionClient.requestActivityUpdates(getCurrentDelay(), pendingIntent);
        else
            startImmediate = true;
    }

    @Override
    protected void handleDeactivation() {
        // Clear start immediate flag
        startImmediate = false;

        // If connected, remove updates
        if (activityRecognitionClient.isConnected())
            activityRecognitionClient.removeActivityUpdates(pendingIntent);
    }

    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putBoolean("connected", connected);
        return report;
    }

    /**
     * <p>Delegate handling a change of connection status</p>
     */
    private final ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            log.info("Google activity recognition services connected");
            connected = true;

            if (!isCurrentlyEnabled())
                return;

            if (startImmediate) {
                startImmediate = false;
                activityRecognitionClient.requestActivityUpdates(getCurrentDelay(), pendingIntent);
            }
        }

        @Override
        public void onDisconnected() {
            log.info("Google activity recognition services disconnected");
            connected = false;
        }
    };

    /**
     * <p>Delegate handling failures of connection</p>
     */
    private final OnConnectionFailedListener onConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

            log.debug("Google activity recognition service connection failed, status "
                    + MoraConstants.toConnectionResultString(connectionResult.getErrorCode()));

            connected = false;
            available = false;
        }
    };

    /**
     * <p>
     * This class serves as a receiver for intents provided by the
     * Google Activity Recognition
     * </p>
     */
    public static class GActSourceReceiver extends RoboIntentService {
        private static final Logger log = LogPrincipal.get();

        /**
         * Central credentials store
         */
        @Inject
        Credentials credentials;

        /**
         * Central item buffer
         */
        @Inject
        ItemBuffer itemBuffer;

        public GActSourceReceiver() {
            super("GActSourceReceiver");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // If intent has an activity recognition result, offer a converted version to the item
            // buffer
            if (ActivityRecognitionResult.hasResult(intent)) {
                log.info("Received an intent for the Google Activity Recognition");

                // Extract info
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

                // Add to item buffer
                itemBuffer.offer(new GoogleActivity(
                        System.currentTimeMillis(),
                        credentials.user,
                        MoraConstants.toDetectedActivityString(result.getMostProbableActivity().getType()),
                        result.getMostProbableActivity().getConfidence()
                ));
            }
        }
    }
}
