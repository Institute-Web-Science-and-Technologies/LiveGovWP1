package eu.liveandgov.sensorcollectorv3.har;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by cehlen on 10/18/13.
 */
public class TimedQueue<V> {
    private long size;
    private Queue<TimeQueueEntry<V>> queue;

    public TimedQueue(long size) {
        this.size = size;
        queue = new LinkedList<TimeQueueEntry<V>>();
    }

    /**
     * Adds the new element to the queue and removes every entry which is older than time-size
     * @param time The time of the data point
     * @param value The sensor value
     */
    public void push(long time, V value) {
        TimeQueueEntry<V> entry = new TimeQueueEntry<V>();
        entry.time = time;
        entry.value = value;

        queue.add(entry);
        limitToSize(time);
    }


    public ArrayList<V> toArrayList() {
        ArrayList<V> list = new ArrayList<V>();
        for(TimeQueueEntry e : queue) {
            list.add((V)e.value);
        }
        return list;
    }

    /**
     * Checks if the queue is too big and removes elements if it has to.
     * @param time The time from which we subtract the queue size
     */
    private void limitToSize(long time) {
        long minTime = time - size;
        while(queue.peek()!=null && queue.peek().time <= minTime) {
            queue.poll();
        }
    }

}
