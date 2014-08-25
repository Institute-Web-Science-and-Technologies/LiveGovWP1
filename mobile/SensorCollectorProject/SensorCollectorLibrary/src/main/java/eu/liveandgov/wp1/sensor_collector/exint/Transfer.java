package eu.liveandgov.wp1.sensor_collector.exint;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.base.Strings;
import com.google.common.io.Files;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * <p>Transfers a file and broadcasts {@link #TRANSFER_COMPLETED_ACTION} when it's done</p>
 * <ul>
 * <li>{@link #HANDLE} is used to track multiple transfers</li>
 * <li>{@link #TRANSFER_EXECUTOR_CLASS} is used to specify transfer strategy</li>
 * <li>{@link #URL}, {@link #ID}, {@link #SECRET}, {@link #COMPRESSED}, {@link #FILE} are used as parameters for the transfer</li>
 * </ul>
 */
public class Transfer extends IntentService {
    private final Logger log = LogPrincipal.get();

    /**
     * Extra field. Handle of a transfer, can be used to maintain record of active and succeeded transfers
     */
    public static final String HANDLE = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.HANDLE";

    /**
     * Extra field. Optional. Contains the class describing the transfer strategy. If not set, defaults to {@link eu.liveandgov.wp1.sensor_collector.exint.PostTransferExecutor}
     */
    public static final String TRANSFER_EXECUTOR_CLASS = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.TRANSFER_EXECUTOR_CLASS";

    /**
     * Extra field. The destination URL of the transfer
     */
    public static final String URL = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.URL";

    /**
     * Extra field. The user ID of the transfer
     */
    public static final String ID = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.ID";

    /**
     * Extra field. The user secret of the transfer
     */
    public static final String SECRET = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.SECRET";

    /**
     * Extra field. The compression status of the transferred file.
     */
    public static final String COMPRESSED = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.COMPRESSED";

    /**
     * Extra field. The file to transfer
     */
    public static final String FILE = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.FILE";

    /**
     * <p>Action name. The action broadcast when the transfer completed successfully or failing.</p>
     * <ul>
     * <li>{@link #HANDLE} corresponds to the sent handle</li>
     * <li>{@link #SUCCESSFUL} indicates the result type</li>
     * </ul>
     */
    public static final String TRANSFER_COMPLETED_ACTION = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.TRANSFER_COMPLETED_ACTION";

    /**
     * Extra field. The transfer result type, true for successful, false otherwise.
     */
    public static final String SUCCESSFUL = "eu.liveandgov.wp1.sensor_collector.exint.Transfer.SUCCESSFUL";

    public static TransferTracker trackedTransferFile(Context context, long handle, Class<? extends TransferExecutor> executorClass, String url, String id, String secret, boolean compressed, File file) {
        return new TransferTracker(context, transferFile(context, handle, executorClass, url, id, secret, compressed, file));
    }

    public static TransferTracker trackedTransferFile(Context context, Class<? extends TransferExecutor> executorClass, String url, String id, String secret, boolean compressed, File file) {
        return new TransferTracker(context, transferFile(context, executorClass, url, id, secret, compressed, file));
    }

    /**
     * Auto-generates the handle for {@link #transferFile(android.content.Context, long, Class, String, String, String, boolean, java.io.File)} and returns it.
     */
    public static long transferFile(Context context, Class<? extends TransferExecutor> executorClass, String url, String id, String secret, boolean compressed, File file) {
        long handle = System.nanoTime() * Double.doubleToLongBits(Math.random());

        return transferFile(context, handle, executorClass, url, id, secret, compressed, file);
    }

    /**
     * Transfers a file to a target using the given parameters mapped to extras of the intent.
     */
    public static long transferFile(Context context, long handle, Class<? extends TransferExecutor> executorClass, String url, String id, String secret, boolean compressed, File file) {
        Intent i = new Intent(context, Transfer.class);

        i.putExtra(HANDLE, handle);
        i.putExtra(TRANSFER_EXECUTOR_CLASS, executorClass);
        i.putExtra(URL, url);
        i.putExtra(ID, id);
        i.putExtra(SECRET, secret);
        i.putExtra(COMPRESSED, compressed);
        i.putExtra(FILE, file);

        context.startService(i);

        return handle;
    }

    public Transfer() {
        super("Transfer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            Class<?> te = (Class<?>) intent.getSerializableExtra(TRANSFER_EXECUTOR_CLASS);

            final TransferExecutor executor;

            try {
                // Create an instance of the passed executor if present and assignable
                if (te != null)
                    executor = (TransferExecutor) te.newInstance();
                else
                    executor = new PostTransferExecutor();
            } catch (InstantiationException e) {
                log.error("Error instantiating the transfer executor", e);
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                log.error("Constructor of the transfer executor could not be accessed", e);
                throw new RuntimeException(e);
            }

            // Get handle
            long handle = intent.getLongExtra(HANDLE, Long.MIN_VALUE);

            // Get URL
            String url = intent.getStringExtra(URL);

            if (Strings.isNullOrEmpty(url)) {
                log.warn("The transfer URL is null or empty");
                completed(handle, false);
                return;
            }

            // Get ID
            String id = intent.getStringExtra(ID);

            if (Strings.isNullOrEmpty(id)) {
                log.warn("The transfer id is null or empty");
                completed(handle, false);
                return;
            }

            // Get secret
            String secret = intent.getStringExtra(SECRET);

            if (Strings.isNullOrEmpty(secret)) {
                log.warn("The transfer secret is null or empty");
                completed(handle, false);
                return;
            }

            // Get compression status
            boolean compressed = intent.getBooleanExtra(COMPRESSED, SensorCollectionOptions.ZIPPED_PERSISTOR);

            // Get file
            File file = (File) intent.getSerializableExtra(FILE);
            if (file == null) {
                log.info("The transfer file is null");
                completed(handle, false);
                return;
            }

            // Check if file exists, if not abort
            if (!file.exists()) {
                log.info("The transfer file does not exist, doing nothing");
                completed(handle, true);
                return;
            }

            // Try to transmit
            try {
                // Do transfer
                executor.transfer(url, id, secret, compressed, file);

                // Complete successfully
                completed(handle, true);
            } catch (IOException e) {
                log.error("Error transferring the file " + file + " to the destination " + url, e);
                completed(handle, false);
            }
        }
    }

    protected void completed(long handle, boolean successful) {
        LocalBroadcastManager
                .getInstance(this)
                .sendBroadcast(new Intent(TRANSFER_COMPLETED_ACTION)
                        .putExtra(HANDLE, handle)
                        .putExtra(SUCCESSFUL, successful));
    }

}
