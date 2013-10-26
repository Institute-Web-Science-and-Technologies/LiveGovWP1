package eu.liveandgov.sensorcollectorv3.connectors;

/**
 * Abstract class providing Producer and Consumer methods
 *
 * Created by hartmann on 10/25/13.
 */
public abstract class Pipeline<S,T> extends Producer<T> implements Consumer<S> { }
