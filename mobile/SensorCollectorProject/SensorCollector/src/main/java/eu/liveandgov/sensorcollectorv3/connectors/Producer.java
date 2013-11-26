package eu.liveandgov.sensorcollectorv3.connectors;

import eu.liveandgov.sensorcollectorv3.connectors.implementations.EmptyConsumer;

/**
 * Producer objects register a consumer, that provided a push method.
 *
 * To be extended by an implementing class.
 *
 * @author cehlen, hartmann
 */
public class Producer<T> {
    // Initialize an EmptyConsumer, so that push method can be called, without Exception.
    protected Consumer<T> consumer = new EmptyConsumer<T>();

    public void setConsumer(Consumer<T> c) {
        consumer = c;
    }
}
