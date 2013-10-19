package eu.liveandgov.sensorcollectorv3.sensors.sensor_producers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;

import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.sensor_queue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.sensors.SensorParser;

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
        GlobalContext.sensorManager.registerListener(this, sensor, delay, handler);
    }

    @Override
    public void stopRecording() {
        GlobalContext.sensorManager.unregisterListener(this);
    }


    // Sensor Listener
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Log.i(LOG_TAG,"Recieved Sensor Sample " + SensorParser.parse(sensorEvent));
        sensorQueue.push(SensorParser.parse(sensorEvent));
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
