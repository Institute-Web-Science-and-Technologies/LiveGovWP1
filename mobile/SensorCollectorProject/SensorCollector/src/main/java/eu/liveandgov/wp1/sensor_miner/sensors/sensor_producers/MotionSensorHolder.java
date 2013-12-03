package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_miner.sensors.SensorSerializer;

/**
 * Created by hartmann on 9/15/13.
 */
public class MotionSensorHolder implements SensorHolder, SensorEventListener {
    private static final String LOG_TAG = "SP";

    private final Sensor  sensor;
    private final int     delay;
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
        // Log.i(LOG_TAG,"Recieved Sensor Sample " + SensorSerializer.fromSensorEvent(sensorEvent));
        sensorQueue.push(SensorSerializer.fromSensorEvent(sensorEvent));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }

    // Object Methods
    @Override
    public String toString(){
        return "SensorHolder for " + sensor.getName();
    }
}
