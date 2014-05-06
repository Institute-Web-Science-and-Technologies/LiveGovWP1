package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import java.util.ArrayList;
import java.util.List;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;

/**
 * Pipeline class, that filters String messages by Prefix.
 *
 * Only messages are passed, that have one of the provided prefixes.
 *
 * Created by cehlen on 10/18/13.
 */
public class PrefixFilter extends Producer<String> implements Consumer<String> {
    private static final String LOG_TAG = "PrefixFilter";
    private List<String> filterList;

    public PrefixFilter() {
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

                // avoid messages getting pushed more than once
                return;
            }
        }
    }
}
