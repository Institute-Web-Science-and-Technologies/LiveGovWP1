package eu.liveandgov.wp1.server.executables;

import eu.liveandgov.wp1.server.DbIngestThread;
import eu.liveandgov.wp1.server.db_helper.BatchInserter;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.SQLException;

/**
 * USAGE:
 *
 * mvn clean compile exec:java -Dexec.mainClass=eu.liveandgov.wp1.server.executables.DirInserter -Dexec.args="/tmp/data/"
 *
 */
public class DirInserter {
    private static final Logger LOG = Logger.getLogger(DbIngestThread.class);

    static {
        BasicConfigurator.configure();
    }


    public static String IMPORT_DIR = "/tmp/upload_data";
    private static long MIN_SIZE_BYTES = 100 * 1024; // Minimum 100 k file size

    public static void main(String[] args) {

        if (args.length == 1) IMPORT_DIR = args[0];

        LOG.info("Starting DirDbIngest on " + IMPORT_DIR);

        PostgresqlDatabase db = new PostgresqlDatabase();

        File targetDir = new File(IMPORT_DIR);
        if (! targetDir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + targetDir.getAbsolutePath() );
        }

        File[] files = targetDir.listFiles();
        BufferedReader reader;

        // dropTables(db);

        for (File curFile : files) {
            try {
                LOG.info("Inserting File " + curFile.getAbsolutePath() );

                if (! curFile.isFile()) {
                    LOG.info("Skipped. Not a file.");
                    continue;
                }

                if ( curFile.getName().endsWith(".gz") ) {
                    LOG.info("Skipped. gz-not supported.");
                    continue;
                }

                if ( curFile.length() < MIN_SIZE_BYTES ) {
                    LOG.info("Skipped. Too small.");
                    continue;
                }

                reader = new BufferedReader(new FileReader(curFile));

                int rows = BatchInserter.batchInsertFile(db, reader);

                LOG.info("Imported files int db. Rows: " + rows);

            } catch (FileNotFoundException e) {
                LOG.error("File not found.", e);
            } catch (IOException e) {
                LOG.error("Error reading file.", e);
            } catch (SQLException e) {
                LOG.error("Error writing to db", e);
            } catch (Exception e) {
                LOG.error("Something else went wrong.", e);
            }
        }
    }

    private static void dropTables(PostgresqlDatabase db) {
        LOG.info("Dropping Tables");
        try {
            new BatchInserter(db).dropTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
