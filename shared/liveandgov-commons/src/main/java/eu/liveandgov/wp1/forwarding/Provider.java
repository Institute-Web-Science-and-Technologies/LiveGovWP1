package eu.liveandgov.wp1.forwarding;

/**
 * Created by Lukas Härtel on 09.05.2014.
 */
public interface Provider {

    public boolean contains(String component);

    public Object provide(String component);
}
