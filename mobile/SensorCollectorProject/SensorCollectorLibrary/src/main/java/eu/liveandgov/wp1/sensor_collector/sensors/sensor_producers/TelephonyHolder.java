package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.data.impl.GSM;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;

/**
 * Created by lukashaertel on 02.12.13.
 */
public class TelephonyHolder implements SensorHolder {

    public static final String LOG_TAG = "TELH";

    /**
     * Returns the Signal Strength in dBm
     */
    private static Integer convertTS27SignalStrength(int i) {
        if (i == 99) {
            return null;
        } else {
            return -113 + 2 * i;
        }
    }

    private static String getTS27SignalStrengthText(int i) {
        if (i == 99) {
            return "unknown";
        } else {
            return String.format(Locale.ENGLISH, "%d", convertTS27SignalStrength(i));
        }
    }

    private static String getSignalStrengthText(SignalStrength signalStrength) {
        if (signalStrength.isGsm()) {
            return String.format(Locale.ENGLISH, "gsm %d", convertTS27SignalStrength(signalStrength.getGsmSignalStrength()));
        } else {
            return String.format(Locale.ENGLISH, "other %d %d, %d %d", signalStrength.getCdmaDbm(), signalStrength.getCdmaEcio(), signalStrength.getEvdoDbm(), signalStrength.getEvdoEcio());
        }
    }

    public String getIdentityText(int cid, int lac) {
        if (cid == NeighboringCellInfo.UNKNOWN_CID) {
            if (lac == NeighboringCellInfo.UNKNOWN_CID) {
                return "unknown";
            } else {
                return String.format(Locale.ENGLISH, "lac: %d", lac);
            }
        } else {
            if (lac == NeighboringCellInfo.UNKNOWN_CID) {
                return String.format(Locale.ENGLISH, "cid: %d", cid);
            } else {
                return String.format(Locale.ENGLISH, "cid: %d lac: %d", cid, lac);
            }
        }
    }

    private static final int LISTEN_FLAGS =
            PhoneStateListener.LISTEN_SERVICE_STATE |
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;

    private final SensorQueue sensorQueue;

    private ScheduledFuture<?> gsmTask;

    private ServiceState lastServiceState;

    private SignalStrength lastSignalStrength;

    private List<NeighboringCellInfo> lastNeighboringCellInfos;

    public TelephonyHolder(SensorQueue sensorQueue) {
        this.sensorQueue = sensorQueue;

        init();
    }

    private void init() {
        lastSignalStrength = null;
        lastServiceState = null;
        lastNeighboringCellInfos = null;
    }


    @Override
    public void startRecording() {
        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, LISTEN_FLAGS);

        gsmTask = GlobalContext.getExecutorService().scheduleAtFixedRate(gsmMethod, 0L, SensorCollectionOptions.GSM_SCAN_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopRecording() {
        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, PhoneStateListener.LISTEN_NONE);

        if (gsmTask != null) {
            gsmTask.cancel(true);
            gsmTask = null;
        }
    }


    private void tryPushToSensorQueue() {
        if (lastNeighboringCellInfos == null || lastSignalStrength == null || lastServiceState == null) {
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

        final GSM.Item[] items = new GSM.Item[lastNeighboringCellInfos.size()];
        for (int i = 0; i < lastNeighboringCellInfos.size(); i++) {
            final NeighboringCellInfo nci = lastNeighboringCellInfos.get(i);

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
                    getIdentityText(nci.getCid(), nci.getLac()),
                    cellType,
                    convertTS27SignalStrength(nci.getRssi())
            );
        }

        sensorQueue.push(new GSM(
                System.currentTimeMillis(),
                GlobalContext.getUserId(),
                serviceState,
                roamingState,
                carrierSelection,
                lastServiceState.getOperatorAlphaLong(),
                getSignalStrengthText(lastSignalStrength),
                items
        ));
    }

    private final Runnable gsmMethod = new Runnable() {
        @Override
        public void run() {
            lastNeighboringCellInfos = GlobalContext.getTelephonyManager().getNeighboringCellInfo();

            tryPushToSensorQueue();
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
