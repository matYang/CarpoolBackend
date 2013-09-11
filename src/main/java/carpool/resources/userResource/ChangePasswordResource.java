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
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class ChangePasswordResource extends PseudoResource{

	//parses passwords from a JSONObject format of {oldPassword: oldPassword, newPassword: newPassword, confirmNewPassword: confirmNewPassword}
	//return String array length of 2, [0] storing the oldPassword, [1] storing the newPassword, returns null of the password format is not correct
	protected String[] parseJSON(Representation entity){
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
			
		} catch (Exception e) {
			e.printStackTrace();
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
		
		try {
			checkEntity(entity);
			userId = Integer.parseInt(getReqAttr("id"));		
			validateAuthentication(userId);

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

		} catch (PseudoException e){
			doPseudoException(e);
        } catch (Exception e1){
        	doException(e1);
		} 

		Representation result = new JsonRepresentation(response);
		
		addCORSHeader();
		return result;
	}


}
