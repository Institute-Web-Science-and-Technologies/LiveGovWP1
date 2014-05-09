package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.packaging.Packaging;

import java.util.Map;

/**
 * <p>Abstract base class representing an unsupported packaging</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class UnsupportedPackaging<Data> implements Packaging<Data> {
    @Override
    public Map<String, ?> pack(Data item) {
        throw new UnsupportedOperationException("Forwarding not supported");
    }

    @Override
    public Data unPack(Map<String, ?> map) {
        throw new UnsupportedOperationException("Forwarding not supported");
    }
}
