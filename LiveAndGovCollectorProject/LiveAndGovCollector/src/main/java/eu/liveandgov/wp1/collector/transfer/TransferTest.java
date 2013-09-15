package eu.liveandgov.wp1.collector.transfer;

import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by hartmann on 9/13/13.
 */
public class TransferTest {
    public TransferTest(){
        Log.i("TEST", "Testing Network");
        TransferThread T = new TransferThread();
        T.transferData("Hello World");
    }

}
