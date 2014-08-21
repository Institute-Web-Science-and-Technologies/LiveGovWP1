package eu.liveandgov.wp1.sensor_miner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.sensor_collector.ServiceSensorControl;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_miner.configuration.SensorMinerOptions;

import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.FIELD_MESSAGE;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.FIELD_STREAMING;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.RETURN_LOG;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.START_STREAMING;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.STOP_STREAMING;
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
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_STATUS_ID;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_SAMPLING;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_TRANSFERRING;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RECORDING_DISABLE;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RETURN_ACTIVITY;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RETURN_STATUS;

/**
 * Basic User Interface implementing the IntentAPI
 * <p/>
 * REMARK:
 * Register intent API handlers in registerListeners() method.
 * <p/>
 * Created by hartmann on 9/26/13.
 */
public class ActivitySensorCollector extends Activity {
    private BroadcastReceiver universalBroadcastReceiver;

    // MIRRORED FLAGS
    private boolean isRecording = false;
    private boolean isTransferring = false;
    private boolean isStreaming = false;
    private boolean isHAR = false;

    // FLAGS
    public boolean isForeground = false;

    // UI EXECUTION SERVICE
    public final ScheduledThreadPoolExecutor executorService;

    // UI Elements
    private ToggleButton recordingToggleButton;
    private ProgressBar recordingProgressBar;
    private Button transferButton;
    private ProgressBar transferProgressBar;
    private EditText annotationText;
    private Button sendButton;
    private TextView logTextView;
    private TextView activityView;
    private EditText idText;
    private Button idButton;
    private ToggleButton streamButton;
    private ToggleButton harButton;
    private ScheduledFuture<?> statusTask;

    public ActivitySensorCollector() {
        // Create the executor service, keep two threads in the pool
        executorService = new ScheduledThreadPoolExecutor(SensorMinerOptions.UI_EXECUTOR_CORE_POOL);

        // If feature is available, enable core thread timeout with five seconds
        if (Build.VERSION.SDK_INT >= 9) {
            executorService.setKeepAliveTime(SensorMinerOptions.UI_EXECUTOR_CORE_TIMEOUT, TimeUnit.MILLISECONDS);
            executorService.allowCoreThreadTimeOut(true);
        }
    }

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
        transferProgressBar.setIndeterminate(false);

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

        // Setup Stream Button
        streamButton = (ToggleButton) findViewById(R.id.streamButton);
        streamButton.setEnabled(true);

        // Setup harButton
        harButton = (ToggleButton) findViewById(R.id.harButton);
        harButton.setEnabled(true);

        // Prevent keyboard automatically popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setupIntentListeners();
    }


    @Override
    public void onResume() {
        super.onResume();
        registerListeners();
        isForeground = true;

        if (statusTask == null) {
            statusTask = executorService.scheduleAtFixedRate(statusMethod, 0L, SensorMinerOptions.REQUEST_STATUS_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onPause() {
        if (statusTask != null) {
            statusTask.cancel(true);
            statusTask = null;
        }

        isForeground = false;
        unregisterListeners();
        super.onPause();
    }

    /* BUTTON HANDLER */
    public void onRecordingToggleButtonClick(View view) {
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

    public void onTransferButtonClick(View view) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ACTION_TRANSFER_SAMPLES);
        startService(intent);
    }

    public void onSendButtonClick(View view) {
        String annotation = annotationText.getText().toString();

        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ACTION_ANNOTATE);
        intent.putExtra(FIELD_ANNOTATION, annotation);
        startService(intent);
        Toast.makeText(this, "Adding annotation: " + annotation, Toast.LENGTH_SHORT).show();
    }

    public void onIdButtonClick(View view) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ACTION_SET_ID);
        intent.putExtra(FIELD_STATUS_ID, idText.getText().toString());
        startService(intent);
    }

    public void onStreamButtonClick(View view) {
        if (!isStreaming) {
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(START_STREAMING);
            startService(intent);
        } else { // already recording
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(STOP_STREAMING);
            startService(intent);
        }
    }

    public void onHarButtonClick(View view) {
        if (!isHAR) {
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(ACTION_START_HAR);
            startService(intent);
        } else { // already recording
            Intent intent = new Intent(this, ServiceSensorControl.class);
            intent.setAction(ACTION_STOP_HAR);
            startService(intent);
        }
    }

    public void onGpsButtonClick(View view) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ExtendedIntentAPI.ACTION_GET_GPS);
        startService(intent);
    }

    public void onDeleteButtonClick(View view) {
        Intent intent = new Intent(this, ServiceSensorControl.class);
        intent.setAction(ExtendedIntentAPI.ACTION_DELETE_SAMPLES);
        startService(intent);
    }

    /* HANDLE RETURN INTENTS */

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
                    updateStatus(intent);
                } else if (action.equals(RETURN_ACTIVITY)) {
                    updateActivity(intent.getStringExtra(FIELD_ACTIVITY));
                } else if (action.equals(RETURN_LOG)) {
                    updateLog(intent);
                }
            }
        };

        // Register Broadcast Listerners.
        registerListeners();
    }


    // REMARK: called onResume
    private void registerListeners() {
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_STATUS));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_ACTIVITY));
        registerReceiver(universalBroadcastReceiver, new IntentFilter(RETURN_LOG));
    }

    // REMARK: called onPause
    private void unregisterListeners() {
        unregisterReceiver(universalBroadcastReceiver);
    }

    private void updateLog(Intent intent) {
        logTextView.setText(intent.getStringExtra(FIELD_MESSAGE) + "\n");

        // scroll to end
//        logTextView.setSelected(true);
//        Spannable textDisplayed = (Spannable) logTextView.getText();
//        Selection.setSelection(textDisplayed, textDisplayed.length());
    }

    private void updateActivity(String activityName) {
        activityView.setText(activityName);
    }

    private final Runnable statusMethod = new Runnable() {
        @Override
        public void run() {
            requestStatus();
        }
    };


    private void requestStatus() {
        Intent requestIntent = new Intent(this, ServiceSensorControl.class);
        requestIntent.setAction(ACTION_GET_STATUS);
        startService(requestIntent);
    }

    private void updateStatus(Intent intent) {
        // Update Flags
        isRecording = intent.getBooleanExtra(FIELD_SAMPLING, false);
        isTransferring = intent.getBooleanExtra(FIELD_TRANSFERRING, false);
        isStreaming = intent.getBooleanExtra(FIELD_STREAMING, false);
        isHAR = intent.getBooleanExtra(FIELD_HAR, false);

        // Update Buttons
        if (isRecording) {
            recordingProgressBar.setVisibility(View.VISIBLE);
            recordingToggleButton.setChecked(true);
        } else {
            recordingProgressBar.setVisibility(View.INVISIBLE);
            recordingToggleButton.setChecked(false);
        }

        if (isTransferring) {
            transferProgressBar.setIndeterminate(true);
        } else {
            transferProgressBar.setIndeterminate(false);
        }

        if (isStreaming) {
            streamButton.setChecked(true);
        } else {
            streamButton.setChecked(false);
        }

        if (isHAR) {
            harButton.setChecked(true);
        } else {
            harButton.setChecked(false);
        }

        // logStatus(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_sensor_collector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ActivitySettings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
