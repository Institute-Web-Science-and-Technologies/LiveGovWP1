package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;

/**
 * Consumer that drops all messages. Serves as default value in class Producer class.
 * <p/>
 * TODO: It would be favorable to use a singleton here.
 * REM: Seems hard to combine with templating
 * <p/>
 * Created by hartmann on 10/25/13.
 */
public class EmptyConsumer<T> implements Consumer<T> {

    @Override
    public void push(T m) {
        // do nothing
    }

}