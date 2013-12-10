package carpool.test.dao;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import org.junit.Test;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoLocation;
import carpool.model.Location;

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
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);			
			test = CarpoolDaoLocation.addLocationToDatabases(test);		
			list = CarpoolDaoLocation.getAllLocation();
			if(list !=null && list.size()==2 && list.get(0).equals(location)&&list.get(1).equals(test)){
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
		try{
			location = CarpoolDaoLocation.addLocationToDatabases(location);			
			test = CarpoolDaoLocation.addLocationToDatabases(test);		
			CarpoolDaoLocation.deleteLocation(location.getId());
			list = CarpoolDaoLocation.getAllLocation();
			if(list.size()==1 && list.get(0).equals(test)){
				CarpoolDaoLocation.deleteLocation(test.getId());
				list = CarpoolDaoLocation.getAllLocation();
				if(list.size()==0){
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

}
