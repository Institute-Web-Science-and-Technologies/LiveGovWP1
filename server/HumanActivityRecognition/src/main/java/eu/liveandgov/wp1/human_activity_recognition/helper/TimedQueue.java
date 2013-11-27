package eu.liveandgov.wp1.human_activity_recognition.helper;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class that holds samples in a time frame of a given duration d.
 * The time frame ends with the time stamp t of the last sample that was pushed to the queue
 * and starts after the time t - d (exclusively).
 *
 *  Time                    ---(t-d)--------t-->
 *  Samples:                + + | + + + + + + |
 *                                            Last Sample in queue
 *  Contents of Queue:            + + + + + +
 *  Removed samples:        + +
 *
 * Created by cehlen, hartmann on 10/18/13.
 */
public class TimedQueue<V> {
    private final long duration;
    private final Deque<TimeQueueEntry<V>> queue = new LinkedList<TimeQueueEntry<V>>();

    private long maxTime = -1;

    /**
     * Creates a TimedQueue of a given duration
     * @param duration
     */
    public TimedQueue(long duration) {
        this.duration = duration;
    }

    /**
     * Adds the new element to the queue and removes every entry which is older than time-duration
     * @param time The time of the data point
     * @param value The sensor value
     */
    public void push(long time, V value) {
        if (time < maxTime) {
            clear();
            System.out.println("Reseting Window");
            // throw new IllegalArgumentException("time has to be greater than maximum: " + time + " vs " + maxTime);
        }

        TimeQueueEntry<V> entry = new TimeQueueEntry<V>();
        entry.time = time;
        entry.value = value;

        maxTime = time;

        queue.addFirst(entry);
        limitToSize();
    }

    /**
     * Simply clears the whole queue
     */
    public void clear() {
        queue.clear();
    }


    public ArrayList<V> toArrayList() {
        ArrayList<V> list = new ArrayList<V>();
        for(TimeQueueEntry e : queue) {
            list.add((V) e.value);
        }
        return list;
    }

    /**
     * Checks if the queue is too big and removes elements if it has to.
     */
    private void limitToSize() {
        long minTime = maxTime - duration;

        TimeQueueEntry<V> e;
        while( (e = queue.peekLast()) != null ) {
            if (e.time > minTime) { break; }
            else { queue.removeLast(); }
        }
    }

    /**
     * Internal helper class that holds the queue entries.
     * @param <V>
     */
    private static class TimeQueueEntry<V> {
        public long time;
        public V value;
    }

}
