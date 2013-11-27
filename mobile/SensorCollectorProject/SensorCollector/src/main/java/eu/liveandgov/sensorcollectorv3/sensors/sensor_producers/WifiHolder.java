package eu.liveandgov.sensorcollectorv3.sensors.sensor_producers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 27.11.13.
 */
public class WifiHolder implements SensorHolder {
    private final BroadcastReceiver scanResultsAvailableEndpoint = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction()))
            {
                // Get receive-time of the intent
                long timestamp_ms = System.currentTimeMillis();

                String generated = SensorSerializer.fromScanResults(timestamp_ms, GlobalContext.getWifiManager().getScanResults());

                Log.d("WIFI HOLDER", generated);

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
        GlobalContext.context.unregisterReceiver(scanResultsAvailableEndpoint);
    }
}
