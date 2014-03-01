package eu.liveandgov.wp1.pipeline;


import eu.liveandgov.wp1.data.CallbackSet;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public abstract class MultiProducer<Item> {
    /**
     * Event, called when the producer lost its last consumers
     */
    public final CallbackSet<Consumer<? super Item>> empty = new CallbackSet<Consumer<? super Item>>();

    /**
     * Event, called when the producer gains his first consumers
     */
    public final CallbackSet<Consumer<? super Item>> nonEmpty = new CallbackSet<Consumer<? super Item>>();


    /**
     * Consumers that handles the items created by this multi-producer
     */
    private final Set<Consumer<? super Item>> consumers = new CopyOnWriteArraySet<Consumer<? super Item>>();

    /**
     * Returns the current consumers
     */
    public final Set<Consumer<? super Item>> getConsumers() {
        return Collections.unmodifiableSet(consumers);
    }

    /**
     * Adds a consumer, may invoke the event nonEmpty
     */
    public final void addConsumer(Consumer<? super Item> consumer) {
        if (consumers.isEmpty()) {
            nonEmpty.call(consumer);
        }

        consumers.add(consumer);
    }

    /**
     * Removes a consumer, may invoke the event empty
     */
    public final void removeConsumer(Consumer<? super Item> consumer) {
        consumers.remove(consumer);

        if (consumers.isEmpty()) {
            empty.call(consumer);
        }
    }

    /**
     * Hands the given item to the consumers
     */
    protected final void produce(Item item) {
        for (Consumer<? super Item> consumer : consumers) {
            consumer.push(item);
        }
    }
}
