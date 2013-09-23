package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserTopBarResource extends PseudoResource{

    @Get 
     //  return the user object constructed using topBar constructor
    public Representation getTopBarUerById() {
        int id = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
			
			Common.d("API::GetUserById:: " + id);
			
        	User user = UserDaoService.getTopBarUserById(id);
        	if (user != null){
        		jsonObject = JSONFactory.toJSON(user);
                setStatus(Status.SUCCESS_OK);

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


}
