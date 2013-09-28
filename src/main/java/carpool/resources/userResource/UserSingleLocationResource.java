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

import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.mappings.*;
import carpool.model.*;
import carpool.model.representation.LocationRepresentation;
import carpool.resources.PseudoResource;



public class UserSingleLocationResource extends PseudoResource{

	//return JSONObject if all fields are valid, null if not, parses Location from string
	
	public static LocationRepresentation parseJSON(String locationString){
		LocationRepresentation location = new LocationRepresentation(locationString);
		//no DB interaction here
		if (LocationRepresentation.isLocationVaild(location)){
			return location;
		}
		else{
			return null;
		}

	}

	@Put
	/**
	 * allows user to change password
	 * @param entity
	 * @return
	 */
	public Representation changeLocation(Representation entity) {
		int userId = -1;
		JSONObject response = new JSONObject();
		LocationRepresentation location = new LocationRepresentation();
		LocationRepresentation updatedLocation = new LocationRepresentation();

		try {
			this.checkEntity(entity);
			
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			location = parseJSON(this.getQueryVal("location"));
			if (location != null){
				
				User user = UserDaoService.getUserById(userId);
				user.setLocation(location);
				UserDaoService.updateUser(user);
				
				response = JSONFactory.toJSON(updatedLocation);
				setStatus(Status.SUCCESS_OK);

			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
