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
	public static int defalutLocationsNum=0;

	public static void init() throws LocationException, ValidationException, LocationNotFoundException{
		if (!CarpoolDaoLocation.isLocationPoolEmpty()){
			return;
		}
		ArrayList<HashMap<String, String>> bufferList = CarpoolLocationLoader.loadLocationFromFile("LocationData.txt");
		defalutLocationsNum = bufferList.size();
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


	/*
	 * 'M' is statute miles
	 * 'K' is kilometers (default)
	 * 'N' is nautical miles
	 */
	public static boolean withIntheDistance(Location l1, Location l2, String Unit, double distance){
		double lat1 = l1.getLat();
		double lat2 = l2.getLat();
		double lon1 = l1.getLng();
		double lon2 = l2.getLng();

		double radlat1 = Math.PI * lat1/180;
		double radlat2 = Math.PI * lat2/180;

		double theta = lon1-lon2;			
		double radtheta = Math.PI * theta/180;		
		double calDist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
		calDist = Math.acos(calDist);			
		calDist = calDist * 180/Math.PI;			
		calDist = calDist * 60 * 1.1515;			
		if (Unit=="K") { calDist = calDist * 1.609344; }
		if (Unit=="N") { calDist = calDist * 0.8684; }
		//System.out.println("The distance between two places is "+calDist+"Km");
		return calDist<=distance;			   

	}

}
