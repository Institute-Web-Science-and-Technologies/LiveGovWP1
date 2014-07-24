package eu.liveandgov.wp1.tests;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.ItemSerialization;

/**
 * Created by Lukas Härtel on 05.07.2014.
 */
public class SerializationTest {
    public static void main(String... args){
        Item i = ItemSerialization.ITEM_SERIALIZATION.deSerialize("GSM,1404836061789,\"dev killmenow\",\"in service\"/\"not roaming\"/\"automatic carrier\"/\"MEDION Mobile\"/\"gsm -73\"/\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263;\"unknown\"/\"hsdpa\"/-263");
        String s = ItemSerialization.ITEM_SERIALIZATION.serialize(i);
        System.out.println(i);
        System.out.println(s);
    }
}
