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

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.messageState;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.factory.JSONFactory;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;



public class DMNoteResource extends PseudoResource{

	protected String parseJSON(Representation entity){
		JSONObject jsonMessage = null;
		String newNote = null;
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			newNote = jsonMessage.getString("note");
		} catch (Exception e){
			e.printStackTrace();
		}

		return newNote;
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
			
	        String newNote = parseJSON(entity);
	        if (newNote != null){
	        	if (Message.isNoteValid(newNote)){
		        	//if valid, update the message
		            newNote = MessageDaoService.updateNote(newNote, messageId, id);
		            if (newNote != null){
		                response = JSONFactory.toJSON(newNote);
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
