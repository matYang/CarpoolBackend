package carpool.location;

import java.util.LinkedHashMap;

import carpool.exception.LocationException;


/**
 * loader that would load the location data from location file into memory when server starts
 *
 */
public class CarpoolLocationLoader {
	
	private static boolean loadFlag;
	private static LinkedHashMap<String, CarpoolLocation> originalLoactions;
	
	
	//TODO
	public static LinkedHashMap<String, CarpoolLocation> loadLocationFromFile(String pathToFile) throws LocationException{
		if (loadFlag == true){
			throw new LocationException("Locations are already loaded");
		}
		
		
		
		loadFlag = true;
		return null;
	}
	
	public static LinkedHashMap<String, CarpoolLocation> loadLocationFromOriginalLoactions() throws LocationException{
		if (loadFlag == false){
			throw new LocationException("Locations are not loaded yet");
		}
		
		return originalLoactions;
	}
	

}
