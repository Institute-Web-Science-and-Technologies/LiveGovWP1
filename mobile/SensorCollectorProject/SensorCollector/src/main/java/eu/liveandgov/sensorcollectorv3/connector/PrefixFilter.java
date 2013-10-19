package eu.liveandgov.sensorcollectorv3.connector;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cehlen on 10/18/13.
 */
public class PrefixFilter extends Producer<String> implements Consumer<String> {
    private static final String LOG_TAG = "PrefixFilter";
    private List<String> filterList;

    public PrefixFilter(Consumer<String> c) {
        this.setConsumer(c);
        this.filterList = new ArrayList<String>();
    }

    public void addFilter(String filter) {
        filterList.add(filter);
    }

    @Override
    public void push(String m) {
        for(String filter : filterList) {
            if(m.startsWith(filter)) {
                consumer.push(m);
            }
        }
    }
}
