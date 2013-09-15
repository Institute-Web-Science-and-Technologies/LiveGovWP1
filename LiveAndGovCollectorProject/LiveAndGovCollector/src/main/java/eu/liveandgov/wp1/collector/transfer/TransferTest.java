package eu.liveandgov.wp1.collector.transfer;

import android.util.Log;

/**
 * Created by hartmann on 9/13/13.
 */
public class TransferTest {
    public TransferTest(){
        // Testing ZMQ interface
        TransferInterface TI = new TransferZMQ();
        for(int i = 0; i< 100; i++){
            TI.transferData("Hello from Android! " + i);
        }

        // Testing HTTP interface
        // Need to spawn new thread for network connection.
        new Thread(new Runnable() {
            @Override
            public void run() {
                new HttpTransfer().transferData("Hello World!");
            }
        }).start();

    }

}
