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
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;
import carpool.resources.dianmingResource.DMResource;




public class LogInResource extends ServerResource{

	/*
	public static int authCodeGenerator(){
		int min = 0;
		int max = 100000000;
		//generating random numbers from 0 to 100,000,000
		int ranNum = min + (int)(Math.random() * ((max - min) + 1));
		return ranNum;
	}
	*/
	
		
	@Post
	public Representation loginAuthentication(Representation entity){
		boolean login = false;
		JSONObject jsonString = null;
		User topBarUser = new User();
		JSONObject jsonObject = new JSONObject();
		String email = "";
		String password = "";
		Series<Cookie> cookies = this.getRequest().getCookies();
		
		if (entity != null){
			
			try {
				jsonString = (new JsonRepresentation(entity)).getJsonObject();
				email = jsonString.getString("email");
				password = jsonString.getString("password");
				
				Common.d("log in, receving paramters: " + email + " " + password);
				topBarUser = UserDaoService.isLoginUserValid(email, password);
				
				if (topBarUser != null && topBarUser.isAbleToLogin()){
					
					login = UserCookieResource.validateCookieSession(topBarUser.getUserId(), cookies);
					//if not login, then log in, if login already, do nothing
					if (!login){
						
			            Series<CookieSetting> cookieSettings = this.getResponse().getCookieSettings(); 
			            cookieSettings.clear(); 
			            
			            CookieSetting newCookie = null;
						try {
							//TODO last login
							newCookie = UserCookieResource.openCookieSession(topBarUser.getUserId());
						} catch (Exception e) {
							setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
							e.printStackTrace();
						}
						
						Common.d("Login, newCookieSetting brief:");
				        Common.d(newCookie.getValue());
				        Common.d(newCookie.getMaxAge());
						
						
			            cookieSettings.add(newCookie); 
			            
			            this.getResponse().setCookieSettings(cookieSettings); 
			            //this.getResponse().getCookieSettings().add(newCookie);
						
					}
					else{
						//already logged in, do nothing to avoid duplicate session
					}
					
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
				Common.d("id conversion error at LoginResource, number format exception or unsuported encoding exception");
				e1.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (Exception e) {
				Common.d("cookie validation error at Login Resource, cookie expcetion");
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

		
		
//		String sessionString = "";
//		try {
//			sessionString = UserCookieResource.getSessionString( this.getResponse().getCookieSettings());
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		Common.d("login opening session string: " + sessionString);
		
		
		
		Representation result = new JsonRepresentation(jsonObject);

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

