package eu.liveandgov.wp1.collector.transfer;

import android.net.ConnectivityManager;

import eu.liveandgov.wp1.collector.persistence.MockPersister;
import eu.liveandgov.wp1.collector.persistence.PersistenceInterface;

/**
 * Created by hartmann on 9/13/13.
 */
public class TransferTest {

    public TransferTest(ConnectivityManager CM){
        PersistenceInterface PI = new MockPersister();

        TransferInterface t = new TransferThread(CM, PI);

        for (int i = 0; i < 100; i++){
            PI.save("SAMPLE " + i);
        }



    }
}
