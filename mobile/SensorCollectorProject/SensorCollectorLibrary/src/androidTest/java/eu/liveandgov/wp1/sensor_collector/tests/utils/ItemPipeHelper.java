package eu.liveandgov.wp1.sensor_collector.tests.utils;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;

/**
 * Created by lukashaertel on 01.03.14.
 */
public class ItemPipeHelper extends PipeHelper<String> {
    public final Consumer<Item> itemNode = new Consumer<Item>() {
        @Override
        public void push(Item item) {
            ItemPipeHelper.this.push(item.toSerializedForm());
        }
    };
}
