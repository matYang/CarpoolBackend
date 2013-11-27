package carpool.resources.dianmingResource;

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


public class DMAutoMatchResource extends PseudoResource{        

    @Get
    /**
    * @return a list of messages that are auto-matched to the target message
    */
    public Representation getMessageAutoMatching() {
        int curMsgId = -1;
        int curUserId = -1;
        JSONArray response = new JSONArray();
        
        try {
            curMsgId = Integer.parseInt(this.getReqAttr("id"));
            curUserId = Integer.parseInt(this.getQueryVal("userId"));
            
            this.validateAuthentication(curUserId);
                    
            ArrayList<Message> matchingMessages = MessageDaoService.autoMatching(curMsgId, curUserId);
            if (matchingMessages != null){
                response = JSONFactory.toJSON(matchingMessages);
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
