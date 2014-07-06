package eu.liveandgov.wp1.tests;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.ItemSerialization;

/**
 * Created by Lukas Härtel on 05.07.2014.
 */
public class SerializationTest {
    public static void main(String... args){
        Item i = ItemSerialization.ITEM_SERIALIZATION.deSerialize("WIFI,10,\"adsa\",\"WLAN-647446\"\"WLAN-647446\"/\"7c:4f:b5:64:74:95\"/2437/-90");
        String s = ItemSerialization.ITEM_SERIALIZATION.serialize(i);
        System.out.println(i);
        System.out.println(s);
    }
}
