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

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserContactResource extends PseudoResource{

	//parses contact information from a JSONObject consisting of name, age, gender, phone, qq
	//return JSONObject if all fields are valid, null if not
	protected JSONObject parseJSON(Representation entity){
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
			
		} catch (Exception e){
			e.printStackTrace();
			Common.d("UserContactResource:: parseJSON error, likely invalid format");
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
		
		try {
			this.checkEntity(entity);
			
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
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

		} catch (UserNotFoundException e){
        	this.doPseudoException(e);
        } catch (Exception e) {
			this.doException(e);
		}
		
		Representation result = new JsonRepresentation(response);
		
		this.addCORSHeader(); 
		return result;
	}
	
}
