package eu.liveandgov.sensorcollectorv3.connector;

/**
 * Created by hartmann on 10/2/13.
 */
public interface Consumer<T> {
    void push(T m);
}
