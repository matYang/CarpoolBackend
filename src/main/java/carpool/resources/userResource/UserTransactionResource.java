package carpool.resources.userResource;

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

import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class UserTransactionResource extends PseudoResource{	

    @Get 
    /**
     * @return  the full transactions of the user
     */
    public Representation getTransactionByUserId() {
    	int id = -1;
    	int intendedUserId = -1;
        JSONArray response = new JSONArray();
        
        try {
			id = Integer.parseInt(this.getReqAttr("id"));
			intendedUserId = Integer.parseInt(this.getQueryVal("intendedUserId"));
			
			this.validateAuthentication(id);
			
        	ArrayList<Transaction> historyTransactions = UserDaoService.getTransactionByUserId(intendedUserId);
        	if (historyTransactions != null){
                response = JSONFactory.toJSON(historyTransactions);
        	}
        	else{
        		setStatus(Status.CLIENT_ERROR_CONFLICT);
        	}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
		} catch (Exception e) {
			this.doException(e);
		}
        
        
        Representation result = new JsonRepresentation(response);
        this.addCORSHeader();
        return result;
    }

}
