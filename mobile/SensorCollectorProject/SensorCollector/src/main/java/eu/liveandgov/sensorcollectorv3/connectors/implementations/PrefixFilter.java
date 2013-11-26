package eu.liveandgov.sensorcollectorv3.connectors.implementations;

import java.util.ArrayList;
import java.util.List;

/**
 * Pipeline class, that filters String messages by Prefix.
 *
 * Only messages are passed, that have one of the provided prefixes.
 *
 * Created by cehlen on 10/18/13.
 */
public class PrefixFilter extends eu.liveandgov.wp1.feature_pipeline.connectors.Producer<String> implements eu.liveandgov.wp1.feature_pipeline.connectors.Consumer<String> {
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
