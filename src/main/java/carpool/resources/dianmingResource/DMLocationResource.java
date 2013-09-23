package carpool.resources.dianmingResource;

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
import carpool.common.Constants.gender;
import carpool.common.Constants.messageState;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;



public class DMLocationResource extends PseudoResource{

	protected Location parseJSON(Representation entity){
		JSONObject jsonMessage = null;
		Location newLocation = null;
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			newLocation = new Location(jsonMessage.getJSONObject("location").getString("province"), jsonMessage.getJSONObject("location").getString("city"), jsonMessage.getJSONObject("location").getString("region"),jsonMessage.getJSONObject("location").getString("university")) ;
		}  catch (Exception e){
			e.printStackTrace();
			Common.d("DMMessage LocationResource:: parseJSON error, likely invalid format");
		}

		return newLocation;
	}
		
	
    @Put 
    public Representation updateMessage(Representation entity) {
        int id = -1;
        int messageId = -1;
        JSONObject response = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
			messageId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			
			this.validateAuthentication(id);
			
	        Location newLocation = parseJSON(entity);
	        if (newLocation != null){
	        	if (Location.isLocationVaild(newLocation)){
		        	//if valid, update the message
		            newLocation = MessageDaoService.updateLocation(newLocation, messageId ,id);
		            if (newLocation != null){
		                response = JSONFactory.toJSON(newLocation);
		                setStatus(Status.SUCCESS_OK);
		            }
		            else{
		            	setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		            }
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_CONFLICT);
	        	}
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }

		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch (Exception e){
			this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }

}
