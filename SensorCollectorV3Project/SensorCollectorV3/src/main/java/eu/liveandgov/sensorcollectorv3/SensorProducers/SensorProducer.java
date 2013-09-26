package eu.liveandgov.sensorcollectorv3.SensorProducers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.sensorcollectorv3.SensorParser;

/**
 * Created by hartmann on 9/15/13.
 */
public class SensorProducer extends Producer implements SensorEventListener {
    private static final String LOG_TAG = "SP";
    ZMQ.Socket s;

    public SensorProducer(Integer PORT){
        super(PORT);
        Log.i(LOG_TAG, "Setting up Socket " + getAddress());
        s = ZMQ.context().socket(ZMQ.PUB);
        s.bind(getAddress());
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Log.i(LOG_TAG,"Recieved Sensor Sample " + SensorParser.parse(sensorEvent));
        // if (inSocket == null) setupConnection();
        s.send(SensorParser.parse(sensorEvent));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }

    @Override
    public String getAddress() {
        return "tcp://127.0.0.1:" + PORT;
    }
}
