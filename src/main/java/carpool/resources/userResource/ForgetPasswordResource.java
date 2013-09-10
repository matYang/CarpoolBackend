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
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;



public class ForgetPasswordResource extends ServerResource{

	@Get
	public Representation forgetPassword(){
        JSONObject response = null; 
        boolean isSent = false;
        String email = "";
        
        try {
        	email = java.net.URLDecoder.decode(getQuery().getValues("email"),"utf-8");
        	
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

        catch (UserNotFoundException e){
        	e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } 
        catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        response = JSONFactory.toJSON(isSent);
        
        Representation result = new JsonRepresentation(response);
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return result;
	}
	
	@Post
	public Representation findPassword(Representation entity){
		boolean passwordChanged = false;
		boolean login = false;
		boolean isValid = false;
		JSONObject jsonString = null;
		int userId = -1;
		String newPassword = "";
		String confirmNewPassword = "";
		String authCode = "";
		User topBarUser = new User();
		JSONObject jsonResponse = new JSONObject(topBarUser);
		
		if (entity != null){
			
			Series<Cookie> cookies = this.getRequest().getCookies();
			try {
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
								login = UserCookieResource.validateCookieSession(userId, cookies);
								//if not login, then log in, if login already, return conflict
								if (!login){
									
						            Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
						            cookieSettings.clear(); 
						            
						            CookieSetting newCookie = null;
									try {
										newCookie = UserCookieResource.openCookieSession(userId);
										setStatus(Status.SUCCESS_OK);
									} catch (Exception e) {
										setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
										e.printStackTrace();
									}
						            cookieSettings.add(newCookie); 
						            
						            this.setCookieSettings(cookieSettings); 
									
								}
								else{
									//already logged in, do nothing
								}
								
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
			} catch (NumberFormatException | UnsupportedEncodingException e1) {
				Common.d("id conversion error at ForgetPasswordResource, number format exception or unsuported encoding exception");
				e1.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (Exception e) {
				Common.d("cookie validation error at ForgetPasswordResource, cookie expcetion");
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		}
		else if (entity == null){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		else{
        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
        }
		

		Representation result = new JsonRepresentation(jsonResponse);

        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        try {
            Common.d(result.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
	}

    @Options
    public Representation takeOptions(Representation entity) {
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        //send anything back will be fine, browser just expects a response
        DMMessage message = new DMMessage();
        Representation result = new JsonRepresentation(message);
        Common.d("exiting Options request");
        setStatus(Status.SUCCESS_OK);
        return result;
    }

}
