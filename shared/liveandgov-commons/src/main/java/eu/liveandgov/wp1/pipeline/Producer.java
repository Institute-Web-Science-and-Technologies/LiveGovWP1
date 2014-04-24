package eu.liveandgov.wp1.pipeline;

/**
 * <p>Producer has one destination consumer it can write items to</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public abstract class Producer<Item> {
    /**
     * Consumer that handles the items created by this producer
     */
    private Consumer<? super Item> consumer = Consumer.EMPTY_CONSUMER;

    /**
     * Returns the current consumer
     */
    public final Consumer<? super Item> getConsumer() {
        return consumer;
    }

    /**
     * Sets the consumer for this producer
     *
     * @param consumer The new consumer
     */
    public void setConsumer(Consumer<? super Item> consumer) {
        // Handle null-parameters
        if (consumer == null) consumer = Consumer.EMPTY_CONSUMER;

        this.consumer = consumer;
    }

    /**
     * Hands the given item to the consumer
     */
    protected final void produce(Item item) {
        consumer.push(item);
    }
}
