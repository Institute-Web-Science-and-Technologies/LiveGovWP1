package eu.liveandgov.wp1.sensor_collector.components;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.apache.log4j.Logger;

import java.util.Arrays;

import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Gravity;
import eu.liveandgov.wp1.data.impl.Gyroscope;
import eu.liveandgov.wp1.data.impl.LinearAcceleration;
import eu.liveandgov.wp1.data.impl.MagneticField;
import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.data.impl.Rotation;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.Threaded;

/**
 * <p>Source for items based on harware or emulated sensors as specified by Android</p>
 * Created by lukashaertel on 17.11.2014.
 */
public abstract class SensorSource extends RegularSampleSource {
    private final Logger log = LogPrincipal.get();
    /**
     * Sensor manager responsible for getting the sensors and for registering and unregistering the
     * listeners
     */
    @Inject
    SensorManager sensorManager;

    /**
     * Correction value for sensor timestamps
     */
    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.motionSensorCorrection")
    long motionSensorCorrection;

    /**
     * Percentage of delay to drop below
     */
    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.dropRate")
    double dropRate;

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
    @Threaded
    Handler handler;

    /**
     * Type of the sensor
     */
    private final int sensorType;

    /**
     * Accuracy value of the sensor
     */
    private int accuracy;

    /**
     * The timestamp of the last offered item
     */
    private Long lastTimestamp;

    /**
     * <p>
     * The number of dropped items, item dropping is necessary, as some devices (tested on LG G2)
     * have sensors (tested accelerometer) that do not respect the rate at all, clogging the system
     * with extremely excessive amount of data.
     * </p>
     */
    private long dropped;


    /**
     * Constructs the sensor source, configurator should be injected
     *
     * @param configurator The configurator for the source
     * @param sensorType   The sensor type
     */
    protected SensorSource(Configurator configurator, int sensorType) {
        super(configurator);
        this.sensorType = sensorType;

        dropped = 0;
    }

    @Override
    protected void handleActivation() {
        Sensor sensor = getSensor();

        log.info("Activating sensor " + sensor);

        if (!sensorManager.registerListener(listener, sensor, getCurrentDelay() * 1000, handler))
            log.warn("Sensor type is not fully supported: " + sensorType);
    }

    private Sensor getSensor() {
        return sensorManager.getDefaultSensor(sensorType);
    }

    @Override
    protected void handleDeactivation() {
        Sensor sensor = getSensor();

        log.info("Deactivating sensor " + sensor);
        sensorManager.unregisterListener(listener, sensor);
    }

    /**
     * Gets the current accuracy
     */
    public int getAccuracy() {
        return accuracy;
    }

    /**
     * <p>Gets the current number of dropped items.</p>
     * <p>Items are dropped if the rate exceed the desired rate.</p>
     *
     * @return Returns the number of dropped items
     */
    public long getDropped() {
        return dropped;
    }

    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putString("sensor", getSensor().getName());
        report.putInt("accuracy", getAccuracy());
        report.putLong("dropped", getDropped());
        return report;
    }


    /**
     * The listener for the sensor events
     */
    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            final long timestamp = (long) (sensorEvent.timestamp / 1E6) + motionSensorCorrection;

            float[] values = Arrays.copyOf(sensorEvent.values, sensorEvent.values.length);

            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    offerItem(new Acceleration(timestamp, credentials.user, values));
                    break;
                case Sensor.TYPE_GRAVITY:
                    offerItem(new Gravity(timestamp, credentials.user, values));
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    offerItem(new Gyroscope(timestamp, credentials.user, values));
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    offerItem(new LinearAcceleration(timestamp, credentials.user, values));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    offerItem(new MagneticField(timestamp, credentials.user, values));
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    offerItem(new Rotation(timestamp, credentials.user, values));
                    break;

                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            accuracy = i;
        }
    };

    private void offerItem(Motion item) {
        if (lastTimestamp == null || lastTimestamp + getCurrentDelay() * dropRate < item.getTimestamp()) {
            lastTimestamp = item.getTimestamp();
            itemBuffer.offer(item);
        } else
            // TODO: What about averaging strategies?
            // Pro: output relating on all input
            // Con: aggregation strategies general for everything?
            dropped++;

    }

    /**
     * Sensor source for accelerometer data
     */
    @Singleton
    public static class AccelerometerSource extends SensorSource {
        @Inject
        public AccelerometerSource(Configurator configurator) {
            super(configurator, Sensor.TYPE_ACCELEROMETER);
        }

        @Override
        protected Integer getDelay(MoraConfig config) {
            return config.acceleration;
        }
    }

    /**
     * Sensor source for linear acceleration data
     */
    @Singleton
    public static class LinearAccelerationSource extends SensorSource {
        @Inject
        public LinearAccelerationSource(Configurator configurator) {
            super(configurator, Sensor.TYPE_LINEAR_ACCELERATION);
        }

        @Override
        protected Integer getDelay(MoraConfig config) {
            return config.linearAcceleration;
        }
    }

    /**
     * Sensor source for gravity data
     */
    @Singleton
    public static class GravitySource extends SensorSource {
        @Inject
        public GravitySource(Configurator configurator) {
            super(configurator, Sensor.TYPE_GRAVITY);
        }

        @Override
        protected Integer getDelay(MoraConfig config) {
            return config.gravity;
        }
    }

    /**
     * Sensor source for magnetic field data
     */
    @Singleton
    public static class MagnetometerSource extends SensorSource {
        @Inject
        public MagnetometerSource(Configurator configurator) {
            super(configurator, Sensor.TYPE_MAGNETIC_FIELD);
        }

        @Override
        protected Integer getDelay(MoraConfig config) {
            return config.magnetometer;
        }
    }

    /**
     * Sensor source for rotation data
     */
    @Singleton
    public static class RotationSource extends SensorSource {
        @Inject
        public RotationSource(Configurator configurator) {
            super(configurator, Sensor.TYPE_ROTATION_VECTOR);
        }

        @Override
        protected Integer getDelay(MoraConfig config) {
            return config.rotation;
        }
    }
}
