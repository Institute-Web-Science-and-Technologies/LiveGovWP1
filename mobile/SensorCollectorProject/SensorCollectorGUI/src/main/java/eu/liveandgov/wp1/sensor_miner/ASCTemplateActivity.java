package eu.liveandgov.wp1.sensor_miner;


import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.RETURN_GPS_SAMPLE;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.RETURN_GPS_SAMPLES;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.RETURN_LOG;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_ANNOTATE;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_GET_STATUS;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_RECORDING_ENABLE;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_SET_ID;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_START_HAR;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_STOP_HAR;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_TRANSFER_SAMPLES;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_ACTIVITY;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_ANNOTATION;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_HAR;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_ID;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_SAMPLES_STORED;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_SAMPLING;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_TRANSFERRING;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RECORDING_DISABLE;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RETURN_ACTIVITY;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RETURN_STATUS;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;

import eu.liveandgov.wp1.sensor_collector.ServiceSensorControl;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;

public class ASCTemplateActivity extends FragmentActivity{
	private static final String LOG_TAG = "ASC";
    private BroadcastReceiver universalBroadcastReceiver;
    protected Thread runStatusLoopThread;

    // FLAGS
    protected boolean isRecording = false;
    protected boolean isTransferring = false;
    protected boolean isHAR = false; 
//    private Handler handler;
    @Override
    protected void onCreate(Bundle arg0) {
    	// TODO Auto-generated method stub
    	super.onCreate(arg0);
    	getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    	setupIntentListeners();
//        runStatusLoop();
//        handler = new Handler();
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	unregisterListeners();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	requestStatus();
    	registerListeners();
    }
    protected void startRecording() {
        if (!isRecording) {
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(ACTION_RECORDING_ENABLE);
            startService(intent);
        } else { // already recording
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(RECORDING_DISABLE);
            startService(intent);
        }
    }
    
    protected void startTransfering() {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ACTION_TRANSFER_SAMPLES);
        startService(intent);
    }
    protected void sendAnnotation(String annotation) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ACTION_ANNOTATE);
        intent.putExtra(FIELD_ANNOTATION, annotation);
        startService(intent);
    }
    protected void startHAR(){
    	Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ACTION_START_HAR);
        startService(intent);
    }
    protected void stopHAR(){
    	Intent intent = new Intent(this, ServiceSensorControl.class);
    	intent.setAction(ACTION_STOP_HAR);
    	startService(intent);
    }
    protected void requestGPSSamples(){
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ExtendedIntentAPI.ACTION_GET_GPS);
        startService(intent);
    }
    protected void sendID(String id) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ACTION_SET_ID);
        intent.putExtra(FIELD_ID, id);
        startService(intent);
    }
    protected void onGpsSamplesReceived(String gpsSamples)
    {
    	
    }
    protected void onGpsSampleReceived(String gpsSample)
    {
    	
    }
    private void setupIntentListeners() {
        // Setup Broadcast Receiver
        universalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) return; // if no action is provided.

                // Log.i(LOG_TAG,"Recieved Broadcast with action " + action);

                // DISPATCHER
                if (action.equals(RETURN_STATUS)) {
                	Log.i("ASC", "status received");
                    updateStatus(intent);
                } else if (action.equals(RETURN_ACTIVITY)) {
                    updateActivity(intent.getStringExtra(FIELD_ACTIVITY));
                } else if (action.equals(RETURN_LOG)) {
                    updateLog(intent);
                } else if (action.equals(RETURN_GPS_SAMPLES)) {
                	onGpsSamplesReceived(intent.getStringExtra(ExtendedIntentAPI.FIELD_GPS_ENTRIES));
                } else if (action.equals(RETURN_GPS_SAMPLE)) {
                	onGpsSampleReceived(intent.getStringExtra(ExtendedIntentAPI.FIELD_GPS_ENTRY));
                }
            }
        };

        // Register Broadcast Listerners.
        registerListeners();
    }
    
    private void registerListeners() {
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_STATUS));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_ACTIVITY));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_LOG));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_GPS_SAMPLES));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_GPS_SAMPLE));
    }
    private void updateLog(Intent intent) {
    }
    
    protected void updateStatus(Intent intent) {
        // Update Flags
        isRecording = intent.getBooleanExtra(FIELD_SAMPLING, false );
        isTransferring = intent.getBooleanExtra(FIELD_TRANSFERRING, false );
        isHAR = intent.getBooleanExtra(FIELD_HAR, false );
    }
//    private void logStatus(Intent intent) {
//        Log.d("STATUS", "SAMPLING:       " + intent.getBooleanExtra(FIELD_SAMPLING, false));
//        Log.d("STATUS", "TRANSFERRING:   " + intent.getBooleanExtra(FIELD_TRANSFERRING, false));
//        Log.d("STATUS", "SAMPLES_STORED: " + intent.getBooleanExtra(FIELD_SAMPLES_STORED,false));
//        Log.d("STATUS", "HAR:            " + intent.getBooleanExtra(FIELD_HAR,false));
//        Log.d("STATUS", "ID:             " + intent.getStringExtra(FIELD_ID));
//    }

    // REMARK: called onPause
    private void unregisterListeners(){
        unregisterReceiver(universalBroadcastReceiver);
    }
    
    //Override this to listen for activity changes
    protected void updateActivity(String activityName) {
    }
    
    //Request Status updates
    protected void requestStatus() {
        Intent requestIntent = new Intent(this, ServiceSensorControl.class);
        Log.i("ASC", "status requested");
        requestIntent.setAction(ACTION_GET_STATUS);
        startService(requestIntent);
    }
}
