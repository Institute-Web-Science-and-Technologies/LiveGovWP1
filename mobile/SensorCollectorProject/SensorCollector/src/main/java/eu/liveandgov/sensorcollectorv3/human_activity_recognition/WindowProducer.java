package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

import android.util.Log;

import java.util.ArrayList;

import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.connectors.Producer;
import eu.liveandgov.sensorcollectorv3.sensors.MotionSensorValue;
import eu.liveandgov.sensorcollectorv3.sensors.Window;

/**
 * Created by cehlen on 10/19/13.
 */
class WindowProducer extends Producer<Window> implements Consumer<MotionSensorValue> {
    private static String LOG_TAG = "WND_PROD";


    private TimedQueue<MotionSensorValue> queue;
    private long windowSize;
    private long overlap;
    private long windowStart = -1;
    private long windowEnd = -1;

    public WindowProducer(long windowSize, long overlap) {
        this.windowSize = windowSize;
        this.overlap = overlap;
        queue = new TimedQueue<MotionSensorValue>(windowSize);
    }

    @Override
    public void push(MotionSensorValue m) {
        if(windowStart == -1) {
            windowStart = m.time;
            windowEnd = m.time + windowSize;
        }
        if(m.time > windowEnd) {
            // NEW WINDOW!
            createNewWindow();

            // Calculate new window timing
            windowStart = windowEnd - overlap;
            windowEnd = windowStart + windowSize;
        }
        queue.push(m.time, m);
    }

    private void createNewWindow() {
        Log.i(LOG_TAG, "New Window!");
        Window w = new Window();

        ArrayList<MotionSensorValue> values = queue.toArrayList();
        w.x = new float[values.size()];
        w.y = new float[values.size()];
        w.z = new float[values.size()];

        for(int i = 0; i < values.size(); i++) {
            // Get meta info from first element
            if(i == 0) {
                w.startTime = values.get(0).time;
                w.id = values.get(0).id;
                w.type = values.get(0).type;
            }

            w.endTime = values.get(i).time;
            w.x[i] = values.get(i).x;
            w.y[i] = values.get(i).y;
            w.z[i] = values.get(i).z;
        }

        // Push the Window!
        consumer.push(w);
    }
}
