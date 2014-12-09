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
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;

/**
 * <p>Source for items based on harware or emulated sensors as specified by Android</p>
 * Created by lukashaertel on 17.11.2014.
 */
public abstract class SensorSource extends RegularSampleSource {
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

    /**
     * Type of the sensor
     */
    private int sensorType;

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
        super(configurator);
        this.sensorType = sensorType;
    }

    @Override
    protected void handleActivation() {
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(sensorType), getCurrentDelay(), handler);
    }

    @Override
    protected void handleDeactivation() {
        sensorManager.unregisterListener(listener);
    }

    /**
     * Gets the current accuracy
     */
    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putString("sensor", sensorManager.getDefaultSensor(sensorType).getName());
        report.putInt("accuracy", getAccuracy());
        return report;
    }

    /**
     * The listener for the sensor events
     */
    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            final long timestamp = (long) (sensorEvent.timestamp / 1E6) + motionSensorCorrection;

            float[] values = Floats.concat(sensorEvent.values);

            final Motion motion;
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    motion = new Acceleration(timestamp, credentials.user, values);
                    break;
                case Sensor.TYPE_GRAVITY:
                    motion = new Gravity(timestamp, credentials.user, values);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    motion = new Gyroscope(timestamp, credentials.user, values);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    motion = new LinearAcceleration(timestamp, credentials.user, values);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    motion = new MagneticField(timestamp, credentials.user, values);
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    motion = new Rotation(timestamp, credentials.user, values);
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
