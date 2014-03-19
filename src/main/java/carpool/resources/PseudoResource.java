package carpool.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.configurations.CarpoolConfig;
import carpool.dbservice.AuthDaoService;
import carpool.encryption.SessionCrypto;
import carpool.exception.PseudoException;
import carpool.exception.auth.AccountAuthenticationException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.validation.EntityTooLargeException;
import carpool.factory.JSONFactory;
import carpool.resources.userResource.userAuthResource.UserAuthenticationResource;

public class PseudoResource extends ServerResource{
	
	/*set the response header to allow for CORS*/
	public void addCORSHeader(){
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
        
		if (responseHeaders == null) { 
			responseHeaders = new Series(Header.class); 
			responseHeaders.add("Access-Control-Allow-Origin", "*");
			responseHeaders.add("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
			responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
			responseHeaders.add("Access-Control-Allow-Headers", "authCode");
			responseHeaders.add("Access-Control-Allow-Headers", "origin, x-requested-with, content-type");
		}
		
		if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        }
	}

	protected Object parseJSON(Representation entity) throws PseudoException{
		return null;
	}
	
	public void checkEntity(Representation entity) throws EntityTooLargeException{
		if (entity != null && entity.getSize() > CarpoolConfig.max_PostLength){
			throw new EntityTooLargeException();
		}
	}
	
	public void checkFileEntity(Representation entity) throws NullPointerException, EntityTooLargeException{
		if (entity != null && entity.getSize() > CarpoolConfig.max_FileLength){
			throw new EntityTooLargeException();
		}
	}

	
	/******************
	 * 
	 *  Cookie Area
	 *  
	 ******************/
	public boolean validateAuthentication(int userId) throws PseudoException{
		return !CarpoolConfig.cookieEnabled ? true : UserAuthenticationResource.validateCookieSession(userId, this.getSessionString());
	}

	
	public void addAuthenticationSession(int userId) throws PseudoException{
		Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
		CookieSetting newCookie = UserAuthenticationResource.openCookieSession(userId);
		cookieSettings.clear();
		cookieSettings.add(newCookie);
		this.setCookieSettings(cookieSettings);
	}
	
	public void closeAuthenticationSession(int userId) throws PseudoException{
		Series<Cookie> cookies = this.getRequest().getCookies();
		UserAuthenticationResource.closeCookieSession(cookies);
		Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
		cookieSettings.clear();
		this.setCookieSettings(cookieSettings);
	}
	
	
	
	/******************
	 * 
	 * Query Area
	 * 
	 ******************/
	public String getReqAttr(String fieldName) throws UnsupportedEncodingException{
		Object attr = this.getRequestAttributes().get(fieldName);
		return attr != null ? java.net.URLDecoder.decode((String)attr, "utf-8") : null;
	}
	
	public String getQueryVal(String fieldName) throws UnsupportedEncodingException{
		String val = getQuery().getValues(fieldName);
		return val != null ? java.net.URLDecoder.decode(val, "utf-8") : null;
	}
	
	public String getPlainQueryVal(String fieldName){
		String val = getQuery().getValues(fieldName);
		return val;
	}
	
