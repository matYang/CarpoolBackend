package carpool.resources.userResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.dbservice.UserDaoService;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.Notification;
import carpool.resources.PseudoResource;

public class UserUncheckedNotificationResource extends PseudoResource{
	

    @Get 
    /**
     * @return  the unread notificaitons of the current user
     */
    public Representation getUncheckedNotificationByUserId() {
    	int id = -1;
        JSONArray response = new JSONArray();
        
        try {
        	id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
			
        	ArrayList<Notification> uncheckedNotifications = UserDaoService.getUncheckedNotificationByUserId(id);
        	if (uncheckedNotifications != null){
                response = JSONFactory.toJSON(uncheckedNotifications);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_CONFLICT);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch (Exception e) {
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }

}
