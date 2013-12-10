package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserContactResource extends PseudoResource{

	protected JSONObject parseJSON(Representation entity){
		JSONObject jsonContact = null;
		
		try {
			jsonContact = (new JsonRepresentation(entity)).getJsonObject();
			
			String name = jsonContact.getString("name");
			int gender = jsonContact.getInt("gender");
			String phone = jsonContact.getString("phone");
			String qq = jsonContact.getString("qq");
			String birthday = jsonContact.getString("birthday");
			//no DB interaction here
			if (!(Validator.isNameFormatValid(name) && Constants.gender.values()[gender] != null && Validator.isPhoneFormatValid(phone) && Validator.isQqFormatValid(qq))){
				return null;
			}
			else{
				return jsonContact;
			}
			
		} catch (Exception e){
			e.printStackTrace();
			DebugLog.d("UserContactResource:: parseJSON error, likely invalid format");
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
		
		try {
			this.checkEntity(entity);
			
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			contact = parseJSON(entity);
			if (contact != null){
				User user = UserDaoService.getUserById(userId);
				user.setName(contact.getString("name"));
				user.setGender(Constants.gender.values()[contact.getInt("gender")]);
				user.setPhone(contact.getString("phone"));
				user.setQq(contact.getString("qq"));
				user.setBirthday(DateUtility.castFromAPIFormat(contact.getString("birthday")));
				UserDaoService.updateUser(user);
				
				response = JSONFactory.toJSON(user);
				setStatus(Status.SUCCESS_OK);
			}
			else{
				DebugLog.d("ChangeContactInfo:: parsed contact is null");
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}

		} catch (UserNotFoundException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
			return this.doException(e);
		}
		
		Representation result = new JsonRepresentation(response);
		
		this.addCORSHeader(); 
		return result;
	}
	
}
