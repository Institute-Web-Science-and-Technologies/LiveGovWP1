package eu.liveandgov.wp1.data;

/**
 * Created by Lukas Härtel on 13.02.14.
 */
public interface Callback<Parameter> {
    public void call(Parameter parameter);
}
