package badstudent.common;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import badstudent.mappings.AllProvinceMappings;
import badstudent.mappings.MappingBase;
import badstudent.model.DMMessage;
import badstudent.model.User;

public class Common {

	public static void d(String message){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + message);
	}

	public static void d(int number){
		System.out.println("DEBUG MESSAGE BY BAD STUDENT " + number);
	}


	public static void d_Chinese(String message){
		try {
			PrintStream ps = new PrintStream(System.out, true, "UTF-8");
			ps.println(message);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

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

	public static void removeFromSocialList(ArrayList<User> list,int id){
		for(User user : list){
			if(user.getUserId()==id){
				list.remove(user);
				break;
			}
		}
	}
	
	public static void removeFromWatchList(ArrayList<DMMessage> list,int id){
		for(DMMessage msg : list){
			if(msg.getMessageId()==id){
				list.remove(msg);
				break;
			}
		}
	}

	//check if the phone is in a valid format
	public static boolean isPhoneFormatValid(String phone){
		if (phone == null || phone.length() != Constants.fixedPhoneLength){
			return false;
		}
		//check if every digit is a number
		for (int i = 0; i < phone.length(); i++) {
			if (Character.isDigit(phone.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	//check if the email is in a valid format
	public static boolean isEmailFormatValid(String email){

		if (email == null || email.length() == 0 || email.length() > Constants.maxEmailLength){
			return false;
		}
		int index = email.indexOf("@");
		if (index < 0){
			return false;
		}
		else{
			if (email.indexOf(".", index) < 0){
				return false;
			}
		}
		return true;
	}
	//check if qq is in a valid format
	public static boolean isQqFormatValid(String qq){
		if (qq == null || qq.length() == 0 || qq.length() > Constants.maxQqLength){
			return false;
		}

		//if qq is longer than 15 or contains "@", assume it uses email,turn to check for email format now
		if (qq.length() > 15 || qq.indexOf("@") > 0){
			int index = qq.indexOf("@");
			if (index < 0){
				return false;
			}
			else{
				if (qq.indexOf(".", index) < 0){
					return false;
				}
			}
		}
		return true;
	} 

	/**
	 * removes the image at the image path
	 * @param imgPath
	 * @return	true if removed, false if not
	 */
	public static boolean removePreviousImg(String imgPath) {
		// TODO
		return false;
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

	public static String getCallerName(){
		return new Throwable().fillInStackTrace().getStackTrace()[2].getMethodName();
	}
	
	public static String getNotificationDateString(Calendar day){
		return (day.MONTH+1) + "月" + day.DAY_OF_MONTH + "日";
	}
	
	public static int getHourDifference(Calendar startTime, Calendar endTime){
		return (int) ((endTime.getTimeInMillis() - startTime.getTimeInMillis())/ (1000*60*60));
	}
	
	public static boolean isTimeValid(Calendar startTime, Calendar endTime){
		Calendar curTime = Calendar.getInstance();
		return (startTime.getTimeInMillis() < endTime.getTimeInMillis()) && endTime.getTimeInMillis() > curTime.getTimeInMillis();
	}


	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**below is v0.9 legacy**/
	public static Date getCurrentDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd");  
		String today = formatter.format(new Date());
		Date retVal = null;
		try {
			retVal =  formatter.parse(today);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	public static boolean isEntryNull(String entry){
		if(entry==null){
			return true;
		}
		if(entry.equals("")){
			return true;
		}
		return false;
	}

	public static String extend(String shitYouAreTooShort){
		Common.d("extend::" + shitYouAreTooShort);
		int length = shitYouAreTooShort.length();
		if ( length < 3){
			for (int i = 0; i < 3 - length; i++){
				shitYouAreTooShort += ("x");
			}
		}
		return shitYouAreTooShort; 
	}
	/*
	public static boolean infoHasChanged(Message message, Message oldMessage) {
		if (!(message.getEmail() == null && oldMessage.getEmail() == null) || (message.getEmail() != null && oldMessage.getEmail() != null && message.getEmail().compareTo(oldMessage.getEmail()) == 0)){
			return true;
		}
		if (!(message.getPhone() == null && oldMessage.getPhone() == null) || (message.getPhone() != null && oldMessage.getPhone() != null && message.getPhone().compareTo(oldMessage.getPhone()) == 0)){
			return true;
		}
		if (!(message.getQq() == null && oldMessage.getQq() == null) || (message.getQq() != null && oldMessage.getQq() != null && message.getQq().compareTo(oldMessage.getQq()) == 0)){
			return true;
		}
		if (!(message.getTwitter() == null && oldMessage.getTwitter() == null) || (message.getTwitter() != null && oldMessage.getTwitter() != null && message.getTwitter().compareTo(oldMessage.getTwitter()) == 0)){
			return true;
		}
		if (!(message.getSelfDefined() == null && oldMessage.getSelfDefined() == null) || (message.getSelfDefined() != null && oldMessage.getSelfDefined() != null && message.getSelfDefined().compareTo(oldMessage.getSelfDefined()) == 0)){
			return true;
		}

		return false;
	}
	*/




}
