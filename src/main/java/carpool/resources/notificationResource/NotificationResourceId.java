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

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.notification.NotificationOwnerNotMatchException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;


public class NotificationResourceId extends PseudoResource{
	

    @Get 
    /**
     * @return  get the notification by its notification id
     */
    public Representation getNotificationById() {
    	int id = -1;
    	int notificationId = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
        	notificationId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			
			this.validateAuthentication(id);
			
        	Notification notification = NotificationDaoService.getUserNotificationById(notificationId, id);
        	if (notification != null){
                jsonObject = JSONFactory.toJSON(notification);
                setStatus(Status.SUCCESS_OK);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
			
		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader();
        return result;
    }
    
    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation checkNotification(Representation entity) {
        int userId = -1;
        int notificationId = -1;
        boolean checked = false;
        
		try {
			this.checkEntity(entity);
			
			notificationId = Integer.parseInt(this.getReqAttr("id"));
			userId = Integer.parseInt(this.getQueryVal("userId"));
			
			this.validateAuthentication(userId);
				
			checked = NotificationDaoService.checkNotification(notificationId, userId);
			if (checked) {
				setStatus(Status.SUCCESS_OK);
				DebugLog.d("@Checked notification with id: " + notificationId);
			} else {
				setStatus(Status.CLIENT_ERROR_CONFLICT);
			}

		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}

		this.addCORSHeader();
        return null;
    }
    
    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteNotification() {
    	boolean deleted = false;
    	
    	int id = -1;
    	int notificationId = -1;
		try {
			notificationId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			
			this.validateAuthentication(id);

			deleted = NotificationDaoService.deleteNotification(notificationId, id);
			if (deleted) {
				setStatus(Status.SUCCESS_OK);
				DebugLog.d("@Delete with id: " + notificationId);
			} else {
				setStatus(Status.CLIENT_ERROR_CONFLICT);
			}
			
        } catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
		
		this.addCORSHeader();
        return null;
    }

}
