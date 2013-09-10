package carpool.resources.userResource;

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

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;



public class ResendActivationEmailResource extends ServerResource{

	@Get
	public Representation reSendUserEmail(){
		int userId = -1;
        boolean isActivated = false;
        JSONObject response = null; 
        boolean isSent = false;;
        
        try {
        	userId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
        	
        	isActivated = UserDaoService.isUserEmailActivated(userId);
        	
        	//if not activated already, assuming without activation login is impossible and does not need to be checked
        	if (!isActivated){
        		isSent = UserDaoService.reSendActivationEmail(userId);
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
        	
		} catch(UserNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (UnsupportedEncodingException e2){
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        
        Representation result = new JsonRepresentation(response);
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return result;
		
	}

}
