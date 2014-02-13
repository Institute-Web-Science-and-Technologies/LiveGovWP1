package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.serialization.Serialization;

/**
 * Created by Lukas Härtel on 13.02.14.
 */
public class Serializer<Item> extends Pipeline<Item, String> {
    public final Serialization<? super Item> serialization;

    public Serializer(Serialization<? super Item> serialization) {
        this.serialization = serialization;
    }

    @Override
    public void push(Item item) {
        produce(serialization.serialize(item));
    }
}
