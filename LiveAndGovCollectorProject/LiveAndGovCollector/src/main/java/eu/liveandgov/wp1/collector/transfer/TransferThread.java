package eu.liveandgov.wp1.collector.transfer;

/**
 * Transfer available data to server when conncetion available.
 *
 * Created by hartmann on 9/12/13.
 */
public class TransferThread implements Runnable {

    public int UPDATE_INTERVAL = 1000; // 1 sec in ms
    public int INITIAL_WAIT = 10000; // 10 sec in ms
    public int STATE = 0;

    public static final int STATE_INIT = 0;
    public static final int STATE_IDLE = 1;
    public static final int STATE_TRANSFER = 2;

    @Override
    public void run() {
        while (true) {
            switch (STATE) {
                case STATE_INIT:
                    doSleep(INITIAL_WAIT);

                case STATE_IDLE:
                    if (isConnected() && haveData()) {
                        STATE = STATE_TRANSFER;
                    }

                case STATE_TRANSFER:
                    transferData();
                    STATE = STATE_IDLE;
            }

            doSleep(UPDATE_INTERVAL);
        }
    }

    /**
     * Causes the thread to sleep for the required time in ms and
     * ignores the InterruptException.
     */
    private void doSleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Returns true if a network connection is present, which is
     * configured to handle the sensor traffic.
     * @return networkFlag
     */
    private boolean isConnected() {
        return true;
    }

    /**
     * Returns true if the required minimum of samples in available for transfer.
     * @return dataFlag
     */
    private boolean haveData() {
        return true;
    }

    private void transferData(){

    }

}
