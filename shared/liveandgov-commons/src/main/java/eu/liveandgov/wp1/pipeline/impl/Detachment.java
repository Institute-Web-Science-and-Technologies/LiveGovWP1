package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;

import java.util.concurrent.Executor;

/**
 * <p>The detachment element uses a given executor to detach the input items calling thread from the continuation</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class Detachment<Item> extends Pipeline<Item, Item> {
    /**
     * The executor detaching the streams by execute
     */
    public final Executor executor;

    /**
     * Creates a new instance with the given values
     * @param executor The executor detaching the streams by execute
     */
    public Detachment(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void push(final Item item) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                produce(item);
            }
        });
    }
}
