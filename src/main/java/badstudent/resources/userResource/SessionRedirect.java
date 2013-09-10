package badstudent.resources.userResource;

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


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.exception.user.UserNotFoundException;
import badstudent.model.*;


public class SessionRedirect extends ServerResource{
	
		
	@Get
	public Representation sessionRedirect(Representation entity){
		Common.d("enter session redirect");
		
		User topBarUser = new User();
		JSONObject jsonObject = new JSONObject();
		String sessionString = "";
		
		Series<Cookie> cookies = this.getRequest().getCookies();
		try {
			sessionString = UserCookieResource.getSessionString(cookies);
			
			Common.d("session redirect receving session string: " + sessionString);
			
			topBarUser = UserDaoService.getUserFromSession(sessionString);
			
			//if able to login, return toBarUser with valid id, front end will redirect to use session mode
			if (topBarUser != null && topBarUser.isAbleToLogin()){
				
				jsonObject = JSONFactory.toJSON(topBarUser);
			}
			//if not, retun defeault user, front end will detect invalid id==-1 and will use non-session
			else{
				jsonObject = JSONFactory.toJSON(new User());
			}
		
		}  catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			Common.d("cookie validation error at Login Resource, cookie expcetion");
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		Representation result = new JsonRepresentation(jsonObject);
        
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		}
		
        try {
            Common.d(result.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
	}

	
}

