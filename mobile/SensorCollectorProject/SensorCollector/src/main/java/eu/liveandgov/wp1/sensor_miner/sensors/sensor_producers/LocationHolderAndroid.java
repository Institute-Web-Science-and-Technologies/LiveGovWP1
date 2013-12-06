package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by lukashaertel on 04.12.13.
 */
public class LocationHolderAndroid extends LocationHolder
{
    private final Looper looper;

    public LocationHolderAndroid(SensorQueue sensorQueue, Looper looper)
    {
        super(sensorQueue);
        this.looper = looper;
    }

    @Override
    public void startRecording() {
        checkEnableGPS();

        GlobalContext.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, SensorCollectionOptions.GPS_DELAY_MS, 0, locationEndpoint, looper);
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

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
