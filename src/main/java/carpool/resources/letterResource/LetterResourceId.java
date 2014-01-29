package carpool.resources.letterResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import carpool.constants.Constants.LetterDirection;
import carpool.constants.Constants.LetterType;
import carpool.constants.Constants.TransactionStateChangeAction;
import carpool.dbservice.LetterDaoService;
import carpool.dbservice.MessageDaoService;
import carpool.dbservice.TransactionDaoService;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.Letter;
import carpool.model.Message;
import carpool.model.Transaction;
import carpool.resources.PseudoResource;

public class LetterResourceId extends PseudoResource{
	
	@Get 
    public Representation getLetters() {
    	int userId = -1;
        JSONArray jsonLetters = new JSONArray();
        
        try {

        	userId = Integer.parseInt(this.getQueryVal("userId"));
			this.validateAuthentication(userId);
			
			int targetUserId = Integer.parseInt(this.getQueryVal("targetUserId"));
			LetterType targetType = LetterType.fromInt(Integer.parseInt(this.getQueryVal("targetType")));
			LetterDirection direction = LetterDirection.fromInt(Integer.parseInt(this.getQueryVal("direction")));
			
        	ArrayList<Letter> letters = LetterDaoService.getUserLetters(userId, targetUserId, targetType, direction);
        	
        	if (letters != null){
        		jsonLetters  = JSONFactory.toJSON(letters);
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
        
        Representation result = new JsonRepresentation(jsonLetters);
        this.addCORSHeader();
        return result;
    }
	
	
	@Put 
    public Representation CheckLetter(Representation entity) {
        int userId = -1;
        int letterId = -1;
        int targetUserId = -1;
        JSONObject jsonLetter = new JSONObject();
        
		try {
			checkEntity(entity);
			//letterId = Integer.parseInt(this.getReqAttr("id"));
			JSONObject hashHolder = (new JsonRepresentation(entity)).getJsonObject();
			userId = hashHolder.getInt("userId");
			targetUserId = hashHolder.getInt("targetUserId");
			
			this.validateAuthentication(userId);
			
			LetterDaoService.checkLetter(userId, targetUserId);

		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(jsonLetter);
        this.addCORSHeader(); 
        return result;
    }
	
	 
	//now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteLetterHistory() {
    	//TODO authentication
    	int letterId = -1;
		try {
			letterId = Integer.parseInt(this.getReqAttr("id"));
			
			LetterDaoService.deleteLetter(letterId);
			setStatus(Status.SUCCESS_OK);

        } catch (PseudoException e){
        	this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e){
			return this.doException(e);
		}
		
	    this.addCORSHeader();
	    return new JsonRepresentation(new JSONObject());
    }
	
}
