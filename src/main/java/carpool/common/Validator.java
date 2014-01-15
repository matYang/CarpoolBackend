package carpool.common;


import java.util.regex.*;

import carpool.constants.CarpoolConfig;


public class Validator {

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


	public static boolean isEmailFormatValid(String email){

		Pattern emailPattern = Pattern.compile(CarpoolConfig.RegexEmailPattern);
		try{
			if (emailPattern.matcher(email).matches() && email.length()<=CarpoolConfig.maxEmailLength) {
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			DebugLog.d(ex);
			return false;
		}
	}

	public static boolean isQqFormatValid(String qq){
		if (qq == null || qq.length() < CarpoolConfig.qqMinLength || qq.length() > CarpoolConfig.qqMaxLength){
			return false;
		}else{
			for (int i = 0; i < qq.length(); i++) {
				if (Character.isDigit(qq.charAt(i)) == false) {
					return false;
				}
			}
			return true;
		}	
	} 

	public static boolean isNameFormatValid(String userName){
		Pattern NamePattern = Pattern.compile(CarpoolConfig.RegexNamePattern);
		if (userName == null || userName.length() == 0 || userName.length() > CarpoolConfig.maxUserNameLength){
			return false;
		}	   
		if (NamePattern.matcher(userName).matches()){
			return true;
		}
		return false;
	}

	public static boolean isPasswordFormatValid(String password){
		if (password == null || password.length() < CarpoolConfig.minPasswordLength || password.length() > CarpoolConfig.maxPasswordLength){
			return false;
		}
		
		Pattern PasswordPattern = Pattern.compile(CarpoolConfig.RegexPwPattern);
		if(PasswordPattern.matcher(password).matches()){
			return true;
		}
		return false;
	}


}
