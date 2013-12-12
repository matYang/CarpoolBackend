package carpool.resources.locationResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.common.DebugLog;
import carpool.dbservice.LocationDaoService;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.Location;
import carpool.model.representation.DefaultLocationRepresentation;
import carpool.resources.PseudoResource;

public class LocationDefaultResource extends PseudoResource{
	
	@Get
	public Representation getDefaultLocation() {
		
		ArrayList<DefaultLocationRepresentation> defaultLocations = new ArrayList<DefaultLocationRepresentation>();
		
		try{
			defaultLocations = LocationDaoService.getDefaultLocations();
		} catch (Exception e){
			return this.doException(e);
		}
		
		JSONArray jsonArray = JSONFactory.toJSON(defaultLocations);
		
		Representation result = new JsonRepresentation(jsonArray);
				
		this.addCORSHeader(); 
		return result;
	}

}
