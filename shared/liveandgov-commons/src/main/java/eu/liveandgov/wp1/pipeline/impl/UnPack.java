package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.packaging.Packaging;
import eu.liveandgov.wp1.pipeline.Pipeline;

import java.util.Map;

/**
 * Created by Lukas Härtel on 17.03.14.
 */
public class UnPack<Data> extends Pipeline<Map<String, ?>, Data> {
    /**
     * The packaging used for un-packing
     */
    public final Packaging<? extends Data> packaging;

    /**
     * Creates a new instance with the given values
     *
     * @param packaging The packaging used for un-packing
     */
    public UnPack(Packaging<? extends Data> packaging) {
        this.packaging = packaging;
    }

    @Override
    public void push(Map<String, ?> map) {
        produce(packaging.unPack(map));
    }
}
