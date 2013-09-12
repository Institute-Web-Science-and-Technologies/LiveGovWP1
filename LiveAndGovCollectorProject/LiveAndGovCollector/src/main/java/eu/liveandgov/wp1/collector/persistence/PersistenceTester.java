package eu.liveandgov.wp1.collector.persistence;

import android.util.Log;

/**
 * Created by hartmann on 9/12/13.
 */
public class PersistenceTester {
    public PersistenceTester(PersistenceInterface PI){
        PI.save("HELLO 1");
        PI.save("HELLO 2");
        PI.save("HELLO 3");
        PI.save("HELLO 4");
        Log.i("TEST", PI.readLines(1).get(0).toString());
        Log.i("TEST", PI.readLines(1).get(0).toString());
        Log.i("TEST", PI.readLines(1).get(0).toString());
    }
}
