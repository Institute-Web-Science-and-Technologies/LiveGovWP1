package eu.liveandgov.wp1.server.executables;

import eu.liveandgov.wp1.server.DbIngestThread;
import eu.liveandgov.wp1.server.db_helper.BatchInserter;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/4/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirInserter {
    private static final Logger LOG = Logger.getLogger(DbIngestThread.class);

    static {
        BasicConfigurator.configure();
    }


    public static String IMPORT_DIR = "/srv/liveandgov/import";
    private static long MIN_SIZE_BYTES = 100 * 1024; // Minimum 100 k file size

    public static void main(String[] args) {
        if (args.length == 1) IMPORT_DIR = args[0];

        LOG.info("Starting DirDbIngest on " + IMPORT_DIR);

        PostgresqlDatabase db = new PostgresqlDatabase();

        File[] files = new File(IMPORT_DIR).listFiles();
        BufferedReader reader;

        for (File curFile : files) {
            try {

                if (! curFile.isFile()) continue;
                if ( curFile.getName().endsWith(".gz") ) continue;
                if ( curFile.length() < MIN_SIZE_BYTES ) continue;

                LOG.info("Inserting File " + curFile.getAbsolutePath() );

                reader = new BufferedReader(new FileReader(curFile));

                int rows = BatchInserter.batchInsertFile(db, reader);

                LOG.info("Imported files int db. Rows: " + rows);

            } catch (FileNotFoundException e) {
                LOG.error("File not found: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                LOG.error("Error reading file: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                LOG.error("Error writing to db");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