	public String getToUtf(String var) throws UnsupportedEncodingException{
		return java.net.URLEncoder.encode(var, "utf-8");
	}
	
	
	/******************
	 * 
	 * Exception Handling Area
	 * 
	 ******************/
	public StringRepresentation doPseudoException(PseudoException e){
		DebugLog.d(e);
		switch(e.getCode()){
			case 1: case 2: case 4: case 8: case 19: case 21:
				//Not Found
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				break;
			case 3: case 5: case 9: case 20:
				//OwnerNotMatch
				setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
				break;
			case 6:
				//TransactionAccessViolation
				setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY);
				break;
			case 7:
				//TransactionStateViolation
				setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
				break;
			case 10: case 11:
				//DuplicateSessionCookie: //SessionEncoding
				this.getResponse().getCookieSettings().clear();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				break;
//			case 12:  gone
//				//UnacceptableSearchState
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				break;
			case 13:
				//UnexceppedCookie
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				break;
			case 14:
				//AccountAuthentication
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				break;
			case 15:
				//EntityTooLarge
				setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
				break;
			case 16:
				//LocationNotFoundException
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				break;
			case 17:
				//LocationException
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				break;
			case 18:
				//ValidationException
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				break;
			default:
				setStatus(Status.SERVER_ERROR_INTERNAL);
				break;
		}
		return new StringRepresentation(e.getExceptionText());
	}
	
	public StringRepresentation doException(Exception e){
		DebugLog.d(e);
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("不好意思..哪里弄错了，请稍后重试");
	}
	
	public Representation buildQuickResponse(String responseText){
		JSONObject response = new JSONObject();	
		response = JSONFactory.toJSON(responseText);
		return new JsonRepresentation(response);
	}
	
	public Representation quickRespond(String responseText){
		addCORSHeader();
		return buildQuickResponse(responseText);
	}
	
	/******************
	 * 
	 * Take the options
	 * 
	 ******************/
    //needed here since backbone will try to send OPTIONS to /id before PUT or DELETE
    @Options
    public Representation takeOptions(Representation entity) {
    	addCORSHeader();
    	setStatus(Status.SUCCESS_OK);
        return new JsonRepresentation(new JSONObject());
    }
    
    
    /******************
     * 
     *  New Session Area
     *  
     ******************/
    protected boolean validateAuthentication() throws PseudoException{
		return !CarpoolConfig.cookieEnabled ? true : validateCookieSession();
	}

	protected String generateAuthenticationSessionString(int userId) throws PseudoException{
		return generateSesstionString(userId);
	}
	
	protected void openAuthenticationSession(int userId) throws PseudoException{
		Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
		CookieSetting newCookie = openSession(userId);
		cookieSettings.add(newCookie);
		this.setCookieSettings(cookieSettings);
	}
	
	protected void closeAuthenticationSession() throws PseudoException{
		closeSession();
	}
	
	protected String getSessionString() throws PseudoException{
		ArrayList<String> sessionString = new ArrayList<String>();
		String newDecryptedString = "";
		
		//first check header for auth, if not in header, then check for cookies for auth
		Series<Header> requestHeaders = (Series<Header>) getRequest().getAttributes().get("org.restlet.http.headers");
		if (requestHeaders != null) {
			if (requestHeaders.getFirstValue(CarpoolConfig.cookie_userSession, true) != null){
				sessionString.add(requestHeaders.getFirstValue(CarpoolConfig.cookie_userSession, true));
			}
		}
		if (sessionString.size() == 0){
			Series<Cookie> cookies = this.getRequest().getCookies();
			for( Cookie cookie : cookies){ 
				if (cookie.getName().equals(CarpoolConfig.cookie_userSession)){
					sessionString.add(cookie.getValue()); 
				}
			} 
		}
		
//		if (sessionString.size() > 1){
//			throw new DuplicateSessionCookieException();
//		}
		if (sessionString.size() == 0){
			return "";
		}
		else{
			try{
				newDecryptedString = SessionCrypto.decrypt(sessionString.get(0));
			}
			catch (Exception e){
				e.printStackTrace();
				throw new SessionEncodingException();
			}
			return newDecryptedString;
		}
	}
	
	protected int getUserIdFromSessionString(String sessionString)throws PseudoException{
		String userIdStr = sessionString.split(CarpoolConfig.redisSeperatorRegex)[1];
		int userId = -1;
		try{
			userId = Integer.parseInt(userIdStr);
		} catch (NumberFormatException e){
			throw new AccountAuthenticationException("UserCookieResource:: getSessionString:: Session does not exist");
		}
		
		return userId;
	}
	
    
    /******************
     * 
     *  Authentication Area
     *  
     ******************/
	private String generateSesstionString(int userId) throws PseudoException{
		// generate session string and stores session in Redis
		 String sessionString = AuthDaoService.generateUserSession(userId);
		 try{
			 String encrypted = SessionCrypto.encrypt(sessionString);
			 return encrypted;
		 } catch (Exception e){
			 throw new SessionEncodingException();
		 }
	}
	
	
	private boolean validateCookieSession() throws PseudoException{
		String sessionString = getSessionString();
		if (sessionString == null || sessionString.length() == 0){
			return false;
		}
		int userId = getUserIdFromSessionString(sessionString);
		if (userId == -1){
			throw new AccountAuthenticationException("UserCookieResource:: validateCookieSession:: Invalid ID, ID is -1");
		}
		boolean login = false;
		
		try{
			String decryptedString = SessionCrypto.decrypt(sessionString);
			login =  AuthDaoService.validateUserSession(userId, decryptedString);
		}
		catch (Exception e){
			e.printStackTrace();
			throw new SessionEncodingException();
		}

		if (!login){
			throw new AccountAuthenticationException("UserCookieResource:: validateCookieSession:: Session Validation Failed");
		}
		return login;
	}
	
	
	private CookieSetting openSession(int userId) throws PseudoException{
        String encryptedString = generateSesstionString(userId);
        CookieSetting newCookieSetting;
        try{
        	 newCookieSetting = new CookieSetting(0, CarpoolConfig.cookie_userSession, encryptedString);
        	 newCookieSetting.setMaxAge(CarpoolConfig.cookie_maxAge);
        }
        catch (Exception e){
			throw new SessionEncodingException();
		}
        
       return newCookieSetting;
	}
	
	
	private boolean closeSession() throws PseudoException{
		try{
			String sessionString = getSessionString();
			String decryptedString = SessionCrypto.decrypt(sessionString);
			return AuthDaoService.closeUserSession(decryptedString);
		}
		catch (AccountAuthenticationException e){
			DebugLog.d(e);
			return true;
		}
		catch (Exception e){
			DebugLog.d(e);
			throw new SessionEncodingException();
		}
	}
	
}
