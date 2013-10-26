package eu.liveandgov.sensorcollectorv3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import eu.liveandgov.sensorcollectorv3.configuration.ExtendedIntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.IntentAPI;

/**
 * Basic User Interface implementing the IntentAPI
 *
 * REMARK:
 * Register intent API handlers in registerListeners() method.
 *
 * Created by hartmann on 9/26/13.
 */
public class ActivitySensorCollector extends Activity {
    private static final String LOG_TAG = "ASC";
    private BroadcastReceiver universalBroadcastReceiver;

    // FLAGS
    private boolean isRecording = false;
    private boolean isTransferring = false;
    private boolean isStreaming = false;
    private boolean isHAR = false;

    // UI Elements
    private ToggleButton    recordingToggleButton;
    private ProgressBar     recordingProgressBar;
    private Button          transferButton;
    private ProgressBar     transferProgressBar;
    private EditText        annotationText;
    private Button          sendButton;
    private TextView        logTextView;
    private TextView        activityView;
    private EditText        idText;
    private Button          idButton;
    private ToggleButton    streamButton;



    /* ANDROID LIFECYCLE MANAGEMENT */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_collector);

        // Setup Recording Toggle Button
        recordingToggleButton = (ToggleButton) findViewById(R.id.recordingToggleButton);
        recordingToggleButton.setEnabled(true);

        // Setup Recording Progress Bar
        recordingProgressBar = (ProgressBar) findViewById(R.id.recordingProgressBar);
        recordingProgressBar.setVisibility(View.INVISIBLE);

        // Setup Transfer Button
        transferButton = (Button) findViewById(R.id.transferButton);
        transferButton.setEnabled(true);

        // Setup Transfer Progress Bar
        transferProgressBar = (ProgressBar) findViewById(R.id.transferProgress);
        // transferProgressBar.setVisibility(View.INVISIBLE);

        // Setup Annotation Text
        annotationText = (EditText) findViewById(R.id.annotationText);
        annotationText.setEnabled(true);

        // Setup Send Button
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setEnabled(true);

        // Setup Log Text View
        logTextView = (TextView) findViewById(R.id.logTextView);
        logTextView.setMovementMethod(new ScrollingMovementMethod());

        // Activity Text
        activityView = (TextView) findViewById(R.id.ActivityText);

        // Setup ID Text
        idText = (EditText) findViewById(R.id.idText);
        idText.setEnabled(true);

        // Setup ID Button
        idButton = (Button) findViewById(R.id.idButton);
        idButton.setEnabled(true);

        streamButton = (ToggleButton) findViewById(R.id.streamButton);
        streamButton.setEnabled(true);

        // Prevent keyboard automatically popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setupIntentListeners();

        runStatusLoop();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterListeners();
    }

    @Override
    public void onResume(){
        super.onResume();
        registerListeners();
    }

    /* BUTTON HANDLER */

    public void onRecordingToggleButtonClick(View view) {
        if (!isRecording) {
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(IntentAPI.RECORDING_ENABLE);
            startService(intent);
        } else { // already recording
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(IntentAPI.RECORDING_DISABLE);
            startService(intent);
        }
    }

    public void onTransferButtonClick(View view) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(IntentAPI.TRANSFER_SAMPLES);
        startService(intent);
    }

    public void onSendButtonClick(View view) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(IntentAPI.ANNOTATE);
        intent.putExtra(IntentAPI.FIELD_ANNOTATION, annotationText.getText().toString());
        startService(intent);
    }


    public void onIdButtonClick(View view) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(IntentAPI.SET_USER_ID);
        intent.putExtra(IntentAPI.FIELD_ID, idText.getText().toString());
        startService(intent);
    }

    public void onStreamButtonClick(View view){
        if (!isStreaming) {
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(ExtendedIntentAPI.START_STREAMING);
            startService(intent);
        } else { // already recording
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(ExtendedIntentAPI.STOP_STREAMING);
            startService(intent);
        }
    }

    public void onHarButtonClick(View view){
        if (!isHAR) {
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(IntentAPI.START_HAR);
            startService(intent);
        } else { // already recording
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(IntentAPI.STOP_HAR);
            startService(intent);
        }
    }

    /* HANDLE RETURN INTENTS */

    private void setupIntentListeners() {
        // Setup Broadcast Receiver
        universalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // Log.i(LOG_TAG,"Recieved Broadcast with action " + action);
                if (action.equals(IntentAPI.RETURN_STATUS)) {
                    updateStatus(intent);
                } else if (action.equals(IntentAPI.RETURN_LOG)) {
                    updateLog(intent);
                } else if (action.equals(IntentAPI.RETURN_ACTIVITY)) {
                    updateActivity(intent.getStringExtra(IntentAPI.FIELD_ACTIVITY));
                }
            }
        };

        registerListeners();
    }

    private void registerListeners(){
        registerReceiver(universalBroadcastReceiver, new IntentFilter(IntentAPI.RETURN_STATUS));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(IntentAPI.RETURN_LOG));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(IntentAPI.RETURN_ACTIVITY));
    }

    private void unregisterListeners(){
        unregisterReceiver(universalBroadcastReceiver);
    }

    private void updateLog(Intent intent) {
        logTextView.setText(intent.getStringExtra(IntentAPI.FIELD_LOG) + "\n");

        // scroll to end
//        logTextView.setSelected(true);
//        Spannable textDisplayed = (Spannable) logTextView.getText();
//        Selection.setSelection(textDisplayed, textDisplayed.length());
    }

    private void updateActivity(String activityName) {
        activityView.setText(activityName);
    }


    private void updateStatus(Intent intent) {
        isRecording = intent.getBooleanExtra(IntentAPI.FIELD_SAMPLING, isRecording /* = default value */ );
        isTransferring = intent.getBooleanExtra(IntentAPI.FIELD_TRANSFERRING, isTransferring );
        isStreaming = intent.getBooleanExtra(ExtendedIntentAPI.FIELD_STREAMING, isStreaming );
        isHAR = intent.getBooleanExtra(IntentAPI.FIELD_HAR, isHAR);

        if (isRecording) {
            recordingProgressBar.setVisibility(View.VISIBLE);
            recordingToggleButton.setChecked(true);
        } else {
            recordingProgressBar.setVisibility(View.INVISIBLE);
            recordingToggleButton.setChecked(false);
        }

        if (isTransferring) {
            // transferProgressBar.setVisibility(View.VISIBLE);
            transferProgressBar.setIndeterminate(true);
        } else {
            // transferProgressBar.setVisibility(View.INVISIBLE);
            transferProgressBar.setIndeterminate(false);
        }

        if (isStreaming) {
            streamButton.setChecked(true);
        } else {
            streamButton.setChecked(false);
        }

    }

    private void requestStatus() {
        Intent requestIntent = new Intent(this, ServiceSensorControl.class);
        requestIntent.setAction(IntentAPI.GET_STATUS);
        startService(requestIntent);
    }

    private void runStatusLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    requestStatus();

                    // wait 1 sec.
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
