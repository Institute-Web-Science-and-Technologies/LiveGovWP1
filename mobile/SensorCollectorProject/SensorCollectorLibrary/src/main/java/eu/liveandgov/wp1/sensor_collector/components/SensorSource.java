package eu.liveandgov.wp1.sensor_collector.components;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;

import com.google.common.primitives.Floats;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Gravity;
import eu.liveandgov.wp1.data.impl.Gyroscope;
import eu.liveandgov.wp1.data.impl.LinearAcceleration;
import eu.liveandgov.wp1.data.impl.MagneticField;
import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.data.impl.Rotation;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.ConfigListener;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.os.SampleSource;

/**
 * <p>Source for items based on harware or emulated sensors as specified by Android</p>
 * Created by lukashaertel on 17.11.2014.
 */
public abstract class SensorSource implements SampleSource, Reporter {
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
     * Central item buffer
     */
    @Inject
    ItemBuffer itemBuffer;

    /**
     * Message handler
     */
    @Inject
    Handler handler;

    /**
     * Configuration handler
     */
    private Configurator configurator;

    /**
     * Type of the sensor
     */
    private int sensorType;

    /**
     * True if sensor is currently active
     */
    private boolean active;

    /**
     * Accuracy value of the sensor
     */
    private int accuracy;

    /**
     * Constructs the sensor source, configurator should be injected
     *
     * @param configurator The configurator for the source
     * @param sensorType   The sensor type
     */
    protected SensorSource(Configurator configurator, int sensorType) {
        this.configurator = configurator;
        this.sensorType = sensorType;

        configurator.initListener(new ConfigListener() {
            @Override
            public void updated(MoraConfig config) {
                // Default update strategy is reactivation
                if (isActive()) {
                    deactivate();
                    activate();
                }
            }
        }, true);
    }

    /**
     * Should return true if the sensor is enabled in the config
     *
     * @param config The config to read
     */
    protected abstract boolean isEnabled(MoraConfig config);

    /**
     * Should return the delay in microseconds (?)
     *
     * @param config The config to read
     */
    protected abstract int getDelay(MoraConfig config);

    @Override
    public void activate() {
        if (active)
            return;

        if (!isEnabled(configurator.getConfig()))
            return;

        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(sensorType), getDelay(configurator.getConfig()), handler);
        active = true;
    }


    @Override
    public void deactivate() {
        if (!active)
            return;

        active = false;
        sensorManager.unregisterListener(listener);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the current accuracy
     */
    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public Bundle getReport() {
        Bundle report = new Bundle();
        report.putString(SPECIAL_KEY_ORIGINATOR, getClass().getSimpleName());

        report.putString("sensor", sensorManager.getDefaultSensor(sensorType).getName());
        report.putInt("accuracy", accuracy);
        report.putBoolean("active", active);
        if (isEnabled(configurator.getConfig())) {
            report.putBoolean("enabled", true);
            report.putInt("delay", getDelay(configurator.getConfig()));
        } else
            report.putBoolean("enabled", false);

        return report;
    }

    /**
     * The listener for the sensor events
     */
    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            final long timestamp = (long) (sensorEvent.timestamp / 1E6) + motionSensorCorrection;

            String userId = GlobalContext.getUserId();
            float[] values = Floats.concat(sensorEvent.values);

            final Motion motion;
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    motion = new Acceleration(timestamp, userId, values);
                    break;
                case Sensor.TYPE_GRAVITY:
                    motion = new Gravity(timestamp, userId, values);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    motion = new Gyroscope(timestamp, userId, values);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    motion = new LinearAcceleration(timestamp, userId, values);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    motion = new MagneticField(timestamp, userId, values);
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    motion = new Rotation(timestamp, userId, values);
                    break;

                default:
                    throw new IllegalArgumentException();
            }

            itemBuffer.offer(motion);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            accuracy = i;
        }
    };

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
        protected boolean isEnabled(MoraConfig config) {
            return config.acceleration != null;
        }

        @Override
        protected int getDelay(MoraConfig config) {
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
        protected boolean isEnabled(MoraConfig config) {
            return config.linearAcceleration != null;
        }

        @Override
        protected int getDelay(MoraConfig config) {
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
        protected boolean isEnabled(MoraConfig config) {
            return config.gravity != null;
        }

        @Override
        protected int getDelay(MoraConfig config) {
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
        protected boolean isEnabled(MoraConfig config) {
            return config.magnetometer != null;
        }

        @Override
        protected int getDelay(MoraConfig config) {
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
        protected boolean isEnabled(MoraConfig config) {
            return config.rotation != null;
        }

        @Override
        protected int getDelay(MoraConfig config) {
            return config.rotation;
        }
    }
}
