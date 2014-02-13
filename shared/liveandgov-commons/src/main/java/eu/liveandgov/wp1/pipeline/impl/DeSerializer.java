package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.serialization.Serialization;

/**
 * Created by Lukas Härtel on 13.02.14.
 */
public class DeSerializer<Item> extends Pipeline<String, Item> {
    public final Serialization<? extends Item> serialization;

    public DeSerializer(Serialization<? extends Item> serialization) {
        this.serialization = serialization;
    }

    @Override
    public void push(String item) {
        produce(serialization.deSerialize(item));
    }
}
