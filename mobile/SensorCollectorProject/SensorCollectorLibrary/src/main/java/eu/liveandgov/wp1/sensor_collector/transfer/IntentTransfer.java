package eu.liveandgov.wp1.sensor_collector.transfer;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.R;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.exint.PostTransferExecutor;
import eu.liveandgov.wp1.sensor_collector.exint.Transfer;
import eu.liveandgov.wp1.sensor_collector.exint.TransferState;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.persistence.Persistor;

/**
 * Created by lukashaertel on 25.08.2014.
 */
public class IntentTransfer implements TransferManager {
    private final Logger log = LogPrincipal.get();

    private final Persistor persistor;

    private final File stageroot;

    private int transfers;

    private long transferSizes;

    public IntentTransfer(Persistor persistor, File stageroot) {
        this.persistor = persistor;
        this.stageroot = stageroot;

        // Zero transfers are initially running
        transfers = 0;
        transferSizes = 0;
    }


    @Override
    public void doTransfer() {
        final String address = getAddress();
        final String secret = getSecret();

        GlobalContext.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {

                // Get a stage file
                final File stageFile = new File(stageroot, RandomStringUtils.randomAlphanumeric(10) + "-" + System.nanoTime());

                // Save the samples to the stage file
                if (!persistor.exportSamples(stageFile)) {
                    log.error("Exporting the samples to stage file failed.");
                    return;
                }

                // Find out if stage files data is compressed
                boolean compressed;
                try {
                    compressed = infereCompressionStatusOf(stageFile);
                } catch (IOException e) {
                    log.error("Could not infer compression status of stage file");
                    throw new RuntimeException(e);
                }

                // Increment the number of transfers
                transfers++;
                transferSizes += stageFile.length();

                // Transfer the file, register a change handler removing the file if completed
                Transfer.trackedTransferFile(
                        GlobalContext.context,
                        PostTransferExecutor.class,

                        address,
                        GlobalContext.getUserId(),
                        secret,
                        compressed,
                        stageFile).changed.register(new Callback<TransferState>() {
                    @Override
                    public void call(TransferState transferState) {
                        transfers--;
                        transferSizes -= stageFile.length();

                        log.debug("Transfer has been completed, result: " + transferState);
                        if (!stageFile.delete())
                            log.warn("Orphan stage file created, " + stageFile);
                    }
                });
            }
        });
    }

    private String getAddress() {
        SharedPreferences settings = GlobalContext.context.getSharedPreferences(GlobalContext.context.getString(R.string.spn), 0);

        return settings.getString(GlobalContext.context.getString(R.string.prf_upload_address), SensorCollectionOptions.DEFAULT_UPLOAD);
    }

    private String getSecret() {
        SharedPreferences settings = GlobalContext.context.getSharedPreferences(GlobalContext.context.getString(R.string.spn), 0);

        return settings.getString(GlobalContext.context.getString(R.string.prf_secret), "");
    }


    private boolean infereCompressionStatusOf(File stageFile) throws IOException {
        log.info("Inferring compression of " + stageFile);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(stageFile);

            // Read files first two bytes and check against the magic-number
            final int first = fileInputStream.read();
            final int second = fileInputStream.read();
            return first == 0x1f && second == 0x8b;
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    @Override
    public boolean isTransferring() {
        return transfers > 0;
    }

    @Override
    public boolean hasStagedSamples() {
        return transfers > 0;
    }

    @Override
    public void deleteStagedSamples() {
        // Do nothing, as stage files will be deleted automatically
    }

    @Override
    public String getStatus() {
        if (transfers == 0)
            return "Intent transfer, waiting";
        else
            return "Intent transfer, transferring " + transfers + " files, total " + filesize(transferSizes);
    }

    private static String rounded(long l, long over, int dl) {
        double d = l / (double) over;
        return Double.toString(Math.round(d * dl) / dl);
    }

    private static String filesize(long s) {
        // Two decimal places
        final int dl = 100;

        // Divide appropriately
        if (s < 1024) return rounded(s, 1, dl) + "Bs";
        if (s < 1024 * 1024) return rounded(s, 1024, dl) + "KB";
        if (s < 1024 * 1024 * 1024) return rounded(s, 1024 * 1024, dl) + "GB";

        // Limit to gigabyte
        return rounded(s, 1024 * 1024, dl) + "GB";
    }
}
