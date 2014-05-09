package eu.liveandgov.wp1.forwarding;

/**
 * <p>Base  class for forwarding components to a function</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public interface Forwarding<Data> {
    /**
     * Forwards the components to the function
     *
     * @param data   The data to forward
     * @param target The target function
     */
    public void forward(Data data, Receiver target);
}
