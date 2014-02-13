package eu.liveandgov.wp1.serialization;

/**
 * 4
 * Created by Lukas Härtel on 08.02.14.
 */
public abstract class Wrapper<Data, HostData> implements Serialization<Data> {
    private final Serialization<HostData> wrapped;

    public Wrapper(Serialization<HostData> wrapped) {
        this.wrapped = wrapped;
    }

    protected abstract HostData transform(Data data);

    protected abstract Data invertTransform(HostData hostData);

    @Override
    public String serialize(Data data) {
        return wrapped.serialize(transform(data));
    }

    @Override
    public Data deSerialize(String string) {
        return invertTransform(wrapped.deSerialize(string));
    }
}
