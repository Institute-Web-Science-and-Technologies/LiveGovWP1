package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.connectors.Consumer;
import eu.liveandgov.wp1.connectors.Producer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 12/11/13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public class CSVFileProducer implements Consumer<TaggedFeatureVector> {

    private final String FILE_PATH = "/tmp/HarFeatureTable.csv";

    private PrintWriter printWriter;

    public CSVFileProducer() {
        super();
        try {
            printWriter = new PrintWriter(FILE_PATH, "UTF-8");
            printWriter.println( TaggedFeatureVector.getCsvHead() );
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void close() {
        printWriter.close();
    }

    @Override
    public void push(TaggedFeatureVector fv) {
        printWriter.println( fv.toCSV() );
    }
}
