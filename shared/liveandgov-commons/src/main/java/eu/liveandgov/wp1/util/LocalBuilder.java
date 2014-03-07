package eu.liveandgov.wp1.util;


import eu.liveandgov.wp1.data.Tuple;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Local builder provides a caller with an existing string builder, nested string builder acquisitions are managed
 * by cycling through a larger set of builders</p>
 * Created by Lukas Härtel on 07.03.14.
 */
public class LocalBuilder {
    /**
     * Maximum nesting of string builders
     */
    public static final int MAXIMUM_NESTED = 16;

    /**
     * Initial capacity of the string builders
     */
    public static final int INITIAL_CAPACITY = 256;

    /**
     * Creates one thread local instance of a string builder
     */
    private static ThreadLocal<StringBuilder> createInstance() {
        return new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                final StringBuilder stringBuilder = new StringBuilder(INITIAL_CAPACITY);

                return stringBuilder;
            }

            @Override
            public StringBuilder get() {
                final StringBuilder result = super.get();
                result.setLength(0);
                return result;
            }

        };
    }

    /**
     * Creates the set of local string builders
     */
    @SuppressWarnings("unchecked")
    private static ThreadLocal<StringBuilder>[] createInstances() {

        final ThreadLocal[] result = new ThreadLocal[MAXIMUM_NESTED];
        for (int i = 0; i < MAXIMUM_NESTED; i++) {
            result[i] = createInstance();
        }

        return result;
    }

    /**
     * The set of local string builders
     */
    private static final ThreadLocal<StringBuilder>[] instance = createInstances();

    /**
     * Atomic integer that represents the instance of the string builder that is to be returned
     * for the current caller
     */
    private static final AtomicInteger taker = new AtomicInteger();

    /**
     * Acquires one string builder
     */
    public static StringBuilder acquireBuilder() {
        // Resolve wrapping of the integer
        final long at = (long) taker.incrementAndGet() - (long) Integer.MIN_VALUE;

        // Wrap into instancearray and acquire
        return instance[(int) (at % instance.length)].get();
    }

}
