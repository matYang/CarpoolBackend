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
import carpool.common.DebugLog;
import carpool.configurations.CarpoolConfig;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.UserSearchState;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.AccountAuthenticationException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.model.representation.SearchRepresentation;
import carpool.resources.PseudoResource;


public class DMSearchResource extends PseudoResource{

	@Get
	public Representation searchMessages() {
		
		JSONArray response = new JSONArray();
		
		try {
			String srStr = this.getPlainQueryVal("searchRepresentation");
			int userId = Integer.parseInt(this.getQueryVal("userId"));
			
			DebugLog.d("SearchMessage received searchRepresentation: " + srStr);
			
			boolean login = false;
			try{
				this.validateAuthentication(userId);
				login = true;
				
				if (userId <= 0){
					login = false;
				}
			}
			catch (AccountAuthenticationException e){
				login = false;
			}
			
			SearchRepresentation sr = srStr != null ? new SearchRepresentation(srStr) : CarpoolConfig.getDefaultSearchRepresentation();
			
			//no need to valdiate location anymore, as an id will only have match or no-match
			ArrayList<Message> searchResult = new ArrayList<Message>();
			searchResult = MessageDaoService.primaryMessageSearch(sr, login, userId);
			response = JSONFactory.toJSON(searchResult);
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e){
			return this.doException(e);
		}
		
		Representation result = new JsonRepresentation(response);
		this.addCORSHeader();
		return result;
	}

	

}
