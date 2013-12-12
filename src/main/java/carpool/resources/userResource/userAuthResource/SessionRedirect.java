package carpool.resources.userResource.userAuthResource;

import java.io.IOException;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;

public class SessionRedirect extends PseudoResource{
	
		
	@Get
	public Representation sessionRedirect(Representation entity){
		DebugLog.d("SessionDirect:: Enter session redirect");
		
		User user = null;
		JSONObject jsonObject = new JSONObject();
		String sessionString = "";
		

		try {
			sessionString = this.getSessionString();
			
			DebugLog.d("session redirect receving session string: " + sessionString);
			
			user = AuthDaoService.getUserFromSession(sessionString);
			//if able to login, return toBarUser with valid id, front end will redirect to use session mode
			if (user != null && user.isAbleToLogin()){
				
				jsonObject = JSONFactory.toJSON(user);
			}
			//if not, retun defeault user, front end will detect invalid id==-1 and will use non-session
			else{				
				long arrival_Id = 2;
				String province = "Ontario";			
				String city = "Waterloo";				
				String region = "Downtown UW"; 
				Double lat = 32.123212;
				Double lng = 23.132123;				
				Location defaultLocation= new Location(province,city,region,"Test1","Test11",lat,lng,arrival_Id);				
				jsonObject = JSONFactory.toJSON(new User("","",defaultLocation, gender.both));
			}
		
		}  catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		}  catch (Exception e) {
			return this.doException(e);
		}

		Representation result = new JsonRepresentation(jsonObject);
        
        this.addCORSHeader();
        return result;
	}

	
}

