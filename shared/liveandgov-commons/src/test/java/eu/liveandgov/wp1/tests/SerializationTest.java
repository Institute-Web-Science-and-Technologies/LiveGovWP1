package eu.liveandgov.wp1.tests;

import com.google.common.base.Strings;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.ItemSerialization;

/**
 * Created by Lukas Härtel on 05.07.2014.
 */
public class SerializationTest {
    public static void main(String... args){

        /**
         ACTG,1408450306084,"HH","tilting" 100
         ACT,1408450307486,"HH","standing"
         ACT,1408450308466,"HH","standing"
         ACT,1408450309476,"HH","standing"
         ACT,1408450310483,"HH","walking"
         ACT,1408450311491,"HH","walking"
         ACT,1408450312497,"HH","walking"
         ACT,1408450313493,"HH","walking"
         */

        String[] tests = {
                "ACTG,1408450306084,\"HH\",\"tilting\" 100",
                "ACT,1408450307486,\"HH\",\"standing\"",
                "ACT,1408450308466,\"HH\",\"standing\"",
                "ACT,1408450309476,\"HH\",\"standing\"",
                "ACT,1408450310483,\"HH\",\"walking\"",
                "ACT,1408450311491,\"HH\",\"walking\"",
                "ACT,1408450312497,\"HH\",\"walking\"",
                "ACT,1408450313493,\"HH\",\"walking\""
        };

        for(String test : tests) {
            Item i = ItemSerialization.ITEM_SERIALIZATION.deSerialize(test);
            System.out.println(test);
            System.out.println("  " + i.getType());
            System.out.println("  " + i.getTimestamp());
            System.out.println("  " + i.getDevice());
            System.out.println("  " + i.toString());

        }
    }
}
