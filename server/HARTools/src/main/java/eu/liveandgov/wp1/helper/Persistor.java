package eu.liveandgov.wp1.helper;

import eu.liveandgov.wp1.data.FeatureVector;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.pipeline.Consumer;
import org.apache.commons.lang.StringUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 07/01/14
 * Time: 01:24
 * To change this template use File | Settings | File Templates.
 */
public class Persistor implements Consumer<Tuple<Long, FeatureVector>> {

    private PrintWriter writer;
    private boolean isFirst = true;
    private String csvHead = null;

    public Persistor(String filePath) {
        try {
            writer = new PrintWriter(filePath, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String generateTableHead(int length){
        if (csvHead != null) { return csvHead; }

        String result = "";
        for (int i = 0; i < length; i++) {
            result += "x" + (i + 1); // start with x1
            if ((i + 1) != length) {
                result += ",";
            }
        }
        return result;
    }

    @Override
    public void push(Tuple<Long, FeatureVector> longFeatureVectorTuple) {
        FeatureVector fv = longFeatureVectorTuple.right;
        if (isFirst) {
            isFirst = false;
            writer.println(generateTableHead(
                    // Need the number of csv rows the message object needs to write
                    // VERY DIRTY HACK: Number of , + 1
                    StringUtils.countMatches(fv.toString(), ",") + 1
            ));
        }

        writer.println(fv.toString());
    }

    public void flush() {
        writer.flush();
    }

    public void setCsvHead(String csvHead) {
        this.csvHead = csvHead;
    }
}

