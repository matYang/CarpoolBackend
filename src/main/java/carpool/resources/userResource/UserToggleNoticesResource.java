package carpool.resources.userResource;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONObject;

import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserToggleNoticesResource extends PseudoResource{


	@Put
	/**
	 * used for user to toggle email notification state
	 * @param entity
	 * @return	the new state of email notification
	 */
	public Representation toggleEmailNotice(Representation entity) {
		int userId = -1;
		JSONObject response = new JSONObject();
		boolean emailNotice = false;
		boolean phoneNotice = false;

		try {
			this.checkEntity(entity);
			
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			emailNotice = Boolean.parseBoolean(this.getQueryVal("emailNotice"));
			phoneNotice = Boolean.parseBoolean(this.getQueryVal("phoneNotice"));
			
			User user = UserDaoService.getUserById(userId);
			user.setEmailNotice(emailNotice);
			user.setPhoneNotice(phoneNotice);
			UserDaoService.updateUser(user);
			
			response = JSONFactory.toJSON(emailNotice);
			setStatus(Status.SUCCESS_OK);


		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch (Exception e) {
			this.doException(e);
		}

		Representation result = new JsonRepresentation(response);
		this.addCORSHeader();
		return result;
	}

}
