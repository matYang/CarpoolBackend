package carpool.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import carpool.configurations.CarpoolConfig;

public class DateUtility {
	
	public static long milisecInDay = 86400000l;
	public static long milisecInHour = 3600000l;
	
	
	public static Calendar getCurTimeInstance(){
		return Calendar.getInstance(TimeZone.getTimeZone(CarpoolConfig.timeZoneIdNY));
	}
	
	public static long getCurTime(){
		Calendar c = getCurTimeInstance();
		return c.getTimeInMillis();
	}
	

	public static String toSQLDateTime(Calendar c){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(c.getTime());
	}

	public static Calendar DateToCalendar(Date date){ 
		Calendar cal = getCurTimeInstance();
		cal.setTime(date);
		return cal;
	}

	

	public static String getNotificationDateString(Calendar day){
		return (day.MONTH+1) + "月" + day.DAY_OF_MONTH + "日";
	}

	public static int getHourDifference(Calendar startTime, Calendar endTime){
		return (int) ((endTime.getTimeInMillis() - startTime.getTimeInMillis())/ (1000*60*60));
	}


	public static Calendar castFromAPIFormat(String dateString){
		Calendar cal = getCurTimeInstance();
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cal.setTime(sdf1.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}
	
	public static String castToAPIFormat(Calendar c){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(c.getTime());
	}

	
	public static String getTimeStamp(){
		return getCurTime() +"";
	}
	

	public static Calendar convertToStandard(Calendar old){
		Calendar Standard = getCurTimeInstance();
		Calendar oldr = Calendar.getInstance(TimeZone.getTimeZone(old.getTimeZone().getID()));
		
		int hdif = Standard.get(Calendar.HOUR_OF_DAY) - oldr.get(Calendar.HOUR_OF_DAY);
		int mdif = Standard.get(Calendar.MINUTE) - oldr.get(Calendar.MINUTE);
		int sdif = Standard.get(Calendar.SECOND) - oldr.get(Calendar.SECOND);
		old.add(Calendar.SECOND,sdif);
		old.add(Calendar.MINUTE,mdif);
		old.add(Calendar.HOUR_OF_DAY,hdif);		
	
		return old;
	}

	
	public static int compareday(Calendar cal1, Calendar cal2){
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)){
			return -1;
		}
		else if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)){
			return -1;
		}
		else if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)){
			return 0;
		}
		else{
			return 1;
		}
		
	}

}
