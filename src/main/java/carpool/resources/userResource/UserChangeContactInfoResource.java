package carpool.resources.userResource;



import java.io.IOException;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserChangeContactInfoResource extends PseudoResource{

	protected JSONObject parseJSON(Representation entity) throws ValidationException{
		JSONObject jsonContact = null;

		try {
			jsonContact = (new JsonRepresentation(entity)).getJsonObject();
		} catch (JSONException | IOException e) {
			DebugLog.d(e);
			return null;
		}
		
		String name = jsonContact.getString("name");
		int gender = jsonContact.getInt("gender");
		String birthday = jsonContact.getString("birthday");
		
		if (!(Validator.isNameFormatValid(name) && Constants.Gender.values()[gender] != null && birthday != null && jsonContact.getJSONObject("location") != null)){
			throw new ValidationException("必填数据不能为空");
		}
		
		String phone = jsonContact.getString("phone");
		if (phone != null && phone.length() > 0){
			if (!Validator.isPhoneFormatValid(phone)){
				throw new ValidationException("电话格式不正确");
			}
		}
		String qq = jsonContact.getString("qq");
		if (qq != null && qq.length() > 0){
			if (!Validator.isQqFormatValid(qq)){
				throw new ValidationException("QQ格式不正确");
			}
		}
		
		return jsonContact;
		
	}
	

	@Put
	/**
	 * allows user to change password
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
				user.setGender(Constants.Gender.values()[contact.getInt("gender")]);
				user.setPhone(contact.getString("phone"));
				user.setQq(contact.getString("qq"));
				user.setBirthday(DateUtility.castFromAPIFormat(contact.getString("birthday")));
				Location location = new Location(contact.getJSONObject("location"));
				user.setLocation(location);
				UserDaoService.updateUser(user);
				
				response = JSONFactory.toJSON(user);
				setStatus(Status.SUCCESS_OK);
			}
			else{
				throw new ValidationException("数据格式不正确");
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
