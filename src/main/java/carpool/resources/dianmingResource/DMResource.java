package carpool.resources.dianmingResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Parser;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.DayTimeSlot;
import carpool.configurations.EnumConfig.Gender;
import carpool.configurations.EnumConfig.MessageType;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.location.LocationNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class DMResource extends PseudoResource{

	//passes received json into message
	protected Message parseJSON(Representation entity){
		JSONObject jsonMessage = null;
		Message message = null;
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			
			message = new Message(jsonMessage.getInt("ownerId"), jsonMessage.getBoolean("isRoundTrip"),
					new Location(jsonMessage.getJSONObject("departure_location")), DateUtility.castFromAPIFormat(jsonMessage.getString("departure_time")), EnumConfig.DayTimeSlot.values()[jsonMessage.getInt("departure_timeSlot")],
					jsonMessage.getInt("departure_seatsNumber"), Parser.parseIntegerList(jsonMessage.getJSONArray("departure_priceList")),
					new Location(jsonMessage.getJSONObject("arrival_location")), DateUtility.castFromAPIFormat(jsonMessage.getString("arrival_time")), EnumConfig.DayTimeSlot.values()[jsonMessage.getInt("arrival_timeSlot")],
					jsonMessage.getInt("arrival_seatsNumber"), Parser.parseIntegerList(jsonMessage.getJSONArray("arrival_priceList")),
					EnumConfig.PaymentMethod.values()[jsonMessage.getInt("paymentMethod")],
					jsonMessage.getString("note"), EnumConfig.MessageType.values()[jsonMessage.getInt("type")], EnumConfig.Gender.values()[jsonMessage.getInt("genderRequirement")]);
		
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
	public Representation getAllMessages() throws LocationNotFoundException {

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
			
	        Message message = parseJSON(entity);
	        if (message != null){
	        	id = message.getOwnerId();
		        this.validateAuthentication(id);
		        
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
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(newJsonMessage);
        this.addCORSHeader();
        return result;
		
	}

}
