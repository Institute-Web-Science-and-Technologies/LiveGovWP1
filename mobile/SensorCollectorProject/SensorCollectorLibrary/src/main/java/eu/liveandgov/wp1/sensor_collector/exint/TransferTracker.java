package eu.liveandgov.wp1.sensor_collector.exint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.data.CallbackSet;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * Tracks a transfer performed by {@link eu.liveandgov.wp1.sensor_collector.exint.Transfer}
 * Created by lukashaertel on 25.08.2014.
 */
public class TransferTracker {
    /**
     * Obtain a logger
     */
    private final Logger log = LogPrincipal.get();

    /**
     * Callback set for state changes
     */
    public final CallbackSet<TransferState> changed = new CallbackSet<TransferState>();

    /**
     * The context this transfer tracker was initialized on, used to un-track if completed
     */
    private final Context context;

    /**
     * The handle this tracker is tracking
     */
    private final long trackedHandle;

    /**
     * The current state of the transfer
     */
    private TransferState state;

    /**
     * Initializes and registers the transfer tracker
     *
     * @param context       The context this tracker is tracking on
     * @param trackedHandle The handle to track
     */
    public TransferTracker(Context context, long trackedHandle) {
        // Copy params
        this.context = context;
        this.trackedHandle = trackedHandle;

        // Initialize state
        state = TransferState.RUNNING;

        // Track
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(Transfer.TRANSFER_COMPLETED_ACTION));
    }

    /**
     * Gets the current state of the tracker
     */
    public TransferState getState() {
        return state;
    }

    /**
     * Receiver for broadcast intents that indicate transfer state change
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check if handle is present
            if (!intent.hasExtra(Transfer.HANDLE)) {
                log.error("Malformed intent received");
                return;
            }

            // Check if state is present
            if (!intent.hasExtra(Transfer.SUCCESSFUL)) {
                log.error("Malformed intent received");
                return;
            }

            // Get the extras
            long handle = intent.getLongExtra(Transfer.HANDLE, Long.MIN_VALUE);
            boolean successful = intent.getBooleanExtra(Transfer.SUCCESSFUL, false);

            // If not representing the tracked intent, abort
            if (trackedHandle != handle)
                return;

            // If state is changing, set and notify
            if (state == TransferState.RUNNING) {
                state = successful ? TransferState.SUCCESSFUL : TransferState.FAILED;

                changed.call(state);
            }

            // Unregister the receiver
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
    };

}
