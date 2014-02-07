package carpool.resources.userResource;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserResourceId extends PseudoResource{

    @Get 
    /**
     * @return  the full user with all fields, including Messages, Transactions, Notifications
     */
    public Representation getUerById() {
        int id = -1;
        int intendedUserId = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
			id = Integer.parseInt(this.getReqAttr("id"));
			String intendedIdString = this.getQueryVal("intendedUserId"); 
			intendedUserId = intendedIdString != null ? Integer.parseInt(this.getQueryVal("intendedUserId")) : id;
			
			String ss = this.getSessionString();
			this.validateAuthentication(id);
			
			//used for personal page, able to retrieve any user's information
	    	User user = UserDaoService.getUserById(intendedUserId);
	    	if (user != null){
	            jsonObject = JSONFactory.toJSON(user);
	    	}
	    	else{
	    		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	    	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader();
        return result;
    }

    //should not be exposed to frontend for now
    @Delete
    public Representation deleteUser() {
    	
    	int id = -1;
		try {
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
				
			UserDaoService.deleteUser(id);
			setStatus(Status.SUCCESS_OK);

        } catch (PseudoException e){
        	this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
		
		this.addCORSHeader();
		return new JsonRepresentation(new JSONObject());
    }

}
