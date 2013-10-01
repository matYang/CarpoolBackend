package carpool.locationService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import carpool.common.DebugLog;
import carpool.exception.ValidationException;
import carpool.exception.location.LocationException;
import carpool.model.representation.LocationRepresentation;


/**
 * loader that would load the location data from location file into memory when server starts
 *
 */
public class CarpoolLocationLoader {
	
	private static boolean loadedFlag;
	
	private static HashMap<String, CarpoolLocation> cache_parentNodeMap = new HashMap<String, CarpoolLocation>();
	private static ArrayList<HashMap<String, CarpoolLocation>> cache_lookupMap = new ArrayList<HashMap<String, CarpoolLocation>>();
	private static ArrayList<CarpoolLocation> backInserterBuffer = new ArrayList<CarpoolLocation>();
	
	private static final Character commentFlag = new Character('#');
	private static final Character commandFlag = new Character('$');
	private static final Character value_start = new Character('[');
	private static final Character value_end = new Character(']');
	private static final Character value_uncertain = new Character('%');
	private static final int value_item_amount = 4;
	private static final String value_item_seperator = ",";
	
	private static final String command_depth_start = "start";
	private static final String command_depth_end = "end";
	private static int depthTracker = -1;
	private static int maxDepth = -1;
	
	private static enum parser_states{base, command, value_name, value_coordinates, value_postal, value_address};
	private static parser_states parser_curState = parser_states.base;
	
	private static int lineTracker = 0;
	
	
	
	private static boolean isWhiteSpace(Character c){
		return Character.isWhitespace(c.charValue());
	}
	
	private static boolean isValueModeStart(Character c){
		return value_start.equals(c);
	}
	
	private static boolean isValueModeEnd(Character c){
		return value_end.equals(c);
	}
	
	private static ArrayList<Character> sanitize(String str)throws ValidationException{
		ArrayList<Character> sanitizedCharArray = new ArrayList<Character>();

		int readIndex = 0;
		boolean valueMode = false;
		
		//make sure not end of string, not end of line, and ignore all comments
		while(readIndex < str.length() && str.charAt(readIndex) != '\n' && str.charAt(readIndex) != commentFlag.charValue()){
			Character c = new Character(str.charAt(readIndex));
			
			//do not ignore white space when in value mode
			if (valueMode || !isWhiteSpace(c)){
				sanitizedCharArray.add(c);
				
				if (!valueMode && isValueModeStart(c)){
					valueMode = true;
				}
				else if (valueMode && isValueModeEnd(c)){
					valueMode = false;
				}
				else{
					if (!valueMode && !isValueModeEnd(c)){
						throw new ValidationException("Invalid " + value_end + " placement at line: " + lineTracker);
					}
					else if (valueMode && isValueModeStart(c)){
						throw new ValidationException("Invalid " + value_start + " placement at line: " + lineTracker);
					}
				}
			}
			readIndex++;
		}
		
		return sanitizedCharArray;
	}
	
	private static BigDecimal[] parseLatLonFromToken(String token) throws ValidationException{
		token.replaceAll("\\s+", "");
		try{
			String[] tokens = token.split(value_item_seperator);
			BigDecimal lat = new BigDecimal(tokens[0]);
			BigDecimal lon = new BigDecimal(tokens[1]);
			BigDecimal[] coordinates = {lat, lon};
			return coordinates;
		}
		catch (Exception e){
			throw new ValidationException("Invalid lat-lon format at line: " + lineTracker);
		}
		
		
	}
	
	private static String parsePostalCodeFromToken(String token){
		token.replaceAll("\\s+", "");
		return token;
	}
	
	private static CarpoolLocation parseLocationFromCharArray(ArrayList<Character> cArray) throws ValidationException{
		String name = "unloaded";
		BigDecimal lat = new BigDecimal(-1);
		BigDecimal lon = new BigDecimal(-1);
		String postalCode = "unloaded";
		String address = "unloaded";
		
		String bufferToken = "";
		int itemTracker = -1;
		for (int i = 0; i < cArray.size(); i++){
			Character curChar = cArray.get(i);
			if (curChar.equals(value_start)){
				itemTracker++;
				if (itemTracker >= value_item_amount){
					throw new ValidationException("Invalid item number at line: " + lineTracker);
				}
				switch (itemTracker){
					case 0: 
						parser_curState = parser_states.value_name;
						break;
					case 1:
						parser_curState = parser_states.value_coordinates;
						break;
					case 2:
						parser_curState = parser_states.value_postal;
						break;
					case 3:
						parser_curState = parser_states.value_address;
						break;
					default:
						throw new ValidationException("Invalid item number at line: " + lineTracker);
				}
				
			}
			else if (curChar.equals(value_end)){
				if (parser_curState == parser_states.value_name){
					name = bufferToken;
				}
				else if (parser_curState == parser_states.value_coordinates){
					BigDecimal[] latlons = parseLatLonFromToken(bufferToken);
					lat = latlons[0];
					lon = latlons[1];
				}
				else if (parser_curState == parser_states.value_postal){
					postalCode = parsePostalCodeFromToken(bufferToken);
				}
				else if (parser_curState == parser_states.value_address){
					address = bufferToken;
				}
				else{
					throw new ValidationException("Invalid value state with item number: " + itemTracker + " at line: " + lineTracker);
				}
			}
			else{
				bufferToken += curChar;
			}
		}
		
		return new CarpoolLocation(name, depthTracker, postalCode, address, lat, lon);
	}
	
