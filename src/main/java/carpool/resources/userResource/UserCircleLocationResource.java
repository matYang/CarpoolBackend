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



public class UserCircleLocationResource extends PseudoResource{
	
	
	@Get 
    /**
     * @return  the ArrayList of university group
     */
    public Representation getUniversityGroup() {
		int userId = -1;
		JSONArray response = new JSONArray();
		ArrayList<Location> locations = new ArrayList<Location>();
		
		try {
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			locations = UserDaoService.getUniversityGroup(userId);
			if (locations != null){
				response = JSONFactory.toJSON(locations);
				setStatus(Status.SUCCESS_OK);
			}
			else{
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}
			
		} catch (PseudoException e){
        	this.doException(e);
        } catch (Exception e) {
			this.doException(e);
		}

		Representation result = new JsonRepresentation(response);
		
		this.addCORSHeader();
		return result;
    }

	@Put
	/**
	 * allows user to add location
	 * @param entity
	 * @return
	 */
	public Representation addLocation(Representation entity) {
		int userId = -1;
		JSONObject response = new JSONObject();
		Location location = new Location();
		Location updatedLocation = new Location();
		
		try {
			this.checkEntity(entity);
			
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			location = UserSingleLocationResource.parseJSON(this.getQueryVal("location"));
			if (location != null){
				updatedLocation = UserDaoService.addLocationToUniversityGroup(userId, location);
				if (updatedLocation != null){
					response = JSONFactory.toJSON(updatedLocation);
					setStatus(Status.SUCCESS_OK);
				}
				else{
					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
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
	
	@Delete
    public Representation removeLocation() {
		int userId = -1;
		Location location = new Location();
		Location updatedLocation = new Location();
		
		try {
			userId = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(userId);
			
			location = UserSingleLocationResource.parseJSON(this.getQueryVal("location"));
			if (location != null){
				updatedLocation = UserDaoService.removeLocationFromUniversityGroup(userId, location);
				if (updatedLocation != null){
					setStatus(Status.SUCCESS_OK);
				}
				else{
					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}

		} catch (PseudoException e){
        	this.doPseudoException(e);
        }  catch (Exception e) {
			this.doException(e);
		}
		
		this.addCORSHeader();
		return null;
    }

}
