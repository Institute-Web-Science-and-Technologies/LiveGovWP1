package eu.liveandgov.wp1.helper;

import eu.liveandgov.wp1.pipeline.Consumer;

import java.util.*;

/**
 * Created by Lukas Härtel on 08.07.2014.
 */
public abstract class TimedQueue2<V> implements Consumer<V> {

    private final SortedMap<Long, V> queue = new TreeMap<Long, V>();

    private final long maxDifference;

    protected abstract long getTime(V v);

    protected TimedQueue2(long maxDifference) {
        this.maxDifference = maxDifference;
    }

    @Override
    public void push(V v) {
        queue.put(getTime(v), v);

        relimit();
    }

    private void relimit() {
        while (!queue.isEmpty()) {
            long et = queue.lastKey();
            long st = queue.firstKey();
            long diff = et - st;

            if (diff <= maxDifference)
                break;

            queue.remove(queue.firstKey());
        }
    }

    public void clear() {
        queue.clear();
    }

    public Collection<V> items() {
        return Collections.unmodifiableCollection(queue.values());
    }


    @Override
    public String toString() {
        return "TimedQueue2{" +
                "queue=" + queue +
                ", maxDifference=" + maxDifference +
                '}';
    }
}
