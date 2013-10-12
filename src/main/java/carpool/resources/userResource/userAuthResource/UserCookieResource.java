package carpool.resources.userResource.userAuthResource;

import java.util.ArrayList;

import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.encryption.SessionCrypto;
import carpool.exception.PseudoException;
import carpool.exception.auth.AccountAuthenticationException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.model.*;

public class UserCookieResource extends ServerResource{

	/**
	 * automatically try to authenticate users on login and sensitive API calls, checks the request to see if there is the right cookie
	 * extends cookie life span if a true shall be returned  
	 * @param the request
	 * @return  true or false the current user has an active login session, if so, login the user and send data back, if not, open login modal window
	 * @throws Exception 
	 */
	public static boolean validateCookieSession(int id, String sessionString) throws PseudoException{
		if (id == -1){
			throw new AccountAuthenticationException("UserCookieResource:: validateCookieSession:: Invalid ID, ID is -1");
		}
		boolean login = false;
		if (sessionString == null){
			throw new AccountAuthenticationException("UserCookieResource:: validateCookieSession:: Session Not Exist");
		}
		else{
			try{
				String decryptedString = SessionCrypto.decrypt(sessionString);
				login =  AuthDaoService.validateUserSession(id, decryptedString);
			}
			catch (Exception e){
				e.printStackTrace();
				throw new SessionEncodingException();
			}
		}
		if (!login){
			throw new AccountAuthenticationException("UserCookieResource:: validateCookieSession:: Session Validation Failed");
		}
		return login;
	}
	
	/**
	 * simply gets the session string from cookies
	 * @param cookies
	 * @return
	 * @throws Exception
	 */
	public static String getSessionString(Series<Cookie> cookies) throws PseudoException{
		ArrayList<String> sessionString = new ArrayList<String>();
		String newEncryptedString = "";
		for( Cookie cookie : cookies){ 
			if (cookie.getName().equals(Constants.cookie_userSession)){
				sessionString.add(cookie.getValue()); 
			}
		} 
		if (sessionString.size() > 1){
			throw new DuplicateSessionCookieException();
		}
		if (sessionString.size() == 0){
			return "";
		}
		else{
			try{
				newEncryptedString = SessionCrypto.decrypt(sessionString.get(0));
			}
			catch (Exception e){
				e.printStackTrace();
				//throw new SessionEncodingException();
			}
			return newEncryptedString;
		}
	}
	
	
	/**
	 * before this method, authenticate cookie session must be checked first, and return false
	 * if user's login credentials match the expected credentials, a new cookie session is started, a new valid session string will be associated with the user
	 * @param cookies
	 * @return  success or failure to start the session
	 */
	public static CookieSetting openCookieSession(int id) throws PseudoException{
		// generate session string and stores session in Redis
        String sessionString = AuthDaoService.generateUserSession(id);
        String encryptedString = "";
        CookieSetting newCookieSetting;
        
        // store the session string in a new cookie
        try{
        	 encryptedString = SessionCrypto.encrypt(sessionString);
        	 newCookieSetting = new CookieSetting(0, Constants.cookie_userSession, encryptedString);
        	 newCookieSetting.setMaxAge(Constants.cookie_maxAge);
        }
        catch (Exception e){
			throw new SessionEncodingException();
		}
        
        DebugLog.d("UserCookie Resource, newCookieSetting brief:");
        DebugLog.d(newCookieSetting.getValue());
        DebugLog.d(newCookieSetting.getMaxAge());
        
       return newCookieSetting;
	}
	
	
	/**
	 * before this method, authenticateCookieSession must be checked first and returns true
	 * delete the session string entry in Redis, redirect to non-login version, make the session string in the cookie no longer valid
	 * @param request
	 * @return  if the session is successful or not
	 */
	public static boolean closeCookieSession(Series<Cookie> cookies) throws PseudoException{
		ArrayList<String> sessionString = new ArrayList<String>();
		boolean logout = false;
		for( Cookie cookie : cookies){ 
			if (cookie.getName().equals(Constants.cookie_userSession)){
				sessionString.add(cookie.getValue()); 
			} 
		} 
		if (sessionString.size() > 1){
			throw new DuplicateSessionCookieException();
		}
		
		if (sessionString.size() == 0){
			logout = true;
		}
		else{
			try{
				String decryptedString = SessionCrypto.decrypt(sessionString.get(0));
				logout =  AuthDaoService.closeUserSession(decryptedString);
			}
			catch (Exception e){
				throw new SessionEncodingException();
			}
			
		}
		return logout;
		
	}
	
}
