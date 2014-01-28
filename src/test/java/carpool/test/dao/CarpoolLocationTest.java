package carpool.test.dao;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import org.junit.Test;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoLocation;
import carpool.dbservice.LocationDaoService;
import carpool.exception.location.LocationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.representation.DefaultLocationRepresentation;

public class CarpoolLocationTest {

	@Test
	public void testCreat(){

		CarpoolDaoBasic.clearBothDatabase();		
		String province = "Ontario";
		String city = "Waterloo";
		String region = "Waterloo";
		String pointName = "pointName";
		String pointAddress = "pointAddress";
		Double lat = 43.656273;
		Double lng = 22.812345;
		long match = 2;
		Location location = new Location(province,city,region,pointName,pointAddress,lat,lng,match);		
		try{			 		
			location = CarpoolDaoLocation.addLocationToDatabases(location);
			//Passed;
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testRead(){
		CarpoolDaoBasic.clearBothDatabase();		
		String province = "Ontario";
		String city = "Waterloo";
		String region = "Waterloo";
		String pointName = "pointName";
		String pointAddress = "pointAddress";
		Double lat = 43.656273;
		Double lng = 22.812345;
		long match = 2;
		Location location = new Location(province,city,region,pointName,pointAddress,lat,lng,match);
		Location test = new Location(province,city,region,pointName,pointAddress,lat,lng,match);
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);
			test.setId(location.getId());
			if(location.equals(test)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		lat = 1.3;
		lng = 1.3;
		location = new Location(province,city,region,"","",lat,lng,match);
		test =  new Location(province,city,region,"","",lat,lng,match);
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);
			test.setId(location.getId());
			if(location.equals(test)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}

	@Test
	public void testUpdate(){
		CarpoolDaoBasic.clearBothDatabase();				
		String province = "Ontario";
		String city = "Waterloo";
		String region = "Waterloo";
		String pointName = "pointName";
		String pointAddress = "pointAddress";
		Double lat = 43.656273;
		Double lng = 22.812345;
		long match = 2;
		Location location = new Location(province,city,region,pointName,pointAddress,lat,lng,match);
		Location test = new Location(province,"Toronto","Downtown",pointName,pointAddress,lat,lng,match);
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);
			location.setCity("Toronto");
			location.setRegion("Downtown");
			CarpoolDaoLocation.updateLocationInDatabases(location);
			location = CarpoolDaoLocation.getLocationById(location.getId());
			test.setId(location.getId());
			if(location.equals(test)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testGetAll(){
		CarpoolDaoBasic.clearBothDatabase();				
		String province = "Ontario";
		String city = "Waterloo";
		String region = "Waterloo";
		String pointName = "pointName";
		String pointAddress = "pointAddress";
		Double lat = 43.656273;
		Double lng = 22.812345;
		long match = 2;
		Location location = new Location(province,city,region,pointName,pointAddress,lat,lng,match);
		Location test = new Location(province,"Toronto","Downtown",pointName,pointAddress,lat,lng,match);
		ArrayList<Location> list = new ArrayList<Location>();
		int defaultLocationsNum = LocationDaoService.defalutLocationsNum;
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);			
			test = CarpoolDaoLocation.addLocationToDatabases(test);		
			list = CarpoolDaoLocation.getAllLocation();
			if(list !=null && list.size()==defaultLocationsNum + 2 && list.get(defaultLocationsNum).equals(location)&&list.get(defaultLocationsNum+1).equals(test)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testGetById(){
		CarpoolDaoBasic.clearBothDatabase();				
		String province = "Ontario";
		String city = "Waterloo";
		String region = "Waterloo";
		String pointName = "pointName";
		String pointAddress = "pointAddress";
		Double lat = 43.656273;
		Double lng = 22.812345;
		long match = 2;
		Location location = new Location(province,city,region,pointName,pointAddress,lat,lng,match);
		Location test = new Location(province,"Toronto","Downtown",pointName,pointAddress,lat,lng,match);		
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);			
			test = CarpoolDaoLocation.addLocationToDatabases(test);		
			Location test2;
			Location test3;
			test2 = CarpoolDaoLocation.getLocationById(location.getId());
			test3 = CarpoolDaoLocation.getLocationById(test.getId());
			if(test2.equals(location)&&test3.equals(test)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testDelete(){
		CarpoolDaoBasic.clearBothDatabase();				
		String province = "Ontario";
		String city = "Waterloo";
		String region = "Waterloo";
		String pointName = "pointName";
		String pointAddress = "pointAddress";
		Double lat = 43.656273;
		Double lng = 22.812345;
		long match = 2;
		Location location = new Location(province,city,region,pointName,pointAddress,lat,lng,match);
		Location test = new Location(province,"Toronto","Downtown",pointName,pointAddress,lat,lng,match);
		ArrayList<Location> list = new ArrayList<Location>();
		int defaultLocationsNum = LocationDaoService.defalutLocationsNum;
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);			
			test = CarpoolDaoLocation.addLocationToDatabases(test);		
			CarpoolDaoLocation.deleteLocation(location.getId());
			list = CarpoolDaoLocation.getAllLocation();
			if(list.size()==defaultLocationsNum + 1 && list.get(defaultLocationsNum).equals(test)){
				CarpoolDaoLocation.deleteLocation(test.getId());
				list = CarpoolDaoLocation.getAllLocation();
				if(list.size()==defaultLocationsNum){
					//Passed;
				}else{
					fail();
				}
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testAddDefaultLocationAndGetDefaultLocations() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();		
		int defaultLocationsNum = LocationDaoService.defalutLocationsNum;
		String province = "Ontario";
		String city1 = "Waterloo";
		String region1 = "Waterloo";
		String pointName1 = "pointName";
		String pointAddress1 = "pointAddress";
		Double lat1 = 43.656273;
		Double lng1 = 22.812345;
		long match1 = -1;
		int radius = 1000;
		String str = "test1";
		Location location = new Location(province,city1,region1,pointName1,pointAddress1,lat1,lng1,match1);
		DefaultLocationRepresentation dlr1 = new DefaultLocationRepresentation(location,radius,str);
		dlr1 = CarpoolDaoLocation.addDefaultLocation(dlr1);

		String city2 = "Toronto";
		String region2 = "Waterloo";
		String pointName2 = "pointName2";
		String pointAddress2 = "pointAddress2";
		Double lat2 = 43.656198;
		Double lng2 = 26.812345;
		long match2 = -1;
		int radius2 = 1000;
		String str2 = "test2";
		Location location2 = new Location(province,city2,region2,pointName2,pointAddress2,lat2,lng2,match2);
		DefaultLocationRepresentation dlr2 = new DefaultLocationRepresentation(location2,radius2,str2);
		dlr2 = CarpoolDaoLocation.addDefaultLocation(dlr2);

		try{
			ArrayList<DefaultLocationRepresentation> list = new ArrayList<DefaultLocationRepresentation>();
			list = CarpoolDaoLocation.getDefaultLocationRepresentations();
			if(list.size()==defaultLocationsNum + 2 && list.get(defaultLocationsNum).equals(dlr1) && list.get(defaultLocationsNum+1).equals(dlr2)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		

		try{
			DefaultLocationRepresentation test1 = CarpoolDaoLocation.getDefaultLocationRepresentationById(dlr1.getId());
			DefaultLocationRepresentation test2 = CarpoolDaoLocation.getDefaultLocationRepresentationById(dlr2.getId());
			if(test1.equals(dlr1) && test2.equals(dlr2)){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception e){
			e.printStackTrace();
			fail();
		}	

	}

	@Test
	public void testIsLocationPoolEmpty() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		boolean isEmpty=false;
		try{
			isEmpty = CarpoolDaoLocation.isLocationPoolEmpty();
			if(!isEmpty){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}

		String province = "Ontario";
		String city1 = "Waterloo";
		String region1 = "Waterloo";
		String pointName1 = "pointName";
		String pointAddress1 = "pointAddress";
		Double lat1 = 43.656273;
		Double lng1 = 22.812345;
		long match1 = -1;
		int radius = 1000;
		String str = "test1";
		Location location = new Location(province,city1,region1,pointName1,pointAddress1,lat1,lng1,match1);
		DefaultLocationRepresentation dlr1 = new DefaultLocationRepresentation(location,radius,str);
		dlr1 = CarpoolDaoLocation.addDefaultLocation(dlr1);

		try{
			isEmpty = CarpoolDaoLocation.isLocationPoolEmpty();
			if(!isEmpty){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testLocationDistance() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();

		String province = "Ontario";
		String city1 = "Waterloo";
		String region1 = "Waterloo";
		String pointName1 = "pointName";
		String pointAddress1 = "410 Westcroft Drive";
		Double lat1 = 43.448931;
		Double lng1 = -80.566277;
		long match1 = -1;		
		Location location = new Location(province,city1,region1,pointName1,pointAddress1,lat1,lng1,match1);

		String city2 = "Waterloo";
		String region2 = "Waterloo";
		String pointName2 = "pointName2";
		String pointAddress2 = "200 University Ave";
		Double lat2 = 43.470487;
		Double lng2 = -80.539326;
		long match2 = -1;		
		Location location2 = new Location(province,city2,region2,pointName2,pointAddress2,lat2,lng2,match2);

		//Test Univeristy vs Home
		boolean within = false;
		within = LocationDaoService.withIntheDistance(location, location2, "K", 4);
		if(within){
			//Passed;
		}else{
			fail();
		}

		within = LocationDaoService.withIntheDistance(location, location2, "K", 3);
		if(!within){
			//Passed;
		}else{
			fail();
		}

		//China
		location2.setLat(22.852133);
		location2.setLng(113.724262);

		within = LocationDaoService.withIntheDistance(location, location2, "K", 1000);
		if(!within){
			//Passed;
		}else{
			fail();
		}

		//The same place
		location2.setLat(lat1);
		location2.setLng(lng1);

		within = LocationDaoService.withIntheDistance(location, location2, "K", 0);
		if(within){
			//Passed;
		}else{
			fail();
		}

		//Test Close places
		location2.setLat(43.447556);
		location2.setLng(-80.567546);

		within = LocationDaoService.withIntheDistance(location, location2, "K", 0.5);
		if(within){
			//Passed;
		}else{
			fail();
		}

	}

	@Test
	public void testReloadDefaultLocations() throws LocationNotFoundException, LocationException, ValidationException{
		CarpoolDaoBasic.clearBothDatabase();
		CarpoolDaoLocation.reloadDefaultLocations();
		int defaultLocationsNum = LocationDaoService.defalutLocationsNum;
		String province = "Ontario";
		String city1 = "Waterloo";
		String region1 = "Waterloo";
		String pointName1 = "pointName";
		String pointAddress1 = "pointAddress";
		Double lat1 = 43.656273;
		Double lng1 = 22.812345;
		long match1 = -1;
		int radius = 1000;
		String str = "test1";
		Location location = new Location(province,city1,region1,pointName1,pointAddress1,lat1,lng1,match1);
		DefaultLocationRepresentation dlr1 = new DefaultLocationRepresentation(location,radius,str);
		dlr1 = CarpoolDaoLocation.addDefaultLocation(dlr1);		

		String city2 = "Toronto";
		String region2 = "Waterloo";
		String pointName2 = "pointName2";
		String pointAddress2 = "pointAddress2";
		Double lat2 = 43.656198;
		Double lng2 = 26.812345;
		long match2 = -1;
		int radius2 = 1000;
		String str2 = "test2";
		Location location2 = new Location(province,city2,region2,pointName2,pointAddress2,lat2,lng2,match2);
		DefaultLocationRepresentation dlr2 = new DefaultLocationRepresentation(location2,radius2,str2);
		dlr2 = CarpoolDaoLocation.addDefaultLocation(dlr2);		

		try{
			ArrayList<DefaultLocationRepresentation> list = new ArrayList<DefaultLocationRepresentation>();
			list = CarpoolDaoLocation.getDefaultLocationRepresentations();
			if(list.size()==defaultLocationsNum + 2 && list.get(defaultLocationsNum).equals(dlr1) && list.get(defaultLocationsNum+1).equals(dlr2)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		

		try{
			DefaultLocationRepresentation test1 = CarpoolDaoLocation.getDefaultLocationRepresentationById(dlr1.getId());
			DefaultLocationRepresentation test2 = CarpoolDaoLocation.getDefaultLocationRepresentationById(dlr2.getId());
			if(test1.equals(dlr1) && test2.equals(dlr2)){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception e){
			e.printStackTrace();
			fail();
		}	
	}


}
