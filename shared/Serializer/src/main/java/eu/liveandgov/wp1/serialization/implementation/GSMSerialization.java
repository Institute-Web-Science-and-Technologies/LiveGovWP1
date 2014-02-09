package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.implementation.GSM;
import eu.liveandgov.wp1.data.Item;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GSMSerialization extends Wrapper<GSM, Item<String>> {
    public static final GSMSerialization GSM_SERIALIZATION = new GSMSerialization();

    private GSMSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(GSM gsm) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(toText(gsm.data.left.serviceState));
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(gsm.data.left.roamingState));
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(gsm.data.left.carrierSelection));
        stringBuilder.append(SLASH);
        stringBuilder.append(escape(gsm.data.left.carrierName));
        stringBuilder.append(SLASH);
        stringBuilder.append(escape(gsm.data.left.signalStrength));

        stringBuilder.append(COLON);

        if (gsm.data.right.size() > 0) {
            appendGSMItem(gsm, stringBuilder, 0);
            for (int i = 1; i < gsm.data.right.size(); i++) {
                stringBuilder.append(SEMICOLON);

                appendGSMItem(gsm, stringBuilder, 0);
            }
        }

        return new Item<String>(gsm.type, gsm.header, stringBuilder.toString());
    }

    private static void appendGSMItem(GSM gsm, StringBuilder stringBuilder, int i) {
        final GSM.GSMItem gsmItem = gsm.data.right.get(i);

        stringBuilder.append(escape(gsmItem.cellIdentity));
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(gsmItem.cellType));
        stringBuilder.append(SLASH);
        stringBuilder.append(gsmItem.rssi);
    }

    @Override
    protected GSM invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final GSM.ServiceState serviceState = fromText(GSM.ServiceState.class, nextString(scanner));
        final GSM.RoamingState roamingState = fromText(GSM.RoamingState.class, nextString(scanner));
        final GSM.CarrierSelection carrierSelection = fromText(GSM.CarrierSelection.class, nextString(scanner));
        final String carrierName = nextString(scanner);
        final String signalStrength = nextString(scanner);

        scanner.skip(COLON_SEPARATED);

        final List<GSM.GSMItem> itemList = new ArrayList<GSM.GSMItem>();
        while (hasNextString(scanner)) {

            final String cellIdentity = nextString(scanner);
            final GSM.CellType cellType = fromText(GSM.CellType.class, nextString(scanner));
            final int rssi = scanner.nextInt();

            itemList.add(new GSM.GSMItem(cellIdentity, cellType, rssi));
        }

        return new GSM(stringItem.type, stringItem.header, Tuple.create(new GSM.GSMStatus(serviceState, roamingState, carrierSelection, carrierName, signalStrength), itemList));
    }
}
