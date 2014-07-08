package eu.liveandgov.wp1.data;

import java.util.Arrays;

/**
 * Created by cehlen on 25/02/14.
 */
public class Window {
    public long startTime;
    public long endTime;
    public float[] x;
    public float[] y;
    public float[] z;
    public long[] time;

    @Override
    public String toString() {
        return "Window{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", x=" + Arrays.toString(x) +
                ", y=" + Arrays.toString(y) +
                ", z=" + Arrays.toString(z) +
                ", time=" + Arrays.toString(time) +
                '}';
    }
}