	private static void backInsertBufferToNodeMap() throws ValidationException, LocationException{
		if (backInserterBuffer.size() != depthTracker+1){
    		throw new ValidationException("BackInserterBuffer not prepared at line: " + lineTracker);
    	}
		
		backInserterBuffer.get(depthTracker).setParent(backInserterBuffer.get(depthTracker-1));
		backInserterBuffer.get(depthTracker-1).addSubLocation(backInserterBuffer.get(depthTracker));
	}
	
	private static void insertToLookupMap(CarpoolLocation newLocation) throws ValidationException{
		if (cache_lookupMap.size() < depthTracker){
			throw new ValidationException("LookupMap insert error, higher lookupMap not added with size: " + cache_lookupMap.size() + " at line: " + lineTracker);
		}
		else{
			if (cache_lookupMap.get(depthTracker) == null){
				cache_lookupMap.set(depthTracker, new HashMap<String, CarpoolLocation>());
			}
			cache_lookupMap.get(depthTracker).put(newLocation.getName(), newLocation);
		}
	}
	
	private static void executeValueFromCharArray(ArrayList<Character> cArray) throws ValidationException, LocationException{
		if (depthTracker < 0){
			throw new ValidationException("Invalid depth when trying to parse value, current depth: " + depthTracker + " at line: " + lineTracker);
		}
		CarpoolLocation newLocation = parseLocationFromCharArray(cArray);
		if (backInserterBuffer.size() == depthTracker+1){
    		backInserterBuffer.remove(depthTracker);
    	}
		backInserterBuffer.add(newLocation);
		
		if (depthTracker == 0){
			cache_parentNodeMap.put(newLocation.getName(), newLocation);
		}
		else{
			backInsertBufferToNodeMap();
		}
		insertToLookupMap(newLocation);
	}
	
	private static void executeCommandFromCharAarray(ArrayList<Character> cArray) throws ValidationException{
		parser_curState = parser_states.command;
		StringBuilder builder = new StringBuilder(cArray.size());
	    
		//start from 1, ignore commandFlag
		for(int i = 1; i < cArray.size(); i++){
	    	builder.append(cArray.get(i));
	    }
	    String commandStr = builder.toString();
	    
	    if (commandStr.equals(command_depth_start)){
	    	depthTracker++;
	    	if (maxDepth < depthTracker){
	    		maxDepth = depthTracker;
	    	}
	    }
	    else if (commandStr.equals(command_depth_end)){
	    	if (backInserterBuffer.size() == depthTracker+1){
	    		backInserterBuffer.remove(depthTracker);
	    	}
	    	else if (backInserterBuffer.size() == depthTracker && depthTracker != maxDepth){
	    		DebugLog.d("WARNING: Ending location with no sub locations at line: " + lineTracker);
	    	}
	    	else{
	    		throw new ValidationException("BackInserterBuffer not in sync with depthTracker at line: " + lineTracker);
	    	}
	    	depthTracker--;
	    }
	}
	
	private static void microStateMachine(String line)throws ValidationException, LocationException{
		ArrayList<Character> sanitizedCharArray = sanitize(line);
		if (sanitizedCharArray.size() > 0){
			Character determingFlag = sanitizedCharArray.get(0);
			if (determingFlag.equals(commandFlag)){
				executeCommandFromCharAarray(sanitizedCharArray);
			}
			else if (determingFlag.equals(value_start)){
				executeValueFromCharArray(sanitizedCharArray);
			}
			else{
				throw new ValidationException("Invalud determing flag at the start of line: " + lineTracker);
			}
		}
		
		
	}
	
	private static void checkSum() throws LocationException{
		if (depthTracker != -1){
			throw new LocationException("Location-data tree not successfully read, ending with depth: " + depthTracker + " at line: " + lineTracker);
		}
	}
	
	public static void loadLocationFromFile(String pathToFile) throws LocationException, ValidationException{
		if (loadedFlag == true){
			throw new LocationException("Locations are already loaded");
		}
		
		DebugLog.d("Starting to load location data from file: " + pathToFile);
		
		BufferedReader br = null;
		 
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(pathToFile));
 
			while ((sCurrentLine = br.readLine()) != null) {
				microStateMachine(sCurrentLine);
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
		DebugLog.d("Location loaded,");
		
		checkSum();
		
		loadedFlag = true;
		
		DebugLog.d("Location loaded succesfully, total depth: " + depthTracker);
	}

	public static boolean isLoaded(){
		return loadedFlag;
	}
	
	public static HashMap<String ,CarpoolLocation> getParentNodeMap() throws LocationException{
		if (!loadedFlag){
			throw new LocationException("Location data not loaded yet");
		}
		return cache_parentNodeMap;
	}
	
	public static ArrayList<HashMap<String, CarpoolLocation>> getLookupMap() throws LocationException{
		if (!loadedFlag){
			throw new LocationException("Location data not loaded yet");
		}
		return cache_lookupMap;
	}
	

}
