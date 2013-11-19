package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.connectors.Consumer;
import eu.liveandgov.wp1.connectors.Producer;
import eu.liveandgov.wp1.sensors.TaggedMotionSensorValue;
import eu.liveandgov.wp1.sensors.TaggedWindow;

import java.util.ArrayList;

/**
 * Created by cehlen on 10/19/13.
 */
public class TaggedWindowProducer extends Producer<TaggedWindow> implements Consumer<TaggedMotionSensorValue> {

    private TimedQueue<TaggedMotionSensorValue> queue;
    private long windowSize;
    private long overlap;
    private long windowStart = -1;
    private long windowEnd = -1;

    public TaggedWindowProducer(long windowSize, long overlap) {
        this.windowSize = windowSize;
        this.overlap = overlap;
        queue = new TimedQueue<TaggedMotionSensorValue>(windowSize);
    }

    @Override
    public void push(TaggedMotionSensorValue m) {
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
        TaggedWindow w = new TaggedWindow();

        ArrayList<TaggedMotionSensorValue> values = queue.toArrayList();
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
        consumer.push(w);
    }
}
