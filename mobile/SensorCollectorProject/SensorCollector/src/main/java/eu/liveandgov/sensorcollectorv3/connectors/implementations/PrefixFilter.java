package eu.liveandgov.sensorcollectorv3.connectors.implementations;

import java.util.ArrayList;
import java.util.List;

import eu.liveandgov.sensorcollectorv3.connectors.Pipeline;

/**
 * Pipeline class, that filters String messages by Prefix.
 *
 * Only messages are passed, that have one of the provided prefixes.
 *
 * Created by cehlen on 10/18/13.
 */
public class PrefixFilter extends Pipeline<String, String> {
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
            }
        }
    }
}
