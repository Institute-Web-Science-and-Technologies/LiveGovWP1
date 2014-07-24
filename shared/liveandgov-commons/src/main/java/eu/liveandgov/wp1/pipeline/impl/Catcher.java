package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Catches a runtime exception during the invokation of the subsequent pipeline</p>
 * Created by Lukas Härtel on 10.03.14.
 */
public class Catcher<Item> extends Pipeline<Item, Item> {
    private final Set<Class<? extends RuntimeException>> caught = new HashSet<Class<? extends RuntimeException>>();

    public Set<Class<? extends RuntimeException>> getCaught() {
        return Collections.unmodifiableSet(caught);
    }

    public void registerException(Class<? extends RuntimeException> exception) {
        caught.add(exception);
    }

    public void unregisterException(Class<? extends RuntimeException> exception) {
        caught.remove(exception);
    }

    @Override
    public void push(Item item) {
        try {
            produce(item);
        } catch (RuntimeException e) {
            if (!caught.contains(e.getClass()))
                throw e;
        }
    }
}
