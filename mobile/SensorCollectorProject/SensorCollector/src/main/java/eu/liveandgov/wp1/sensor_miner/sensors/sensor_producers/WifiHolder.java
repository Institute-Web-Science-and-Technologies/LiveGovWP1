package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_miner.sensors.SensorSerializer;
import eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers.SensorHolder;

/**
 * Created by lukashaertel on 27.11.13.
 */
public class WifiHolder implements SensorHolder {
    public static String LOG_TAG = "WIFIH";

    private final SensorQueue sensorQueue;
    private final int     delay;
    private final Handler handler;
    private long lastScanRequest;

    public WifiHolder(SensorQueue sensorQueue,int delay, Handler handler)
    {
        this.sensorQueue = sensorQueue;
        this.delay = delay;
        this.handler = handler;
    }

    private void startNextScan() {
        if(GlobalContext.getWifiManager().startScan())
        {
            lastScanRequest = System.currentTimeMillis();
        }
    }

    @Override
    public void startRecording() {
        GlobalContext.context.registerReceiver(scanResultsAvailableEndpoint, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);

        startNextScan();
    }

    @Override
    public void stopRecording() {
        try {
            GlobalContext.context.unregisterReceiver(scanResultsAvailableEndpoint);
        } catch (IllegalArgumentException e) {
            Log.w(LOG_TAG, "Receiver already unregistered");
        }
    }

    private final BroadcastReceiver scanResultsAvailableEndpoint = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction()))
            {
                // Get receive-time of the intent
                long timestamp_ms = System.currentTimeMillis();

                String generated = SensorSerializer.fromScanResults(timestamp_ms, GlobalContext.getWifiManager().getScanResults());

                Log.d(LOG_TAG, generated);

                // Push converted scan results to queue
                sensorQueue.push(generated);

                // If results are on time, schedule the next scan at the handler with the given delay
                if(lastScanRequest + delay > timestamp_ms)
                {
                    handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            startNextScan();
                        }
                    }, lastScanRequest + delay);
                }
                else
                {
                    // Else, scan immediately
                    startNextScan();
                }
            }
            else
            {
                throw new IllegalStateException("Illegal configuration of broadcast receiver");
            }
        }
    };

}