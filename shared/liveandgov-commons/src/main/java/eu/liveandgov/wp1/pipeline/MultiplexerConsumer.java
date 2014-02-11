package eu.liveandgov.wp1.pipeline;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Replaces the old MultiProducer</p>
 * <p>This pipeline element provides a mutable consumer-set. On receiving an item on its input, it distributes
 * it to all consumers in this set.</p>
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
        for (Consumer<? super Item> consumer : consumers) {
            consumer.push(item);
        }
    }
}
