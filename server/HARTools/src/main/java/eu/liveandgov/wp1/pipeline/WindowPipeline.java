package eu.liveandgov.wp1.pipeline;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.Window;
import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.helper.TimedQueue;
import eu.liveandgov.wp1.helper.TimedQueue2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by cehlen on 25/02/14.
 */
public class WindowPipeline extends Pipeline<Tuple<Long, Acceleration>, Tuple<Long, Window>> {

    private TimedQueue2<Acceleration> queue;
    private long window_size;
    private long delta;
    private long last_time = -1;
    private long next_window_end = -1;
    private long current_trip_id;


    public WindowPipeline(long window_size, long overlap) {
        this.window_size = window_size;
        this.delta = window_size - overlap;
        queue = new TimedQueue2<Acceleration>(window_size) {
            @Override
            protected long getTime(Acceleration acceleration) {
                return acceleration.getTimestamp();
            }
        };
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
        queue.push(longAccelerationTuple.right);
    }

    public void clear() {
        last_time = -1;
        next_window_end = -1;
        queue.clear();
    }

    private void pushWindow() {
        Window w = new Window();

        Collection<Acceleration> values = queue.items();

        w.startTime = Long.MAX_VALUE;
        w.endTime = Long.MIN_VALUE;
        w.x = new float[values.size()];
        w.y = new float[values.size()];
        w.z = new float[values.size()];
        w.time = new long[values.size()];

        int i = 0;
        for (Acceleration a : values) {
            w.startTime = Math.min(a.getTimestamp(), w.startTime);
            w.endTime = Math.max(a.getTimestamp(), w.endTime);

            w.x[i] = a.values[0];
            w.y[i] = a.values[1];
            w.z[i] = a.values[2];
            w.time[i] = a.getTimestamp();

            i++;
        }

        Tuple<Long, Window> value = new Tuple<Long, Window>(current_trip_id, w);

        produce(value);
    }
}
