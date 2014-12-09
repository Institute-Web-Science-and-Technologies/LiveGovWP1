package eu.liveandgov.wp1.sensor_collector.components;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Velocity;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.MoraConstants;
import roboguice.service.RoboIntentService;

/**
 * <p>
 * The location source based on Google play services
 * </p>
 * <p>
 * Package private, as it is constructed by the provider
 * </p>
 * <p>
 * Created on 05.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
@Singleton
class PlayServicesLocationSource extends RegularSampleSource implements LocationSource {
    private static final Logger log = LogPrincipal.get();

    /**
     * <p>The user credentials</p>
     */
    private Credentials credentials;

    /**
     * <p>The targeted item buffer</p>
     */
    private ItemBuffer itemBuffer;

    /**
     * <p>The location client for the location request</p>
     */
    private final LocationClient locationClient;

    /**
     * <p>The actual location request</p>
     */
    private final LocationRequest locationRequest;

    private final PendingIntent pendingIntent;

    /**
     * <p>The connection status</p>
     */
    private boolean connected;

    /**
     * <p>True if after connection, the source should start</p>
     */
    private boolean startImmediate;

    PlayServicesLocationSource(Configurator configurator, Credentials credentials, ItemBuffer itemBuffer, Context context) {
        super(configurator);
        this.credentials = credentials;
        this.itemBuffer = itemBuffer;

        connected = false;
        startImmediate = false;

        locationClient = new LocationClient(context, connectionCallbacks, onConnectionFailedListener);
        locationClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create the pending intent receiving the locations
        Intent intent = new Intent(context, PlayServicesLocationSourceReceiver.class);
        pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    protected Integer getDelay(MoraConfig config) {
        return config.gps;
    }

    @Override
    protected void handleActivation() {
        // Set new parameters
        locationRequest.setInterval(getCurrentDelay());
        locationRequest.setFastestInterval(getCurrentDelay());

        // If connected, we may start immediately, else set a flag starting on successful connection
        if (connected)
            locationClient.requestLocationUpdates(locationRequest, pendingIntent);
        else
            startImmediate = true;
    }

    @Override
    protected void handleDeactivation() {
        // Clear start immediate flag
        startImmediate = false;

        // Remove listener from location client
        locationClient.removeLocationUpdates(pendingIntent);
    }

    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putBoolean("connected", connected);
        report.putBoolean("active", isActive());
        return report;
    }

    /**
     * <p>Delegate handling a change of connection status</p>
     */
    private final GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            log.info("Google play services connected");
            connected = true;

            if (!isCurrentlyEnabled())
                return;

            if (startImmediate) {
                startImmediate = false;
                locationClient.requestLocationUpdates(locationRequest, pendingIntent);
            }
        }

        @Override
        public void onDisconnected() {
            log.info("Google play services disconnected");
            connected = false;
        }
    };

    /**
     * <p>Delegate handling failures of connection</p>
     */
    private final GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener = new GooglePlayServicesClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            log.debug("Google play service connection failed, status "
                    + MoraConstants.toConnectionResultString(connectionResult.getErrorCode()));

            connected = false;
        }
    };

    /**
     * <p>
     * This class serves as a receiver for intents provided by the
     * Play location services
     * </p>
     */
    public static class PlayServicesLocationSourceReceiver extends RoboIntentService {
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

        @Inject
        Configurator configurator;

        public PlayServicesLocationSourceReceiver() {
            super("PlayServicesLocationSourceReceiver");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // Get the location
            if (intent.hasExtra(LocationClient.KEY_LOCATION_CHANGED)) {
                Location location = intent.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);

                // Always offer the GPS item
                itemBuffer.offer(new GPS(
                        System.currentTimeMillis(),
                        credentials.user,
                        location.getLatitude(),
                        location.getLongitude(),
                        location.hasAltitude() ? location.getAltitude() : null
                ));

                // Offer the velocity if configured to
                if (configurator.getConfig().velocity && location.hasSpeed())
                    itemBuffer.offer(new Velocity(System.currentTimeMillis(), credentials.user, location.getSpeed()));
            }
        }
    }
}
