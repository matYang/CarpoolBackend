package carpool.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.common.Constants.userSearchState;
import carpool.common.Constants.userState;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.PseudoResource;



public class UserResourceId extends PseudoResource{

    //this parseJSON parses received json into messages
    //it assumes that an id is present
	protected User parseJSON(Representation entity){
		JSONObject jsonUser = null;
		User user = null;
		
		try {
			jsonUser = (new JsonRepresentation(entity)).getJsonObject();
			Common.d("@Post::receive jsonMessage: " +  jsonUser.toString());
			
			int userId = jsonUser.getInt("userId");
			String password = jsonUser.getString("password");
			String name = jsonUser.getString("name");
			int level = jsonUser.getInt("level");
			int averageScore = jsonUser.getInt("averageScore");
			int totalTransition = jsonUser.getInt("totalTransition");
			int age = jsonUser.getInt("age");
			int gender = jsonUser.getInt("gender");
			String phone = jsonUser.getString("phone");
			String email = jsonUser.getString("email");
			String qq = jsonUser.getString("qq");
			String imgPath = jsonUser.getString("imgPath");
			Location location = new Location(jsonUser.getJSONObject("location").getString("province"), jsonUser.getJSONObject("location").getString("city"), jsonUser.getJSONObject("location").getString("region"),jsonUser.getJSONObject("location").getString("university"));
			boolean emailActivated = jsonUser.getBoolean("emailActivated");
			boolean phoneActivated = jsonUser.getBoolean("phoneActivated");
			userState state = Constants.userState.values()[jsonUser.getInt("state")];
			userSearchState searchState = Constants.userSearchState.values()[jsonUser.getInt("searchState")];
			Calendar lastLogin = Common.parseDateString(jsonUser.getString("lastLogin"));
			Calendar creationTime = Common.parseDateString(jsonUser.getString("creationTime"));
			String paypal = jsonUser.getString("paypal");
			
			
			//no DB interaction is necessary here
			if (User.isPasswordFormatValid(password) && User.isAgeValid(age) && User.isGenderValid(Constants.gender.values()[gender]) && Common.isPhoneFormatValid(phone) && Common.isEmailFormatValid(email) && Common.isQqFormatValid(qq) && Location.isLocationVaild(location)){
				user = new User(userId, password, name, level, averageScore, totalTransition, new ArrayList<Message>(), new ArrayList<Message>(), new ArrayList<User>(), new ArrayList<Transaction>(), new ArrayList<Notification>(), new ArrayList<String>(), age, Constants.gender.values()[gender], phone, email, qq, imgPath, location, emailActivated, phoneActivated, true, true, state, searchState, lastLogin, creationTime, paypal );
			}
			
		}catch (Exception e){
			  e.printStackTrace();
			  Common.d("UserIdResouce:: parseJSON error, likely invalid format");
		}

		return user;
	}
    

    @Get 
    /**
     * @return  the full user with all fields, including Messages, Transactions, Notifications
     */
    public Representation getUerById() {
        int id = -1;
        int intendedUserId = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
			id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			this.validateAuthentication(id);
			Common.d("API::GetUserById:: " + id);
			
			//used for personal page, able to retrieve any user's information
	    	User user = UserDaoService.getUserById(intendedUserId);
	    	if (user != null){
	        	//TODO user.setPassword(Message.goofyPasswordTrickHackers);
	            jsonObject = JSONFactory.toJSON(user);
	    	}
	    	else{
	    		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	    	}
			
		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch (Exception e) {
			this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader();
        return result;
    }

    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation updateUser(Representation entity) {
        int id = -1;
        boolean goOn = true;
        JSONObject newJsonUser = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
			
	        User user = parseJSON(entity);
	        if (user != null){
	        	if (user.isUserValid()){
		        	//if available, update the User, before the password is changed to the goofy password
		            User updateFeedBack = UserDaoService.updateUser(user, id);
		            if (updateFeedBack != null){
		                newJsonUser = JSONFactory.toJSON(updateFeedBack);
		                Common.d("@Put::resources::updateUser: newJsonUser" + newJsonUser.toString());
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

		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(newJsonUser);
        this.addCORSHeader();
        return result;
    }
    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteUser() {
    	boolean goOn = true;
    	
    	int id = -1;
		try {
			id = Integer.parseInt(this.getReqAttr("id"));
			this.validateAuthentication(id);
				
			UserDaoService.deleteUser(id);
			setStatus(Status.SUCCESS_OK);

        } catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
		
		this.addCORSHeader();
        return null;
    }

}
