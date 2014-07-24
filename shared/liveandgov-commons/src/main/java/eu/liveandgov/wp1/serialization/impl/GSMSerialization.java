package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.GSM;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * <p>Serialization of the GSM item</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class GSMSerialization extends AbstractSerialization<GSM> {
    /**
     * The one instance of the serialization
     */
    public static final GSMSerialization GSM_SERIALIZATION = new GSMSerialization();

    /**
     * Hidden constructor
     */
    protected GSMSerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, GSM gsm) {
        appendString(stringBuilder, toText(gsm.serviceState));
        stringBuilder.append(SLASH);
        appendString(stringBuilder, toText(gsm.roamingState));
        stringBuilder.append(SLASH);
        appendString(stringBuilder, toText(gsm.carrierSelection));
        stringBuilder.append(SLASH);
        appendString(stringBuilder, gsm.carrierName);
        stringBuilder.append(SLASH);
        appendString(stringBuilder, gsm.signalStrength);

        stringBuilder.append(SLASH);

        if (gsm.items.length > 0) {
            appendGSMItem(gsm, stringBuilder, 0);
            for (int i = 1; i < gsm.items.length; i++) {
                stringBuilder.append(SEMICOLON);

                appendGSMItem(gsm, stringBuilder, 0);
            }
        }
    }

    /**
     * Writes one GSM sub-item to the string builder
     *
     * @param gsm           The GSM item
     * @param stringBuilder The target string builder
     * @param i             The index of the item to write
     */
    private static void appendGSMItem(GSM gsm, StringBuilder stringBuilder, int i) {
        final GSM.Item item = gsm.items[i];

        appendString(stringBuilder, item.cellIdentity);
        stringBuilder.append(SLASH);
        appendString(stringBuilder, toText(item.cellType));
        stringBuilder.append(SLASH);
        stringBuilder.append(item.rssi);
    }

    @Override
    protected GSM deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final GSM.ServiceState serviceState = fromText(GSM.ServiceState.class, nextString(scanner));
        final GSM.RoamingState roamingState = fromText(GSM.RoamingState.class, nextString(scanner));
        final GSM.CarrierSelection carrierSelection = fromText(GSM.CarrierSelection.class, nextString(scanner));
        final String carrierName = nextString(scanner);
        final String signalStrength = nextString(scanner);

        final List<GSM.Item> itemList = new ArrayList<GSM.Item>();
        while (hasNextString(scanner)) {

            final String cellIdentity = nextString(scanner);
            final GSM.CellType cellType = fromText(GSM.CellType.class, nextString(scanner));
            final int rssi = scanner.nextInt();

            itemList.add(new GSM.Item(cellIdentity, cellType, rssi));
        }

        return new GSM(timestamp, device, serviceState, roamingState, carrierSelection, carrierName, signalStrength, itemList.toArray(GSM.Item.EMPTY_ARRAY));
    }


}
