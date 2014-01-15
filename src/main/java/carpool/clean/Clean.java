package carpool.clean;


import java.util.Calendar;
import java.util.Date;

import carpool.cleanRoutineTask.MessageCleaner;
import carpool.cleanRoutineTask.RedisCleaner;
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.exception.location.LocationNotFoundException;
import carpool.model.*;


public class Clean{

	//public static final String timeZoneId = "asia/shanghai";
	public static final String timeZoneId = "America/New_York";
	public static final String fileName = "messageHistory.txt";

	public static Calendar dateToCalendar(Date date){ 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	
	public void cleanSchedules() throws LocationNotFoundException{
		MessageCleaner.Clean();
		TransactionCleaner.Clean();
		RedisCleaner.Clean();
	}


}


