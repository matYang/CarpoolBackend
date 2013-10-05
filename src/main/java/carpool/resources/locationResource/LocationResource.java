package carpool.resources.locationResource;

import java.util.ArrayList;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.factory.JSONFactory;
import carpool.locationService.LocationService;
import carpool.model.*;
import carpool.resources.PseudoResource;

public class LocationResource extends PseudoResource{


	@Get
	public Representation searchLocation() {
		ArrayList<String> searchResult = null;
		try{
			//get query parameter _depth *_name
			int depth = Integer.parseInt(this.getQueryVal("depth"));
			
			//counties do not have parent node, so use depth=-1 as an indicator of their parent's depth
			if (depth == -1){
				DebugLog.d("SearchLocation: depth: " + depth);
				searchResult = LocationService.getAllNamesWithDepth(depth+1);
			}
			else{
				String name = this.getQueryVal("name");
				DebugLog.d("SearchLocation: depth: " + depth + " name: " + name);
				searchResult = LocationService.getAllSubLocationNamesWithNode(depth, name);
			}
		}
		catch (Exception e){
			this.doException(e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		
		
		JSONArray jsonArray = searchResult != null ? new JSONArray(searchResult) : new JSONArray (new ArrayList<String>());
		
		Representation result = new JsonRepresentation(jsonArray);
		
		this.printResult(result);		
		this.addCORSHeader(); 
		return result;
	}
	
}

