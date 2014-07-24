package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.MultiPipeline;

/**
 * <p>This pipeline element provides a mutable consumer-set. On receiving an item on its input, it distributes it to all consumers in this set.</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class Multiplexer<Item> extends MultiPipeline<Item, Item> {
    @Override
    public void push(Item item) {
        produce(item);
    }
}
