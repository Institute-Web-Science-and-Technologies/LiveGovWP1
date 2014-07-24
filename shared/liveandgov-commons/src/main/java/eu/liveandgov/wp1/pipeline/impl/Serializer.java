package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.serialization.Serialization;

/**
 * <p>Converts a stream of items into a stream of strings by serializing them</p>
 * Created by Lukas Härtel on 13.02.14.
 */
public class Serializer<Item> extends Pipeline<Item, String> {
    /**
     * The serialization used for serializing
     */
    public final Serialization<? super Item> serialization;

    /**
     * Creates a new instance with the given values
     * @param serialization The serialization used for serializing
     */
    public Serializer(Serialization<? super Item> serialization) {
        this.serialization = serialization;
    }

    @Override
    public void push(Item item) {
        produce(serialization.serialize(item));
    }
}
