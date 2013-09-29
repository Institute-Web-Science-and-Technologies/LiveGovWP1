package eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;

import eu.liveandgov.sensorcollectorv3.Sensors.GlobalContext;
import eu.liveandgov.sensorcollectorv3.Sensors.MessageQueue;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorParser;

/**
 * Created by hartmann on 9/15/13.
 */
public class MotionSensorHolder implements SensorHolder, SensorEventListener {
    private static final String LOG_TAG = "SP";

    private final Sensor  sensor;
    private final int     delay;
    private final Handler handler;

    public MotionSensorHolder(Sensor sensor, int delay, Handler handler) {
        this.sensor = sensor;
        this.delay = delay;
        this.handler = handler;
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
        Log.i(LOG_TAG,"Recieved Sensor Sample " + SensorParser.parse(sensorEvent));
        MessageQueue.push(SensorParser.parse(sensorEvent));
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
