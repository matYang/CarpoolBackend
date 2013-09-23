package carpool.resources.userResource;

import java.io.IOException;
import java.util.ArrayList;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserResource extends PseudoResource{

	/*set the response header to allow for CORS*/
	public static Series<Header> addHeader(Series<Header> responseHeaders){
		if (responseHeaders == null) { 
			responseHeaders = new Series(Header.class); 
			responseHeaders.add("Access-Control-Allow-Origin", "*");
			responseHeaders.add("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
			responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
			responseHeaders.add("Access-Control-Allow-Headers", "authCode");
			responseHeaders.add("Access-Control-Allow-Headers", "origin, x-requested-with, content-type");
		}
		return responseHeaders;
	}

	//passes received JSON into message
	//note that this parseJSON assumes the user does not exist yet, it is different from the parseJSON in UserResource
	protected User parseJSON(Representation entity){
		JSONObject jsonUser = null;
		User user = null;
		
		try {
			jsonUser = (new JsonRepresentation(entity)).getJsonObject();

			String password = jsonUser.getString("password");
			int gender = jsonUser.getInt("gender");
			String email = jsonUser.getString("email");
			Location location = new Location(java.net.URLDecoder.decode(jsonUser.getJSONObject("location").getString("province"),"utf-8"), jsonUser.getJSONObject("location").getString("city"), jsonUser.getJSONObject("location").getString("region"),jsonUser.getJSONObject("location").getString("university"));
			
			DebugLog.d("getting user location: " + location.toString() + " is location valid: " + Location.isLocationVaild(location));
			DebugLog.d("getting user password: " + password + " is password valid: " + Validator.isPasswordFormatValid(password));
			DebugLog.d("getting user email: " + email + " is email valid: " + Validator.isEmailFormatValid(email));
			
			if (Validator.isPasswordFormatValid(password) && Constants.gender.values()[gender] != null && Validator.isEmailFormatValid(email) && Location.isLocationVaild(location) && UserDaoService.isEmailAvailable(email)){
				user = new User(password,Constants.gender.values()[gender],email, location);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return user;
	}
	
	@Get
	/**
	 * Retrieve all users from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllUsers() {

		ArrayList<User> allUsers = UserDaoService.getAllUsers();
		JSONArray jsonArray = new JSONArray();
		
		if (allUsers == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allUsers);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);

		this.addCORSHeader();
		return result;
	}
	

	@Post
	public Representation createUser(Representation entity) {
		
		JSONObject newJsonUser = new JSONObject();
		User creationFeedBack = null;
		
		try{
			this.checkEntity(entity);
			User newUser = parseJSON(entity);
			
			//if available, add the message
			if (newUser != null){
				if (newUser.validate()){
					creationFeedBack = UserDaoService.createNewUser(newUser);
					//creation feedback with null indicates the user is not valid
					if (creationFeedBack != null){
						DebugLog.d("@Post::resources::createUser: available: " + creationFeedBack.getName() + " id: " +  creationFeedBack.getUserId() + " createUser: " + creationFeedBack.toString());
						creationFeedBack.prepareTopBarUser();
						
						boolean emailSent = UserDaoService.sendActivationEmail(creationFeedBack.getUserId(), creationFeedBack.getEmail());
						newJsonUser = JSONFactory.toJSON(creationFeedBack);
					}
					else{
						setStatus(Status.CLIENT_ERROR_CONFLICT);
					}
				}
				else{
					setStatus(Status.CLIENT_ERROR_CONFLICT);
				}
					
			}
			else{
				DebugLog.d("Registration null user recorded");
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		} catch(PseudoException e){
			this.doPseudoException(e);
		}

		Representation result = new JsonRepresentation(newJsonUser);
		
		this.addCORSHeader(); 
		return result;
	}

}
