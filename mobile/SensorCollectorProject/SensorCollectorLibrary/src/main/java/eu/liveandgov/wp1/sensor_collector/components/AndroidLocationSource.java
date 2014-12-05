package eu.liveandgov.wp1.sensor_collector.components;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.util.HashMap;

import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.GoogleActivity;
import eu.liveandgov.wp1.data.impl.Velocity;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.MoraConstants;
import roboguice.service.RoboIntentService;

/**
 * <p>
 * The location source based on basic Android
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

class AndroidLocationSource extends RegularSampleSource implements LocationSource {
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
     * <p>The location manager for the location requests</p>
     */
    private LocationManager locationManager;

    private final PendingIntent pendingIntent;

    /**
     * <p>Hash map mapping from provider to its status</p>
     */
    private final HashMap<String, Integer> providersAndStatus;

    /**
     * <p>Constructs the regular location source on a configurator</p>
     *
     * @param configurator    The configurator to use
     * @param locationManager The location manager to use
     */
    AndroidLocationSource(Configurator configurator, Credentials credentials, ItemBuffer itemBuffer, LocationManager locationManager, Context context) {
        super(configurator);

        this.credentials = credentials;
        this.itemBuffer = itemBuffer;
        this.locationManager = locationManager;

        // Create the pending intent receiving the locations
        Intent intent = new Intent(context, AndroidLocationSourceReceiver.class);
        pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        providersAndStatus = Maps.newHashMap();
    }

    @Override
    protected Integer getDelay(MoraConfig config) {
        return config.gps;
    }

    @Override
    protected void handleActivation() {
        // Build the criteria
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        c.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        c.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        c.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        // Find the best provider
        String bestProvider = locationManager.getBestProvider(c, false);


        // Register listener for this provider
        locationManager.requestSingleUpdate(bestProvider, pendingIntent);
        locationManager.requestLocationUpdates(bestProvider, getCurrentDelay(), 0, pendingIntent);
    }

    @Override
    protected void handleDeactivation() {
        locationManager.removeUpdates(locationEndpoint);
    }

    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putSerializable("providersAndStatus", providersAndStatus);
        return report;
    }

    protected final LocationListener locationEndpoint = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Always offer the GPS item
            itemBuffer.offer(new GPS(
                    System.currentTimeMillis(),
                    credentials.user,
                    location.getLatitude(),
                    location.getLongitude(),
                    location.hasAltitude() ? location.getAltitude() : null
            ));

            // Offer the velocity if configured to
            if (getConfigurator().getConfig().velocity && location.hasSpeed())
                itemBuffer.offer(new Velocity(System.currentTimeMillis(), credentials.user, location.getSpeed()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            log.info("Location provider status for " + provider + " changed to "
                    + MoraConstants.toLocationListenerStatusString(status));

            providersAndStatus.put(provider, status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            log.info("Location provider added " + provider);

            providersAndStatus.put(provider, null);
        }

        @Override
        public void onProviderDisabled(String provider) {
            log.info("Location provider removed " + provider);

            providersAndStatus.remove(provider);
        }
    };

    /**
     * <p>
     * This class serves as a receiver for intents provided by the
     * Android location services
     * </p>
     */
    public static class AndroidLocationSourceReceiver extends RoboIntentService {
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

        public AndroidLocationSourceReceiver() {
            super("AndroidLocationSourceReceiver");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
                Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

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