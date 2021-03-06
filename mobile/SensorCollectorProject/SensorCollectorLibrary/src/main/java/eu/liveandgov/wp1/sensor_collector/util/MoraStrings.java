package eu.liveandgov.wp1.sensor_collector.util;

import java.lang.reflect.Array;
import java.util.Random;

/**
 * <p>Utility functions dealing with strings or generation of them</p>
 * Created by lukashaertel on 30.11.2014.
 */
public class MoraStrings {
    public static final char[] ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    /**
     * Generates a random string with the desired length
     *
     * @param seed   The seed for the random
     * @param length The desired length
     * @return A randomly generated string
     */
    public static String randomAlphanumeric(long seed, int length) {
        Random r = new Random(seed);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++)
            b.append(ALPHANUMERIC[r.nextInt(ALPHANUMERIC.length)]);

        return b.toString();
    }

    /**
     * Generates a random string with the desired length
     *
     * @param length The desired length
     * @return A randomly generated string
     */
    public static String randomAlphanumeric(int length) {
        return randomAlphanumeric(System.nanoTime(), length);
    }

    /**
     * <p>Appends an object, deeply appending arrays</p>
     *
     * @param b The target
     * @param o The object
     */
    public static void appendDeep(StringBuilder b, Object o) {
        if (o == null) {
            b.append((Object) null);
            return;
        }

        if (o.getClass().isArray()) {
            int l = Array.getLength(o);
            b.append("[");
            for (int i = 0; i < l; i++) {
                if (i > 0)
                    b.append(", ");

                // Recursive invokation
                appendDeep(b, Array.get(o, i));
            }
            b.append("]");

            return;
        }

        b.append(o);
    }
}
