package carpool.resources.transactionResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.transactionStateChangeAction;
import carpool.constants.Constants.transactionStateChangeAdminAction;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.transaction.TransactionOwnerNotMatchException;
import carpool.exception.transaction.TransactionStateViolationException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserResource;
import carpool.resources.userResource.userAuthResource.UserCookieResource;



public class TransactionAdminResource extends PseudoResource{
	

    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation updateTransaction(Representation entity) {
        int transactionId = -1;
        int stateIndex = -1;
        JSONObject newJsonTransaction = new JSONObject();
        Transaction transaction = new Transaction();
        String access_admin = "";
        
		try {
			this.checkEntity(entity);
			
			transactionId = Integer.parseInt(this.getReqAttr("id"));
			stateIndex = Integer.parseInt(this.getQueryVal("stateIndex"));
			access_admin = this.getQueryVal("access_admin");
			
			if (!access_admin.equals(Constants.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("invalid authorization value");
				
			}
			
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
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(newJsonTransaction);
        this.addCORSHeader();
        return result;
    }
    
}
