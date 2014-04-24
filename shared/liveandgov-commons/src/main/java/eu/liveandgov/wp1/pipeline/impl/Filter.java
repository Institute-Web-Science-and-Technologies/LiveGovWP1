package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * <p>Abstract class filtering a stream based on a predicate</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public abstract class Filter<Item> extends Pipeline<Item, Item> {
    /**
     * Filter on the items
     *
     * @param item The item to check
     * @return True if the item should be let through
     */
    protected abstract boolean filter(Item item);

    @Override
    public void push(Item o) {
        if (filter(o)) {
            produce(o);
        }
    }
}
