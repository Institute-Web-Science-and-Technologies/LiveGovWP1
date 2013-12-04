package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_miner.sensors.SensorSerializer;

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
            return String.format(Locale.ENGLISH, "gsm %f%%", convertTS27SignalStrength(signalStrength.getGsmSignalStrength()));
        }
        else
        {
            return String.format(Locale.ENGLISH, "other %d %d, %d %d", signalStrength.getCdmaDbm(), signalStrength.getCdmaEcio(), signalStrength.getEvdoDbm(), signalStrength.getEvdoEcio());
        }
    }

    /**
     * Returns the Signal Strength in percent
     */
    public static Float convertTS27SignalStrength(int i)
    {
        if(i == 99)
        {
            return null;
        }
        else
        {
            return 100.0f * i / 31f;
        }
    }

    public static Double convert3GPP2LonLat(int i)
    {
        if(i == Integer.MAX_VALUE)
        {
            return null;
        }
        else
        {
            // i is in quarter seconds, so multiply with 0.25, then convert to minutes, then degree
            return  (i * 0.25) / 60.0 / 60.0;
        }
    }
    public static String getCellIdentityText(CellIdentityCdma cellIdentityCdma)
    {
        final Double lat = convert3GPP2LonLat(cellIdentityCdma.getLatitude());
        final Double lon = convert3GPP2LonLat(cellIdentityCdma.getLongitude());

        if(lat == null || lon == null)
        {
            return String.format(Locale.ENGLISH, "cdma %d:%d:%d at unknown", cellIdentityCdma.getBasestationId(), cellIdentityCdma.getNetworkId(), cellIdentityCdma.getSystemId());
        }
        else
        {
            return String.format(Locale.ENGLISH, "cdma %d:%d:%d at %f, %f", cellIdentityCdma.getBasestationId(), cellIdentityCdma.getNetworkId(), cellIdentityCdma.getSystemId(), lat, lon);
        }
    }

    public static String getCellIdentityText(CellIdentityGsm cellIdentityGsm)
    {
        return String.format(Locale.ENGLISH, "gsm %d at %d:%d:%d", cellIdentityGsm.getCid(), cellIdentityGsm.getMcc(),cellIdentityGsm.getMnc(), cellIdentityGsm.getLac());

    }

    public static String getCellIdentityText(CellIdentityLte cellIdentityLte)
    {
        return String.format(Locale.ENGLISH, "lte %d:%d at %d:%d", cellIdentityLte.getCi(), cellIdentityLte.getPci(),  cellIdentityLte.getMcc(),cellIdentityLte.getMnc());
    }

    public static String getCellSignalStrengthText(CellSignalStrengthCdma cellSignalStrength)
    {
        return String.format(Locale.ENGLISH, "cdma %d %d, %d %d", cellSignalStrength.getCdmaDbm(), cellSignalStrength.getCdmaEcio(), cellSignalStrength.getEvdoDbm(), cellSignalStrength.getEvdoEcio());
    }

    public static String getCellSignalStrengthText(CellSignalStrengthGsm cellSignalStrength)
    {
        return String.format(Locale.ENGLISH, "gsm %d", cellSignalStrength.getDbm());
    }

    public static String getCellSignalStrengthText(CellSignalStrengthLte cellSignalStrength)
    {
        return String.format(Locale.ENGLISH, "lte %d", cellSignalStrength.getDbm());
    }

    private static final int LISTEN_FLAGS =
            PhoneStateListener.LISTEN_CELL_INFO |
            PhoneStateListener.LISTEN_SERVICE_STATE |
            PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;

    private final SensorQueue sensorQueue;

    private List<CellInfo> lastCellInfo;

    private SignalStrength lastSignalStrength;

    private ServiceState lastServiceState;

    public TelephonyHolder(SensorQueue sensorQueue)
    {
        this.sensorQueue = sensorQueue;

        init();
    }

    private void init() {
        lastCellInfo = null;
        lastSignalStrength = null;
        lastServiceState = null;
    }

    private void prepare()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            // Versions prior than API level 17 cannot apparently not enumerate cells, so initialize
            // empty
            lastCellInfo = Collections.emptyList();
        }
        else
        {
            // Otherwise enumerate with telephony manager
            lastCellInfo = GlobalContext.getTelephonyManager().getAllCellInfo();
        }
    }

    @Override
    public void startRecording()
    {
        if(lastCellInfo == null || lastSignalStrength == null || lastServiceState == null)
        {
            prepare();
        }

        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, LISTEN_FLAGS);
    }

    @Override
    public void stopRecording()
    {
        GlobalContext.getTelephonyManager().listen(phoneStateEndpoint, PhoneStateListener.LISTEN_NONE);
    }

    private void tryPushToSensorQueue()
    {
        if(lastCellInfo == null || lastSignalStrength == null || lastServiceState == null)
        {
            // We have not yet acquired enough data for the state to be intrinsically writable to
            // the sensor queue, so return
            return;
        }

        sensorQueue.push(SensorSerializer.fromPhoneState(lastServiceState, lastSignalStrength, lastCellInfo));
    }

    private final PhoneStateListener phoneStateEndpoint = new PhoneStateListener()
    {
        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            super.onCellInfoChanged(cellInfo);

            lastCellInfo = cellInfo;

            tryPushToSensorQueue();
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            lastSignalStrength = signalStrength;

            tryPushToSensorQueue();
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);

            lastServiceState = serviceState;

            tryPushToSensorQueue();
        }
    };
}
