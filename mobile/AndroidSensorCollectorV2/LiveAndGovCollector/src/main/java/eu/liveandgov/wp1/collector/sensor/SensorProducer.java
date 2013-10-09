package eu.liveandgov.wp1.collector.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.jeromq.ZMQ;

/**
 * Created by hartmann on 9/15/13.
 */
public class SensorProducer implements SensorEventListener {
    private static final String LOG_TAG = "SP";
    public final Integer PORT;
    ZMQ.Socket s;

    public SensorProducer(Integer PORT){
        this.PORT = PORT;
        setupConnection();
    }

    private void setupConnection(){
        Log.i(LOG_TAG, "Setting up Connnection");
        s = ZMQ.context().socket(ZMQ.PUB);
        s.bind("tcp://127.0.0.1:" + PORT);
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

    public String getAddress() {
        return "tcp://127.0.0.1:" + PORT;
    }
}
