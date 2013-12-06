package net.hh.test;


import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/6/13
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestExecutable {

    private static Logger Log = Logger.getLogger(TestExecutable.class);

    static {
        Layout layout = new PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %c %x - %m%n");
        Log.addAppender(new ZmqAppender("tcp://*:51001", layout));
    }

    public static void main(String[] argv) throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            Log.info("Hello World! ");
        }
    }

}
