package carpool.common;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.json.JSONObject;

import carpool.aws.AwsMain;
import carpool.configurations.ServerConfig;
import carpool.factory.JSONFactory;
import carpool.interfaces.PseudoModel;
import carpool.log4j.log4j;

public class DebugLog {
	private static Logger logger = Logger.getLogger(DebugLog.class);
//	private static PatternLayout layout;
//	private static DailyRollingFileAppender dFileAppender;
	
//	public static void initializeLogger(){
//		try {
//			logger.setLevel(Level.INFO);
//			layout = new PatternLayout(CarpoolConfig.log4jBasicPatternLayout);
//			logger.addAppender(new ConsoleAppender(layout));
//			dFileAppender = new DailyRollingFileAppender(layout, CarpoolConfig.log4LogFileFolder+"applicationStart"+CarpoolConfig.log4jLogFileSuffix, "'.'yyyy-MM-dd");
//			logger.addAppender(dFileAppender);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void initializeLogger(){		
		log4j.configure();		
	}
	
	public static void d(Exception e){
		//using reflection to get caller name, 500x faster than stack trace
		//if not accessible, do:  right click on project -> configure build path -> remove JRE system library -> add Library -> JRE System Library (Default) -> OK, clean & recompile
		String caller = sun.reflect.Reflection.getCallerClass(2).getName();
		logger.info(caller + " got Exception! ",e);			
		e.printStackTrace();
	}

	public static void d(String message){
		log(message);
	}
	
	public static void d(PseudoModel p){
		if (ServerConfig.isOnLocal){
			JSONObject json = JSONFactory.toJSON(p);
			log(json.toString());
		}

	}

	private static void log(String message){
		logger.info(message);
	}

}
