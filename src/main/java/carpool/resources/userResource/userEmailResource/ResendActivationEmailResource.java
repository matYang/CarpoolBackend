package carpool.resources.userResource.userEmailResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class ResendActivationEmailResource extends PseudoResource{

	@Get
	public Representation reSendUserEmail(){
		int userId = -1;
        boolean isActivated = false;
        JSONObject response = null; 
        boolean isSent = false;;
        
        try {
        	userId = Integer.parseInt(this.getReqAttr("userId"));
        	
        	isActivated = EmailDaoService.isUserEmailActivated(userId);
        	
        	//if not activated already, assuming without activation login is impossible and does not need to be checked
        	if (!isActivated){
        		isSent = EmailDaoService.reSendActivationEmail(userId);
        		if (isSent){
        			setStatus(Status.SUCCESS_OK);
        		}
        		else{
        			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        		}
        		response = JSONFactory.toJSON(isSent);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_CONFLICT);
        	}
        	
		} catch(PseudoException e){
			this.doPseudoException(e);
		} catch (Exception e) {
			this.doException(e);
		}
        
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
		
	}

}
