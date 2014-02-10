package eu.liveandgov.wp1.serialization.tests;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.implementation.ItemSwitch;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class SerTest {
    public static void main(String[] args) {
        final Item<?> acc = ItemSwitch.ITEM_SWITCH.deSerialize("ACC,1377605748123,9HAD-FEJ3-GE3A-GRKA,0.9813749 0.0021324 0.0142523");
        final Item<?> gps = ItemSwitch.ITEM_SWITCH.deSerialize("GPS,1377605748156,9HAD-FEJ3-GE3A-GRKA,50.32124 25.2453 136.5335");
        final Item<?> wifi = ItemSwitch.ITEM_SWITCH.deSerialize("WIFI,1341244415,wifiUser,\"WiFi AP\"/\"00:12:42\"/2412/-45; \"Another WiFi AP\"/\"33:13:53\"/2437/-56");
        final Item<?> tag = ItemSwitch.ITEM_SWITCH.deSerialize("TAG,1378114981049,ab85d157c5260ebe,\"test tag\"");
        final Item<?> blt = ItemSwitch.ITEM_SWITCH.deSerialize("BLT,1385988380374,bluetoothUser,\"C8:F7:33:B7:B5:B4\"/computer/computer laptop/bonded/\"LAPTOP\"/-46");
        final Item<?> prx = ItemSwitch.ITEM_SWITCH.deSerialize("PRX,1391515681876,mrazr,platform/in proximity/Anschützstraße");
        final Item<?> wtn = ItemSwitch.ITEM_SWITCH.deSerialize("WTN,1391515649824,mrazr,platform/40981/Anschützstraße");


        System.out.println(acc);
        System.out.println(ItemSwitch.ITEM_SWITCH.serialize(acc));
        System.out.println(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(acc)));
        System.out.println();

        assert acc.equals(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(acc)));

        System.out.println(gps);
        System.out.println(ItemSwitch.ITEM_SWITCH.serialize(gps));
        System.out.println(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(gps)));
        System.out.println();

        assert gps.equals(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(gps)));

        System.out.println(wifi);
        System.out.println(ItemSwitch.ITEM_SWITCH.serialize(wifi));
        System.out.println(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(wifi)));
        System.out.println();

        assert wifi.equals(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(wifi)));

        System.out.println(tag);
        System.out.println(ItemSwitch.ITEM_SWITCH.serialize(tag));
        System.out.println(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(tag)));
        System.out.println();

        assert tag.equals(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(tag)));

        System.out.println(blt);
        System.out.println(ItemSwitch.ITEM_SWITCH.serialize(blt));
        System.out.println(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(blt)));
        System.out.println();

        assert blt.equals(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(blt)));

        System.out.println(prx);
        System.out.println(ItemSwitch.ITEM_SWITCH.serialize(prx));
        System.out.println(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(prx)));
        System.out.println();

        assert prx.equals(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(prx)));

        System.out.println(wtn);
        System.out.println(ItemSwitch.ITEM_SWITCH.serialize(wtn));
        System.out.println(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(wtn)));
        System.out.println();

        assert wtn.equals(ItemSwitch.ITEM_SWITCH.deSerialize(ItemSwitch.ITEM_SWITCH.serialize(wtn)));

    }
}
