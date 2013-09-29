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

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;

/**
 * Basic User Interface implementing the IntentAPI
 *
 * Created by hartmann on 9/26/13.
 */
public class ActivitySensorCollector extends Activity {
    private static final String LOG_TAG = "ASC";
    private BroadcastReceiver universalBroadcastReceiver;
    private boolean isRecording = false;
    private boolean isTransferring = false;

    // UI Elements
    private ToggleButton    recordingToggleButton;
    private ProgressBar     recordingProgressBar;
    private Button          transferButton;
    private ProgressBar     transferProgressBar;
    private EditText        annotationText;
    private Button          sendButton;
    private TextView        logTextView;

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
        transferProgressBar = (ProgressBar) findViewById(R.id.transferProgressBar);
        transferProgressBar.setVisibility(View.INVISIBLE);

        // Setup Annotation Text
        annotationText = (EditText) findViewById(R.id.annotationText);
        annotationText.setEnabled(false);

        // Setup Send Button
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setEnabled(false);

        // Setup Log Text View
        logTextView = (TextView) findViewById(R.id.logTextView);
        logTextView.setMovementMethod(new ScrollingMovementMethod());

        // Prevent keyboard automatically popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setupIntentListeners();

        requestStatus();

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
        intent.putExtra("tag", "My first annotation");
        startService(intent);
    }

    /* HANDLE RETURN INTENTS */

    private void setupIntentListeners() {
        // Setup Broadcast Receiver
        universalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i(LOG_TAG,"Recieved Broadcast with action " + action);
                if (action.equals(IntentAPI.RETURN_STATUS)) {
                    updateStatus(intent);
                } else if (action.equals(IntentAPI.RETURN_LOG)) {
                    updateLog(intent);
                }
            }
        };

        registerReceiver(universalBroadcastReceiver, new IntentFilter(IntentAPI.RETURN_STATUS));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(IntentAPI.RETURN_LOG));
    }

    private void updateLog(Intent intent) {
        logTextView.append(intent.getStringExtra(IntentAPI.FIELD_LOG) + "\n");
    }

    private void updateStatus(Intent intent) {
        isRecording = intent.getBooleanExtra(IntentAPI.FIELD_SAMPLING, isRecording);
        isTransferring = intent.getBooleanExtra(IntentAPI.FIELD_TRANSFERRING, isTransferring);

        if (isRecording) {
            recordingProgressBar.setVisibility(View.VISIBLE);
        } else {
            recordingProgressBar.setVisibility(View.INVISIBLE);
        }

        if (isTransferring) {
            transferProgressBar.setVisibility(View.VISIBLE);
        } else {
            transferProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void requestStatus() {
        Intent requestIntent = new Intent(this, ServiceSensorControl.class);
        requestIntent.setAction(IntentAPI.GET_STATUS);
        startService(requestIntent);
    }
}
