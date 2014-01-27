package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

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

import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserNotificationResource extends PseudoResource{	

    @Get 
    /**
     * @return  the full notifications of the target user
     */
    public Representation getNotificationByUserId() {
    	int id = -1;
    	int intendedUserId = -1;
        JSONArray response = new JSONArray();
        
        try {
        	id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("userId"));
			
			this.validateAuthentication(id);
			
        	ArrayList<Notification> historyNotifications = UserDaoService.getNotificationByUserId(intendedUserId);
        	if (historyNotifications != null){
                response = JSONFactory.toJSON(historyNotifications);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_CONFLICT);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch (Exception e) {
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }
    
}
