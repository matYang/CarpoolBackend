package badstudent.resources.transactionResource;

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


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.common.Constants.messageState;
import badstudent.common.Constants.transactionStateChangeAction;
import badstudent.common.Constants.transactionStateChangeAdminAction;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.exception.message.MessageNotFoundException;
import badstudent.exception.message.MessageOwnerNotMatchException;
import badstudent.exception.transaction.TransactionNotFoundException;
import badstudent.exception.transaction.TransactionOwnerNotMatchException;
import badstudent.exception.transaction.TransactionStateViolationException;
import badstudent.model.*;
import badstudent.mappings.*;
import badstudent.resources.userResource.UserCookieResource;
import badstudent.resources.userResource.UserResource;

public class TransactionAdminResource extends ServerResource{
	

    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation updateTransaction(Representation entity) {
        int transactionId = -1;
        int stateIndex = -1;
        boolean goOn = true;
        JSONObject newJsonTransaction = new JSONObject();
        Transaction transaction = new Transaction();
        String access_admin = "";
        
		try {
			transactionId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			stateIndex = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("stateIndex"),"utf-8"));
			access_admin = java.net.URLDecoder.decode(getQuery().getValues("access_admin"),"utf-8");
			
			if (access_admin.equals(Constants.access_admin)){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_TransactionLength){
				transactionStateChangeAdminAction stateChangeAdminAction = transactionStateChangeAdminAction.fromInt(stateIndex);
				
		        if (stateChangeAdminAction != null){
		        	switch(stateChangeAdminAction){
		        		case investigation_cancel:
		        			transaction = TransactionDaoService.investigationCancelTransaction(transactionId);
		        			break;
		        			
		        		case investigation_release:
		        			transaction = TransactionDaoService.investigationReleaseTransaction(transactionId);
		        			break;
		        			
		        		default:
		        			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		        			break;
		        	}
		        }
		        else{
		        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		        }
		        
		        newJsonTransaction = JSONFactory.toJSON(transaction);
	        }
	        else if (entity == null){
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
	        }
			
		} catch (TransactionNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (TransactionStateViolationException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch(Exception e1){
			e1.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

        
        Representation result =  new JsonRepresentation(newJsonTransaction);
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
