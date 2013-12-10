package carpool.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.constants.CarpoolConfig;
import carpool.exception.PseudoException;
import carpool.exception.validation.EntityTooLargeException;
import carpool.factory.JSONFactory;
import carpool.resources.userResource.userAuthResource.UserCookieResource;

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
	
	public void checkEntity(Representation entity) throws NullPointerException, EntityTooLargeException{
		if (entity == null){
			throw new NullPointerException();
		}
		else if (entity != null && entity.getSize() > CarpoolConfig.max_PostLength){
			throw new EntityTooLargeException();
		}
	}
	
	public void checkFileEntity(Representation entity) throws NullPointerException, EntityTooLargeException{
		if (entity == null){
			throw new NullPointerException();
		}
		else if (entity != null && entity.getSize() > CarpoolConfig.max_FileLength){
			throw new EntityTooLargeException();
		}
	}

	
	public boolean validateAuthentication(int userId) throws PseudoException{
		if (!CarpoolConfig.cookieEnabled){
			return true;
		}
		return UserCookieResource.validateCookieSession(userId, this.getSessionString());
	}
	
	public void clearUserCookies(){
		Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
        cookieSettings.clear(); 
	}
	
	public void addAuthenticationSession(int userId) throws PseudoException{
		Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
		CookieSetting newCookie = UserCookieResource.openCookieSession(userId);
		cookieSettings.add(newCookie);
		this.setCookieSettings(cookieSettings);
	}
	
	public void closeAuthenticationSession(int userId) throws PseudoException{
		Series<Cookie> cookies = this.getRequest().getCookies();
		UserCookieResource.closeCookieSession(cookies);
	}
	
	public String getSessionString() throws PseudoException{
		Series<Cookie> cookies = this.getRequest().getCookies();
		String sessionString = UserCookieResource.getSessionString(cookies);
		return sessionString;
	}
	
	public String getReqAttr(String fieldName) throws UnsupportedEncodingException{
		Object attr = this.getRequestAttributes().get(fieldName);
		return attr != null ? java.net.URLDecoder.decode((String)attr, "utf-8") : null;
	}
	
	public String getQueryVal(String fieldName) throws UnsupportedEncodingException{
		String val = getQuery().getValues(fieldName);
		return val != null ? java.net.URLDecoder.decode(val, "utf-8") : null;
	}
	
	public String getSearchQueryVal(String fieldName){
		String val = getQuery().getValues(fieldName);
		return val;
	}
	
	public StringRepresentation doPseudoException(PseudoException e){
		DebugLog.d(e);
		switch(e.getCode()){
			case 1: case 2: case 4: case 8: case 19:
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
			case 12:
				//UnacceptableSearchState
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				break;
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
				//InformationValidation
				setStatus(Status.CLIENT_ERROR_CONFLICT);
				break;
			case 17:
				//LocationException
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				break;
			case 18:
				//ValidationException
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				break;
			case 21:
				//LocationNotFoundException
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			default:
				setStatus(Status.SERVER_ERROR_INTERNAL);
				break;
		}
		return new StringRepresentation(e.getExceptionText());
	}
	
	public StringRepresentation doException(Exception e){
		DebugLog.d(e);
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("Oops, something went wrong, please try again later");
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

    //needed here since backbone will try to send OPTIONS to /id before PUT or DELETE
    @Options
    public Representation takeOptions(Representation entity) {
    	addCORSHeader();
    	setStatus(Status.SUCCESS_OK);
        return new JsonRepresentation(new JSONObject());
    }

}
