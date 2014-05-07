package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.packaging.Packaging;
import eu.liveandgov.wp1.pipeline.Pipeline;

import java.util.Map;

/**
 * Created by Lukas Härtel on 17.03.14.
 */
public class Pack<Data> extends Pipeline<Data, Map<String, ?>> {
    /**
     * The packaging used for packing
     */
    public final Packaging<? super Data> packaging;

    /**
     * Creates a new instance with the given values
     *
     * @param packaging The packaging used for packing
     */
    public Pack(Packaging<? super Data> packaging) {
        this.packaging = packaging;
    }

    @Override
    public void push(Data data) {
        produce(packaging.pack(data));
    }
}
