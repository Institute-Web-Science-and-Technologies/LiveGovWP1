package eu.liveandgov.wp1.data;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Lukas Härtel on 13.02.14.
 */
public final class CallbackSet<Parameter> {
    private final Set<Callback<? super Parameter>> callbacks = new CopyOnWriteArraySet<Callback<? super Parameter>>();

    public Set<Callback<? super Parameter>> getCallbacks() {
        return Collections.unmodifiableSet(callbacks);
    }

    /**
     * Registers a callback to be called by invoke
     */
    public void register(Callback<? super Parameter> callback) {
        callbacks.add(callback);
    }

    /**
     * Unregisters a callback to be called by invoke
     */
    public void unregister(Callback<? super Parameter> callback) {
        callbacks.remove(callback);
    }

    /**
     * Invokes all callbacks with the provided paramter
     */
    public void invoke(Parameter parameter) {
        for (Callback<? super Parameter> callback : callbacks) {
            callback.call(parameter);
        }
    }
}
