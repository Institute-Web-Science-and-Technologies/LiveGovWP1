package eu.liveandgov.wp1.pipeline;

import java.util.Set;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public interface MultiProducer<Item> {
    public Set<Consumer<? super Item>> getConsumers();
}
