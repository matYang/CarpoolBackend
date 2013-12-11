package carpool.locationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONObject;

import carpool.carpoolDAO.CarpoolDaoLocation;
import carpool.constants.CarpoolConfig;
import carpool.exception.location.LocationException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.representation.LocationRepresentation;


/**
 * universal interface for location access, to replace the old LocationManager
 *
 */
public class LocationService {	

	public static void init() throws LocationException, ValidationException{
//		if (!CarpoolDaoLocation.isLocationDefaultEmpty()){
//			return; //TODO add empty checking
//		}
		ArrayList<HashMap<String, String>> bufferList = CarpoolLocationLoader.loadLocationFromFile("LocationData.txt");
		for (HashMap<String, String> bufferMap : bufferList){
			//TODO match for default locations can not be determined before they are added to mysql in the first place
			Location location = new Location(bufferMap.get("province"),bufferMap.get("city"),bufferMap.get("region"),bufferMap.get("name"),bufferMap.get("address"),Double.parseDouble(bufferMap.get("lat")),Double.parseDouble(bufferMap.get("lng")),-1l);
			//location = CarpoolDaoLocation.addLocationToDatabases(location);
			//CarpoolDaoLocation.addDefaultLocations(location.getId(), Integer.parseInt(bufferMap.get("radius")), bufferMap.get("synonyms"));
		}
	}
	
	
	public static final boolean isLocationRepresentationValid(long match_Id){
		return true;
	}
	
	
	public static final boolean isLocationRepresentationValid(LocationRepresentation locRep){
		return true;
	}
	
	public static final boolean isLocationRepresentationStored(LocationRepresentation locRep){
		return true;
	}
	
}
