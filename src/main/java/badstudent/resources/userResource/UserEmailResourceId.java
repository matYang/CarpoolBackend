package badstudent.resources.userResource;

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
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.exception.user.UserNotFoundException;
import badstudent.model.*;
import badstudent.mappings.*;

public class UserEmailResourceId extends ServerResource{


	@Put
	/**
	 * @param entity   
	 */
	public Representation changeEmail(Representation entity) {
		Series<Cookie> cookies = this.getRequest().getCookies();
		JSONObject jsonString = new JSONObject();
		boolean emailChanged = false;

		
		if (entity != null && entity.getSize() < Constants.max_userLength){
			
			try {
				int userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
				String email = java.net.URLDecoder.decode(getQuery().getValues("email"),"utf-8");
				//TODO: maybe better to also send back old email and check against it
				//though placing userId in post entity, when checking cookie session the id is still effectively verified
				if (UserCookieResource.validateCookieSession(userId, cookies)){
					String sessionString = UserCookieResource.getSessionString(cookies);
		        	if (Common.isEmailFormatValid(email)){
		        		if (UserDaoService.isEmailAvailable(email)){
		    				emailChanged = UserDaoService.changeEmail(userId, email, sessionString);
		    				if (emailChanged){
		    					setStatus(Status.SUCCESS_OK);
		    					boolean logout = UserCookieResource.closeCookieSession(cookies);
		    					if (!logout){
		    						setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
		    					}
		    				}
		    				else{
		    					setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
		    				}
		    				jsonString = JSONFactory.toJSON(emailChanged);
		        		}
		        		else{
		        			setStatus(Status.CLIENT_ERROR_CONFLICT);
		        		}
		        	}
		        	else{
		        		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
			} catch (NumberFormatException e) {
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (JSONException e) {
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (NullPointerException e){
				  e.printStackTrace();
				  setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (IOException e) {
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (Exception e) {
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
		
		Representation result = new JsonRepresentation(jsonString);
		
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}

	
	
	//needed here since backbone will try to send OPTIONS before POST
	@Options
	public Representation takeOptions(Representation entity) {
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 

		/*send anything back will be fine, browser only expects a response
		Message message = new Message();
		Representation result = new JsonRepresentation(message);*/

		return null;
	}


}
