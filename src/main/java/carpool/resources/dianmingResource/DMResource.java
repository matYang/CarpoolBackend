package carpool.resources.dianmingResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class DMResource extends PseudoResource{

	//passes received json into message
	protected Message parseJSON(Representation entity, int userId){
		JSONObject jsonMessage = null;
		Message message = null;
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			DebugLog.d("@Post::receive jsonMessage: " +  jsonMessage.toString());
			
			message = new Message(jsonMessage.getInt("ownerId"), Constants.paymentMethod.values()[jsonMessage.getInt("paymentMethod")], 
					new Location(jsonMessage.getJSONObject("location").getString("province"), jsonMessage.getJSONObject("location").getString("city"), jsonMessage.getJSONObject("location").getString("region"),jsonMessage.getJSONObject("location").getString("university")), DateUtility.parseDateString(jsonMessage.getString("startTime")),  DateUtility.parseDateString(jsonMessage.getString("endTime")), 
					jsonMessage.getString("note"), Constants.messageType.values()[jsonMessage.getInt("type")], Constants.gender.values()[jsonMessage.getInt("genderRequirement")], jsonMessage.getInt("price"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return message;
	}
	
	
	@Get
	/**
	 * Retrieve all messages from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllMessages() {

		ArrayList<Message> allMessages = MessageDaoService.getAllMessages();
		JSONArray jsonArray = new JSONArray();
		
		if (allMessages == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allMessages);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);
		this.addCORSHeader();
		return result;
	}

	

	@Post
	public Representation createMessage(Representation entity) {
		
		int id = -1;
        JSONObject newJsonMessage = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
			id = Integer.parseInt(this.getQueryVal("userId"));
			this.validateAuthentication(id);
			
	        Message message = parseJSON(entity, id);
	        if (message != null){
	        	if (message.validate() && message.getOwnerId() == id){
		        	//if create the message
		            Message creationFeedBack = MessageDaoService.createNewMessage(message);
		            if (creationFeedBack != null){
		                newJsonMessage = JSONFactory.toJSON(creationFeedBack);
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
        
        Representation result =  new JsonRepresentation(newJsonMessage);
        this.addCORSHeader();
        return result;
		
	}

}
