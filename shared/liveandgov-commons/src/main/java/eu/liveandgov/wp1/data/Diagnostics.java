package eu.liveandgov.wp1.data;

import eu.liveandgov.wp1.util.LocalBuilder;

import java.util.*;

/**
 * <p>Maps items to corresponding times and sorts them ascending</p>
 * Created by Lukas Härtel on 02.03.14.
 */
public class Diagnostics<E> {
    /**
     * Set of results
     */
    private final NavigableMap<Double, Set<E>> results;

    /**
     * Creates a new instance
     */
    public Diagnostics() {
        results = new TreeMap<Double, Set<E>>();
    }

    /**
     * Put the item with the given start and end-time
     *
     * @param startNanoseconds The start time
     * @param endNanoseconds   The end time
     * @param e                The item
     */
    public void put(long startNanoseconds, long endNanoseconds, E e) {
        final double time = (endNanoseconds - startNanoseconds) / 1.0e+6;
        Set<E> d = results.get(time);
        if (d == null) {
            results.put(time, d = new HashSet<E>());
        }

        d.add(e);
    }

    /**
     * Returns the sorted set of the times
     */
    public SortedSet<Double> times() {
        return Collections.unmodifiableSortedSet(results.navigableKeySet());
    }

    /**
     * Gets all items for a given time
     *
     * @param time The time to check
     * @return Returns an unmodifiable set to this items
     */
    public Set<E> get(double time) {
        final Set<E> d = results.get(time);

        if (d == null)
            return Collections.emptySet();
        else
            return Collections.unmodifiableSet(d);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();

        stringBuilder.append('{');

        boolean sep0 = false;
        for (Map.Entry<Double, Set<E>> entry : results.entrySet()) {
            if (sep0) stringBuilder.append("; ");
            stringBuilder.append(entry.getKey());
            stringBuilder.append("ms: ");

            boolean sep1 = false;
            for (E e : entry.getValue()) {
                if (sep1) stringBuilder.append(", ");
                stringBuilder.append(e);
                sep1 = true;
            }

            sep0 = true;
        }

        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
