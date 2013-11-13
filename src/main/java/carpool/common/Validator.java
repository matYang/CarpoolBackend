package carpool.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import carpool.constants.Constants;
import carpool.constants.Constants.userSearchState;


public class Validator {

	//check if the phone is in a valid format
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

	public static boolean isStringNullOrEmpty(String entry){
		if(entry==null){
			return true;
		}
		if(entry.equals("")){
			return true;
		}
		return false;
	}

	/**
	 * check if age is valid
	 * @param age
	 * @return true if age is valid
	 */
	public static boolean isAgeValid(int age){
	    if(age>5 && age<99){
	        return true;
	    }
	    return false;
	}

	/**
	 * check if user's name is in a valid format
	 * @param userName
	 * @return true if name is valid
	 */
	public static boolean isNameFormatValid(String userName){
	    if (userName == null || userName.length() == 0 || userName.length() > Constants.maxUserNameLength){
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
	 */
	public static boolean isPasswordFormatValid(String password){
	    if (password == null || password.length() == 0 || password.length() > Constants.maxPasswordLength){
	        return false;
	    }
	    return true;
	}


}
