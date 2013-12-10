package carpool.resources.userResource.userEmailResource;

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
import org.restlet.data.Cookie;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserEmailResourceId extends PseudoResource{


	@Put
	/**
	 * @param entity   
	 */
	public Representation changeEmail(Representation entity) {
		JSONObject jsonString = new JSONObject();
		boolean emailChanged = false;


		try {
			this.checkEntity(entity);
			
			int userId = Integer.parseInt(this.getReqAttr("id"));
			String email = this.getQueryVal("email");

			//though placing userId in post entity, when checking cookie session the id is still effectively verified
			this.validateAuthentication(userId);
			
			String sessionString = this.getSessionString();
        	if (Validator.isEmailFormatValid(email)){
        		if (EmailDaoService.isEmailAvailable(email)){
    				emailChanged = EmailDaoService.changeEmail(userId, email, sessionString);
    				if (emailChanged){
    					setStatus(Status.SUCCESS_OK);
    					this.closeAuthenticationSession(userId);
    				}
    				else{
    					setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
    				}
    				jsonString = JSONFactory.toJSON(emailChanged);
        		}
        		else{
        			setStatus(Status.CLIENT_ERROR_CONFLICT);
        		}
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        	}

		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
			return this.doException(e);
		}
		
		Representation result = new JsonRepresentation(jsonString);
		
		this.addCORSHeader();
		return result;
	}

}
