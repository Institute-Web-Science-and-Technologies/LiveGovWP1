package eu.liveandgov.wp1.pipeline;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public class MultiplexerConsumer<Item> implements Consumer<Item> {
    private final Set<Consumer<? super Item>> consumers;

    public Set<Consumer<? super Item>> getConsumers() {
        return consumers;
    }

    public MultiplexerConsumer() {
        consumers = new HashSet<Consumer<? super Item>>();
    }

    @Override
    public void push(Item item) {
        for(Consumer<? super Item> consumer : consumers){
            consumer.push(item);
        }
    }
}
