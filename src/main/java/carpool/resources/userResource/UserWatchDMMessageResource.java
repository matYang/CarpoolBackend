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
import org.json.JSONObject;

import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
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
    public Representation watchMessage(Representation entity) {
        int id = -1;
        int targetMessageId = -1;
		JSONObject response = new JSONObject();
		Message watchedMessage = null;

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
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
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
        	this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch(Exception e){
			this.doException(e);
		}
	      
		this.addCORSHeader();
        return null;
    }

}
