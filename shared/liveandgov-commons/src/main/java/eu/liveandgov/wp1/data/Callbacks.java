package eu.liveandgov.wp1.data;

import com.google.common.base.Function;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <p>Utility functions on callbacks</p>
 * Created by Lukas Härtel on 13.02.14.
 */
public class Callbacks {
    /**
     * Converts a runnable into a callback
     */
    public static Callback<Object> convert(final Runnable runnable) {
        return new Callback<Object>() {
            @Override
            public void call(Object parameter) {
                runnable.run();
            }
        };
    }

    /**
     * Converts a function into a callback
     */
    public static <T> Callback<T> convert(final Function<? super T, ?> function) {
        return new Callback<T>() {
            @Override
            public void call(T parameter) {
                function.apply(parameter);
            }
        };
    }

    /**
     * Creates a sequential composition of the callbacks given
     *
     * @param a   The callback to be invoked first
     * @param b   The callback to be invoked second
     * @param <T> The Common subtype for the callbacks
     * @return Returns a new callback
     */
    public static <T> Callback<T> sequence(final Callback<? super T> a, final Callback<? super T> b) {
        return new Callback<T>() {
            @Override
            public void call(T t) {
                a.call(t);
                b.call(t);
            }
        };
    }

    /**
     * Creates a parallel composition of the callbacks given
     *
     * @param a   One of the callbacks to be invoked
     * @param b   One of the callbacks to be invoked
     * @param <T> The Common subtype for the callbacks
     * @return Returns a new callback
     */
    public static <T> Callback<T> parallel(final ExecutorService executor, final Callback<? super T> a, final Callback<? super T> b) {
        return new Callback<T>() {
            @Override
            public void call(final T t) {
                final Future<?> fa = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        a.call(t);
                    }
                });
                final Future<?> fb = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        b.call(t);
                    }
                });
                try {
                    fa.get();
                    fb.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
