package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.annotations.Optional;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * <p>Surrounds the string representation provided by to string with an optional prefix or optional suffix</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class Surround extends Pipeline<Object, String> {
    /**
     * The prefix or null if no prefix desired
     */
    @Optional
    public final String prefix;

    /**
     * The suffix or null if no suffix desired
     */
    @Optional
    public final String suffix;

    public Surround(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void push(Object o) {
        if (prefix == null && suffix == null)
            produce(o.toString());
        else {
            final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();

            if (prefix != null) stringBuilder.append(prefix);
            stringBuilder.append(o);
            if (suffix != null) stringBuilder.append(suffix);

            produce(stringBuilder.toString());
        }
    }
}
