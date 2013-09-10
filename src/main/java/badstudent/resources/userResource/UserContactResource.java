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

public class UserContactResource extends ServerResource{

	//parses contact information from a JSONObject consisting of name, age, gender, phone, qq
	//return JSONObject if all fields are valid, null if not
	private JSONObject parseJSON(Representation entity){
		JSONObject jsonContact = null;
		
		try {
			jsonContact = (new JsonRepresentation(entity)).getJsonObject();
			
			String name = jsonContact.getString("name");
			int age = jsonContact.getInt("age");
			int gender = jsonContact.getInt("gender");
			String phone = jsonContact.getString("phone");
			String qq = jsonContact.getString("qq");
			//no DB interaction here
			if (!(User.isNameFormatValid(name) && User.isAgeValid(age) && User.isGenderValid(Constants.gender.values()[gender]) && Common.isPhoneFormatValid(phone) && Common.isQqFormatValid(qq))){
				return null;
			}
			else{
				return jsonContact;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
			Common.d("likely invalid location string format");
		} catch (Exception e){
			e.printStackTrace();
			Common.d("UserContactResource:: parseJSON error, likely invalid gender format");
		}

		return null;
	}
	

	@Put
	/**
	 * allows user to change password
	 * @param entity
	 * @return
	 */
	public Representation changeContactInfo(Representation entity) {
		int userId = -1;
		JSONObject response = new JSONObject();
		JSONObject contact = new JSONObject();
		User topBarUser = new User();
		
		if (entity != null && entity.getSize() < Constants.max_userLength){
			try {
				userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
				if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
					contact = parseJSON(entity);
					if (contact != null){
						topBarUser = UserDaoService.changeContactInfo(userId, contact.getString("name"), contact.getInt("age"), Constants.gender.values()[contact.getInt("gender")], contact.getString("phone"), contact.getString("qq"));
						if (topBarUser != null){
							response = JSONFactory.toJSON(topBarUser);
							setStatus(Status.SUCCESS_OK);
						}
						else{
							setStatus(Status.CLIENT_ERROR_FORBIDDEN);
						}
					}
					else{
						Common.d("parsed contact is null");
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
			Common.d("contact info entity null");
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
