package eu.liveandgov.wp1.backend;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Util {
	public static class SLDLogger {
		private static Logger loggerInstance = null;
	    private SLDLogger() {
	    	loggerInstance = Logger.getLogger(SLDLogger.class);
			PatternLayout layout = new PatternLayout("%p %d{yyyy-MM-dd HH:mm:ss} %C{1}: %m%n");
	        try {
				FileAppender fileAppender = new FileAppender(layout, "/var/log/ServiceLineDetection.log", true);
				loggerInstance.addAppender(fileAppender);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    public static Logger log() {
	        if (loggerInstance == null) {
	            new SLDLogger();
	        }
	        return loggerInstance;
	    }
	}
}
