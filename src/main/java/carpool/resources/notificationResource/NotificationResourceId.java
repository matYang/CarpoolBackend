package carpool.resources.notificationResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONArray;

import carpool.common.DebugLog;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class NotificationResourceId extends PseudoResource{
	

    @Get 
    /**
     * @return  get the notification from a user
     */
    public Representation getNotificationById() {
    	int id = -1;
        JSONArray response = new JSONArray();
        
        try {
        	//notificationId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			this.validateAuthentication(id);
			
        	ArrayList<Notification> notifications = UserDaoService.getNotificationByUserId(id);
        	if (notifications != null){
        		response = JSONFactory.toJSON(notifications);
                setStatus(Status.SUCCESS_OK);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }
    

    @Put 
    public Representation checkNotification(Representation entity) {
        int userId = -1;
        int notificationId = -1;
        
		try {
			this.checkEntity(entity);
			
			notificationId = Integer.parseInt(this.getReqAttr("id"));
			userId = (new JsonRepresentation(entity)).getJsonObject().getInt("userId");
			
			this.validateAuthentication(userId);
				
			NotificationDaoService.checkNotification(notificationId, userId);
			setStatus(Status.SUCCESS_OK);
			DebugLog.d("@Checked notification with id: " + notificationId);

		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch(Exception e){
			this.doException(e);
		}

		this.addCORSHeader();
        return null;
    }
    
    
    @Delete
    public Representation deleteNotification() {
    	//TODO authentication
    	int notificationId = -1;
		try {
			notificationId = Integer.parseInt(this.getReqAttr("id"));

			NotificationDaoService.deleteNotification(notificationId);
			setStatus(Status.SUCCESS_OK);
			DebugLog.d("@Delete with id: " + notificationId);
			
        } catch (PseudoException e){
        	this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch(Exception e){
			this.doException(e);
		}
		
		this.addCORSHeader();
        return null;
    }

}
