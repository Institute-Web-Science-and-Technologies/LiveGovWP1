package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.Locale;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 02.12.13.
 */
public class TelephonyHolder implements SensorHolder {

    public static final String LOG_TAG = "TELH";

    public static interface PhoneState {
        public boolean isValid();

        public ServiceState getServiceState();

        public SignalStrength getSignalStrength();

        public Iterable<? extends NeighboringCellInfo> getNeighboringCellInfos();
    }

    private static final int LISTEN_FLAGS =
            PhoneStateListener.LISTEN_SERVICE_STATE |
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;

    private final SensorQueue sensorQueue;

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

    public final PhoneState lastPhoneState = new PhoneState() {
        @Override
        public boolean isValid() {
            return lastServiceState != null && lastSignalStrength != null && lastNeighboringCellInfos != null;
        }

        @Override
        public ServiceState getServiceState() {
            return lastServiceState;
        }

        @Override
        public SignalStrength getSignalStrength() {
            return lastSignalStrength;
        }

        @Override
        public Iterable<? extends NeighboringCellInfo> getNeighboringCellInfos() {
            return lastNeighboringCellInfos;
        }
    };


    @Override
    public void startRecording() {
        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, LISTEN_FLAGS);
    }

    @Override
    public void stopRecording() {
        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, PhoneStateListener.LISTEN_NONE);
    }


    private void tryPushToSensorQueue() {
        if (lastNeighboringCellInfos == null || lastSignalStrength == null || lastServiceState == null) {
            // We have not yet acquired enough data for the state to be intrinsically writable to
            // the sensor queue, so return
            return;
        }

        sensorQueue.push(SensorSerializer.phoneState.toSSFDefault(lastPhoneState));
    }

    private final PhoneStateListener phoneStateEndpoint = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            lastSignalStrength = signalStrength;

            // TODO: Enumerate neighboring cells in a thread on a regular schedule instead of
            // fetching it on other events
            lastNeighboringCellInfos = GlobalContext.getTelephonyManager().getNeighboringCellInfo();

            tryPushToSensorQueue();
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);

            lastServiceState = serviceState;
            lastNeighboringCellInfos = GlobalContext.getTelephonyManager().getNeighboringCellInfo();

            tryPushToSensorQueue();
        }
    };
}
