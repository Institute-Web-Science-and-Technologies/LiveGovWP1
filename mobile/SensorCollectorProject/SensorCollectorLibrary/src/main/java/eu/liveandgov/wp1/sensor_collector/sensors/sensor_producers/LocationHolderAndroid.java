package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * Created by lukashaertel on 04.12.13.
 */
public class LocationHolderAndroid extends LocationHolder {
    private final Logger log = LogPrincipal.get();

    private final Looper looper;

    public LocationHolderAndroid(SensorQueue sensorQueue, Looper looper) {
        super(sensorQueue);
        this.looper = looper;
    }

    @Override
    public void startRecording() {
        checkEnableAndroidGPS();

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        c.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        c.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        c.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        String p = GlobalContext.getLocationManager().getBestProvider(c, false);

        GlobalContext.getLocationManager().requestSingleUpdate(p, locationEndpoint, looper);
        GlobalContext.getLocationManager().requestLocationUpdates(p, SensorCollectionOptions.GPS_DELAY_MS, 0, locationEndpoint, looper);
    }

    @Override
    public void stopRecording() {
        GlobalContext.getLocationManager().removeUpdates(locationEndpoint);
    }

    /**
     * This is the endpoint for updates from the location service provided by Android
     */
    private final LocationListener locationEndpoint = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            receivedNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            log.debug("Status changed: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            log.debug("Provider enabled: " + provider);

        }

        @Override
        public void onProviderDisabled(String provider) {
            log.debug("Provider disabled: " + provider);
        }
    };

    private void checkEnableAndroidGPS() {
        if (!SensorCollectionOptions.ASK_GPS) return;

        final boolean gps_enabled = GlobalContext.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gps_enabled) {
            Toast toast = Toast.makeText(GlobalContext.context, "Please enable location services.", Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GlobalContext.context.startActivity(intent);
        }

    }
}
