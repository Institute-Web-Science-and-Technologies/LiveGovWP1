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
public class LocationProducer implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, SensorEventListener {
    private static final String LOG_TAG = "LOCP";
    ZMQ.Socket s;
    private LocationClient locationClient;
    private LocationRequest locationRequest;
    private Context context;
    private Looper myLooper;

    public LocationProducer(Looper myLooper){
        this.myLooper = myLooper;
    }

    public void setContext(Context c) {
        context = c;
        init();
    }

    private void init() {
        locationClient = new LocationClient(this.context, this, this);
        locationClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

        // Google Play Services
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.context);
        if(ConnectionResult.SUCCESS == resultCode) {
            Log.d(LOG_TAG, "Google Play Services available.");
        } else {
            Log.d(LOG_TAG, "Google Play Services NOT available.");
        }
    }

    //@Override
    //public void onSensorChanged(SensorEvent sensorEvent) {
        // Log.i(LOG_TAG,"Recieved Sensor Sample " + SensorParser.parse(sensorEvent));
        // if (inSocket == null) setupConnection();
        // s.send(sensorParser.parse(sensorEvent));
    //}

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        locationClient.requestLocationUpdates(locationRequest, this, myLooper);
    }

    @Override
    public void onDisconnected() {
        Log.d(LOG_TAG, "onDisconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Connection failed!");
        Log.d(LOG_TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        //String msg = "Updated Location: " +
        //        Double.toString(location.getLatitude()) + ", " +
        //        Double.toString(location.getLongitude());
        String locString = SensorParser.parse(location);
        Log.d(LOG_TAG, locString);
        s.send(locString);
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
