package eu.liveandgov.wp1.pipeline.implementations;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class StartsWithPipeline extends PredicatePipeline<String> {
    private final Set<String> prefixes;

    public Set<String> getPrefixes() {
        return prefixes;
    }

    public StartsWithPipeline() {
        prefixes = new HashSet<String>();
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
