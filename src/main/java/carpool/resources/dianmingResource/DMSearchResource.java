package carpool.resources.dianmingResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;

import carpool.common.DateUtility;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.constants.Constants.userSearchState;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.AccountAuthenticationException;
import carpool.factory.JSONFactory;
import carpool.locationService.LocationService;
import carpool.model.*;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;
import carpool.resources.PseudoResource;


public class DMSearchResource extends PseudoResource{

	@Get
	public Representation getAllMessages() {
		
		JSONArray response = new JSONArray();
		
		try {
			String srStr = this.getQueryVal("searchRepresentation");
			int userId = Integer.parseInt(this.getQueryVal("userId"));
			
			boolean login = false;
			try{
				this.validateAuthentication(userId);
				login = true;
			}
			catch (AccountAuthenticationException e){
				login = false;
			}
			
			SearchRepresentation sr = srStr != null ? new SearchRepresentation(srStr) : CarpoolConfig.getDefaultSearchRepresentation();
			
			//not checking for date..because an invalid date will have no search result anyways
			if (LocationService.isLocationRepresentationValid(sr.getDepartureLocation()) && LocationService.isLocationRepresentationValid(sr.getArrivalLocation()) ){
				ArrayList<Message> searchResult = new ArrayList<Message>();
				searchResult = MessageDaoService.primaryMessageSearch(sr, login, userId);
				response = JSONFactory.toJSON(searchResult);
			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch (Exception e){
			this.doException(e);
		}
		
		Representation result = new JsonRepresentation(response);
		this.addCORSHeader();
		return result;
	}

	

}
