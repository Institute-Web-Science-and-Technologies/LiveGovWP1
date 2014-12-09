package eu.liveandgov.wp1.sensor_collector.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.util.List;

import eu.liveandgov.wp1.data.impl.WiFi;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * <p>Sample source for WiFi state</p>
 * Created by lukashaertel on 05.12.14.
 */
@Singleton
public class WifiSource extends RegularSampleSource {
    private static final Logger log = LogPrincipal.get();

    @Inject
    Context context;

    @Inject
    WifiManager wifiManager;

    /**
     * Central credentials store
     */
    @Inject
    Credentials credentials;

    /**
     * Central item buffer
     */
    @Inject
    ItemBuffer itemBuffer;

    /**
     * Message handler
     */
    @Inject
    Handler handler;

    private long lastScanRequest;


    @Inject
    public WifiSource(Configurator configurator) {
        super(configurator);

        lastScanRequest = Long.MIN_VALUE;
    }

    private void startNextScan() {
        if (wifiManager.startScan()) {
            lastScanRequest = SystemClock.uptimeMillis();
        }
    }

    @Override
    protected boolean isAvailable() {
        return wifiManager.isWifiEnabled();
    }

    @Override
    protected void handleActivation() {
        // Register receiver for wifi results
        context.registerReceiver(scanResultsAvailableEndpoint, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);

        // Start first scan immediately
        startNextScan();
    }

    @Override
    protected void handleDeactivation() {
        try {
            context.unregisterReceiver(scanResultsAvailableEndpoint);
        } catch (IllegalArgumentException e) {
            log.error("Receiver already unregistered", e);
        }
    }


    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putLong("lastScanRequest", lastScanRequest);
        return report;
    }

    @Override
    protected Integer getDelay(MoraConfig config) {
        return config.wifi;
    }


    private final BroadcastReceiver scanResultsAvailableEndpoint = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {

                // Get receive-time of the intent in system up-time
                long scanEndTime = SystemClock.uptimeMillis();

                // Get scan results
                final List<ScanResult> scanResults = wifiManager.getScanResults();

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
                    itemBuffer.offer(new WiFi(
                            System.currentTimeMillis(),
                            credentials.user,
                            items
                    ));
                }

                // If results are on time, schedule the next scan at the handler with the given delay
                if (lastScanRequest + getCurrentDelay() > scanEndTime) {
                    if (!handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            startNextScan();
                        }
                    }, lastScanRequest + getCurrentDelay())) {
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
}
