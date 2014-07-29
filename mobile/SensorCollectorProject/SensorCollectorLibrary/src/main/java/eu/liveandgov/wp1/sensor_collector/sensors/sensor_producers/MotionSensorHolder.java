package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Handler;

import com.google.common.primitives.Floats;

import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Gravity;
import eu.liveandgov.wp1.data.impl.Gyroscope;
import eu.liveandgov.wp1.data.impl.LinearAcceleration;
import eu.liveandgov.wp1.data.impl.MagneticField;
import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.data.impl.Rotation;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;

/**
 * Created by hartmann on 9/15/13.
 */
public class MotionSensorHolder implements SensorHolder, SensorEventListener {
    private static final String LOG_TAG = "SP";
    /**
     * Correction value for sensor timestamps
     */
    private static final long timestampCorrectionMs;

    /**
     * Initializers checks build-version and sets the appropriate timestamp correction
     */
    static {
        // If build-version is above jelly-bean mr1 (17), timestamps of the sensors are already in
        // utc, otherwise convert by rebasing them on the uptime
        //
        // We comute a global correction based on the fact, that currentTimeMillis is in UTC
        // and nanoTime is in uptime.

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            timestampCorrectionMs = (long) (System.currentTimeMillis() - (System.nanoTime() / 1E6));
        } else {
            timestampCorrectionMs = 0;
        }
    }


    private final Sensor sensor;
    private final int delay;
    private final Handler handler;
    private final SensorQueue sensorQueue;


    public MotionSensorHolder(SensorQueue sensorQueue, Sensor sensor, int delay, Handler handler) {
        this.sensor = sensor;
        this.delay = delay;
        this.handler = handler;
        this.sensorQueue = sensorQueue;
    }

    // SensorHolder
    @Override
    public void startRecording() {
        GlobalContext.getSensorManager().registerListener(this, sensor, delay, handler);
    }

    @Override
    public void stopRecording() {
        GlobalContext.getSensorManager().unregisterListener(this);
    }


    // Sensor Listener
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final long timestamp = (long) (sensorEvent.timestamp / 1E6) + timestampCorrectionMs;

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

        sensorQueue.push(motion);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }

    // Object Methods
    @Override
    public String toString() {
        return "SensorHolder for " + sensor.getName();
    }
}
