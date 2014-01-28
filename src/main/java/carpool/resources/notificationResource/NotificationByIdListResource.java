package carpool.resources.notificationResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.common.Parser;
import carpool.constants.Constants.NotificationStateChangeActon;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;


public class NotificationByIdListResource extends PseudoResource{
	

    

    @Post 
    public Representation checkNotification(Representation entity) {
        int userId = -1;
        int notificationId = -1;
        JSONObject response = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
			notificationId = Integer.parseInt(this.getReqAttr("id"));
			JSONObject payload = (new JsonRepresentation(entity)).getJsonObject();
			userId = payload.getInt("userId");
			NotificationStateChangeActon stateChangeAction = NotificationStateChangeActon.fromInt(payload.getInt("stateChangeAction"));
			ArrayList<Integer> idArray = Parser.parseIntegerList(payload.getJSONArray("idArray"));
			
			this.validateAuthentication(userId);
			
			if (stateChangeAction == NotificationStateChangeActon.check){
				NotificationDaoService.checkNotification(idArray, userId);
			}
			else if (stateChangeAction == NotificationStateChangeActon.delete){
				NotificationDaoService.deleteNotification(idArray, userId);
			}

			setStatus(Status.SUCCESS_OK);
			DebugLog.d("Check notification with id: " + notificationId);

		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}

		this.addCORSHeader();
        return new JsonRepresentation(response);
    }


}
