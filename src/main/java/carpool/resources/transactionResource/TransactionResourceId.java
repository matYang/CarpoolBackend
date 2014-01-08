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

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.transactionStateChangeAction;
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
import carpool.resources.userResource.userAuthResource.UserAuthenticationResource;



public class TransactionResourceId extends PseudoResource{
	

    @Get 
    /**
     * @return  get the transaction by its transaction id
     */
    public Representation getTransactionById() {
    	int id = -1;
    	int transactionId = -1;
        JSONObject jsonObject = new JSONObject();
        
        try {
			transactionId = Integer.parseInt(this.getReqAttr("id"));
			id = Integer.parseInt(this.getQueryVal("userId"));
			this.validateAuthentication(id);
		
        	Transaction transaction = TransactionDaoService.getUserTransactionById(transactionId, id);
        	if (transaction != null){
                jsonObject = JSONFactory.toJSON(transaction);
                setStatus(Status.SUCCESS_OK);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
        
        Representation result = new JsonRepresentation(jsonObject);
        this.addCORSHeader(); 
        return result;
    }
    
    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation updateTransaction(Representation entity) {
        int userId = -1;
        int transactionId = -1;
        int stateIndex = -1;
        JSONObject newJsonTransaction = new JSONObject();
        Transaction transaction = null;
        
		try {
			
			transactionId = Integer.parseInt(this.getReqAttr("id"));
			JSONObject hashHolder = (new JsonRepresentation(entity)).getJsonObject();
			userId = hashHolder.getInt("userId");
			stateIndex = hashHolder.getInt("stateChangeAction");
			
			this.validateAuthentication(userId);
			
			transactionStateChangeAction stateChangeAction = transactionStateChangeAction.fromInt(stateIndex);
			
	        if (stateChangeAction != null){
	        	switch(stateChangeAction){
	        		case cancel:
	        			transaction = TransactionDaoService.cancelTransaction(transactionId, userId);
	        			break;
	        			
	        		 case report:
	        		 	transaction = TransactionDaoService.reportTransaction(transactionId, userId);
	        		 	break;
	        			
	        		case evaluate:
	        			int score = hashHolder.getInt("score");
	        			transaction = TransactionDaoService.evaluateTransaction(transactionId, userId, score);
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
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}

        
        Representation result =  new JsonRepresentation(newJsonTransaction);
        this.addCORSHeader(); 
        return result;
    }

}
