package eu.liveandgov.wp1.serialization;

import com.google.common.base.Function;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public interface Serialization<Data> {
    public String serialize(Data data);

    public Data deSerialize(String string);
}
