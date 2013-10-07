package carpool.resources.userResource;

import java.util.ArrayList;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserWatchUserResource extends PseudoResource{  

    @Get 
    public Representation getWatchedUsers() {
        int id = -1;
        int intendedUserId = -1;
        ArrayList<User> watchedUsers = new ArrayList<User>();
        JSONArray response = new JSONArray();
        
        try {
        	//id of the current logged in user
			id = Integer.parseInt(this.getReqAttr("id"));
			//id from which is list is to be retrieved from
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			//make sure the current user has logged in
			this.validateAuthentication(id);
			DebugLog.d("API::getWatchedUsers:: " + id);
			
        	watchedUsers = UserDaoService.getWatchedUsers(intendedUserId);
        	if (watchedUsers != null){
        		response = JSONFactory.toJSON(watchedUsers);
        		setStatus(Status.SUCCESS_OK);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
        	
		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }

	@Put
	public Representation watchUser(Representation entity) {
		int id = -1;
		int targetUserId = -1;
		JSONObject response = new JSONObject();
		User watchedUser = null;

		try {
			this.checkEntity(entity);

			id = Integer.parseInt(this.getReqAttr("id"));
			targetUserId = Integer.parseInt(this.getQueryVal("targetUserId"));

			this.validateAuthentication(id);

			watchedUser = UserDaoService.watchUser(id, targetUserId);
			if (watchedUser != null) {
				response = JSONFactory.toJSON(watchedUser);
				setStatus(Status.SUCCESS_OK);
			} else {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}

		} catch (PseudoException e) {
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
		} catch (Exception e) {
			this.doException(e);
		}

		Representation result =  new JsonRepresentation(response);
		this.addCORSHeader();
        return result;
    }
    
    @Delete
    public Representation deWatchUser() {
    	boolean deWatched = false;
    	int userId = -1;
    	int targetUserId = -1;
    	
		try {
			userId = Integer.parseInt(this.getReqAttr("id"));
			targetUserId = Integer.parseInt(this.getQueryVal("targetUserId"));

			this.validateAuthentication(userId);

			deWatched = UserDaoService.deWatchUser(userId, targetUserId);
			
			if (deWatched) {
				setStatus(Status.SUCCESS_OK);
			} else {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}

		} catch (PseudoException e) {
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
		} catch(Exception e){
			this.doException(e);
		}
	      
		this.addCORSHeader();
        return null;
    }

}
