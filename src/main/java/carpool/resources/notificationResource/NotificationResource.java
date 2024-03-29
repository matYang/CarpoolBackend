package carpool.resources.notificationResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;

import carpool.dbservice.*;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserResource;


public class NotificationResource extends PseudoResource{

	@Get
	/**
	 * Retrieve all notifications from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllNotifications() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException {

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
