package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.dianmingResource.DMResource;




public class LogInResource extends PseudoResource{

	
	@Post
	public Representation loginAuthentication(Representation entity){
		boolean login = false;
		JSONObject jsonString = null;
		User topBarUser = new User();
		JSONObject jsonObject = new JSONObject();
		String email = "";
		String password = "";
		Series<Cookie> cookies = this.getRequest().getCookies();
		

		try {
			this.checkEntity(entity);
			
			jsonString = (new JsonRepresentation(entity)).getJsonObject();
			email = jsonString.getString("email");
			password = jsonString.getString("password");
			
			Common.d("log in, receving paramters: " + email + " " + password);
			topBarUser = UserDaoService.isLoginUserValid(email, password);
			
			if (topBarUser != null && topBarUser.isAbleToLogin()){
				
				login = UserCookieResource.validateCookieSession(topBarUser.getUserId(), cookies);
					
				this.closeAuthenticationSession(topBarUser.getUserId());
	            this.clearUserCookies();
	            this.addAuthenticationSession(topBarUser.getUserId());
				
				jsonObject = JSONFactory.toJSON(topBarUser);
				setStatus(Status.SUCCESS_OK);
			}
			else{
				//if user failed authentication, do not return topBarUser
				if (topBarUser == null){
					setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				}
				//if user fails account state validation, eg email not activated, still return topBarUser
				else{
					jsonObject = JSONFactory.toJSON(topBarUser);
					setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
				}
			}
		
		} catch (PseudoException e){
        	this.doPseudoException(e);
        } 
		catch (Exception e) {
			this.doException(e);
		}
			
		
		Representation result = new JsonRepresentation(jsonObject);

        this.addCORSHeader();
        return result;
	}

	
}

