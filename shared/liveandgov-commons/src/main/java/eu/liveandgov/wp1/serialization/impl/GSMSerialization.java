package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.GSM;
import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GSMSerialization extends Wrapper<GSM, Arbitrary> {
    public static final GSMSerialization GSM_SERIALIZATION = new GSMSerialization();

    private GSMSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(GSM gsm) {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();

        stringBuilder.append(toText(gsm.serviceState));
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(gsm.roamingState));
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(gsm.carrierSelection));
        stringBuilder.append(SLASH);
        stringBuilder.append(escape(gsm.carrierName));
        stringBuilder.append(SLASH);
        stringBuilder.append(escape(gsm.signalStrength));

        stringBuilder.append(COLON);

        if (gsm.items.length > 0) {
            appendGSMItem(gsm, stringBuilder, 0);
            for (int i = 1; i < gsm.items.length; i++) {
                stringBuilder.append(SEMICOLON);

                appendGSMItem(gsm, stringBuilder, 0);
            }
        }

        return new Arbitrary(gsm, gsm.getType(), stringBuilder.toString());
    }

    private static void appendGSMItem(GSM gsm, StringBuilder stringBuilder, int i) {
        final GSM.Item item = gsm.items[i];

        stringBuilder.append(escape(item.cellIdentity));
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(item.cellType));
        stringBuilder.append(SLASH);
        stringBuilder.append(item.rssi);
    }

    @Override
    protected GSM invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final GSM.ServiceState serviceState = fromText(GSM.ServiceState.class, nextString(scanner));
        final GSM.RoamingState roamingState = fromText(GSM.RoamingState.class, nextString(scanner));
        final GSM.CarrierSelection carrierSelection = fromText(GSM.CarrierSelection.class, nextString(scanner));
        final String carrierName = nextString(scanner);
        final String signalStrength = nextString(scanner);

        scanner.skip(COLON_SEPARATED);

        final List<GSM.Item> itemList = new ArrayList<GSM.Item>();
        while (hasNextString(scanner)) {

            final String cellIdentity = nextString(scanner);
            final GSM.CellType cellType = fromText(GSM.CellType.class, nextString(scanner));
            final int rssi = scanner.nextInt();

            itemList.add(new GSM.Item(cellIdentity, cellType, rssi));
        }

        return new GSM(item, serviceState, roamingState, carrierSelection, carrierName, signalStrength, itemList.toArray(GSM.Item.EMPTY_ARRAY));
    }
}
