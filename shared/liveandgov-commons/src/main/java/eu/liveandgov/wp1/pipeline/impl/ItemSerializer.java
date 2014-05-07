package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * <p>Special case of the serializer where the serialized form of an item is used rather than the serialization</p>
 * <p>The serialized form uses a local backing store to reduce the number of serializations</p>
 * Created by Lukas Härtel on 13.02.14.
 */
public class ItemSerializer extends Pipeline<Item, String> {

    @Override
    public void push(Item item) {
        produce(item.toSerializedForm());
    }
}
