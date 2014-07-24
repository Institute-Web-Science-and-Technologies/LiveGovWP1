package eu.liveandgov.wp1.pipeline;

import com.google.common.base.Function;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <p>Utility functions on consumers</p>
 * Created by Lukas Härtel on 13.02.14.
 */
public class Consumers {
    /**
     * Converts a runnable into a consumer
     */
    public static Consumer<Object> convert(final Runnable runnable) {
        return new Consumer<Object>() {
            @Override
            public void push(Object parameter) {
                runnable.run();
            }
        };
    }

    /**
     * Converts a function into a consumer
     */
    public static <T> Consumer<T> convert(final Function<? super T, ?> function) {
        return new Consumer<T>() {
            @Override
            public void push(T parameter) {
                function.apply(parameter);
            }
        };
    }

    /**
     * Creates a sequential composition of the consumers given
     *
     * @param a   The consumer to be supplied first
     * @param b   The consumer to be supplied second
     * @param <T> The Common subtype for the consumers
     * @return Returns a new consumer
     */
    public static <T> Consumer<T> sequence(final Consumer<? super T> a, final Consumer<? super T> b) {
        return new Consumer<T>() {
            @Override
            public void push(T t) {
                a.push(t);
                b.push(t);
            }
        };
    }

    /**
     * Creates a parallel composition of the consumers given
     *
     * @param a   One of the consumers to be supplied
     * @param b   One of the consumers to be supplied
     * @param <T> The Common subtype for the consumers
     * @return Returns a new consumer
     */
    public static <T> Consumer<T> parallel(final ExecutorService executor, final Consumer<? super T> a, final Consumer<? super T> b) {
        return new Consumer<T>() {
            @Override
            public void push(final T t) {
                final Future<?> fa = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        a.push(t);
                    }
                });
                final Future<?> fb = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        b.push(t);
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
