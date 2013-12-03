package eu.liveandgov.wp1.sensor_miner.connectors;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;

/**
 * Interface for a producer class that allows many consumers to subscribe.
 *
 * Created by hartmann on 11/12/13.
 */
public interface MultiProducer<T> {

    /**
     * Add consumer.
     * It is possible for the same producer to be registered more than once.
     * @param consumer
     */
    public void addConsumer(Consumer<T> consumer);

    /**
     * Returns true if a consumer has been removed, and false if the consumer was not registered.
     * @param consumer
     * @return status
     */
    public boolean removeConsumer(Consumer<T> consumer);

}
