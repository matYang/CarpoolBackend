package carpool.resources.dianmingResource;

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
import carpool.common.Constants.messageState;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;



public class DMResourceId extends ServerResource{

	//passes received json into message
		//note that this parseJSON
		private DMMessage parseJSON(Representation entity, int messageId, int userId){
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
				message = new DMMessage(messageId, jsonMessage.getInt("ownerId"), Constants.paymentMethod.values()[jsonMessage.getInt("paymentMethod")], 
						new Location(jsonMessage.getJSONObject("location").getString("province"), jsonMessage.getJSONObject("location").getString("city"), jsonMessage.getJSONObject("location").getString("region"),jsonMessage.getJSONObject("location").getString("university")), Common.parseDateString(jsonMessage.getString("startTime")), Common.parseDateString(jsonMessage.getString("endTime")), 
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
			} catch (Exception e){
				  e.printStackTrace();
				  Common.d("DMResourceId:: parseJSON error, likely invalid gender format");
			}

			return message;
		}
		
		

    @Get 
    /**
     * @return  the the message by its message id
     */
    public Representation getMessageById() {
    	int id = -1;
    	int messageId = -1;
        boolean goOn = false;
        JSONObject jsonObject = new JSONObject();
        
        try {
			messageId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn){
	        	DMMessage message = DMMessageDaoService.getMessageById(messageId);
	        	if (message != null){
	                jsonObject = JSONFactory.toJSON(message);
	                setStatus(Status.SUCCESS_OK);
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	        	}
	        }
			
		} catch (MessageNotFoundException e){
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
    public Representation updateMessage(Representation entity) {
        int id = -1;
        int messageId = -1;
        boolean goOn = true;
        JSONObject newJsonMessage = new JSONObject();
        
		try {
			messageId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_DMMessageLength){
		        DMMessage message = parseJSON(entity, messageId, id);
		        if (message != null){
		        	if (message.isMessageValid() && message.getOwnerId() == id){
			        	//if available, update the message
			            DMMessage updateFeedBack = DMMessageDaoService.updateMessage(message);
			            if (updateFeedBack != null){
			                newJsonMessage = JSONFactory.toJSON(updateFeedBack);
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
			
			
		} catch (MessageOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (MessageNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
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
    
    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteMessage() {
    	boolean deleted = false;
    	boolean goOn = true;
    	
    	int id = -1;
    	int messageId = -1;
		try {
			messageId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			//not full user here
			if (goOn){
		   		 deleted = DMMessageDaoService.deleteMessage(messageId, id);
		   		 if (deleted){
			       	 setStatus(Status.SUCCESS_OK);
			       	 Common.d("@Delete with id: " + messageId);
			     }
			     else{
			       	 setStatus(Status.CLIENT_ERROR_CONFLICT);
			     }
			}
			
        } catch (MessageOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (MessageNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (DuplicateSessionCookieException e1){
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
