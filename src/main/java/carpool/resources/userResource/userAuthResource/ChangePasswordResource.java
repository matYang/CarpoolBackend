package carpool.resources.userResource.userAuthResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.data.Status;
import org.json.JSONObject;

import carpool.common.Validator;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class ChangePasswordResource extends PseudoResource{

	//parses passwords from a JSONObject format of {oldPassword: oldPassword, newPassword: newPassword, confirmNewPassword: confirmNewPassword}
	protected String[] parseJSON(Representation entity){
		JSONObject jsonPasswords = null;
		String[] passwords = new String[2];
		
		try {
			jsonPasswords = (new JsonRepresentation(entity)).getJsonObject();
			
			String oldPassword = jsonPasswords.getString("oldPassword");
			String newPassword = jsonPasswords.getString("newPassword");
			String confirmNewPassword = jsonPasswords.getString("confirmNewPassword");

			if (Validator.isPasswordFormatValid(oldPassword) && Validator.isPasswordFormatValid(newPassword) && Validator.isPasswordFormatValid(confirmNewPassword) && newPassword.equals(confirmNewPassword)){
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
		String quickResponseText = "";
		
		try {
			this.checkEntity(entity);
			userId = Integer.parseInt(this.getReqAttr("id"));		
			this.validateAuthentication(userId);

			passwords = parseJSON(entity);
			if (passwords != null){
				boolean passwordChaneged = UserDaoService.changePassword(userId, passwords[0], passwords[1]);
				if (passwordChaneged){
					setStatus(Status.SUCCESS_OK);
					quickResponseText = "Password has been changed";
				}
				else{
					setStatus(Status.CLIENT_ERROR_CONFLICT);
					quickResponseText = "Password change failed, please try again later";
				}
			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				quickResponseText = "Invalid passwod format";
			}
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e){
        	return this.doException(e);
		}
		
		this.addCORSHeader();
		return this.quickRespond(quickResponseText);

		
	}


}
