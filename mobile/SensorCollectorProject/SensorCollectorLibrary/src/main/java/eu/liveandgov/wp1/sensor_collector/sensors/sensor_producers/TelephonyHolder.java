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

    public static String getStateName(int i)
    {
        if(i == ServiceState.STATE_EMERGENCY_ONLY)
        {
            return "emergency only";
        }
        else if(i == ServiceState.STATE_IN_SERVICE)
        {
            return "in service";
        }
        else if(i == ServiceState.STATE_OUT_OF_SERVICE)
        {
            return "out of service";
        }
        else if(i == ServiceState.STATE_POWER_OFF)
        {
            return "power off";
        }
        else
        {
            return "unknown";
        }
    }

    public static String getRoamingText(boolean isRoaming)
    {
        // TODO: Proper opposite of roaming??!
        return isRoaming ? "roaming" : "not roaming";
    }

    public static String getManualModeText(boolean isManualMode)
    {
        return isManualMode ? "manual carrier" : "automatic carrier";
    }

    public static String getSignalStrengthText(SignalStrength signalStrength)
    {
        if(signalStrength.isGsm())
        {
            return String.format(Locale.ENGLISH, "gsm %d", convertTS27SignalStrength(signalStrength.getGsmSignalStrength()));
        }
        else
        {
            return String.format(Locale.ENGLISH, "other %d %d, %d %d", signalStrength.getCdmaDbm(), signalStrength.getCdmaEcio(), signalStrength.getEvdoDbm(), signalStrength.getEvdoEcio());
        }
    }

    /**
     * Returns the Signal Strength in dBm
     */
    public static Integer convertTS27SignalStrength(int i)
    {
        if(i == 99)
        {
            return null;
        }
        else
        {
            return -113 + 2 * i;
        }
    }

    public static String getNetworkTypeText(int nt)
    {
        switch (nt)
        {
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
        }

        return "other";
    }

    public static String getTS27SignalStrengthText(int i)
    {
        if(i == 99)
        {
            return "unknown";
        }
        else
        {
            return String.format(Locale.ENGLISH, "%d", convertTS27SignalStrength(i));
        }
    }

    public static String getIdentityText(int cid, int lac)
    {
        if(cid == NeighboringCellInfo.UNKNOWN_CID)
        {
            if(lac == NeighboringCellInfo.UNKNOWN_CID)
            {
                return "unknown";
            }
            else
            {
                return String.format(Locale.ENGLISH, "lac: %d", lac);
            }
        }
        else
        {
            if(lac == NeighboringCellInfo.UNKNOWN_CID)
            {
                return String.format(Locale.ENGLISH, "cid: %d", cid);
            }
            else
            {
                return String.format(Locale.ENGLISH, "cid: %d lac: %d", cid, lac);
            }
        }
    }

    private static final int LISTEN_FLAGS =
            PhoneStateListener.LISTEN_SERVICE_STATE |
            PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;

    private final SensorQueue sensorQueue;

    private ServiceState lastServiceState;

    private SignalStrength lastSignalStrength;

    private List<NeighboringCellInfo> lastNeighboringCellInfos;

    public TelephonyHolder(SensorQueue sensorQueue)
    {
        this.sensorQueue = sensorQueue;

        init();
    }

    private void init() {
        lastSignalStrength = null;
        lastServiceState = null;
        lastNeighboringCellInfos = null;
    }

    @Override
    public void startRecording()
    {
        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, LISTEN_FLAGS);
    }

    @Override
    public void stopRecording()
    {
        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, PhoneStateListener.LISTEN_NONE);
    }

    private void tryPushToSensorQueue()
    {
        if(lastNeighboringCellInfos == null || lastSignalStrength == null || lastServiceState == null)
        {
            // We have not yet acquired enough data for the state to be intrinsically writable to
            // the sensor queue, so return
            return;
        }

        sensorQueue.push(SensorSerializer.fromPhoneState(lastServiceState, lastSignalStrength, lastNeighboringCellInfos));
    }

    private final PhoneStateListener phoneStateEndpoint = new PhoneStateListener()
    {
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
