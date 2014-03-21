package carpool.common;


import java.util.regex.*;

import carpool.configurations.ValidationConfig;


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

		Pattern emailPattern = Pattern.compile(ValidationConfig.RegexEmailPattern);
		try{
			if (emailPattern.matcher(email).matches() && email.length()<=ValidationConfig.maxEmailLength) {
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
		if (qq == null || qq.length() < ValidationConfig.qqMinLength || qq.length() > ValidationConfig.qqMaxLength){
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
		Pattern NamePattern = Pattern.compile(ValidationConfig.RegexNamePattern);
		Pattern WhiteSpacePattern = Pattern.compile(ValidationConfig.RegexNameWhiteSpacePattern);
		if (userName == null || userName.length() == 0 || userName.length() > ValidationConfig.maxUserNameLength){
			return false;
		}	
		if(WhiteSpacePattern.matcher(userName).matches()){
			userName = userName.replaceAll("\\s+", "");
		}
		if (NamePattern.matcher(userName).matches()){
			return true;
		}
		return false;
	}

	public static boolean isPasswordFormatValid(String password){
		if (password == null || password.length() < ValidationConfig.minPasswordLength || password.length() > ValidationConfig.maxPasswordLength){
			return false;
		}
		
		Pattern PasswordPattern = Pattern.compile(ValidationConfig.RegexPwPattern);
		if(PasswordPattern.matcher(password).matches()){
			return true;
		}
		return false;
	}


}
