package eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.jeromq.ZMQ;

import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.SensorQueue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorParser;

/**
 *
 * GUIDE - If com.google.* imports do not resolve:
 * Need to install GooglePlayServices and GoogleAPI from the Android SDK Manager
 * Moreover you need to add the GoolgePlayServicesLib.jar file from the
 * SDK directory manually to the modules directory:
 * Right Click on Project -> Open Module Settings (F4) -> Edit SDK -> Add path to jar file
 *
 * Created by hartmann on 9/15/13.
 */
public class LocationHolder implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, SensorHolder {
    /*
    * Change these if you want to change the interval
    */
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    private static final int MILISECONDS_PER_SECOND = 1000;
    private static final long UPDATE_INTERVAL = UPDATE_INTERVAL_IN_SECONDS * MILISECONDS_PER_SECOND;
    private static final long FASTETST_INTERVAL = FASTEST_INTERVAL_IN_SECONDS * MILISECONDS_PER_SECOND;

    private static final String LOG_TAG = "LOCP";
    private LocationClient locationClient;
    private LocationRequest locationRequest;
    private Looper myLooper;
    private boolean available = false;
    private boolean connected = false;
    private boolean startImmediate = false;

    private final SensorQueue sensorQueue;

    public LocationHolder(SensorQueue sensorQueue, Looper myLooper){
        this.myLooper = myLooper;
        this.sensorQueue = sensorQueue;
        init();
    }

    private void init() {
        locationClient = new LocationClient(GlobalContext.context, this, this);
        locationClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTETST_INTERVAL);

        // Google Play Services
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(GlobalContext.context);
        if(ConnectionResult.SUCCESS == resultCode) {
            available = true;
            Log.d(LOG_TAG, "Google Play Services available.");
        } else {
            available = false;
            Log.d(LOG_TAG, "Google Play Services NOT available.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        connected = true;
        if(startImmediate) {
            locationClient.requestLocationUpdates(locationRequest, this, myLooper);
        }
    }

    @Override
    public void onDisconnected() {
        Log.d(LOG_TAG, "onDisconnected");
        connected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        available = false;
        connected = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        String locString = SensorParser.parse(location);
        Log.d(LOG_TAG, locString);
        sensorQueue.push(locString);
    }

    @Override
    public void startRecording() {
        if(!available) {
            return;
        }
        if(connected) {
            locationClient.requestLocationUpdates(locationRequest, this, myLooper);
        } else {
            startImmediate = true;
        }
    }

    @Override
    public void stopRecording() {
        if(!available) {
            return;
        }
        locationClient.removeLocationUpdates(this);
        startImmediate = false;
    }
}
