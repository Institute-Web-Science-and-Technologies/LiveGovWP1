package eu.liveandgov.wp1.data;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>Set of callbacks that may be registered, unregistered or invoked</p>
 * Created by Lukas Härtel on 13.02.14.
 *
 * @param <Parameter> Type of the one parameter of the methods
 */
public final class CallbackSet<Parameter> implements Callback<Parameter> {
    /**
     * Store of all callbacks
     */
    private final Set<Callback<? super Parameter>> callbacks = new CopyOnWriteArraySet<Callback<? super Parameter>>();

    /**
     * Returns an unmodifiable set of the callbacks
     */
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
     * Invokes all callbacks with the provided parameter
     */
    @Override
    public void call(Parameter parameter) {
        for (Callback<? super Parameter> callback : callbacks) {
            callback.call(parameter);
        }
    }
}
