package eu.liveandgov.wp1.serialization;

import com.google.common.base.Function;

/**
 * <p>Contains methods working on serialization instances</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class Serializations {
    /**
     * This method converts a serialization object into a serialization functor
     *
     * @param serialization The serialization object to convert
     * @param <T>           The type of the input items
     * @return Returns a functor that maps T to String
     */
    public static <T> Function<T, String> serialization(final Serialization<T> serialization) {
        return new Function<T, String>() {
            @Override
            public String apply(T t) {
                return serialization.serialize(t);
            }
        };
    }

    /**
     * This method converts a serialization object into a de-serialization functor
     *
     * @param serialization The serialization object to convert
     * @param <T>           The type of the output items
     * @return Returns a functor that maps String to T
     */
    public static <T> Function<String, T> deSerialization(final Serialization<T> serialization) {
        return new Function<String, T>() {
            @Override
            public T apply(String s) {
                return serialization.deSerialize(s);
            }
        };
    }
}
