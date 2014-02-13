package eu.liveandgov.wp1.data;

import com.google.common.base.Function;

/**
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
}
