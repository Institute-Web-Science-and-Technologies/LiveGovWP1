package eu.liveandgov.wp1.human_activity_recognition.helper;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.containers.CountWindow;
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
public class Persistor<T> implements Consumer<T> {

    private PrintWriter writer;
    private boolean isFirst = true;

    public Persistor(String filePath) {
        try {
            writer = new PrintWriter(filePath, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void push(T message) {
        if (isFirst) {
            isFirst = false;
            writer.println(generateTableHead(
                    // Need the number of csv rows the message object needs to write
                    // VERY DIRTY HACK
                    StringUtils.countMatches(message.toString(), ",")
            ));
        }

        writer.println(message.toString());
    }

    public void clear() {
        writer.flush();
    }


    public String generateTableHead(int length){
        String result = "";
        for (int i = 0; i < length; i++) {
            result += "x" + i;
            if ((i + 1) != length) {
                result += ",";
            }
        }
        return result;
    }

}
