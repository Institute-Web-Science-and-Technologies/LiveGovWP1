package eu.liveandgov.sensorcollectorv3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by hartmann on 9/22/13.
 */
public class MonitorThread implements Runnable {
    public static long sampleCount = 0;
    private final Handler handler;

    public MonitorThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        while(true) {

            String msg = String.format(
                    "Sample count: %d \n" +
                            "File size: %d \n", sampleCount,
                    MainActivity.P.getFile().length()
            );

            Bundle msgB = new Bundle();
            msgB.putString("msg", msg);

            Message m = new Message();
            m.setData(msgB);

            handler.sendMessage(m);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }
    }
}
