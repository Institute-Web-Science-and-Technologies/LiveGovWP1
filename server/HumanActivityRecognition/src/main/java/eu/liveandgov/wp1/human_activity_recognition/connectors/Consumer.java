package eu.liveandgov.wp1.human_activity_recognition.connectors;

/**
 * A consumer accepts messages and performes actions on it.
 *
 * Created by hartmann on 10/2/13.
 */
public interface Consumer<T> {
    /**
     * Push a message to the consumer.
     * This method should be non-blocking.
     *
     * @param message to be handled.
     */
    void push(T message);
}
