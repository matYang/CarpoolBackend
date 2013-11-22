package carpool.resources.userResource;

import java.io.IOException;
import java.util.ArrayList;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.factory.JSONFactory;
import carpool.locationService.LocationService;
import carpool.model.*;
import carpool.model.representation.LocationRepresentation;
import carpool.resources.PseudoResource;


public class UserResource extends PseudoResource{


	protected User parseJSON(Representation entity) throws ValidationException{
		JSONObject jsonUser = null;
		User user = null;
		
		try {
			jsonUser = (new JsonRepresentation(entity)).getJsonObject();
			
			String password = jsonUser.getString("password");
			String email = jsonUser.getString("email");
			LocationRepresentation location = new LocationRepresentation(jsonUser.getJSONObject("location"));
			gender g = Constants.gender.fromInt(jsonUser.getInt("gender"));
			
			//if email is used, do not register
			if (!EmailDaoService.isEmailAvailable(email)){
				throw new ValidationException("Email already in use");
			}
			
			if (Validator.isPasswordFormatValid(password) && Validator.isEmailFormatValid(email) && LocationService.isLocationRepresentationValid(location)){
				user = new User(password, email, location, g);
			}
		} catch (JSONException|IOException e) {
			throw new ValidationException("Invalid data formats");
		}

		return user;
	}
	
	@Get
	/**
	 * Retrieve all users from server. This API is intended solely for testing purposes
	 */
	public Representation getAllUsers() {

		ArrayList<User> allUsers = UserDaoService.getAllUsers();
		JSONArray jsonArray = JSONFactory.toJSON(allUsers);
		
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
			
			if (newUser.validate()){
				creationFeedBack = UserDaoService.createNewUser(newUser);

				DebugLog.d("@Post::resources::createUser: available: " + creationFeedBack.getEmail() + " id: " +  creationFeedBack.getUserId());
				
				EmailDaoService.sendActivationEmail(creationFeedBack.getUserId(), creationFeedBack.getEmail());
				newJsonUser = JSONFactory.toJSON(creationFeedBack);

			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}

		} catch(PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
		}

		Representation result = new JsonRepresentation(newJsonUser);
		
		this.addCORSHeader(); 
		return result;
	}

}
