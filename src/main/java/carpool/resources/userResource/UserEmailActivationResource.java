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

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.encryption.EmailCrypto;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserEmailActivationResource extends PseudoResource{

	@Get
	public Representation activiateUserEmail(){
		int userId = -1;
		String authCode = "";
        User topBarUser = new User();
        JSONObject response = new JSONObject(topBarUser);; 

        
        try {
        	String encryptedKey = this.getQueryVal("key");
        	DebugLog.d("encryptedKey: " + encryptedKey);
        	String[] decodedKey = EmailCrypto.decrypt(encryptedKey);
        	
        	userId = Integer.parseInt(decodedKey[0]);
        	authCode = decodedKey[1];
        	
        	//activate email anyways
        	topBarUser = UserDaoService.activateUserEmail(userId, authCode);
        	
        	if (topBarUser != null && topBarUser.isEmailActivated() && topBarUser.isAbleToLogin()){
        		this.closeAuthenticationSession(userId);
        		this.clearUserCookies();
        		this.addAuthenticationSession(userId);
  
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
        	this.doPseudoException(e);
        }  catch (Exception e) {
			this.doException(e);
		}
        
        
        Representation result = new JsonRepresentation(response);

        this.addCORSHeader();
        return result;
		
	}

}
