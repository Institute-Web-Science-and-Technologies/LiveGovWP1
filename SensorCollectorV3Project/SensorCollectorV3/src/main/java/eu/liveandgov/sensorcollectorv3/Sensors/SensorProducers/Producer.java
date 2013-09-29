package eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers;

import android.hardware.SensorEventListener;

/**
 * Created by cehlen on 9/25/13.
 */
public abstract class Producer implements SensorEventListener {
    public final Integer PORT;

    public Producer(Integer PORT) {
        this.PORT = PORT;
    }

    public String getAddress() {
        return "tcp://127.0.0.1:" + PORT;
    }
}
