package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * <p>Streams the incoming objects directly to the output</p>
 * Created by Lukas Härtel on 13.02.14.
 */
public class Identity<Item> extends Pipeline<Item, Item> {
    @Override
    public void push(Item item) {
        produce(item);
    }
}
