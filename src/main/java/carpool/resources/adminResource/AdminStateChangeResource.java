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
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.MessageState;
import carpool.configurations.EnumConfig.TransactionState;
import carpool.configurations.EnumConfig.TransactionStateChangeAction;
import carpool.configurations.EnumConfig.UserState;
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
import carpool.resources.userResource.userAuthResource.UserAuthenticationResource;



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
			
			if (!access_admin.equals(EnumConfig.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("invalid authorization value");
				
			}
			
			if (moduleName.equalsIgnoreCase("user")){
				AdminService.changeUserState(id, UserState.fromInt(stateIndex));
			}
			else if (moduleName.equalsIgnoreCase("message")){
				AdminService.changeMessageState(id, MessageState.fromInt(stateIndex));
			}
			else if (moduleName.equalsIgnoreCase("transaction")){
				AdminService.changeTransactionState(id, TransactionState.fromInt(stateIndex));
			}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(new JSONObject());
        this.addCORSHeader();
        return result;
    }
    

    
}
