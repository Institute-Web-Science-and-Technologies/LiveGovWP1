package eu.liveandgov.wp1.shared.logging;

/**
 * Servlet to handle the Logging of the Webserver
 * http://logging.apache.org/log4j/1.2/manual.html
 */

import org.apache.log4j.*;

import javax.servlet.http.HttpServlet;
import java.io.IOException;

public class Log4jInitServlet extends HttpServlet {

    public static String ZMQ_LOG_ADDRESS = "tcp://*:50110";

    public void init() {
        try {
            Layout layout = new PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %c %x - %m%n");
            Appender fileAppender = new FileAppender(layout,"/srv/log/UploadServlet.log",true);
            Appender zmqAppender = new ZmqAppender(ZMQ_LOG_ADDRESS, layout);

            Logger.getRootLogger().addAppender(fileAppender);
            Logger.getRootLogger().addAppender(zmqAppender);

            Logger.getRootLogger().setLevel(Level.DEBUG);

            Logger.getRootLogger().info("Initialized Looger");

            System.out.println("Hello from InitServlet");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error opening Logfile");
        }
    }


}