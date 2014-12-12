package eu.liveandgov.wp1.sensor_miner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.inject.Inject;

import eu.liveandgov.wp1.sensor_collector.MoraService;
import eu.liveandgov.wp1.sensor_collector.api.MoraAPIHullConnection;
import eu.liveandgov.wp1.sensor_collector.api.MoraIntents;
import roboguice.activity.RoboActivity;

/**
 * <p>
 * Base activity connecting to the api and listening for status updates
 * </p>
 * <p>
 * Created on 12.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
public abstract class BaseMoraActivity extends RoboActivity {
    @Inject
    MoraAPIHullConnection api;

    BroadcastReceiver statusUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (api.getImplementation() != null)
                updateStatus();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        Log.d("MORA", "Starting and binding the mora service");
        Intent mora = new Intent(this, MoraService.class);
        startService(mora);
        bindService(mora, api, 0);

        // Start listening for status updates
        registerReceiver(statusUpdatedReceiver, new IntentFilter(MoraIntents.STATUS_UPDATED));
    }

    @Override
    public void onPause() {
        // Stop listening for status updates
        unregisterReceiver(statusUpdatedReceiver);

        unbindService(api);
        Log.d("MORA", "Unbinding the mora service");


        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent keyboard automatically popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected abstract void updateStatus();
}
