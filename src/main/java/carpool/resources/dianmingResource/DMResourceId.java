package carpool.resources.dianmingResource;


import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Parser;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class DMResourceId extends PseudoResource{


	private Message parseJSON(Representation entity, int messageId){
		JSONObject jsonMessage = null;
		Message message = null;
		
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			
			message = new Message(jsonMessage.getInt("ownerId"), jsonMessage.getBoolean("isRoundTrip"),
					new Location(jsonMessage.getJSONObject("departure_location")), DateUtility.castFromAPIFormat(jsonMessage.getString("departure_time")), Constants.DayTimeSlot.values()[jsonMessage.getInt("departure_timeSlot")],
					jsonMessage.getInt("departure_seatsNumber"), Parser.parsePriceList(jsonMessage.getJSONArray("departure_priceList")),
					new Location(jsonMessage.getJSONObject("arrival_location")), DateUtility.castFromAPIFormat(jsonMessage.getString("arrival_time")), Constants.DayTimeSlot.values()[jsonMessage.getInt("arrival_timeSlot")],
					jsonMessage.getInt("arrival_seatsNumber"), Parser.parsePriceList(jsonMessage.getJSONArray("arrival_priceList")),
					Constants.paymentMethod.values()[jsonMessage.getInt("paymentMethod")],
					jsonMessage.getString("note"), Constants.messageType.values()[jsonMessage.getInt("type")], Constants.gender.values()[jsonMessage.getInt("genderRequirement")]);
			message.setMessageId(messageId);
		
		} catch (Exception e){
			  e.printStackTrace();
			  DebugLog.d("DMResourceId:: parseJSON error, likely invalid gender format");
		}

		return message;
	}
		
		

    @Get 
    /**
     * @return  the the message by its message id
     */
    public Representation getMessageById() {
    	int id = -1;
    	int messageId = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
        	messageId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			
			String ssr = this.getSessionString();
			this.validateAuthentication(id);
			
        	Message message = MessageDaoService.getMessageById(messageId);
        	if (message != null){
                jsonObject = JSONFactory.toJSON(message);
                setStatus(Status.SUCCESS_OK);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e){
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader();
        return result;
    }
    
    
    @Put 
    public Representation updateMessage(Representation entity) {
        int id = -1;
        int messageId = -1;
        JSONObject newJsonMessage = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
			messageId = Integer.parseInt(this.getReqAttr("id"));
			Message message = parseJSON(entity, messageId);
			id = message.getOwnerId();
			this.validateAuthentication(id);
			
	        
	        if (message != null){
	        	if (message.validate()){
		        	//if available, update the message
		            Message updateFeedBack = MessageDaoService.updateMessage(message);
		            if (updateFeedBack != null){
		                newJsonMessage = JSONFactory.toJSON(updateFeedBack);
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
    
    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deactivateMessage() {
    	//TODO authentication
    	int messageId = -1;
		try {
			messageId = Integer.parseInt(this.getReqAttr("id"));
			
			MessageDaoService.closeMessage(messageId);
			setStatus(Status.SUCCESS_OK);

        } catch (PseudoException e){
        	this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e){
			return this.doException(e);
		}
		
	    this.addCORSHeader();
        return null;
    }

}
