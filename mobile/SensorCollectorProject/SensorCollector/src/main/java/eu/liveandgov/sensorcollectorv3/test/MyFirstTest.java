package eu.liveandgov.sensorcollectorv3.test;

import android.test.ActivityTestCase;
import eu.liveandgov.sensorcollectorv3.connector.Consumer;

/**
 * Created by hartmann on 10/19/13.
 */
public class MyFirstTest extends ActivityTestCase {

    static class TestConsumer<T> implements Consumer<T> {
        private T m = null;

        @Override
        public void push(T m) {
            this.m = m;
        }

        public T getLast() {
            return m;
        }
    }

    public void testConnector(){

        TestConsumer<String> a = new TestConsumer<String>();

        String m = "ABC";
        a.push(m);
        assertEquals(m, a.getLast());
    }
    
}
