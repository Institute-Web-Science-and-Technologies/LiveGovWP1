package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.Window;
import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.helper.TimedQueue;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by cehlen on 25/02/14.
 */
public class WindowPipeline extends Pipeline<Tuple<Long, Acceleration>, Tuple<Long, Window>> {

    private TimedQueue queue;
    private long window_size;
    private long delta;
    private long last_time = -1;
    private long next_window_end = -1;
    private long current_trip_id;


    public WindowPipeline(long window_size, long overlap) {
        this.window_size = window_size;
        this.delta = window_size - overlap;
        queue = new TimedQueue<Acceleration>(window_size);
    }

    @Override
    public void push(Tuple<Long, Acceleration> longAccelerationTuple) {
        if (current_trip_id != longAccelerationTuple.left) {
            current_trip_id = longAccelerationTuple.left;
            clear();
        }
        long time = longAccelerationTuple.right.getTimestamp();
        if (next_window_end == -1 || time < last_time) {
            next_window_end = time + window_size;
            last_time = time;
        }

        if (time > next_window_end) {
            pushWindow();

            next_window_end = time + delta;
        }
        queue.push(time, longAccelerationTuple.right);
    }

    private void clear() {
        last_time = -1;
        next_window_end = -1;
        queue.clear();
    }

    private void pushWindow() {
        Window w = new Window();

        ArrayList<Acceleration> values = queue.toArrayList();
        Collections.reverse(values);
        w.x = new float[values.size()];
        w.y = new float[values.size()];
        w.z = new float[values.size()];
        w.time = new long[values.size()];

        for(int i = 0; i < values.size(); i++) {
            // Get meta info from first element
            if(i == 0) {
                w.startTime = values.get(i).getTimestamp();
            }

            w.endTime = values.get(i).getTimestamp();
            w.x[i] = values.get(i).values[0];
            w.y[i] = values.get(i).values[1];
            w.z[i] = values.get(i).values[2];
            w.time[i] = values.get(i).getTimestamp();
        }
        Tuple<Long, Window> value = new Tuple<Long, Window>(current_trip_id, w);
        produce(value);
    }
}
