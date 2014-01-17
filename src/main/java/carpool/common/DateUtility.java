package carpool.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import carpool.constants.CarpoolConfig;

public class DateUtility {

	public static String toSQLDateTime(Calendar c){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(c.getTime());
	}

	public static Calendar DateToCalendar(Date date){ 
		Calendar cal = Calendar.getInstance();
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
		Calendar cal = Calendar.getInstance();
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
	
	public static long getCurTime(){
		Calendar c = Calendar.getInstance();
		return c.getTimeInMillis();
	}
	
	public static String getTimeStamp(){
		return getCurTime() +"";
	}

	public static Calendar ConvertToStandard(Calendar old){
		//System.out.println("old time: "+DateUtility.castToAPIFormat(old));
		
		Calendar Standard = Calendar.getInstance(TimeZone.getTimeZone(CarpoolConfig.standardTimeZone));
		//System.out.println("standard time: "+DateUtility.castToAPIFormat(Standard));
		Calendar oldr = Calendar.getInstance(TimeZone.getTimeZone(old.getTimeZone().getID()));
		
		int hdif = Standard.get(Calendar.HOUR_OF_DAY) - oldr.get(Calendar.HOUR_OF_DAY);
		int mdif = Standard.get(Calendar.MINUTE) - oldr.get(Calendar.MINUTE);
		int sdif = Standard.get(Calendar.SECOND) - oldr.get(Calendar.SECOND);
		old.add(Calendar.SECOND,sdif);
		old.add(Calendar.MINUTE,mdif);
		old.add(Calendar.HOUR_OF_DAY,hdif);		
	
		
		return old;
	}

}
