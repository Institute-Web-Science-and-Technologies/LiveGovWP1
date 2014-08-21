package eu.liveandgov.wp1.sensor_collector.sensors;

import android.hardware.Sensor;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.ActivityHolder;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.BluetoothHolder;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.LocationHolderAndroid;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.LocationHolderPlayServices;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.MotionSensorHolder;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.SensorHolder;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.TelephonyHolder;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.WifiHolder;

import static junit.framework.Assert.assertNotNull;


/**
 * Singleton class that holds the sensor thread.
 * <p/>
 * This thread is responsible for:
 * startRecording / unregister individual sensors
 * <p/>
 * The singleton property makes is easy to start/stop recording via static method calls.
 * <p/>
 * Created by hartmann on 9/22/13.
 */
public class SensorThread implements Runnable {
    private final Logger log = LogPrincipal.get();

    private Set<SensorHolder> activeSensors = new CopyOnWriteArraySet<SensorHolder>();
    private Handler sensorHandler;
    private SensorQueue sensorQueue;
    private Thread thread;

    /* Private Singleton */
    private static SensorThread instance;

    private SensorThread(SensorQueue sensorQueue) {
        assertNotNull(sensorQueue);

        this.sensorQueue = sensorQueue;
        this.thread = new Thread(this);
    }

    /*  Static Methods */
    public static void setup(SensorQueue sensorQueue) {
        instance = new SensorThread(sensorQueue);
    }

    /**
     * Starts SensorThread
     * Configuration obtained from {@link .Configuration.SensorCollectionOptions}
     * Need to call setup first.
     */
    public static void start() {
        instance.thread.start();
    }

    public static void stopAllRecording() {
        for (SensorHolder p : instance.activeSensors) {
            p.stopRecording();
        }
    }

    public static void startAllRecording() {
        for (SensorHolder p : instance.activeSensors) {
            p.startRecording();
        }
    }


    // Runnable
    @Override
    public void run() {
        log.info("Starting Sensorloop");
        // Register MotionSensorHolder
        Looper.prepare();

        // setup message handler
        sensorHandler = new Handler();

        setupSensorHolder();

        // wait for messages
        Looper.loop();
    }

    private void setupSensorHolder() {
        setupMotionSensor(Sensor.TYPE_ACCELEROMETER, SensorCollectionOptions.REC_ACC);
        setupMotionSensor(Sensor.TYPE_LINEAR_ACCELERATION, SensorCollectionOptions.REC_LINEAR_ACC);
        setupMotionSensor(Sensor.TYPE_GRAVITY, SensorCollectionOptions.REC_GRAVITY_ACC);
        setupMotionSensor(Sensor.TYPE_GYROSCOPE, SensorCollectionOptions.REC_GYROSCOPE);
        setupMotionSensor(Sensor.TYPE_MAGNETIC_FIELD, SensorCollectionOptions.REC_MAGNETOMETER);
        setupMotionSensor(Sensor.TYPE_ROTATION_VECTOR, SensorCollectionOptions.REC_ROTATION);

        if (SensorCollectionOptions.REC_WIFI)
            setupWifiUpdate(SensorCollectionOptions.WIFI_SCAN_DELAY_MS);
        if (SensorCollectionOptions.REC_BLT)
            setupBluetoothUpdate(SensorCollectionOptions.BLT_SCAN_DELAY_MS);
        if (SensorCollectionOptions.REC_GPS) setupLocationUpdate();
        if (SensorCollectionOptions.REC_G_ACT) setupActivityUpdate();
        if (SensorCollectionOptions.REC_GSM) setupTelephonyUpdate();
    }


    private void setupMotionSensor(int sensorType, int delay) {
        if (delay == SensorCollectionOptions.SensorOptions.OFF) return;

        Sensor sensor = GlobalContext.getSensorManager().getDefaultSensor(sensorType);

        if (sensor == null) {
            log.info("Sensor " + sensorType + " not available.");
            // sensor not found
            return;
        }

        log.info("Registering Listener for " + sensor.getName());
        MotionSensorHolder holder = new MotionSensorHolder(sensorQueue, sensor, delay, sensorHandler);
        activeSensors.add(holder);
    }

    private void setupLocationUpdate() {
        // Check if Google Play Services are available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(GlobalContext.context);

        if (ConnectionResult.SUCCESS == resultCode) {
            log.info("Registering Listener for GPS using GooglePlayServices.");
            LocationHolderPlayServices holder = new LocationHolderPlayServices(sensorQueue, Looper.myLooper());
            activeSensors.add(holder);
        } else {
            log.info("Register fallback GPS listener.");
            LocationHolderAndroid holder = new LocationHolderAndroid(sensorQueue, Looper.myLooper());
            activeSensors.add(holder);
        }
    }

    private void setupTelephonyUpdate() {
        log.info("Registering Listener for Telephone State");
        TelephonyHolder holder = new TelephonyHolder(sensorQueue);
        activeSensors.add(holder);
    }

    private void setupActivityUpdate() {
        log.info("Registering Listener for ACTIVITY");
        ActivityHolder holder = new ActivityHolder();
        activeSensors.add(holder);
    }

    private void setupWifiUpdate(int delay) {
        log.info("Registering Listener for Wifi");
        WifiHolder holder = new WifiHolder(sensorQueue, delay, sensorHandler);
        activeSensors.add(holder);
    }

    private void setupBluetoothUpdate(int delay) {
        log.info("Registering Listener for Bluetooth");
        BluetoothHolder holder = new BluetoothHolder(sensorQueue, delay, sensorHandler);
        activeSensors.add(holder);
    }
}
