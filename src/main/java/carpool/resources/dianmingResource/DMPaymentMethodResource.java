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

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.paymentMethod;
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



public class DMPaymentMethodResource extends PseudoResource{

	
	protected paymentMethod parseJSON(Representation entity){
		JSONObject jsonMessage = null;
		paymentMethod newPaymentMethod = null;
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			newPaymentMethod = Constants.paymentMethod.values()[jsonMessage.getInt("paymentMethod")];
		} catch (Exception e){
			e.printStackTrace();
			DebugLog.d("DMMessage PaymentResource:: parseJSON error, likely invalid payment format");
		}

		return newPaymentMethod;
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
			
	        paymentMethod newPaymentMethod = parseJSON(entity);
	        if (newPaymentMethod != null){
	        	if (Message.isPaymentMethodValid(newPaymentMethod)){
		        	//if valid, update the message
	        		newPaymentMethod = MessageDaoService.updatePaymentMethod(newPaymentMethod, messageId, id);
		            if (newPaymentMethod != null){
		                response = JSONFactory.toJSON(newPaymentMethod);
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
