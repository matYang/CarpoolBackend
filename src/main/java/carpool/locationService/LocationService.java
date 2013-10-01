package carpool.locationService;

import java.util.ArrayList;
import java.util.HashMap;

import carpool.exception.ValidationException;
import carpool.exception.location.LocationException;


/**
 * universal interface for location access, to replace the old LocationManager
 *
 */
public class LocationService {
	
	private static HashMap<String, CarpoolLocation> parentNodeMap;
	private static ArrayList<HashMap<String, CarpoolLocation>> lookupMap;
	

	public static void init() throws LocationException, ValidationException{
		CarpoolLocationLoader.loadLocationFromFile("LocationData.txt");
		parentNodeMap = CarpoolLocationLoader.getParentNodeMap();
		lookupMap = CarpoolLocationLoader.getLookupMap();
	}
	
	
	
	
	

}
