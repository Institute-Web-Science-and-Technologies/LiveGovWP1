package eu.liveandgov.wp1.sensor_collector.transfer;

import android.content.SharedPreferences;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.R;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.exint.Post2TransferExecutor;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.persistence.Persistor;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * Transfer sensor.log file to server using HTTP/POST request
 * <p/>
 * Use:
 * * .setup()      - setup instance
 * * .doTransfer() - trigger sample transfer
 * <p/>
 * Created by hartmann on 8/30/13.
 */
public class TransferThreadPost implements Runnable, TransferManager {
    private static final Logger log = LogPrincipal.get();

    private Thread thread;

    private Persistor persistor;

    private File stageFile;

    public TransferThreadPost(Persistor persistor, File stageFile) {
        this.stageFile = stageFile;
        this.persistor = persistor;
        thread = new Thread(this);
    }

    @Override
    public void doTransfer() {
        if (thread.isAlive()) {
            log.info("Already running.");
            return;
        }
        if (thread.getState() == Thread.State.TERMINATED) thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean isTransferring() {
        return thread.isAlive();
    }

    @Override
    public boolean hasStagedSamples() {
        return stageFile.length() > 0;
    }

    @Override
    public void deleteStagedSamples() {
        stageFile.delete();
    }

    @Override
    public String getStatus() {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
        stringBuilder.append("StageFile: ");
        stringBuilder.append(Math.round(stageFile.length() / 1024.0));
        stringBuilder.append("kb. ");

        if (isTransferring())
            stringBuilder.append("transferring");
        else
            stringBuilder.append("waiting");

        return stringBuilder.toString();
    }

    public void run() {
        boolean success;

        try {

            // TODO: Check methods if with success return should rather throw an exception in
            // order to make calling more uniform.


            // get stage file
            if (stageFile.exists()) {
                log.info("Found old stage file.");
            } else {
                success = persistor.exportSamples(stageFile);
                if (!success) {
                    log.error("Staging failed");
                    return;
                }
            }

            boolean isCompressed = infereCompressionStatusOf(stageFile);

            // transfer staged File
            success = transferFile(stageFile, isCompressed);
            if (!success) {
                log.error("Transfer failed");
                return;
            }

            // delete local copy
            success = stageFile.delete();
            if (!success) {
                log.error("Deletion failed");
                return;
            }

            // terminate
            log.info("Transfer finished successfully");

        } catch (IOException e) {
            log.error("Error opening stage file", e);
        }
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

    public boolean transferFile(File file, boolean compressed) {
        try {
            return Post2TransferExecutor.INSTANCE.transfer(getAddress(), GlobalContext.getUserId(), getSecret(), compressed, file);
        } catch (IOException e) {
            log.error("IO exception occured", e);
            return false;
        }
    }

    private String getAddress() {
        SharedPreferences settings = GlobalContext.context.getSharedPreferences(GlobalContext.context.getString(R.string.spn), 0);

        return settings.getString(GlobalContext.context.getString(R.string.prf_upload_address), SensorCollectionOptions.DEFAULT_UPLOAD);
    }

    private String getSecret() {
        SharedPreferences settings = GlobalContext.context.getSharedPreferences(GlobalContext.context.getString(R.string.spn), 0);

        return settings.getString(GlobalContext.context.getString(R.string.prf_secret), "");
    }

    public boolean transferFile(File file) {
        return transferFile(file, false);
    }


}

