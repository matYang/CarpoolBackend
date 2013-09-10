package carpool.resources.notificationResource;

import java.io.UnsupportedEncodingException;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.notification.NotificationOwnerNotMatchException;
import carpool.model.*;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;


public class NotificationResourceId extends ServerResource{
	

    @Get 
    /**
     * @return  get the notification by its notification id
     */
    public Representation getNotificationById() {
    	int id = -1;
    	int notificationId = -1;
        boolean goOn = false;
        JSONObject jsonObject = new JSONObject();
        
        try {
			notificationId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn){
	        	Notification notification = NotificationDaoService.getUserNotificationById(notificationId, id);
	        	if (notification != null){
	                jsonObject = JSONFactory.toJSON(notification);
	                setStatus(Status.SUCCESS_OK);
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	        	}
	        }
			
		} catch (NotificationOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (NotificationNotFoundException e){
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
        
        
        Representation result = new JsonRepresentation(jsonObject);
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return result;
    }
    
    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation checkNotification(Representation entity) {
        int userId = -1;
        int notificationId = -1;
        boolean checked = false;
        boolean goOn = true;
        
		try {
			notificationId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			userId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_NotificationLength){
				
				checked = NotificationDaoService.checkNotification(notificationId, userId);
		   		 if (checked){
			       	 setStatus(Status.SUCCESS_OK);
			       	 Common.d("@Checked notification with id: " + notificationId);
			     }
			     else{
			       	 setStatus(Status.CLIENT_ERROR_CONFLICT);
			     }
		        
	        }
	        else if (entity == null){
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
	        }
			
		} catch (NotificationOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (NotificationNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (DuplicateSessionCookieException e1){
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
		}catch(Exception e1){
			e1.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

        
        //set the response header
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        }  

        return null;
    }
    
    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteNotification() {
    	boolean deleted = false;
    	boolean goOn = true;
    	
    	int id = -1;
    	int notificationId = -1;
		try {
			notificationId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			//not full user here
			if (goOn){
		   		 deleted = NotificationDaoService.deleteNotification(notificationId, id);
		   		 if (deleted){
			       	 setStatus(Status.SUCCESS_OK);
			       	 Common.d("@Delete with id: " + notificationId);
			     }
			     else{
			       	 setStatus(Status.CLIENT_ERROR_CONFLICT);
			     }
			}
			
        } catch (NotificationOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (NotificationNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (DuplicateSessionCookieException e1){
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
        return new JsonRepresentation(new JSONObject());
    }

}
