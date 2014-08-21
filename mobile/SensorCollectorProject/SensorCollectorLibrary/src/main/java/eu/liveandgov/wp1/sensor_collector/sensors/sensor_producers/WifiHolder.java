package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.Toast;

import org.apache.log4j.Logger;

import java.util.List;

import eu.liveandgov.wp1.data.impl.WiFi;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * Created by lukashaertel on 27.11.13.
 */
public class WifiHolder implements SensorHolder {
    private final Logger log = LogPrincipal.get();

    private final SensorQueue sensorQueue;
    private final int delay;
    private final Handler handler;
    private long lastScanRequest;

    public WifiHolder(SensorQueue sensorQueue, int delay, Handler handler) {
        this.sensorQueue = sensorQueue;
        this.delay = delay;
        this.handler = handler;
    }

    private void startNextScan() {
        if (GlobalContext.getWifiManager().startScan()) {
            lastScanRequest = SystemClock.uptimeMillis();
        }
    }

    @Override
    public void startRecording() {
        checkEnableWiFi();
        log.debug("Start Recording");
        GlobalContext.context.registerReceiver(scanResultsAvailableEndpoint, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);

        startNextScan();
    }

    @Override
    public void stopRecording() {
        log.debug("Stop Recording");
        try {
            GlobalContext.context.unregisterReceiver(scanResultsAvailableEndpoint);
        } catch (IllegalArgumentException e) {
            log.warn("Receiver already unregistered");
        }
    }

    private final BroadcastReceiver scanResultsAvailableEndpoint = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                log.debug("Scan results available");

                // Get receive-time of the intent in system uptime
                long scanEndtime = SystemClock.uptimeMillis();

                // Get scan results
                final List<ScanResult> scanResults = GlobalContext.getWifiManager().getScanResults();

                // If scan results are not null, push
                if (scanResults != null) {
                    final WiFi.Item[] items = new WiFi.Item[scanResults.size()];
                    for (int i = 0; i < scanResults.size(); i++) {
                        final ScanResult sr = scanResults.get(i);

                        items[i] = new WiFi.Item(
                                sr.SSID,
                                sr.BSSID,
                                sr.frequency,
                                sr.level
                        );
                    }

                    // Push converted scan results to queue
                    sensorQueue.push(new WiFi(
                            System.currentTimeMillis(),
                            GlobalContext.getUserId(),
                            items
                    ));
                }

                // If results are on time, schedule the next scan at the handler with the given delay
                if (lastScanRequest + delay > scanEndtime) {
                    if (!handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            startNextScan();
                        }
                    }, lastScanRequest + delay)) {
                        // If failed to schedule, scan immediately
                        startNextScan();
                    }
                } else {
                    // Else, scan immediately
                    startNextScan();
                }
            } else {
                throw new IllegalStateException("Illegal configuration of broadcast receiver");
            }
        }
    };

    private void checkEnableWiFi() {
        if (!SensorCollectionOptions.ASK_WIFI) return;

        if (!GlobalContext.getWifiManager().isWifiEnabled()) {
            Toast toast = Toast.makeText(GlobalContext.context, "Please enable WiFi.", Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GlobalContext.context.startActivity(intent);
        }
    }
}
