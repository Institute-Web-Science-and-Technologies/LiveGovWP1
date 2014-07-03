package eu.liveandgov.wp1.forwarding;

/**
 * Created by Lukas Härtel on 09.05.2014.
 */
public interface Receiver {
    public void receive(String component, Object item);
}
