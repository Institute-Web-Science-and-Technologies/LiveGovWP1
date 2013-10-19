package eu.liveandgov.sensorcollectorv3.connector;

/**
 * Created by cehlen on 10/19/13.
 */
public abstract class Producer<T> {
    protected Consumer<T> consumer;

    public void setConsumer(Consumer<T> c) {
        consumer = c;
    }
}
