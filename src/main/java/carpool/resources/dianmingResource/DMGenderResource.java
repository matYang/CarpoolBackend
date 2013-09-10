package carpool.resources.dianmingResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import carpool.common.Constants.gender;
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



public class DMGenderResource extends ServerResource{

	//passes received json into message
		//note that this parseJSON 
		private gender parseJSON(Representation entity){
			JSONObject jsonMessage = null;
			gender newGender = null;
			try {
				jsonMessage = (new JsonRepresentation(entity)).getJsonObject();
				newGender = Constants.gender.values()[jsonMessage.getInt("gender")];
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (NullPointerException e){
				  e.printStackTrace();
			} catch (Exception e){
				  e.printStackTrace();
				  Common.d("DMMessage GendeResource:: parseJSON error, likely invalid gender format");
			}

			return newGender;
		}
		
	
    @Put 
    public Representation updateMessage(Representation entity) {
        int id = -1;
        int messageId = -1;
        boolean goOn = true;
        JSONObject response = new JSONObject();
        
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
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_userLength){
		        gender newGender = parseJSON(entity);
		        if (newGender != null){
		        	if (User.isGenderValid(newGender)){
			        	//if valid, update the message
			            newGender = DMMessageDaoService.updateGender(newGender, messageId, id);
			            if (newGender != null){
			                response = JSONFactory.toJSON(newGender);
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
		} catch(Exception e1){
			e1.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        
        
        Representation result =  new JsonRepresentation(response);
        //set the response header
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        }  

        return result;
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
