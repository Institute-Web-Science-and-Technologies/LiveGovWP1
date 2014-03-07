package eu.liveandgov.wp1.data.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Unit of a data object</p>
 * Created by Lukas Härtel on 08.02.14.
 */
@Retention(RetentionPolicy.CLASS)
public @interface Unit {
    public String value();
}
