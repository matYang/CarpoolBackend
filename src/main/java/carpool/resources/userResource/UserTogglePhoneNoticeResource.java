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
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;



public class UserTogglePhoneNoticeResource extends ServerResource{


	@Put
	/**
	 * used for user to toggle phone notification state
	 * @param entity
	 * @return	the new state of phone notification
	 */
	public Representation togglePhoneNotice(Representation entity) {
		int userId = -1;
		JSONObject response = new JSONObject();
		boolean phoneNotice = false;
		boolean toggleSuccess = false;
		
		if (entity == null || (entity != null && entity.getSize() < Constants.max_userLength)){
			try {
				userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
				if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
					phoneNotice = Boolean.parseBoolean(java.net.URLDecoder.decode(getQuery().getValues("phoneNotice"),"utf-8"));
					
					toggleSuccess = UserDaoService.togglePhoneNotice(userId, phoneNotice);
					if (toggleSuccess){
						response = JSONFactory.toJSON(phoneNotice);
						setStatus(Status.SUCCESS_OK);
					}
					else{
						setStatus(Status.CLIENT_ERROR_FORBIDDEN);
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
			} catch(UnsupportedEncodingException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch(IOException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (Exception e) {
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		else{
			setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
		}
		
		Representation result = new JsonRepresentation(response);
		
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
