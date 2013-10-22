package eu.liveandgov.sensorcollectorv3.har;

import android.util.Log;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;

/**
 * Created by cehlen on 10/18/13.
 */
public class HAR implements Consumer<String> {
    private static final String LOG_TAG = "HAR";
    private TimedQueue queue;
    private long windowSize;
    private long overlap;
    private long windowStart = -1;
    private long windowEnd = -1;

    public HAR(long windowSize, long overlap) {
        this.windowSize = windowSize;
        this.overlap = overlap;

        queue = new TimedQueue(windowSize);
    }

    @Override
    public void push(String m) {
        long time = getTimeFromValue(m);
        // Check if we want to get the window
        if(time > windowEnd) {
            //String[] window = queue.toArray();
        }
        queue.push(time, m);
        if(windowStart < 0) {
            windowStart = time;
            windowEnd = time + windowSize;
        }


    }


    private long getTimeFromValue(String value) {
        int first = value.indexOf(",");
        int second = value.indexOf(",", first+1);
        if(first == -1 || second == -1) {
            Log.i(LOG_TAG, "Value does not fit our format!");
            return -1;
        }
        return Long.parseLong(value.substring(first+1, second-1));
    }
}
