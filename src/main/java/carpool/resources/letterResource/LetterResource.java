package carpool.resources.letterResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.LetterDaoService;
import carpool.exception.PseudoException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.Letter;
import carpool.resources.PseudoResource;

public class LetterResource extends PseudoResource{

	//passes received json into message
	protected Letter parseJSON(Representation entity){
		JSONObject jsonLetter = null;
		Letter letter = null;
		try {
			jsonLetter = (new JsonRepresentation(entity)).getJsonObject();
			DebugLog.d("@Post::receive jsonLetter: " +  jsonLetter.toString());
			
			letter = new Letter(jsonLetter.getInt("from_userId"), jsonLetter.getInt("to_userId"), Constants.LetterType.fromInt(jsonLetter.getInt("type")) ,jsonLetter.getString("content"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return letter;
	}
	
	
	@Get
	public Representation getAllLetters() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException {

		ArrayList<Letter> allLetters = LetterDaoService.getAllLetters();
		JSONArray jsonArray = new JSONArray();
		
		if (allLetters == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allLetters);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);
		this.addCORSHeader();
		return result;
	}
	
	@Post
	public Representation sendLetter(Representation entity) {
		
		int id = -1;
        JSONObject newJsonLetter = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
	        Letter letter = parseJSON(entity);
	        if (letter != null){
		        id = letter.getFrom_userId();
		        this.validateAuthentication(id);
		        
	        	if (letter.validate()){
		        	//if create the message
	        		Letter creationFeedBack = LetterDaoService.sendLetter(letter);
		            if (creationFeedBack != null){
		                newJsonLetter = JSONFactory.toJSON(creationFeedBack);
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
        
        Representation result =  new JsonRepresentation(newJsonLetter);
        this.addCORSHeader();
        return result;
		
	}
	
	
}
