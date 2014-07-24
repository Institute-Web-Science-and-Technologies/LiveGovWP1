package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.serialization.Serialization;

/**
 * <p>Converts a stream of strings into a stream of items by de-serializing them</p>
 * Created by Lukas Härtel on 13.02.14.
 */
public class DeSerializer<Item> extends Pipeline<String, Item> {
    /**
     * The serialization used for de-serializing
     */
    public final Serialization<? extends Item> serialization;

    /**
     * Creates a new instance with the given values
     * @param serialization The serialization used for de-serializing
     */
    public DeSerializer(Serialization<? extends Item> serialization) {
        this.serialization = serialization;
    }

    @Override
    public void push(String item) {
        produce(serialization.deSerialize(item));
    }
}
