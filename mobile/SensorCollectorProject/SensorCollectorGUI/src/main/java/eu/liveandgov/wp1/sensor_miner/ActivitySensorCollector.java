package eu.liveandgov.wp1.sensor_miner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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

import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.sensor_collector.MoraService;
import eu.liveandgov.wp1.sensor_collector.api.MoraAPI;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.api.RecorderConfig;
import eu.liveandgov.wp1.sensor_collector.api.ThreadedMoraAPI;
import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.util.MoraStrings;
import eu.liveandgov.wp1.sensor_miner.configuration.SensorMinerOptions;

/**
 * Basic User Interface implementing the IntentAPI
 * <p/>
 * REMARK:
 * Register intent API handlers in registerListeners() method.
 * <p/>
 * Created by hartmann on 9/26/13.
 */
public class ActivitySensorCollector extends Activity {
    // UI EXECUTION SERVICE
    private final ScheduledThreadPoolExecutor executorService;

    private final RecorderConfig recorderConfig;

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
    private ScheduledFuture<?> statusTask;


    public ActivitySensorCollector() {
        // Create the executor service, keep two threads in the pool
        executorService = new ScheduledThreadPoolExecutor(SensorMinerOptions.UI_EXECUTOR_CORE_POOL);

        // If feature is available, enable core thread timeout with five seconds
        if (Build.VERSION.SDK_INT >= 9) {
            executorService.setKeepAliveTime(SensorMinerOptions.UI_EXECUTOR_CORE_TIMEOUT, TimeUnit.MILLISECONDS);
            executorService.allowCoreThreadTimeOut(true);
        }

        recorderConfig = new RecorderConfig(ImmutableSet.of(DataCommons.TYPE_ACTIVITY), Long.MAX_VALUE, 1);
    }

    private ThreadedMoraAPI api;

    private final ServiceConnection apiConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("MORA", "Service connected");
            api = new ThreadedMoraAPI(MoraAPI.Stub.asInterface(service), executorService);
            api.registerRecorder(recorderConfig);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            api = null;
            Log.d("MORA", "Service disconnected");
        }
    };

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

        // Setup Stream Button
        streamButton = (ToggleButton) findViewById(R.id.streamButton);
        streamButton.setEnabled(true);

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

        // Prevent keyboard automatically popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (statusTask == null) {
            statusTask = executorService.scheduleAtFixedRate(statusMethod, 0L, SensorMinerOptions.REQUEST_STATUS_INTERVAL, TimeUnit.MILLISECONDS);
        }

        Log.d("MORA", "Starting and binding the mora service");
        Intent mora = new Intent(this, MoraService.class);
        startService(mora);
        bindService(mora, apiConnection, 0);
    }

    @Override
    public void onPause() {
        unbindService(apiConnection);
        Log.d("MORA", "Unbinding the mora service");

        if (statusTask != null) {
            statusTask.cancel(true);
            statusTask = null;
        }

        super.onPause();
    }

    public void onRecordingToggleButtonClick(View view) {
        if (api.isRecording()) {
            if (!api.isStreaming())
                recordingProgressBar.setVisibility(View.INVISIBLE);

            recordingToggleButton.setChecked(false);

            api.stopRecording();
        } else {
            api.startRecording();

            recordingToggleButton.setChecked(true);

            if (!api.isStreaming())
                recordingProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void onStreamButtonClick(View view) {
        if (api.isStreaming()) {
            if (!api.isRecording())
                recordingProgressBar.setVisibility(View.INVISIBLE);

            streamButton.setChecked(false);

            api.stopStreaming();
        } else {
            api.startStreaming();

            streamButton.setChecked(true);

            if (!api.isRecording())
                recordingProgressBar.setVisibility(View.VISIBLE);
        }
    }


    public void onTransferButtonClick(View view) {
        transferProgressBar.setIndeterminate(true);

        api.getTrips(new Callback<List<Trip>>() {
            @Override
            public void call(List<Trip> trips) {
                try {
                    for (Trip trip : trips)
                        api.transferTrip(trip);
                } finally {
                    transferProgressBar.setIndeterminate(false);
                }
            }
        });
    }

    public void onSendButtonClick(View view) {
        String annotation = annotationText.getText().toString();

        api.annotate(annotation);

        Toast.makeText(this, "Adding annotation: " + annotation, Toast.LENGTH_SHORT).show();
    }

    public void onIdButtonClick(View view) {
        String id = idText.getText().toString();

        MoraConfig config = api.getConfig();
        config.user = id;
        api.setConfig(config);


        Toast.makeText(this, "User name set: " + id, Toast.LENGTH_SHORT).show();
    }

    public void onDeleteButtonClick(View view) {
        api.getTrips(new Callback<List<Trip>>() {
            @Override
            public void call(List<Trip> trips) {
                for (Trip trip : trips)
                    api.deleteTrip(trip);
            }
        });
    }

    private final Runnable statusMethod = new Runnable() {
        @Override
        public void run() {
            if (api != null) {
                // Get all reports
                api.getReports(new Callback<List<Bundle>>() {
                    @Override
                    public void call(List<Bundle> bundles) {
                        StringBuilder b = new StringBuilder();
                        for (Bundle r : bundles) {
                            // Append reporter
                            if (r.containsKey(Reporter.SPECIAL_KEY_ORIGINATOR))
                                b.append(r.get(Reporter.SPECIAL_KEY_ORIGINATOR)).append("\r\n");
                            else
                                b.append("Unknown reporter\r\n");

                            // Append keys
                            for (String k : r.keySet())
                                if (!Reporter.SPECIAL_KEY_ORIGINATOR.equals(k)) {
                                    b.append(k).append(": ");
                                    MoraStrings.appendDeep(b, r.get(k));
                                    b.append("\r\n");
                                }
                            b.append("\r\n");
                        }

                        logTextView.setText(b);
                    }
                });

                List<String> activities = api.getRecorderItems(recorderConfig);

                for (String activity : activities)
                    activityView.setText(activity);
            }
        }
    };

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
