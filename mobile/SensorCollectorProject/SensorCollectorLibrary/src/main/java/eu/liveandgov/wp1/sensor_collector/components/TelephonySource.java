package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.data.impl.GSM;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.MoraConstants;

/**
 * <p>Sample source for cellular state</p>
 * Created by lukashaertel on 05.12.14.
 */
@Singleton
public class TelephonySource extends RegularSampleSource {
    private static final Logger log = LogPrincipal.get();

    @Inject
    TelephonyManager telephonyManager;

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

    @Inject
    ScheduledExecutorService scheduledExecutorService;

    private ScheduledFuture<?> gsmTask;

    private ServiceState lastServiceState;

    private SignalStrength lastSignalStrength;

    private List<NeighboringCellInfo> lastNeighboringCellInfo;


    @Inject
    public TelephonySource(Configurator configurator) {
        super(configurator);

        lastSignalStrength = null;
        lastServiceState = null;
        lastNeighboringCellInfo = Collections.emptyList();
    }

    @Override
    protected void handleActivation() {
        telephonyManager.listen(phoneStateEndpoint, PhoneStateListener.LISTEN_SERVICE_STATE |
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        gsmTask = scheduledExecutorService.scheduleAtFixedRate(gsmMethod, 0L, getCurrentDelay(), TimeUnit.MILLISECONDS);
    }

    @Override
    protected void handleDeactivation() {
        telephonyManager.listen(phoneStateEndpoint, PhoneStateListener.LISTEN_NONE);

        if (gsmTask != null) {
            gsmTask.cancel(true);
            gsmTask = null;
        }
    }


    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putParcelable("lastServiceState", lastServiceState);
        report.putParcelable("lastSignalStrength", lastSignalStrength);
        report.putLong("items", lastNeighboringCellInfo.size());
        return report;
    }

    @Override
    protected Integer getDelay(MoraConfig config) {
        return config.gsm;
    }

    private final Runnable gsmMethod = new Runnable() {
        @Override
        public void run() {
            log.info("Telephony source composing new element");
            lastNeighboringCellInfo = telephonyManager.getNeighboringCellInfo();

            if (lastNeighboringCellInfo == null || lastSignalStrength == null || lastServiceState == null) {
                // We have not yet acquired enough data for the state to be intrinsically writable to
                // the sensor queue, so return
                return;
            }

            final GSM.ServiceState serviceState;
            switch (lastServiceState.getState()) {
                case ServiceState.STATE_EMERGENCY_ONLY:
                    serviceState = GSM.ServiceState.EMERGENCY_ONLY;
                    break;
                case ServiceState.STATE_IN_SERVICE:
                    serviceState = GSM.ServiceState.IN_SERVICE;
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    serviceState = GSM.ServiceState.OUT_OF_SERVICE;
                    break;

                case ServiceState.STATE_POWER_OFF:
                    serviceState = GSM.ServiceState.POWER_OFF;
                    break;

                default:
                    serviceState = GSM.ServiceState.UNKNOWN;
                    break;
            }

            final GSM.RoamingState roamingState;
            if (lastServiceState.getRoaming()) {
                roamingState = GSM.RoamingState.ROAMING;
            } else {
                roamingState = GSM.RoamingState.NOT_ROAMING;
            }

            final GSM.CarrierSelection carrierSelection;
            if (lastServiceState.getIsManualSelection()) {
                carrierSelection = GSM.CarrierSelection.MANUAL_CARRIER;
            } else {
                carrierSelection = GSM.CarrierSelection.AUTOMATIC_CARRIER;
            }

            final GSM.Item[] items = new GSM.Item[lastNeighboringCellInfo.size()];
            for (int i = 0; i < lastNeighboringCellInfo.size(); i++) {
                final NeighboringCellInfo nci = lastNeighboringCellInfo.get(i);

                final GSM.CellType cellType;
                switch (nci.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        cellType = GSM.CellType.GPRS;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        cellType = GSM.CellType.EDGE;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        cellType = GSM.CellType.UMTS;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        cellType = GSM.CellType.HSDPA;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        cellType = GSM.CellType.HSUPA;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        cellType = GSM.CellType.HSPA;
                        break;
                    default:
                        cellType = GSM.CellType.UNKNOWN;
                }

                items[i] = new GSM.Item(
                        MoraConstants.getCellIdentityText(nci.getCid(), nci.getLac()),
                        cellType,
                        MoraConstants.convertTS27SignalStrength(nci.getRssi())
                );
            }

            itemBuffer.offer(new GSM(
                    System.currentTimeMillis(),
                    credentials.user,
                    serviceState,
                    roamingState,
                    carrierSelection,
                    lastServiceState.getOperatorAlphaLong(),
                    MoraConstants.getSignalStrengthText(lastSignalStrength),
                    items
            ));
        }
    };

    private final PhoneStateListener phoneStateEndpoint = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            lastSignalStrength = signalStrength;
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);

            lastServiceState = serviceState;
        }
    };
}
