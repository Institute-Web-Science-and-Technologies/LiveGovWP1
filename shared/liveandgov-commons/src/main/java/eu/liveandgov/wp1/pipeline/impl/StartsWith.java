package eu.liveandgov.wp1.pipeline.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class StartsWith extends Filter<String> {
    private final Set<String> prefixes = new HashSet<String>();

    public final Set<String> getPrefixes() {
        return Collections.unmodifiableSet(prefixes);
    }

    public final void addPrefix(String prefix) {
        prefixes.add(prefix);
    }

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
