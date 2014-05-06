package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import java.util.ArrayList;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.connectors.MultiProducer;

/**
 * Receives messages from one produces, and publishes them to many consumers.
 *
 * Created by hartmann on 11/12/13.
 */
public class Multiplexer<T> implements Consumer<T>, MultiProducer<T> {

    private ArrayList<Consumer<T>> consumers = new ArrayList<Consumer<T>>();

    @Override
    public void push(T message) {
        for (Consumer<T> c : consumers) {
            c.push(message);
        }
    }

    @Override
    public void addConsumer(Consumer<T> consumer) {
        consumers.add(consumer);
    }

    @Override
    public boolean removeConsumer(Consumer<T> consumer) {
        return consumers.remove(consumer);
    }
}
