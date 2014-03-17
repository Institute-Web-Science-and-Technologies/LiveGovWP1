package eu.liveandgov.wp1.packaging;

import com.google.common.base.Function;

import java.util.Map;

/**
 * <p>Contains methods working on packaging instances</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class Packagings {
    /**
     * This method converts a packaging object into a packaging functor
     *
     * @param packaging The packaging object to convert
     * @param <T>       The type of the input items
     * @return Returns a functor that maps T to Map
     */
    public static <T> Function<T, Map<String, ?>> serialization(final Packaging<T> packaging) {
        return new Function<T, Map<String, ?>>() {
            @Override
            public Map<String, ?> apply(T t) {
                return packaging.pack(t);
            }
        };
    }

    /**
     * This method converts a packaging object into a de-packaging functor
     *
     * @param packaging The packaging object to convert
     * @param <T>       The type of the output items
     * @return Returns a functor that maps Map to T
     */
    public static <T> Function<Map<String, ?>, T> deSerialization(final Packaging<T> packaging) {
        return new Function<Map<String, ?>, T>() {
            @Override
            public T apply(Map<String, ?> map) {
                return packaging.unPack(map);
            }
        };
    }
}
