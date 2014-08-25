package eu.liveandgov.wp1.sensor_collector.exint;

import java.io.File;
import java.io.IOException;

/**
 * Strategy to execute a transfer
 * Created by lukashaertel on 25.08.2014.
 */
public interface TransferExecutor {
    /**
     * Transfers a file with the given options to a specified destination
     * TODO: id and secret in a {@link android.os.Bundle} instead of directly
     *
     * @param target     The target to transfer to
     * @param id         The id of the sending user
     * @param secret     The secret associated with the sending user
     * @param compressed The compression status of the file
     * @param file       The file to transmit
     * @throws IOException Throws an IO exception if the transfer caused an exception
     */
    void transfer(String target, String id, String secret, boolean compressed, File file) throws IOException;
}
