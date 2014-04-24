package eu.liveandgov.wp1.data;

/**
 * <p>SAM representing the call of a void method with one parameter</p>
 * Created by Lukas Härtel on 13.02.14.
 *
 * @param <Parameter> Type of the one parameter of the method
 */
public interface Callback<Parameter> {
    /**
     * Method taking one parameter
     *
     * @param parameter The Parameter to the method
     */
    public void call(Parameter parameter);
}
