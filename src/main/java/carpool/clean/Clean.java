package carpool.clean;


import java.util.Calendar;
import java.util.Date;

import carpool.cleanRoutineTask.MessageCleaner;
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.model.*;


public class Clean{

	public static final String timeZoneId = "asia/shanghai";
	public static final String fileName = "messageHistory.txt";

	public static Calendar dateToCalendar(Date date){ 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static void writeMessageToFile(Message message){
		//TODO add to use S3 storage module later
	}

	
	public void cleanSchedules(){
		MessageCleaner.Clean();
		TransactionCleaner.Clean();
	}


}


