package carpool.common;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import carpool.aws.awsMain;
import carpool.constants.CarpoolConfig;

public class DebugLog {
	private static Logger log =	Logger.getLogger(DebugLog.class);
	private static Logger rootLogger = Logger.getRootLogger();
	
	public static void d(Exception e){
		try {
			e.printStackTrace();
			rootLogger.setLevel(Level.INFO);
			PatternLayout layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
			rootLogger.addAppender(new ConsoleAppender(layout));
			RollingFileAppender fileAppender;
			fileAppender = new RollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+CarpoolConfig.debugLogPrefix+CarpoolConfig.log4jLogFileSuffix);
			rootLogger.addAppender(fileAppender);
			log.warn(null);
			log.warn(e);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void d(String message){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + message);
		try {
			log(message);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public static void d(int number){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + number);
		try {
			log(number+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void d_Chinese(String message){
		try {
			PrintStream ps = new PrintStream(System.out, true, "UTF-8");
			ps.println(message);
			try {
				logChinese(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static void log(String message) throws IOException{
		rootLogger.setLevel(Level.INFO);
		PatternLayout layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
		rootLogger.addAppender(new ConsoleAppender(layout));
		RollingFileAppender fileAppender = new RollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+CarpoolConfig.debugLogPrefix+CarpoolConfig.log4jLogFileSuffix);
		rootLogger.addAppender(fileAppender);
		log.info(message);
	}

	private static void logChinese(String message) throws IOException{
		rootLogger.setLevel(Level.INFO);
		PatternLayout layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
		rootLogger.addAppender(new ConsoleAppender(layout));
		RollingFileAppender fileAppender = new RollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+CarpoolConfig.debugLogChinesePrefix+CarpoolConfig.log4jLogFileSuffix);
		rootLogger.addAppender(fileAppender);
		log.info(message);
	}

}
