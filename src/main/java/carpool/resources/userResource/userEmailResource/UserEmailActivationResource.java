package carpool.resources.userResource.userEmailResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.configurations.EnumConfig;
import carpool.dbservice.*;
import carpool.encryption.EmailCrypto;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserEmailActivationResource extends PseudoResource{

	@Get
	public Representation activiateUserEmail(){
		int userId = -1;
		String authCode = "";
        User user = null;
        JSONObject response = new JSONObject(); 

        
        try {
        	String encryptedKey = this.getPlainQueryVal("key");
        	encryptedKey = java.net.URLEncoder.encode(encryptedKey, "utf-8");
        	String[] decodedKey = EmailCrypto.decrypt(encryptedKey);
        	
        	userId = Integer.parseInt(decodedKey[0]);
        	authCode = decodedKey[1];
        	
        	//activate email anyways
        	user = EmailDaoService.activateUserEmail(userId, authCode);
        	
        	if (user != null && user.isEmailActivated() && user.isAbleToLogin()){
        		this.closeAuthenticationSession(userId);
        		this.addAuthenticationSession(userId);
  
        		setStatus(Status.SUCCESS_OK);
        		response = JSONFactory.toJSON(user);
        	}
        	else if (user == null){
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
        	else if (!user.isEmailActivated()){
        		//alert: authCode has been expired
        		response = JSONFactory.toJSON(user);
        		throw new ValidationException("Email Authcode expired");
        	}
        	else if (!user.isAbleToLogin()){
        		response = JSONFactory.toJSON(user);
        		throw new ValidationException("User can not log in");
        	}
        	
		} catch (UserNotFoundException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        }  catch (Exception e) {
			return this.doException(e);
		}
        
        
        Representation result = new JsonRepresentation(response);

        this.addCORSHeader();
        return result;
		
	}

}
