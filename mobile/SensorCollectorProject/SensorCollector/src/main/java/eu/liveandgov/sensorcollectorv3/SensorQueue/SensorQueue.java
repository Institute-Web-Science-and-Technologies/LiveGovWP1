package eu.liveandgov.sensorcollectorv3.SensorQueue;

import eu.liveandgov.sensorcollectorv3.Monitor.Monitorable;

/**
 * Created by hartmann on 10/2/13.
 */
public interface SensorQueue extends Monitorable {
    /**
     * Push message m to the queue.
     * Drop m if the queue is full.
     *
     * @param message
     */
    void push(String message);

    /**
     * Pull message from queue.
     * Return null if queue is empty.
     *
     * @return message
     */
    String pull();

    /**
     * Pull message from queue.
     * Block if queue is empty
     *
     * @return message
     */
    String blockingPull();

}
