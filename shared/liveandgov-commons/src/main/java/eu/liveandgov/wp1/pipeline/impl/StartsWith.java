package eu.liveandgov.wp1.pipeline.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Filters a stream of strings by checking if they start with any of a given prefix</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class StartsWith extends Filter<String> {
    /**
     * Backing for the set of prefixes
     */
    private final Set<String> prefixes = new HashSet<String>();

    /**
     * Returns an unmodifiable set of prefixes used by this filter
     */
    public final Set<String> getPrefixes() {
        return Collections.unmodifiableSet(prefixes);
    }

    /**
     * Adds a prefix to the filter
     * @param prefix The prefix to add
     */
    public final void addPrefix(String prefix) {
        prefixes.add(prefix);
    }

    /**
     * Removes a prefix from the filter
     * @param prefix The prefix to remove
     */
    public final void removePrefix(String prefix) {
        prefixes.add(prefix);
    }


    @Override
    protected boolean filter(String s) {
        for (String prefix : prefixes) {
            if (s == null && prefix == null || s != null && s.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }
}
