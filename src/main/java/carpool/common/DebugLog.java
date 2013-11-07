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
	private static boolean configure =false;
	public static void d(String message){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + message);
		log(message);
	}

	public static void d(int number){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + number);
		log(number+"");
	}

	public static void d_Chinese(String message){
		try {
			PrintStream ps = new PrintStream(System.out, true, "UTF-8");
			ps.println(message);
			logChinese(message);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static void log(String message){
		try{
			if(!configure){
				log.setLevel(Level.INFO);
				PatternLayout layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
				log.addAppender(new ConsoleAppender(layout));
				RollingFileAppender fileAppender = new RollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+CarpoolConfig.debugLogPrefix+CarpoolConfig.log4jLogFileSuffix);
				log.addAppender(fileAppender);
				configure = true;
			}		
			log.info(message);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void logChinese(String message){
		try{
			if(!configure){
				log.setLevel(Level.INFO);
				PatternLayout layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
				log.addAppender(new ConsoleAppender(layout));
				RollingFileAppender fileAppender = new RollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+CarpoolConfig.debugLogChinesePrefix+CarpoolConfig.log4jLogFileSuffix);
				log.addAppender(fileAppender);
				configure = true;
			}
			log.info(message);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
