package location;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import carpool.exception.ValidationException;
import carpool.exception.location.LocationException;
import carpool.locationService.LocationService;
import carpool.model.representation.LocationRepresentation;

public class LocationTest {
	
	@Test
	public void locationRepresentationTest(){
		String depth0 = "Canada";
		String depth1 = "Ontario";
		String depth2 = "Toronto";
		String depth3 = "ConvocationHall";
		int customDepthIndex = 3;
		ArrayList<String> nameList = new ArrayList<String>();
		nameList.add(depth0);
		nameList.add(depth1);
		nameList.add(depth2);
		nameList.add(depth3);
		JSONObject locJSON = new JSONObject();
		JSONArray locNameListArray = new JSONArray(nameList);
		try {
			locJSON.put("hierarchyNameList", locNameListArray);
			locJSON.put("customDepthIndex", customDepthIndex);

			LocationRepresentation locRepA = new LocationRepresentation(depth0 + "_" + depth1 + "_" + depth2 + "_" + depth3 + "_" + customDepthIndex);
			LocationRepresentation locRepB = new LocationRepresentation(nameList, customDepthIndex);
			LocationRepresentation locRepC = new LocationRepresentation(locJSON);
			LocationRepresentation locRepD = new LocationRepresentation(locRepA.toJSON());
			LocationRepresentation locRepE = new LocationRepresentation(locRepD.getPrimaryLocationString(), locRepD.getCustomLocationString(), locRepD.getCustomDepthIndex());
			LocationRepresentation locRepF = new LocationRepresentation(locJSON);
			locRepF.setCustomDepthIndex(locRepD.getCustomDepthIndex());
			locRepF.setHierarchyNameList(locRepB.getHierarchyNameList());
			
			assertTrue(locRepA.equals(locRepB));
			assertTrue(locRepA.equals(locRepC));
			assertTrue(locRepA.equals(locRepD));
			assertTrue(locRepA.equals(locRepE));
			assertTrue(locRepA.equals(locRepF));
			assertTrue(locRepB.equals(locRepC));
			assertTrue(locRepB.equals(locRepF));
			assertTrue(locRepB.equals(locRepE));
			assertTrue(locRepB.equals(locRepA));
			assertTrue(locRepC.equals(locRepF));
			assertTrue(locRepA.equals(locRepA));
			
			assertTrue(locRepD.equals(locRepC));
			assertTrue(locRepE.equals(locRepA));
			assertTrue(locRepF.equals(locRepE));
			
		} catch (JSONException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void locationServiceTest(){
		
		try {
			LocationService.init();
			LocationService.printParentNodeMap();
			LocationService.printLookupMap();
		} catch (LocationException | ValidationException e) {
			e.printStackTrace();
			fail();
		}
		
		try {
			ArrayList<String> lv1 = new ArrayList<String>();
			ArrayList<String> lv2 = new ArrayList<String>();
			ArrayList<String> lv3 = new ArrayList<String>();
			ArrayList<String> lv4 = new ArrayList<String>();
			ArrayList<String> checkPoint = new ArrayList<String>();
			
			lv1.add("Canada");
			
			lv2.add("Ontario");
			
			//LinkedHashMap is reverse insertion order
			lv3.add("Mississauga");
			lv3.add("Toronto");
			lv3.add("Waterloo");
			
			lv4.add("Square One");
			lv4.add("Pearson Airport - terminal 3");
			lv4.add("Pearson Airport - terminal 1");
			lv4.add("York Dale");
			lv4.add("Eaton Center");
			lv4.add("CN Tower");
			lv4.add("Convocation Hall");
			lv4.add("DC Library");
			lv4.add("Matthew's Sweet Little Home");
			
			assertTrue(LocationService.getAllNamesWithDepth(0).equals(lv1));
			assertTrue(LocationService.getAllNamesWithDepth(1).equals(lv2));
			assertTrue(LocationService.getAllNamesWithDepth(2).equals(lv3));
			checkPoint = LocationService.getAllNamesWithDepth(3);
			//assertTrue(LocationService.getAllNamesWithDepth(3).equals(lv4));
			
			LocationRepresentation validLocRepA = new LocationRepresentation(lv1.get(0)+"_"+lv2.get(0)+"_"+lv3.get(1)+"_"+lv4.get(3)+"_"+3);
			LocationRepresentation validLocRepB = new LocationRepresentation(lv1.get(0)+"_"+lv2.get(0)+"_"+lv3.get(0)+"_"+lv4.get(1)+"_"+3);
			LocationRepresentation invalidLocRepA = new LocationRepresentation(lv1.get(0)+"_"+lv2.get(0)+"_"+lv3.get(2)+"_"+lv4.get(2)+"_"+3);
			LocationRepresentation invalidLocRepB = new LocationRepresentation(lv1.get(0)+"_"+lv2.get(0)+"_"+lv3.get(0)+"_"+lv4.get(4)+"_"+2);
			LocationRepresentation invalidLocRepC = new LocationRepresentation(lv1.get(0)+"_"+lv2.get(0)+"_Kitchener_"+2);
			
			assertTrue(LocationService.isLocationRepresentationValid(validLocRepA));
			assertTrue(LocationService.isLocationRepresentationValid(validLocRepB));
			assertTrue(!LocationService.isLocationRepresentationValid(invalidLocRepA));
			assertTrue(!LocationService.isLocationRepresentationValid(invalidLocRepB));
			assertTrue(!LocationService.isLocationRepresentationValid(invalidLocRepC));
			
			System.out.println(LocationService.getAllSubLocationNamesWithNode(2, lv3.get(1)));
			System.out.println(LocationService.getAllSubLocationNamesWithNode(1, lv2.get(0)));
			System.out.println(LocationService.getAllSubLocationNamesWithNode(0, lv1.get(0)));
			
			System.out.println(LocationService.getSpecificJSONNode(3, lv4.get(2)));
			
			System.out.println(LocationService.getLocationRepresentation(3, lv4.get(3)));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
