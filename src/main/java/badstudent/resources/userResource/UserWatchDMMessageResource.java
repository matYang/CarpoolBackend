package badstudent.resources.userResource;

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


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.exception.user.UserNotFoundException;
import badstudent.model.*;
import badstudent.mappings.*;

public class UserWatchDMMessageResource extends ServerResource{  

    @Get 
    public Representation getWatchedMessages() {
        int id = -1;
        int intendedUserId = -1;
        ArrayList<DMMessage> watchedMessages = new ArrayList<DMMessage>();
        boolean goOn = false;
        JSONArray response = new JSONArray();
        
        try {
			id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			intendedUserId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("intendedUserId"),"utf-8"));
			
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn){
	        	watchedMessages = UserDaoService.getWatchedMessaegs(intendedUserId);
	        	if (watchedMessages != null){
	        		response = JSONFactory.toJSON(watchedMessages);
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
    //TODO
    public Representation watchMessage(Representation entity) {
        int id = -1;
        int targetMessageId = -1;
        boolean goOn = true;
        JSONObject response = new JSONObject();
        DMMessage watchedMessage = new DMMessage();
        
        if (entity != null && entity.getSize() < Constants.max_userLength){
    	    try {
	   		 	id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
	   		 	targetMessageId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("targetMessageId"),"utf-8"));
	   		 	
	   		 	if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
	   		 		goOn = true;
	   		 	}
	   		 	else{
	   		 		goOn = false;
	   		 		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
	   		 	}
	   		 	
	   		 	if (goOn){
				    watchedMessage = UserDaoService.watchMessage(id, targetMessageId);
				    if (watchedMessage != null){
				    	response = JSONFactory.toJSON(watchedMessage);
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
    //TODO
    public Representation deWatchMessage() {
    	boolean deWatched = false;
    	boolean goOn = true;
    	int userId = -1;
    	int targetMessageId = -1;
    	
		try {
			userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			targetMessageId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("targetMessageId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}	

			//not full user here
			if (goOn){
				 deWatched = UserDaoService.deWatchMessage(userId, targetMessageId);
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
