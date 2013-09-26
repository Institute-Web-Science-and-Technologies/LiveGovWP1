package eu.liveandgov.sensorcollectorv3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;

/**
 * Basic User Interface implementing the IntentAPI
 *
 * Created by hartmann on 9/26/13.
 */
public class ActivitySensorCollector extends Activity {
    private BroadcastReceiver universalBroadcastReceiver;
    private boolean isRecording = false;
    private boolean isTransferring = false;

    /* ANDROID LIFECYCLE MANAGEMENT */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_collector);
        setupIntentListeners();
    }


    /* BUTTON HANDLER */

    public void onRecordingToggleButtonClick(View view) {
        if (!isRecording) {
            Intent intent = new Intent(IntentAPI.SAMPLING_ENABLE);
            startService(intent);
        } else { // already recording
            Intent intent = new Intent(IntentAPI.SAMPLING_DISABLE);
            startService(intent);
        }
    }

    public void onTransferButtonClick(View view) {
        Intent intent = new Intent(IntentAPI.TRANSFER_SAMPLES);
        getApplicationContext().startService(intent);
    }

    public void onSendButtonClick(View view) {
        Intent intent = new Intent(IntentAPI.ANNOTATE);
        intent.putExtra("tag", "My first annotation");
        getApplicationContext().startService(intent);
    }

    /* HANDLE RETURN INTENTS */

    private void setupIntentListeners() {
        // Setup Broadcast Receiver
        universalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
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
    }

    private void updateStatus(Intent intent) {

    }

}
