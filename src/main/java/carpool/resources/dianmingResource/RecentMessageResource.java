package carpool.resources.dianmingResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.message.MessageNotFoundException;
import carpool.factory.JSONFactory;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserResource;
import carpool.resources.userResource.userAuthResource.UserCookieResource;



public class RecentMessageResource extends PseudoResource{

	@Get
	/**
	 * Retrieve all messages from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getRecentMessages() {
		
		ArrayList<Message> recentMessages = MessageDaoService.getRecentMessages();
		JSONArray jsonArray = new JSONArray();
		
		if (recentMessages == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(recentMessages);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);
		this.addCORSHeader();
		return result;
	}


}
