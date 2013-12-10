package carpool.locationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONObject;

import carpool.constants.CarpoolConfig;
import carpool.exception.location.LocationException;
import carpool.exception.validation.ValidationException;
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
		if (!CarpoolLocationLoader.isLoaded()){
			throw new LocationException("Location data not successfully loaded");
		}
	}
	
	private static final CarpoolLocation getNode(int depth, String name) throws LocationException{
		CarpoolLocation node;
		if (depth >= lookupMap.size() || (node = lookupMap.get(depth).get(name)) == null){
			throw new LocationException("Location not found");
		}
		return node;
	}
	
	private static final ArrayList<CarpoolLocation> getAllNodesWithDepth(int depth){
		return new ArrayList<CarpoolLocation>(lookupMap.get(depth).values());
	}
	
	
	public static final boolean isLocationRepresentationValid(long match_Id){
//		try{
//			if (locRep.getCustomDepthIndex() >= locRep.getHierarchyNameList().size() || locRep.getCustomDepthIndex() >= lookupMap.size() || locRep.getHierarchyNameList().size() > lookupMap.size()){
//				return false;
//			}
//			
//			ArrayList<String> hierachyNameList = locRep.getHierarchyNameList();
//			int tracker = 0;
//			CarpoolLocation curLoc = parentNodeMap.get(hierachyNameList.get(tracker));
//			tracker++;
//			while(tracker < locRep.getCustomDepthIndex()){
//				curLoc = curLoc.getSubLocation(hierachyNameList.get(tracker));
//				tracker++;
//			}
//			
//			CarpoolLocation parentLoc;
//			while (curLoc != null && tracker < locRep.getHierarchyNameList().size()){
//				parentLoc = curLoc;
//				curLoc = lookupMap.get(tracker).get(hierachyNameList.get(tracker));
//				if (curLoc != null && !curLoc.getParent().getName().equals(parentLoc.getName())){
//					return false;
//				}
//				tracker++;
//			}
//			return true;
//
//		} catch (NullPointerException e){
//			e.printStackTrace();
//			return false;
//		}
		return true;
	}
	
	public static final boolean isLocationRepresentationStored(LocationRepresentation locRep){
		try{
			ArrayList<String> hierachyNameList = locRep.getHierarchyNameList();
			int tracker = 0;
			CarpoolLocation curLoc = parentNodeMap.get(hierachyNameList.get(tracker));
			tracker++;
			while(tracker < locRep.getHierarchyNameList().size()){
				curLoc = curLoc.getSubLocation(hierachyNameList.get(tracker));
				tracker++;
			}
			return curLoc != null;

		} catch (NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static final ArrayList<String> getAllNamesWithDepth(int depth){
		ArrayList<String> nameList = new ArrayList<String>();
		for (CarpoolLocation loc :  getAllNodesWithDepth(depth)){
			nameList.add(loc.getName());
		}
		return nameList;
	}
	
	public static final ArrayList<String> getAllSubLocationNamesWithNode(int depth, String name)throws  LocationException{
		ArrayList<String> nameList = new ArrayList<String>();
		CarpoolLocation node = getNode(depth, name);
		for (CarpoolLocation loc : node.getSubLocations().values()){
			nameList.add(loc.getName());
		}
		return nameList;
	}
	
	public static final JSONObject getSpecificJSONNode(int depth, String name) throws LocationException, ValidationException{
		CarpoolLocation node = getNode(depth, name);
		return node.toJSON();
		
	}
	
	public static final LocationRepresentation getLocationRepresentation(int depth, String name) throws LocationException{
		ArrayList<String> hierarchyNameList = new ArrayList<String>();
		CarpoolLocation node = getNode(depth, name);
		while (node !=  null){
			hierarchyNameList.add(node.getName());
			node = node.getParent();
		}
		Collections.reverse(hierarchyNameList);
		return new LocationRepresentation(hierarchyNameList, CarpoolConfig.customDepthIndex);
		
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
