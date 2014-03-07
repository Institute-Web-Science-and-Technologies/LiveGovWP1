package eu.liveandgov.wp1.data;

import java.util.*;

/**
 * Created by Lukas Härtel on 02.03.14.
 */
public class Diagnostics<E> {
    private final NavigableMap<Double, Set<E>> results;

    public Diagnostics() {
        results = new TreeMap<Double, Set<E>>();
    }

    public void put(long startNanoseconds, long endNanoseconds, E e) {
        final double time = (endNanoseconds - startNanoseconds) / 1.0e+6;
        Set<E> d = results.get(time);
        if (d == null) {
            results.put(time, d = new HashSet<E>());
        }

        d.add(e);
    }

    public SortedSet<Double> times() {
        return Collections.unmodifiableSortedSet(results.navigableKeySet());
    }

    public Set<E> get(double time) {
        final Set<E> d = results.get(time);

        if (d == null)
            return Collections.emptySet();
        else
            return Collections.unmodifiableSet(d);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("{");

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
