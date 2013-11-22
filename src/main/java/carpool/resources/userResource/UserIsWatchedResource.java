package carpool.resources.userResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import carpool.common.DebugLog;
import carpool.dbservice.UserDaoService;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.User;
import carpool.resources.PseudoResource;

public class UserIsWatchedResource extends PseudoResource{
	
	@Get
	public Representation isUserWatched(){
		int id = -1;
        int intendedUserId = -1;
        Boolean isUserWatched = false;
        JSONObject response = new JSONObject();
        
        try {
			
			//id from which is list is to be retrieved from
        	id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			//make sure the current user has logged in
			this.validateAuthentication(id);
			
        	isUserWatched = UserDaoService.isUserWatched(id, intendedUserId);
        	response = JSONFactory.toJSON(isUserWatched);
        	
        	
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

}
