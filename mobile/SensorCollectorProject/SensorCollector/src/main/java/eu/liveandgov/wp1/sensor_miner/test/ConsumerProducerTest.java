package eu.liveandgov.wp1.sensor_miner.test;

import junit.framework.TestCase;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;

/**
 * Created by hartmann on 10/19/13.
 */
public class ConsumerProducerTest extends TestCase {

    static class TestConsumer<T> implements Consumer<T> {
        private T m = null;

        @Override
        public void push(T m) {
            this.m = m;
            System.out.println("Consumed: " + m);
        }

        public T getLast() {
            return m;
        }
    }

    static class TestProducer<T> extends Producer<T> {
        Consumer<T> getConsumer() {
            return consumer;
        }

        public void produce(T m) {
            consumer.push(m);
            consumer.push(m);
        }
    }

    public void testConsumer(){
        TestConsumer<String> c = new TestConsumer<String>();

        String m = "ABC";
        c.push(m);
        assertEquals(m, c.getLast());
    }

    public void testProducer(){
        TestProducer<String> p = new TestProducer<String>();
        TestConsumer<String> c = new TestConsumer<String>();
        p.setConsumer(c);
        p.produce("ABC");
        assertEquals( ((TestConsumer<String>) p.getConsumer()).getLast(), "ABC" );
    }

}
