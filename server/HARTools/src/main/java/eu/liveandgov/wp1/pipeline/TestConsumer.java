package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.data.Tuple;

/**
 * Created by cehlen on 25/02/14.
 */
public class TestConsumer implements Consumer<Tuple<Long, String>> {

    @Override
    public void push(Tuple<Long, String> longStringTuple) {
        System.out.println("TripID: " + longStringTuple.left + " Activity: " + longStringTuple.right);
    }
}
