package carpool.dbservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONObject;

import carpool.carpoolDAO.CarpoolDaoLocation;
import carpool.constants.CarpoolConfig;
import carpool.exception.location.LocationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.locationService.CarpoolLocationLoader;
import carpool.model.Location;
import carpool.model.representation.DefaultLocationRepresentation;


/**
 * universal interface for location access, to replace the old LocationManager
 *
 */
public class LocationDaoService {	

	public static void init() throws LocationException, ValidationException, LocationNotFoundException{
		if (!CarpoolDaoLocation.isLocationPoolEmpty()){
			return;
		}
		ArrayList<HashMap<String, String>> bufferList = CarpoolLocationLoader.loadLocationFromFile("LocationData.txt");
		for (HashMap<String, String> bufferMap : bufferList){

			Location location = new Location(bufferMap.get("province"),bufferMap.get("city"),bufferMap.get("region"),bufferMap.get("name"),bufferMap.get("address"),Double.parseDouble(bufferMap.get("lat")),Double.parseDouble(bufferMap.get("lng")),-1l);
			DefaultLocationRepresentation defaultLocationRep = new DefaultLocationRepresentation(location, Integer.parseInt(bufferMap.get("radius")), bufferMap.get("synonyms"));
			CarpoolDaoLocation.addDefaultLocation(defaultLocationRep);
		}
	}

	public static ArrayList<DefaultLocationRepresentation> getDefaultLocations(){
		ArrayList<DefaultLocationRepresentation> defaultLocationList = new ArrayList<DefaultLocationRepresentation>();
		defaultLocationList = CarpoolDaoLocation.getDefaultLocationRepresentations();		
		return defaultLocationList;
	}

	public static Location addLocation(Location newLocation){
		newLocation = CarpoolDaoLocation.addLocationToDatabases(newLocation);	
		return newLocation;
	}


	public static boolean isLocationValid(Location location){
		return CarpoolDaoLocation.isLocationPoolEmpty();		
	}



}
