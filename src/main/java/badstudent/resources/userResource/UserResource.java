package badstudent.resources.userResource;

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


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.dbservice.*;
import badstudent.model.*;

public class UserResource extends ServerResource{

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
	private User parseJSON(Representation entity){
		JSONObject jsonUser = null;
		try {
			jsonUser = (new JsonRepresentation(entity)).getJsonObject();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Common.d("@Post::receive jsonUser: " +  jsonUser.toString());
		

		User user = null;
		try {
			String password = jsonUser.getString("password");
			int gender = jsonUser.getInt("gender");
			String email = jsonUser.getString("email");
			Location location = new Location(java.net.URLDecoder.decode(jsonUser.getJSONObject("location").getString("province"),"utf-8"), jsonUser.getJSONObject("location").getString("city"), jsonUser.getJSONObject("location").getString("region"),jsonUser.getJSONObject("location").getString("university"));
			
			Common.d("getting user location: " + location.toString() + " is location valid: " + Location.isLocationVaild(location));
			Common.d("getting user password: " + password + " is password valid: " + User.isPasswordFormatValid(password));
			Common.d("getting user gender: " + gender + " is gender valid: " + User.isGenderValid(Constants.gender.values()[gender]));
			Common.d("getting user email: " + email + " is email valid: " + Common.isEmailFormatValid(email));
			
			if (User.isPasswordFormatValid(password) && User.isGenderValid(Constants.gender.values()[gender]) && Common.isEmailFormatValid(email) && Location.isLocationVaild(location) && UserDaoService.isEmailAvailable(email)){
				user = new User(password,Constants.gender.values()[gender],email, location);
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (NullPointerException e){
			e.printStackTrace();
			Common.d("likely invalid location string format");
			return null;
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

		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}
	

	@Post
	public Representation createMessage(Representation entity) {
		
		JSONObject newJsonUser = new JSONObject();
		User creationFeedBack = null;
		
		if (entity != null && entity.getSize() < Constants.max_userLength){
			User newUser = parseJSON(entity);
			
			//if available, add the message
			if (newUser != null){
				if (newUser.isNewUserValid()){
					creationFeedBack = UserDaoService.createNewUser(newUser);
					//creation feedback with null indicates the user is not valid
					if (creationFeedBack != null){
						Common.d("@Post::resources::createUser: available: " + creationFeedBack.getName() + " id: " +  creationFeedBack.getUserId() + " createUser: " + creationFeedBack.toString());
						creationFeedBack.prepareTopBarUser();
						
						boolean emailSent = UserDaoService.sendActivationEmail(creationFeedBack.getUserId(), creationFeedBack.getEmail());
						/*
						if (emailSent){
							setStatus(Status.SUCCESS_OK);
						}
						else{
							setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
						}
						*/
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
				Common.d("Registration null user recorded");
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		else if (entity == null){
			Common.d("Registration null entity recorded");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		else{
			setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
		}
		Representation result = new JsonRepresentation(newJsonUser);
		
		/*set the response header*/
		Series<Header> responseHeaders = addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}

	
	
	//needed here since backbone will try to send OPTIONS before POST
	@Options
	public Representation takeOptions(Representation entity) {
		/*set the response header*/
		Series<Header> responseHeaders = addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 

		/*send anything back will be fine, browser only expects a response
		Message message = new Message();
		Representation result = new JsonRepresentation(message);*/

		return new JsonRepresentation(new JSONObject());
	}


}
