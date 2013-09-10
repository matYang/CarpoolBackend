package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;



public class UserWatchUserResource extends ServerResource{  

    @Get 
    //return  the minified users
    public Representation getWatchedUsers() {
        int id = -1;
        int intendedUserId = -1;
        ArrayList<User> watchedUsers = new ArrayList<User>();
        boolean goOn = false;
        JSONArray response = new JSONArray();
        
        try {
        	//id of the current logged in user
			id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			//id from which is list is to be retrieved from
			intendedUserId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("intendedUserId"),"utf-8"));
			
			//make sure the current user has logged in
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			Common.d("API::getWatchedUsers:: " + id);
			
			if (goOn){
	        	watchedUsers = UserDaoService.getWatchedUsers(intendedUserId);
	        	if (watchedUsers != null){
	        		response = JSONFactory.toJSON(watchedUsers);
	        		setStatus(Status.SUCCESS_OK);
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	        	}
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
		} catch (UnsupportedEncodingException e2) {
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
    

    @Put 
    public Representation watchUser(Representation entity) {
        int id = -1;
        int targetUserId = -1;
        boolean goOn = true;
        JSONObject response = new JSONObject();
        User watchedUser = new User();
        
        if (entity != null && entity.getSize() < Constants.max_userLength){
    	    try {
	   		 	id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
	   		 	targetUserId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("targetUserId"),"utf-8"));
	   		 	
	   		 	if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
	   		 		goOn = true;
	   		 	}
	   		 	else{
	   		 		goOn = false;
	   		 		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
	   		 	}
	   		 	
	   		 	if (goOn){
				    watchedUser = UserDaoService.watchUser(id, targetUserId);
				    if (watchedUser != null){
				    	response = JSONFactory.toJSON(watchedUser);
				    	setStatus(Status.SUCCESS_OK);
				    }
				    else{
				    	setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				    }
				}
				else{
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
			} catch (UnsupportedEncodingException e) {
	   			e.printStackTrace();
	   			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	   		} catch(Exception e1){
	   			e1.printStackTrace();
	   			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	   		}
	           
       	}
        else if (entity == null){
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
       	else{
       		setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
       	}
		
        
        
		Representation result =  new JsonRepresentation(response);
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
		    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		}  

        return result;
    }
    
    @Delete
    public Representation deWatchUser() {
    	boolean deWatched = false;
    	boolean goOn = true;
    	int userId = -1;
    	int targetUserId = -1;
    	
		try {
			userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			targetUserId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("targetUserId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}	

			//not full user here
			if (goOn){
				 deWatched = UserDaoService.deWatchUser(userId, targetUserId);
				 if (deWatched){
					 setStatus(Status.SUCCESS_OK);
				 }
				 else{
					 setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				 }
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
		} catch(NumberFormatException e){
        	e.printStackTrace();
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        } catch(UnsupportedEncodingException e1){
			e1.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch(Exception e2){
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	      
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return null;
    }



    //needed here since backbone will try to send OPTIONS to /id before PUT or DELETE
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
