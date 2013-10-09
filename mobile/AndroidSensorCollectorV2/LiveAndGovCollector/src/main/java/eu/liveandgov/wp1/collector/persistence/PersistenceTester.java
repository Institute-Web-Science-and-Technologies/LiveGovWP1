package eu.liveandgov.wp1.collector.persistence;

import android.util.Log;


/**
 * Created by hartmann on 9/12/13.
 */
public class PersistenceTester {

    public PersistenceTester(PersistenceInterface PI){
        assert( PI.getRecordCount() == 0 );

        PI.save("HELLO 1");
        PI.save("HELLO 2");
        PI.save("HELLO 3");
        PI.save("HELLO 4");

        assertLog( PI.getRecordCount() == 4 );

        assertLog( PI.readLines(1).get(0).equals("HELLO 1"));
        assertLog( PI.readLines(1).get(0).equals("HELLO 2"));
        assertLog( PI.readLines(1).get(0).equals("HELLO 3"));
        assertLog( PI.readLines(1).get(0).equals("HELLO 4"));

        assert( PI.getRecordCount() == 0 );

    }

    private void assertLog(boolean result){
        if (result) {
            Log.i("TEST", "SUCCESS");
        } else {
            Log.i("TEST", "FAILED");
        }
    }
}
