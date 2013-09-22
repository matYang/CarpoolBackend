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
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.encryption.EmailCrypto;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.EntityTooLargeException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class ForgetPasswordResource extends PseudoResource{

	@Get
	public Representation forgetPassword(){
        JSONObject response = null; 
        boolean isSent = false;
        String email = "";
        
        try {
        	email = this.getQueryVal("email");
        	
        	//check the email format first
        	if (Common.isEmailFormatValid(email)){
        		//if the email format is valid, check if this email has been registered
        		if (!UserDaoService.isEmailAvailable(email)){
        			//this will need a translation from email to id, another SQL query, wonder if could be improved
            		isSent = UserDaoService.sendChangePasswordEmail(email);
            		if (isSent){
            			setStatus(Status.SUCCESS_OK);
            		}
            		else{
            			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
            		}
            	}
            	else{
            		setStatus(Status.CLIENT_ERROR_CONFLICT);
            	}
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        	}
		} 

        catch (PseudoException e){
        	this.doPseudoException(e);
        } 
        catch (Exception e) {
			this.doException(e);
		}
        response = JSONFactory.toJSON(isSent);
        Representation result = new JsonRepresentation(response);
        
        this.addCORSHeader();
        return result;
	}
	
	@Post
	public Representation findPassword(Representation entity){
		boolean passwordChanged = false;
		boolean isValid = false;
		JSONObject jsonString = null;
		int userId = -1;
		String newPassword = "";
		String confirmNewPassword = "";
		String authCode = "";
		User topBarUser = new User();
		JSONObject jsonResponse = new JSONObject(topBarUser);
		
		try {
			this.checkEntity(entity);

			jsonString = (new JsonRepresentation(entity)).getJsonObject();
			
			String key = jsonString.getString("key");
			String[] keys = EmailCrypto.decrypt(key);
			userId = Integer.parseInt(keys[0]);
			authCode = keys[1];
			
			newPassword = jsonString.getString("newPassword");
			confirmNewPassword = jsonString.getString("confirmNewPassword");
			
			isValid = UserDaoService.isResetPasswordValid(userId, authCode);
			if (isValid){
				if (User.isPasswordFormatValid(newPassword) && newPassword.equals(confirmNewPassword)){
					passwordChanged = UserDaoService.resetUserPassword(userId, newPassword);
					
					if (passwordChanged){
						//the only thing to check here is for email, try combine them into one
						topBarUser = UserDaoService.getTopBarUserById(userId);
						
						if (topBarUser != null && topBarUser.isAbleToLogin()){
							
							this.closeAuthenticationSession(userId);
				            this.clearUserCookies();
				            this.addAuthenticationSession(userId);
							
							jsonResponse = JSONFactory.toJSON(topBarUser);
						}
						else if (topBarUser == null){
							setStatus(Status.CLIENT_ERROR_FORBIDDEN);
						}
						else{
							setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
							jsonResponse = JSONFactory.toJSON(topBarUser);
						}
					}
					else{
						setStatus(Status.SERVER_ERROR_INTERNAL);
					}
				}
				else{
					setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
				}
			}
			else{
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}

			
			
		} catch (PseudoException e) {
			this.doPseudoException(e);
		} catch (Exception e) {
			this.doException(e);
		}
		

		Representation result = new JsonRepresentation(jsonResponse);
        this.printResult(result);
        this.addCORSHeader();
        return result;
	}


}
