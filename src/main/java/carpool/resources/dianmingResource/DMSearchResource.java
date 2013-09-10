package carpool.resources.dianmingResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.common.Constants.messageType;
import carpool.common.Constants.userSearchState;
import carpool.dbservice.*;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.auth.UnacceptableSearchStateException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.mappings.*;
import carpool.model.*;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;



public class DMSearchResource extends ServerResource{

	@Get
	public Representation getAllMessages() {
		
		JSONArray response = new JSONArray();
		
		try {
			int userId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"), "utf-8"));
			
			
			Location location = new Location(getQuery().getValues("location"));
			Calendar date = Common.parseDateString(getQuery().getValues("date"));
			String searchStateString = getQuery().getValues("searchState");
			userSearchState searchState = Constants.userSearchState.values()[Integer.parseInt(searchStateString)];
			
			
			//not checking for date..because an invalid date will have no search result anyways
			if (Location.isLocationVaild(location) && User.isSearchStateValid(searchState)){
				boolean login = UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies());
				ArrayList<DMMessage> searchResult = new ArrayList<DMMessage>();
				
				//if not loged in only basic search can be used
				if (!login){
					//only basic userState types
					if(searchState == Constants.userSearchState.universityAsk || searchState == Constants.userSearchState.universityHelp || searchState == Constants.userSearchState.regionAsk || searchState == Constants.userSearchState.regionHelp){
						searchResult = DMMessageDaoService.primaryMessageSearch(location, date, searchState);
						if (searchResult == null){
							setStatus(Status.SERVER_ERROR_INTERNAL);
						}
						else{
							response = JSONFactory.toJSON(searchResult);
							setStatus(Status.SUCCESS_OK);
						}
					}
					//other wise unauthorized, much login
					else{
						setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
					}
				}
				//if logged in, extended search used to record search state
				else{
					searchResult = DMMessageDaoService.extendedMessageSearch(location, date, searchState, userId);
					if (searchResult == null){
						setStatus(Status.SERVER_ERROR_INTERNAL);
					}
					else{
						response = JSONFactory.toJSON(searchResult);
						setStatus(Status.SUCCESS_OK);
					}
				}
				
			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		} catch(UserNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch(UnacceptableSearchStateException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (ParseException e) {
			e.printStackTrace();
			Common.d("DMSearchResource:: ParseException, likely dateString foramt error");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		
		
		Representation result = new JsonRepresentation(response);

		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}

	

}
