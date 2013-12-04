package eu.liveandgov.wp1.server.executables;

import eu.liveandgov.wp1.server.DbIngestThread;
import eu.liveandgov.wp1.server.db_helper.BatchInserter;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.SQLException;

/**
 * User: hartmann
 * Date: 10/19/13
 *
 * Enable port forwarding to server to run this script
 *
 * ssh -L 5432:localhost:5432 LG
 *
 *
 */
public class LocalUploadTest {
    public static final String IMPORT_FILE = "/tmp/test.ssf";
    private static final Logger LOG = Logger.getLogger(DbIngestThread.class);

    static {
        BasicConfigurator.configure();
    }

    public static void main(String[] args){
        LOG.info("Starting DB Ingest");

        PostgresqlDatabase db = new PostgresqlDatabase();

        try {
            File outFile = new File(IMPORT_FILE);

            BufferedReader reader;
            reader = new BufferedReader(new FileReader(outFile));

            int rows = BatchInserter.batchInsertFile(db, reader);

            LOG.info("Imported files int db. Rows: " + rows);

            } catch (FileNotFoundException e) {
                LOG.info("File not found: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                LOG.info("Error reading file: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                LOG.info("Error writing to database.");
                e.printStackTrace();
            } catch (Exception e) {
                LOG.info("Something (else) went wrong.");
                e.printStackTrace();
            }
        }

}
