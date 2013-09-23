package carpool.resources.userResource;

import java.io.IOException;

import org.restlet.engine.header.Header;
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

import carpool.common.DebugLog;
import carpool.constants.Constants;
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
		DebugLog.d("enter session redirect");
		
		User topBarUser = new User();
		JSONObject jsonObject = new JSONObject();
		String sessionString = "";
		

		try {
			sessionString = this.getSessionString();
			
			DebugLog.d("session redirect receving session string: " + sessionString);
			
			topBarUser = UserDaoService.getUserFromSession(sessionString);
			
			//if able to login, return toBarUser with valid id, front end will redirect to use session mode
			if (topBarUser != null && topBarUser.isAbleToLogin()){
				
				jsonObject = JSONFactory.toJSON(topBarUser);
			}
			//if not, retun defeault user, front end will detect invalid id==-1 and will use non-session
			else{
				jsonObject = JSONFactory.toJSON(new User());
			}
		
		}  catch (PseudoException e){
			this.doPseudoException(e);
		}  catch (Exception e) {
			this.doException(e);
		}

		Representation result = new JsonRepresentation(jsonObject);
        
        this.addCORSHeader();
		this.printResult(result);
        return result;
	}

	
}

