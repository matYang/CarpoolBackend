package carpool.locationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import carpool.exception.ValidationException;
import carpool.exception.location.LocationException;
import carpool.model.representation.LocationRepresentation;


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
	
	private static final CarpoolLocation getNode(String name, int depth){
		return lookupMap.get(depth).get(name);
	}
	
	private static final ArrayList<CarpoolLocation> getAllNodesWithDepth(int depth){
		return (ArrayList<CarpoolLocation>) lookupMap.get(depth).values();
	}
	
	
	public static final boolean isLocationRepresentationValid(LocationRepresentation locRep){
		try{
			if (locRep.getCustomDepthIndex() >= locRep.getHierarchyNameList().size() || locRep.getCustomDepthIndex() >= lookupMap.size()){
				return false;
			}
			
			ArrayList<String> hierachyNameList = locRep.getHierarchyNameList();
			int tracker = 0;
			CarpoolLocation curLoc = parentNodeMap.get(hierachyNameList.get(tracker));
			tracker++;
			while(tracker < hierachyNameList.size()){
				curLoc = curLoc.getSubLocation(hierachyNameList.get(tracker));
				tracker++;
			}
			return true;

		} catch (NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static final ArrayList<LocationRepresentation> getAllNamesWithDepth(int depth){
		
	}
	
	
	
	public static void printParentNodeMap(){
		System.out.println("-----------Location ParentNodeMap------------");
		for (Entry<String, CarpoolLocation> cl : parentNodeMap.entrySet()){
			cl.getValue().loopPrint();
		}
	}
	
	public static void printLookupMap(){
		System.out.println("-----------Location LookupMap------------");
		for (int i = 0; i < lookupMap.size(); i++){
			System.out.println(lookupMap.get(i).keySet());
		}
	}
	
}
