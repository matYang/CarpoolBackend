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
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;



public class UserResourceId extends ServerResource{

    //this parseJSON parses received json into messages
    //it assumes that an id is present
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

		Common.d("@Post::receive jsonMessage: " +  jsonUser.toString());
		

		User user = null;
		try {
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
				user = new User(userId, password, name, level, averageScore, totalTransition, new ArrayList<DMMessage>(), new ArrayList<DMMessage>(), new ArrayList<User>(), new ArrayList<Transaction>(), new ArrayList<Notification>(), new ArrayList<String>(), age, Constants.gender.values()[gender], phone, email, qq, imgPath, location, emailActivated, phoneActivated, true, true, state, searchState, lastLogin, creationTime, paypal );
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		  catch (NullPointerException e){
			  e.printStackTrace();
			  Common.d("likely invalid location string format");
			  return null;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e){
			  e.printStackTrace();
			  Common.d("UserIdResouce:: parseJSON error, likely invalid gender format");
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
        boolean goOn = false;
        JSONObject jsonObject = new JSONObject();
        
        try {
			id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			intendedUserId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("intendedUserId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			Common.d("API::GetUserById:: " + id);
			
			if (goOn){
				//used for personal page, able to retrieve any user's information
	        	User user = UserDaoService.getUserById(intendedUserId);
	        	if (user != null){
	            	//TODO user.setPassword(Message.goofyPasswordTrickHackers);
	                jsonObject = JSONFactory.toJSON(user);

	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	        	}
	        }
			
		} catch (UserNotFoundException e){
        	e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

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
			id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_userLength){
		        User user = parseJSON(entity);
		        if (user != null){
		        	if (user.isUserValid()){
			        	//if available, update the User, before the password is changed to the goofy password
			            User updateFeedBack = UserDaoService.updateUser(user, id);
			            if (updateFeedBack != null){
			            	//goofy password sent to front end instead of real password
			                //TODO user.setPassword(Message.goofyPasswordTrickHackers);
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
	        }
	        else if (entity == null){
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
	        }
		} catch (UserNotFoundException e){
        	e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
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
		} catch(Exception e1){
			e1.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        Representation result =  new JsonRepresentation(newJsonUser);
        //set the response header
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        }  

        return result;
    }
    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteUser() {
    	boolean deleted = false;
    	boolean goOn = true;
    	
    	int id = -1;
		try {
			id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			//not full user here
			if (goOn){
		   		 UserDaoService.deleteUser(id);
			     setStatus(Status.SUCCESS_OK);
			     Common.d("@Delete with id: " + id);
			}
        } catch (UserNotFoundException e){
        	e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch(NumberFormatException e){
        	e.printStackTrace();
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        } catch(UnsupportedEncodingException e1){
			e1.printStackTrace();
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch(Exception e2){
			e2.printStackTrace();
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return null;
    }



    //needed here since backbone will try to send OPTIONS to /id before PUT or DELETE
    @Options
    public Representation takeOptions(Representation entity) {
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 
        //send anything back will be fine, browser just expects a response

        return new JsonRepresentation(new JSONObject());
    }

}
