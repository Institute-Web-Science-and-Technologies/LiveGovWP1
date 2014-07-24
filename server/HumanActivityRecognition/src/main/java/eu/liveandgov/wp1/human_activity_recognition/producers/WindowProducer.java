package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.human_activity_recognition.containers.TaggedWindow;
import eu.liveandgov.wp1.human_activity_recognition.helper.TimedQueue;

import java.util.ArrayList;

/**
 * Created by cehlen on 10/19/13.
 */
public class WindowProducer extends Producer<TaggedWindow> implements Consumer<MotionSensorValue> {

    private TimedQueue<MotionSensorValue> queue;
    private long windowSize;
    private long delta;
    private long lastTime = -1;
    private long nextWindowEnd = -1;

    public WindowProducer(long windowSize, long overlap) {
        this.windowSize = windowSize;
        this.delta = windowSize - overlap;
        queue = new TimedQueue<MotionSensorValue>(windowSize);
    }

    public void push(MotionSensorValue m) {
        if (nextWindowEnd == -1 || m.time < lastTime) {
            // lazy loading or monotony violated
            nextWindowEnd = m.time + windowSize;
            lastTime = m.time;
        }

        if(m.time > nextWindowEnd) {
            // NEW WINDOW!
            pushWindow();

            // Calculate new window timing
            nextWindowEnd = m.time + delta;
        }

        queue.push(m.time, m);
    }

    public void clear() {
        lastTime = -1;
        nextWindowEnd = -1;
        queue.clear();
    }

    private void pushWindow() {
        TaggedWindow w = new TaggedWindow();

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
                w.tag = values.get(0).tag;
            }

            w.endTime = values.get(i).time;
            w.x[i] = values.get(i).x;
            w.y[i] = values.get(i).y;
            w.z[i] = values.get(i).z;
        }

        // Push the TaggedWindow!
//        System.out.println("Pushing Window Of size: " + values.size());
        consumer.push(w);
    }
}
