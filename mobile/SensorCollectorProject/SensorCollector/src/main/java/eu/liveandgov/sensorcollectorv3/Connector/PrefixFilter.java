package eu.liveandgov.sensorcollectorv3.Connector;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cehlen on 10/18/13.
 */
public class PrefixFilter implements Consumer {
    private static final String LOG_TAG = "PrefixFilter";
    private List<String> filterList;
    private Consumer consumer;

    public PrefixFilter(Consumer c) {
        this.consumer = c;
        this.filterList = new ArrayList<String>();
    }

    public void addFilter(String filter) {
        filterList.add(filter);
    }

    @Override
    public void push(String m) {
        for(String filter : filterList) {
            Log.i(LOG_TAG, "Filter " + filter + " MSG: " + m);
            if(m.startsWith(filter)) {

                consumer.push(m);
            }
        }
    }
}
