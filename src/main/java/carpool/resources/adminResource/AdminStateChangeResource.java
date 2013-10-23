package carpool.resources.adminResource;

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
import carpool.constants.Constants.transactionState;
import carpool.constants.Constants.transactionStateChangeAction;
import carpool.constants.Constants.userState;
import carpool.dbservice.*;
import carpool.dbservice.admin.AdminService;
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



public class AdminStateChangeResource extends PseudoResource{
	

    @Get
    public Representation updateTransaction(Representation entity) {
    	String access_admin = "";
    	String moduleName = "";
    	int id = -1;
        int stateIndex = -1;
        
		try {
			
			access_admin = this.getQueryVal("access_admin");
			moduleName = this.getQueryVal("moduleName");
			id = Integer.parseInt(this.getQueryVal("id"));
			stateIndex = Integer.parseInt(this.getQueryVal("stateIndex"));
			
			if (!access_admin.equals(Constants.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("invalid authorization value");
				
			}
			
			if (moduleName.equalsIgnoreCase("user")){
				AdminService.changeUserState(id, userState.fromInt(stateIndex));
			}
			else if (moduleName.equalsIgnoreCase("message")){
				AdminService.changeMessageState(id, messageState.fromInt(stateIndex));
			}
			else if (moduleName.equalsIgnoreCase("transaction")){
				AdminService.changeTransactionState(id, transactionState.fromInt(stateIndex));
			}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(new JSONObject());
        this.addCORSHeader();
        return result;
    }
    
}