package eu.liveandgov.wp1.pipeline;


import eu.liveandgov.wp1.data.CallbackSet;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private final List<Consumer<? super Item>> consumers = new CopyOnWriteArrayList<Consumer<? super Item>>();

    /**
     * Returns the current consumers
     */
    public final List<Consumer<? super Item>> getConsumers() {
        return Collections.unmodifiableList(consumers);
    }

    /**
     * Adds a consumer, may invoke the event nonEmpty
     */
    public final void addConsumer(Consumer<? super Item> consumer) {
        if (consumers.isEmpty()) {
            nonEmpty.invoke(consumer);
        }

        consumers.add(consumer);
    }

    /**
     * Removes a consumer, may invoke the event empty
     */
    public final void removeConsumer(Consumer<? super Item> consumer) {
        consumers.remove(consumer);

        if (consumers.isEmpty()) {
            empty.invoke(consumer);
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
