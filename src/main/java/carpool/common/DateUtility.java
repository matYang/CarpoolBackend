package carpool.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

	public static String CalendarToUTCString(Calendar c){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(c.getTime());
	}

	public static String getNotificationDateString(Calendar day){
		return (day.MONTH+1) + "月" + day.DAY_OF_MONTH + "日";
	}

	public static int getHourDifference(Calendar startTime, Calendar endTime){
		return (int) ((endTime.getTimeInMillis() - startTime.getTimeInMillis())/ (1000*60*60));
	}

	/**
	 * parses the date string to a Calendar instance
	 * should use UTC date
	 * @param dateString
	 * @return
	 * @throws ParseException 
	 */
	public static Calendar parseDateString(String dateString) throws ParseException{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		cal.setTime(sdf1.parse(dateString));
		return cal;
	}
	
	
	//currently using milliseconds
	public static Calendar castFromAPIFormat(String apiDateString){
		Calendar representatedDate = Calendar.getInstance();
		representatedDate.setTimeInMillis(Long.parseLong(apiDateString));
		return representatedDate;
	}
	
	public static String castToAPIFormat(Calendar date){
		return Long.toString(date.getTimeInMillis());
	}

}
