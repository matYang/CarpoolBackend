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

public class ChangePasswordResource extends ServerResource{

	//parses passwords from a JSONObject format of {oldPassword: oldPassword, newPassword: newPassword, confirmNewPassword: confirmNewPassword}
	//return String array length of 2, [0] storing the oldPassword, [1] storing the newPassword, returns null of the password format is not correct
	private String[] parseJSON(Representation entity){
		JSONObject jsonPasswords = null;
		String[] passwords = new String[2];
		
		try {
			jsonPasswords = (new JsonRepresentation(entity)).getJsonObject();
			
			String oldPassword = jsonPasswords.getString("oldPassword");
			String newPassword = jsonPasswords.getString("newPassword");
			String confirmNewPassword = jsonPasswords.getString("confirmNewPassword");
			//no DB interaction here
			if (User.isPasswordFormatValid(oldPassword) && User.isPasswordFormatValid(newPassword) && User.isPasswordFormatValid(confirmNewPassword) && newPassword.equals(confirmNewPassword)){
				passwords[0] = oldPassword;
				passwords[1] = newPassword;
			}
			else{
				passwords = null;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (NullPointerException e){
			  e.printStackTrace();
			  Common.d("likely invalid location string format");
			  return null;
		}

		return passwords;
	}
	

	@Put
	/**
	 * allows user to change password
	 * @param entity
	 * @return
	 */
	public Representation changePassword(Representation entity) {
		int userId = -1;
		String[] passwords = new String[2];
		JSONObject response = new JSONObject();
		
		if (entity != null && entity.getSize() < Constants.max_userLength){
			try {
				userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
				if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
					userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
					passwords = parseJSON(entity);
					if (passwords != null){
						boolean passwordChaneged = UserDaoService.changePassword(userId, passwords[0], passwords[1]);
						if (passwordChaneged){
							setStatus(Status.SUCCESS_OK);
						}
						else{
							setStatus(Status.CLIENT_ERROR_CONFLICT);
						}
						response = JSONFactory.toJSON(passwordChaneged);
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
