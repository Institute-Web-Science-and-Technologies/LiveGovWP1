package eu.liveandgov.sensorcollectorv3.har;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.connector.Producer;
import eu.liveandgov.sensorcollectorv3.sensors.MotionSensorValue;
import eu.liveandgov.sensorcollectorv3.sensors.Window;

/**
 * Created by cehlen on 10/19/13.
 */
public class WindowProducer extends Producer<Window> implements Consumer<MotionSensorValue> {

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
        Window w = new Window();

        MotionSensorValue[] values = queue.toArray();
        w.x = new float[values.length];
        w.y = new float[values.length];
        w.z = new float[values.length];

        for(int i = 0; i < values.length; i++) {
            // Get meta info from first element
            if(i == 0) {
                w.startTime = values[0].time;
                w.id = values[0].id;
                w.type = values[0].type;
            }

            w.endTime = values[i].time;
            w.x[i] = values[i].x;
            w.y[i] = values[i].y;
            w.z[i] = values[i].z;
        }

        // Push the Window!
        consumer.push(w);
    }
}
