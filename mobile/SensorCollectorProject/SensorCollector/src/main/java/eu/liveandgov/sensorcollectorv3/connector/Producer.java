package eu.liveandgov.sensorcollectorv3.connector;

/**
 * Created by cehlen on 10/19/13.
 */
public class Producer<T> {
    protected Consumer<T> consumer = null;

    public void setConsumer(Consumer<T> c) {
        consumer = c;
    }
}
