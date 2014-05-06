package eu.liveandgov.wp1.sensor_collector.tests.utils;

/**
 * Created by lukashaertel on 13.01.14.
 */
public class StringHelper {
    public static final String pl(String n, int c)
    {
        return c == 1 ? n : n + "s";
    }
}
