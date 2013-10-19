package eu.liveandgov.sensorcollectorv3.har;

import android.util.Log;

import eu.liveandgov.sensorcollectorv3.Connector.Consumer;

/**
 * Created by cehlen on 10/18/13.
 */
public class HAR implements Consumer {
    private static final String LOG_TAG = "HAR";



    @Override
    public void push(String m) {
        
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
