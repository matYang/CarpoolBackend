package carpool.resources.dianmingResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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



public class DMTimingResource extends PseudoResource{

	protected ArrayList<Calendar> parseJSON(Representation entity){
		JSONObject jsonMessage = null;
		ArrayList<Calendar> newTiming = new ArrayList<Calendar>();
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			newTiming.add(Common.parseDateString(jsonMessage.getString("startTime")));
			newTiming.add(Common.parseDateString(jsonMessage.getString("endTime")));
		} catch (Exception e){
			e.printStackTrace();
			Common.d("DMMessage TimingResource:: parseJSON error, likely invalid format");
			return null;
		}

		return newTiming;
	}
	
    @Put 
    public Representation updateMessage(Representation entity) {
        int id = -1;
        int messageId = -1;
        JSONArray response = new JSONArray();
        
		try {
			this.checkEntity(entity);
			
			messageId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			
			this.validateAuthentication(id);
			
	        ArrayList<Calendar> newTiming = parseJSON(entity);
	        if (newTiming != null){
	        	if (Message.isTimeValid(newTiming.get(0), newTiming.get(1))){
		        	//if valid, update the message
		            newTiming = MessageDaoService.updateTime(newTiming.get(0), newTiming.get(1), messageId, id);
		            if (newTiming != null){
		            	ArrayList<JSONObject> jsonCals = new ArrayList<JSONObject>();
		            	for (int i = 0; i < newTiming.size(); i++){
		            		jsonCals.add(JSONFactory.toJSON(Common.CalendarToUTCString(newTiming.get(i))));
		            	}
		                response = new JSONArray(jsonCals);
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
