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

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.message.MessageNotFoundException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;



public class DMResource extends ServerResource{

	//passes received json into message
	//note that this parseJSON
	private DMMessage parseJSON(Representation entity, int userId){
		JSONObject jsonMessage = null;
		try {
			jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Common.d("@Post::receive jsonMessage: " +  jsonMessage.toString());

		DMMessage message = null;
		try {

			message = new DMMessage(jsonMessage.getInt("ownerId"), Constants.paymentMethod.values()[jsonMessage.getInt("paymentMethod")], 
					new Location(jsonMessage.getJSONObject("location").getString("province"), jsonMessage.getJSONObject("location").getString("city"), jsonMessage.getJSONObject("location").getString("region"),jsonMessage.getJSONObject("location").getString("university")), Common.parseDateString(jsonMessage.getString("startTime")),  Common.parseDateString(jsonMessage.getString("endTime")), 
					jsonMessage.getString("note"), Constants.messageType.values()[jsonMessage.getInt("type")], Constants.gender.values()[jsonMessage.getInt("genderRequirement")], jsonMessage.getInt("price"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		  catch (NullPointerException e){
			e.printStackTrace();
			Common.d("likely invalid location string format");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return message;
	}
	
	
	@Get
	/**
	 * Retrieve all messages from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllMessages() {
		

		ArrayList<DMMessage> allMessages = DMMessageDaoService.getAllMessages();
		JSONArray jsonArray = new JSONArray();
		
		if (allMessages == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allMessages);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);

		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}

	

	@Post
	public Representation createMessage(Representation entity) {
		
		int id = -1;
        boolean goOn = true;
        JSONObject newJsonMessage = new JSONObject();
        
		try {
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_DMMessageLength){
		        DMMessage message = parseJSON(entity, id);
		        if (message != null){
		        	if (message.isMessageValid() && message.getOwnerId() == id){
			        	//if create the message
			            DMMessage creationFeedBack = DMMessageDaoService.createNewMessage(message);
			            if (creationFeedBack != null){
			                newJsonMessage = JSONFactory.toJSON(creationFeedBack);
			                setStatus(Status.SUCCESS_OK);
			            }
			            else{
			            	setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			            }
		        	}
		        	else{
		        		setStatus(Status.CLIENT_ERROR_CONFLICT);
		        	}
		        }
		        else{
		        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		        }
	        }
	        else if (entity == null){
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
	        }
			
			
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
        
        
        Representation result =  new JsonRepresentation(newJsonMessage);
        //set the response header
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        }  

        return result;
		
	}

	
	
	//needed here since backbone will try to send OPTIONS before POST
	@Options
	public Representation takeOptions(Representation entity) {
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 

		return new JsonRepresentation(new JSONObject());
	}


}
