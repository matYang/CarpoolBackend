package carpool.resources.notificationResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;

import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserResource;


public class NotificationResource extends PseudoResource{

	@Get
	/**
	 * Retrieve all notifications from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllNotifications() {

		ArrayList<Notification> allNotifications = NotificationDaoService.getAllNotifications();
		JSONArray jsonArray = new JSONArray();
		
		if (allNotifications == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allNotifications);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);
		this.addCORSHeader();
		return result;
	}


}
