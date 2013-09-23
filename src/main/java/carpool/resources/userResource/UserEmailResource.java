package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserEmailResource extends PseudoResource{

	
	@Get 
    /**used when user registers email or tries to change the email later on
     * @return  true or false, true if email available, false if the user name has already been taken
     */
    public Representation verifyEmail(){
		boolean isFormatCorrect = false;
        boolean isAvailable = false;
        JSONObject jsonObject = new JSONObject();
        String email = "";
        
        try {
        	email = this.getQueryVal("email");
        	isFormatCorrect = Validator.isEmailFormatValid(email);
        	if (isFormatCorrect){
        		isAvailable = UserDaoService.isEmailAvailable(email);
        		
        		jsonObject = JSONFactory.toJSON(isAvailable);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        	}
        	
		} catch (Exception e) {
			this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader();
        return result;
    }

}
