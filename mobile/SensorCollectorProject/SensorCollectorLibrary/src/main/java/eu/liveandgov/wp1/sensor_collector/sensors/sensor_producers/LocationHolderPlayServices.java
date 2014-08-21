package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

import static junit.framework.Assert.assertNotNull;

/**
 * GUIDE - If com.google.* imports do not resolve:
 * Need to install GooglePlayServices and GoogleAPI from the Android SDK Manager
 * Moreover you need to add the GoolgePlayServicesLib.jar file from the
 * SDK directory manually to the modules directory:
 * Right Click on Project -> Open Module Settings (F4) -> Edit SDK -> Add path to jar file
 * <p/>
 * Created by hartmann on 9/15/13.
 */
public class LocationHolderPlayServices extends LocationHolder implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
    private final Logger log = LogPrincipal.get();
    private LocationClient locationClient;
    private LocationRequest locationRequest;
    private Looper myLooper;
    private boolean available = false;
    private boolean connected = false;
    private boolean startImmediate = false;

    public LocationHolderPlayServices(SensorQueue sensorQueue, Looper myLooper) {
        super(sensorQueue);

        this.myLooper = myLooper;
        init();
    }

    private void init() {
        locationClient = new LocationClient(GlobalContext.context, this, this);
        locationClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(SensorCollectionOptions.GPS_DELAY_MS);
        locationRequest.setFastestInterval(SensorCollectionOptions.GPS_DELAY_MS);

        // Google Play Services
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(GlobalContext.context);
        if (ConnectionResult.SUCCESS == resultCode) {
            available = true;
            log.debug("Google Play Services available.");
        } else {
            available = false;
            log.debug("Google Play Services NOT available.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        connected = true;
        if (startImmediate) {
            locationClient.requestLocationUpdates(locationRequest, this, myLooper);
        }
    }

    @Override
    public void onDisconnected() {
        log.debug("onDisconnected");
        connected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        available = false;
        connected = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO: Add speed and bearing (Ausrichtung) to SSF if they are available.
        receivedNewLocation(location);
    }

    @Override
    public void startRecording() {
        checkEnablePlayServiceGPS();

        if (!available) {
            return;
        }
        if (connected) {
            locationClient.requestLocationUpdates(locationRequest, this, myLooper);
        } else {
            startImmediate = true;
        }
    }

    @Override
    public void stopRecording() {
        if (!available) {
            return;
        }
        locationClient.removeLocationUpdates(this);
        startImmediate = false;
    }

    private void checkEnablePlayServiceGPS() {
        if (!SensorCollectionOptions.ASK_GPS) return;

        String provider = Settings.Secure.getString(GlobalContext.context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        assertNotNull(provider);

        if (provider.equals("")) {
            Toast toast = Toast.makeText(GlobalContext.context, "Please enable location services.", Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GlobalContext.context.startActivity(intent);
        }

    }
}
