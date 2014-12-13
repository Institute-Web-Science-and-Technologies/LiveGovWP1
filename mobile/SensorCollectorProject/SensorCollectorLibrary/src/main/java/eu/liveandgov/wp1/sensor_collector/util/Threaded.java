package eu.liveandgov.wp1.sensor_collector.util;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation denotes that a new thread is to be used for the provided object
 * </p>
 * <p>
 * Created on 13.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
@BindingAnnotation
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Threaded {
}
