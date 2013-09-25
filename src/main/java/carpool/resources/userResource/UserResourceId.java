package carpool.resources.userResource;

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

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.userSearchState;
import carpool.constants.Constants.userState;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserResourceId extends PseudoResource{

    @Get 
    /**
     * @return  the full user with all fields, including Messages, Transactions, Notifications
     */
    public Representation getUerById() {
        int id = -1;
        int intendedUserId = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
			id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			this.validateAuthentication(id);
			DebugLog.d("API::GetUserById:: " + id);
			
			//used for personal page, able to retrieve any user's information
	    	User user = UserDaoService.getUserById(intendedUserId);
	    	if (user != null){
	        	//TODO user.setPassword(Message.goofyPasswordTrickHackers);
	            jsonObject = JSONFactory.toJSON(user);
	    	}
	    	else{
	    		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	    	}
			
		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch (Exception e) {
			this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader();
        return result;
    }

    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteUser() {
    	
    	int id = -1;
		try {
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
				
			UserDaoService.deleteUser(id);
			setStatus(Status.SUCCESS_OK);

        } catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
		
		this.addCORSHeader();
        return null;
    }

}
