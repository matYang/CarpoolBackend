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



public class UserToggleEmailNoticeResource extends PseudoResource{


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
		boolean toggleSuccess = false;

		try {
			this.checkEntity(entity);
			
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			emailNotice = Boolean.parseBoolean(this.getQueryVal("emailNotice"));
			
			toggleSuccess = UserDaoService.toggleEmailNotice(userId, emailNotice);
			if (toggleSuccess){
				response = JSONFactory.toJSON(emailNotice);
				setStatus(Status.SUCCESS_OK);
			}
			else{
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}

		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch (Exception e) {
			this.doException(e);
		}

		Representation result = new JsonRepresentation(response);
		this.addCORSHeader();
		return result;
	}

}
