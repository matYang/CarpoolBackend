package carpool.clean;


import java.util.Calendar;
import java.util.Date;

import carpool.cleanRoutineTask.MessageCleaner;
import carpool.cleanRoutineTask.RedisCleaner;
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.common.DateUtility;
import carpool.exception.location.LocationNotFoundException;
import carpool.model.*;


public class Clean{

	public static Calendar dateToCalendar(Date date){ 
		Calendar calendar = DateUtility.getCurTimeInstance();
		calendar.setTime(date);
		return calendar;
	}

	
	public void cleanSchedules() throws LocationNotFoundException{
		MessageCleaner.Clean();
		TransactionCleaner.Clean();
		RedisCleaner.Clean();
	}


}


