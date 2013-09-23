package carpool.resources.dianmingResource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.common.Constants.gender;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class DMGenderResource extends PseudoResource{

	protected gender parseJSON(Representation entity){
		JSONObject jsonMessage = null;
		gender newGender = null;
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
			newGender = Constants.gender.values()[jsonMessage.getInt("gender")];
		} catch (Exception e){
			e.printStackTrace();
			Common.d("DMMessage GendeResource:: parseJSON error, likely invalid gender format");
		}

		return newGender;
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
			
	        gender newGender = parseJSON(entity);
	        if (newGender != null){
	        	if (User.isGenderValid(newGender)){
		        	//if valid, update the message
		            newGender = MessageDaoService.updateGender(newGender, messageId, id);
		            if (newGender != null){
		                response = JSONFactory.toJSON(newGender);
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
