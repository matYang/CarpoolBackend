package carpool.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import carpool.constants.Constants;
import carpool.mappings.AllProvinceMappings;
import carpool.mappings.MappingBase;


public class Validator {

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

	public static boolean isStringNullOrEmpty(String entry){
		if(entry==null){
			return true;
		}
		if(entry.equals("")){
			return true;
		}
		return false;
	}

}
