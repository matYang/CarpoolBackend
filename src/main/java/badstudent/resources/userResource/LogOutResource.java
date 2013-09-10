package badstudent.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import badstudent.resources.dianmingResource.DMResource;


public class LogOutResource extends ServerResource{


	@Put
	public Representation logoutAuthentication(Representation entity){
		JSONObject jsonObject = new JSONObject();
		int id = -1;
		boolean loggedin = false;
		boolean logout = false;
		
		if (entity != null){
			
			Series<Cookie> cookies = this.getRequest().getCookies();
			try {
				id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
				loggedin = UserCookieResource.validateCookieSession(id, cookies);
				
				//if logged in, then logout, if logout fail, return conflict .if not logged-in, return success
				if (loggedin){
					logout = UserCookieResource.closeCookieSession(cookies);
					if (!logout){
						setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
					}
					else{
						setStatus(Status.SUCCESS_OK);
					}
					jsonObject = JSONFactory.toJSON(new User());
				}
				
			} catch (UserNotFoundException e){
	        	e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
	        } catch (DuplicateSessionCookieException e1){
				//TODO clear cookies, set name and value
				e1.printStackTrace();
				this.getResponse().getCookieSettings().clear();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (SessionEncodingException e){
				//TODO modify session where needed
				e.printStackTrace();
				this.getResponse().getCookieSettings().clear();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (NumberFormatException | UnsupportedEncodingException e1) {
				Common.d("id conversion error at LoginResource, number format exception or unsuported encoding exception");
				e1.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (Exception e) {
				Common.d("cookie validation error at Login Resource, cookie expcetion");
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		}
		else if (entity == null){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		else{
        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
        }
		
		Representation result = new JsonRepresentation(jsonObject);

        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return result;
	}

    @Options
    public Representation takeOptions(Representation entity) {
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        //send anything back will be fine, browser just expects a response
        DMMessage message = new DMMessage();
        Representation result = new JsonRepresentation(message);
        Common.d("exiting Options request");
        setStatus(Status.SUCCESS_OK);
        return result;
    }
	
}

