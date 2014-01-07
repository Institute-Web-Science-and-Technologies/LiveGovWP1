package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.TaggedWindow;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 07/01/14
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class PrintProducer<T> extends Producer<T> implements Consumer<T> {

    private String prefix;

    public PrintProducer(String prefix) {
        this.prefix = prefix;
    }

    public void push(T message) {
        System.out.println(prefix + " " + message.toString());
        consumer.push(message);
    }

    public void clear() {
        consumer.clear();
    }
}
