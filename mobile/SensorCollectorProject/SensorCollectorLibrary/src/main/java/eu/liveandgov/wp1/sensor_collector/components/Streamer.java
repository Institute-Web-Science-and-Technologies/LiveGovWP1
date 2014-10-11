package eu.liveandgov.wp1.sensor_collector.components;

import com.google.inject.Inject;

import org.zeromq.ZMQ;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;

/**
 * Created by lukashaertel on 07.10.2014.
 */
public class Streamer implements SampleTarget {
    ZMQ.Socket s;
    public Streamer(){
        ZMQ.Context x = ZMQ.context(1);
        s = x.socket(ZMQ.PUB);
        s.connect("addressherepls");

    }

    @Override
    public void handle(Item item) {
        s.send(item.toSerializedForm());
    }
}
