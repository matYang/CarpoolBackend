package carpool.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.userSearchState;


public class Validator {

	/**
	 * TODO check if the phone is in a valid format, only ignore white space, -, (, )
	 * */
	public static boolean isPhoneFormatValid(String phone){
		if (phone == null){
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

	/**
	 * TODO check if the email is in a valid format
	 * */
	public static boolean isEmailFormatValid(String email){

		if (email == null || email.length() == 0 || email.length() > CarpoolConfig.maxEmailLength){
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
	
	/**
	 * check if qq is in a valid format
	 */
	public static boolean isQqFormatValid(String qq){
		if (qq == null || qq.length() == 0 || qq.length() > CarpoolConfig.maxQqLength){
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
	 * check if user's name is in a valid format
	 * TODO make sure the name does not contain special characters, like !@$,[ etc, it can only contain English and Chinese characters
	 */
	public static boolean isNameFormatValid(String userName){
	    if (userName == null || userName.length() == 0 || userName.length() > CarpoolConfig.maxUserNameLength){
	        return false;
	    }
	    //check for @
	    if (userName.indexOf("@") >= 0){
	        return false;
	    }
	
	    return true;
	}

	
	/**
	 * check if password is in a valid format
	 * TODO make sure password only contains low/capital letters, numbers, and special characters, no Chinese
	 */
	public static boolean isPasswordFormatValid(String password){
	    if (password == null || password.length() < CarpoolConfig.minPasswordLength || password.length() > CarpoolConfig.maxPasswordLength){
	        return false;
	    }
	    return true;
	}


}
