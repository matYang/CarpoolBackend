package carpool.common;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

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
	private static PatternLayout layout;
	private static RollingFileAppender fileAppender;

	public static void d(Exception e){
		try{
			String today = getTime();
			if(!configure){
				log.setLevel(Level.INFO);
				layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
				log.addAppender(new ConsoleAppender(layout));

				configure = true;
			}
			fileAppender = new RollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+today+CarpoolConfig.debugLogChinesePrefix+CarpoolConfig.log4jLogFileSuffix);
			log.addAppender(fileAppender);
			log.info(e);			
		}catch(IOException e1){
			log.info(e1);
		}
	}

	public static void d(String message){
		//System.out.println("DEBUG MESSAGE BY BAD STUDENT " + message);
		log(message);
	}

	public static void d(int number){
		//System.out.println("DEBUG MESSAGE BY BAD STUDENT " + number);
		log(number+"");
	}

	public static void d_Chinese(String message){
		try {
			PrintStream ps = new PrintStream(System.out, true, "UTF-8");
			ps.println(message);
			log(message);
		} catch (UnsupportedEncodingException e) {
			log.info(e);
		}
	}

	private static void log(String message){
		try{
			String today = getTime();
			if(!configure){
				log.setLevel(Level.INFO);
				layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
				log.addAppender(new ConsoleAppender(layout));
				fileAppender = new RollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+today+CarpoolConfig.log4jLogFileSuffix);
				log.addAppender(fileAppender);
				configure = true;
			}		
			log.info(message);
		}catch(IOException e){
			log.info(e);
		}
	}

	private static String getTime(){
		Calendar today = Calendar.getInstance();

		int day = today.get(Calendar.DAY_OF_MONTH);
		int month = today.get(Calendar.MONTH);
		int year = today.get(Calendar.YEAR);

		return year + "-" + month + "-" + day;
	}

}
