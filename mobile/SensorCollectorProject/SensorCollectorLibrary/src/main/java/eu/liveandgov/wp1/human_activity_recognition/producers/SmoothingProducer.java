package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;

/**
 * Smoothing Producer
 *
 * smooth a stream of objects, in the sense that the last N objects
 * have to be equal for the next one to be emitted.
 *
 * Created by Heinrich Hartmann, Mar. 8. 2014
 */
public class SmoothingProducer extends Producer<String> implements Consumer<String> {

    private String[] cache;
    private int index;

    public SmoothingProducer(int length){
        if(length < 1) throw new IllegalArgumentException();

        cache = new String[length];
        index = 0;
    }

    @Override
    public void push(String message) {
        addToCache(message);
        if ( isAllEqual() ) {
            consumer.push(message);
        } else {
            consumer.push("unknown");
        }
    }

    private boolean isAllEqual() {
        String cmp = cache[0];
        for ( int i = 0; i< cache.length; i++ ){
            if ( ! cmp.equals(cache[i]) ){
                return false;
            }
        }
        return true;
    }

    private void addToCache(String message) {
        cache[index] = message;
        index = (index + 1) % cache.length;
    }
}
