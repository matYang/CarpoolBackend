package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.EmailCrypto;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;



public class UserEmailActivationResource extends ServerResource{

	@Get
	public Representation activiateUserEmail(){
		int userId = -1;
		String authCode = "";
        User topBarUser = new User();
        JSONObject response = new JSONObject(topBarUser);; 
        Series<Cookie> cookies = this.getRequest().getCookies();
        boolean login = false;
        
        try {
        	String encryptedKey = java.net.URLDecoder.decode(getQuery().getValues("key"),"utf-8");
        	Common.d(getQuery().getValues("key"));
        	Common.d("encryptedKey: " + encryptedKey);
        	String[] decodedKey = EmailCrypto.decrypt(encryptedKey);
        	
        	userId = Integer.parseInt(decodedKey[0]);
        	authCode = decodedKey[1];
        	
        	//activate email anyways
        	topBarUser = UserDaoService.activateUserEmail(userId, authCode);
        	
        	if (topBarUser != null && topBarUser.isEmailActivated() && topBarUser.isAbleToLogin()){
        		login = UserCookieResource.validateCookieSession(userId, cookies);
        		if (!login){
        			Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
    	            cookieSettings.clear(); 
    	            
    	            CookieSetting newCookie = null;
    				newCookie = UserCookieResource.openCookieSession(userId);
    	            cookieSettings.add(newCookie); 
    	            
    	            this.setCookieSettings(cookieSettings); 
        		}
        		else{
        			//already logged in, do nothing
        		}
        		setStatus(Status.SUCCESS_OK);
        		response = JSONFactory.toJSON(topBarUser);
        	}
        	else if (topBarUser == null){
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
        	else if (!topBarUser.isEmailActivated()){
        		//alert: authCode has been expired
        		response = JSONFactory.toJSON(topBarUser);
        		setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
        	}
        	else if (!topBarUser.isAbleToLogin()){
        		//other states have gone invalid
        		response = JSONFactory.toJSON(topBarUser);
        		setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED);
        	}
        	
		} catch (UserNotFoundException e){
        	e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        
        Representation result = new JsonRepresentation(response);
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return result;
		
	}

}
