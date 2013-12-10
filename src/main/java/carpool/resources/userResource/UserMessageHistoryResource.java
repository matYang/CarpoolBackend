package carpool.resources.userResource;

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

import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserMessageHistoryResource extends PseudoResource{	

    @Get 
    /**
     * @return  the full history messages of the user
     */
    public Representation getMessageByUserId() {
    	int id = -1;
    	int intendedUserId = -1;
    	
        JSONArray response = new JSONArray();
        
        try {
			id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			this.validateAuthentication(id);
			
        	ArrayList<Message> historyMessages = UserDaoService.getHistoryMessageByUserId(intendedUserId);
        	if (historyMessages != null){
                response = JSONFactory.toJSON(historyMessages);
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
