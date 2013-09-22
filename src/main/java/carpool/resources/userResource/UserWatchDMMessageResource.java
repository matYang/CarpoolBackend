package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

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



public class UserWatchDMMessageResource extends PseudoResource{  

    @Get 
    public Representation getWatchedMessages() {
        int id = -1;
        int intendedUserId = -1;
        ArrayList<Message> watchedMessages = new ArrayList<Message>();
        JSONArray response = new JSONArray();
        
        try {
			id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			this.validateAuthentication(id);
			
			watchedMessages = UserDaoService.getWatchedMessaegs(intendedUserId);
			if (watchedMessages != null) {
				response = JSONFactory.toJSON(watchedMessages);
				setStatus(Status.SUCCESS_OK);
			} else {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}
			
		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }
    

    @Put 
    public Representation watchMessage(Representation entity) {
        int id = -1;
        int targetMessageId = -1;
		JSONObject response = new JSONObject();
		Message watchedMessage = new Message();

		try {
			this.checkEntity(entity);

			id = Integer.parseInt(this.getReqAttr("id"));
			targetMessageId = Integer.parseInt(this.getQueryVal("targetMessageId"));

			this.validateAuthentication(id);

			watchedMessage = UserDaoService.watchMessage(id, targetMessageId);
			if (watchedMessage != null) {
				response = JSONFactory.toJSON(watchedMessage);
				setStatus(Status.SUCCESS_OK);
			} else {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}

		} catch (PseudoException e) {
			this.doPseudoException(e);
		} catch (Exception e) {
			this.doException(e);
		}

		Representation result = new JsonRepresentation(response);
		this.addCORSHeader();
        return result;
    }
    
    @Delete
    public Representation deWatchMessage() {
    	boolean deWatched = false;
    	int userId = -1;
    	int targetMessageId = -1;
    	
		try {
			userId = Integer.parseInt(this.getReqAttr("id"));
			targetMessageId = Integer.parseInt(this.getQueryVal("targetMessageId"));

			this.validateAuthentication(userId);

			deWatched = UserDaoService.deWatchMessage(userId, targetMessageId);
			if (deWatched) {
				setStatus(Status.SUCCESS_OK);
			} else {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			}
   
        } catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
	      
		this.addCORSHeader();
        return null;
    }

}
