package carpool.locationService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import carpool.common.DebugLog;
import carpool.exception.location.LocationException;
import carpool.exception.validation.ValidationException;
import carpool.model.representation.LocationRepresentation;


/**
 * loader that would load the location data from location file into memory when server starts
 *
 */
public class CarpoolLocationLoader {
	
	private static final int value_item_amount = 9;
	
	private static boolean loadedFlag;
	private static ArrayList<HashMap<String, String>> locationBufferList = new ArrayList<HashMap<String, String>>();
	private static int lineTracker = 0;
	
	
	
	private static ArrayList<String> sanitize(String str)throws ValidationException{
		ArrayList<String> sanitizedStrArray = new ArrayList<String>();
		str = str.trim();
		if (str.length() == 0 || str.charAt(0) == '#'){
			//empty or comment line, ignore
			return null;
		}
		str = str.substring(1, str.length()-1);
		String[] strArr = str.split("\\]\\-\\[");
		if (strArr.length != value_item_amount){
			throw new ValidationException("Invalid location entry at line: " + lineTracker);
		}
		for (int i = 0; i < value_item_amount; i++){
			sanitizedStrArray.add(strArr[i]);
		}
		return sanitizedStrArray;
	}
	

	
	private static void microParser(String line)throws ValidationException, LocationException{
		ArrayList<String> sanitizedStrArray = sanitize(line);
		if (sanitizedStrArray != null){
			HashMap<String, String> locationBufferMap = new HashMap<String, String>();
			locationBufferMap.put("province", sanitizedStrArray.get(0));
			locationBufferMap.put("city", sanitizedStrArray.get(1));
			locationBufferMap.put("region", sanitizedStrArray.get(2));
			locationBufferMap.put("name", sanitizedStrArray.get(3));
			locationBufferMap.put("address", sanitizedStrArray.get(4));
			locationBufferMap.put("lat", sanitizedStrArray.get(5));
			locationBufferMap.put("lng", sanitizedStrArray.get(6));
			locationBufferMap.put("radius", sanitizedStrArray.get(7));
			locationBufferMap.put("synonyms", sanitizedStrArray.get(8));
			locationBufferList.add(locationBufferMap);
		}
		
	}

	
	public static ArrayList<HashMap<String, String>> loadLocationFromFile(String pathToFile) throws LocationException, ValidationException{
		if (loadedFlag == true){
			throw new LocationException("Locations are already loaded");
		}
		
		DebugLog.d("Starting to load location data from file: " + pathToFile);
		BufferedReader br = null;
		 
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(pathToFile));
 
			while ((sCurrentLine = br.readLine()) != null) {
				microParser(sCurrentLine);
				lineTracker++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		loadedFlag = true;	
		DebugLog.d("Location loaded succesfully");
		return locationBufferList;
	}


}
