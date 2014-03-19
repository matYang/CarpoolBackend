package carpool.test.dao;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoDriver;
import carpool.carpoolDAO.CarpoolDaoPassenger;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.Gender;
import carpool.configurations.EnumConfig.LicenseType;
import carpool.configurations.EnumConfig.PassengerVerificationOrigin;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.User;
import carpool.model.identityVerification.DriverVerification;
import carpool.model.identityVerification.PassengerVerification;

public class CarpoolDPVerificationTest {

	@Test
	public void testCreate(){
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);
		User user2 =  new User("xch933h", "c2xioloo.ca", departureLocation, Gender.both);
		try {
			user = CarpoolDaoUser.addUserToDatabase(user);
			user2 = CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}

		if(user.getDriverVerification()==null&&user.getPassengerVerification()==null){
			//Passed;
		}else fail();

		String LicenseNum = "32344fgfdgtert";
		LicenseType lt = EnumConfig.LicenseType.driverLisence_a;
		Calendar licenseIssueDate = Calendar.getInstance();
		String licenseImgLink = "23rhj0eirhf_-=sdfewrf234rewffdsf&8r343gf3rfgerafg4th5yyt45tgerfgtwrvy56";
		PassengerVerificationOrigin origin = EnumConfig.PassengerVerificationOrigin.driver;
		DriverVerification driver = new DriverVerification(user.getUserId(), user.getName(),LicenseNum , lt, licenseIssueDate,licenseImgLink);		
		PassengerVerification passenger = new PassengerVerification(user2.getUserId(), user.getName(), LicenseNum, licenseImgLink, licenseImgLink, origin);
		try{
			driver = CarpoolDaoDriver.addDriverToDatabases(driver);
			passenger = CarpoolDaoPassenger.addPassengerToDatabases(passenger);
		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}	

	}

	@Test
	public void testRead(){
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);
		User user2 =  new User("xch933h", "c2xioloo.ca", departureLocation, Gender.both);
		try {
			user = CarpoolDaoUser.addUserToDatabase(user);
			user2 = CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}

		if(user.getDriverVerification()==null&&user.getPassengerVerification()==null){
			//Passed;
		}else fail();

		String LicenseNum = "32344fgfdgtert";
		LicenseType lt = EnumConfig.LicenseType.driverLisence_a;
		Calendar licenseIssueDate = Calendar.getInstance();
		String licenseImgLink = "23rhj0eirhf_-=sdfewrf234rewffdsf&8r343gf3rfgerafg4th5yyt45tgerfgtwrvy56";
		PassengerVerificationOrigin origin = EnumConfig.PassengerVerificationOrigin.driver;
		DriverVerification driver = new DriverVerification(user.getUserId(), user.getName(),LicenseNum , lt, licenseIssueDate,licenseImgLink);		
		PassengerVerification passenger = new PassengerVerification(user2.getUserId(), user.getName(), LicenseNum, licenseImgLink, licenseImgLink, origin);

		DriverVerification driverTest = null;
		PassengerVerification passengerTest = null;

		try{
			driverTest = CarpoolDaoDriver.addDriverToDatabases(driver);
			passengerTest = CarpoolDaoPassenger.addPassengerToDatabases(passenger);

			if(passengerTest==null || driverTest==null || !driverTest.equals(driver) || !passengerTest.equals(passenger)){
				fail();
			}else{
				//Passed;
			}

		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}	
	}

	@Test
	public void testUpdate(){
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);
		User user2 =  new User("xch933h", "c2xioloo.ca", departureLocation, Gender.both);
		try {
			user = CarpoolDaoUser.addUserToDatabase(user);			
			user2 = CarpoolDaoUser.addUserToDatabase(user2);			

		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}

		if(user.getDriverVerification()==null&&user.getPassengerVerification()==null){
			//Passed;
		}else fail();

		String LicenseNum = "32344fgfdgtert";
		LicenseType lt = EnumConfig.LicenseType.driverLisence_a;
		Calendar licenseIssueDate = Calendar.getInstance();
		String licenseImgLink = "23rhj0eirhf_-=sdfewrf234rewffdsf&8r343gf3rfgerafg4th5yyt45tgerfgtwrvy56";
		PassengerVerificationOrigin origin = EnumConfig.PassengerVerificationOrigin.driver;
		DriverVerification driver = new DriverVerification(user.getUserId(), user.getName(),LicenseNum , lt, licenseIssueDate,licenseImgLink);		
		PassengerVerification passenger = new PassengerVerification(user2.getUserId(), user.getName(), LicenseNum, licenseImgLink, licenseImgLink, origin);

		DriverVerification driverTest = null;
		PassengerVerification passengerTest = null;

		try{
			driver = CarpoolDaoDriver.addDriverToDatabases(driver);			
			String newLicenseImgLink = "dssssewrewrt2345r5sdfdsfdf3er";			
			driver.setLicenseImgLink(newLicenseImgLink);
			driver.setUserId(user2.getUserId());
			driver.setRealName(user2.getName());
			CarpoolDaoDriver.updateDriverVerificationInDatabases(driver);

			passenger = CarpoolDaoPassenger.addPassengerToDatabases(passenger);
			passenger.setBackImgLink(newLicenseImgLink);
			passenger.setRecommenderId(100);
			passenger.setReviewerId(900);
			CarpoolDaoPassenger.updatePassengerVerificationInDatabases(passenger);

			driverTest = CarpoolDaoDriver.addDriverToDatabases(driver);
			passengerTest = CarpoolDaoPassenger.addPassengerToDatabases(passenger);


			if(passengerTest==null || driverTest==null || !driverTest.equals(driver) || !passengerTest.equals(passenger)){
				fail();
			}else{
				//Passed;
			}

		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}	

	}

	@Test
	public void testGetAllDPVerifications(){
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);
		User user2 =  new User("xch933h", "c2xioloo.ca", departureLocation, Gender.both);
		try {
			user = CarpoolDaoUser.addUserToDatabase(user);
			user2 = CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}

		if(user.getDriverVerification()==null&&user.getPassengerVerification()==null){
			//Passed;
		}else fail();

		String LicenseNum = "32344fgfdgtert";
		LicenseType lt = EnumConfig.LicenseType.driverLisence_a;
		Calendar licenseIssueDate = Calendar.getInstance();
		String licenseImgLink = "23rhj0eirhf_-=sdfewrf234rewffdsf&8r343gf3rfgerafg4th5yyt45tgerfgtwrvy56";
		PassengerVerificationOrigin origin = EnumConfig.PassengerVerificationOrigin.driver;
		DriverVerification driver = new DriverVerification(user.getUserId(), user.getName(),LicenseNum , lt, licenseIssueDate,licenseImgLink);		
		PassengerVerification passenger = new PassengerVerification(user2.getUserId(), user.getName(), LicenseNum, licenseImgLink, licenseImgLink, origin);

		DriverVerification driverTest = null;
		PassengerVerification passengerTest = null;

		try{
			driver = CarpoolDaoDriver.addDriverToDatabases(driver);
			driverTest = CarpoolDaoDriver.getDriverVerificationById(driver.getVerificationId());
			String newLicenseImgLink = "dssssewrewrt2345r5sdfdsfdf3er";			
			driverTest.setLicenseImgLink(newLicenseImgLink);
			driverTest.setUserId(user2.getUserId());
			driverTest.setRealName(user2.getName());
			driverTest = CarpoolDaoDriver.addDriverToDatabases(driverTest);

			passenger = CarpoolDaoPassenger.addPassengerToDatabases(passenger);
			passengerTest = CarpoolDaoPassenger.getPassengerVerificationById(passenger.getVerificationId());
			passengerTest.setBackImgLink(newLicenseImgLink);
			passengerTest.setRecommenderId(100);
			passengerTest.setReviewerId(900);
			passengerTest = CarpoolDaoPassenger.addPassengerToDatabases(passengerTest);		


			ArrayList<DriverVerification> dlist = new ArrayList<DriverVerification>();
			ArrayList<PassengerVerification> plist = new ArrayList<PassengerVerification>();

			dlist = CarpoolDaoDriver.getAllDriverVerifications();
			plist = CarpoolDaoPassenger.getAllPassengerVerifications();

			if(dlist.size()==2&&dlist.get(0).equals(driver)&&dlist.get(1).equals(driverTest)){
				//Passed;
			}else{
				fail();
			}

			if(plist.size()==2&&plist.get(0).equals(passenger)&&plist.get(1).equals(passengerTest)){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}	

	}

	@Test
	public void testDelete(){
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);
		User user2 =  new User("xch933h", "c2xioloo.ca", departureLocation, Gender.both);
		try {
			user = CarpoolDaoUser.addUserToDatabase(user);
			user2 = CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}

		if(user.getDriverVerification()==null&&user.getPassengerVerification()==null){
			//Passed;
		}else fail();

		String LicenseNum = "32344fgfdgtert";
		LicenseType lt = EnumConfig.LicenseType.driverLisence_a;
		Calendar licenseIssueDate = Calendar.getInstance();
		String licenseImgLink = "23rhj0eirhf_-=sdfewrf234rewffdsf&8r343gf3rfgerafg4th5yyt45tgerfgtwrvy56";
		PassengerVerificationOrigin origin = EnumConfig.PassengerVerificationOrigin.driver;
		DriverVerification driver = new DriverVerification(user.getUserId(), user.getName(),LicenseNum , lt, licenseIssueDate,licenseImgLink);		
		PassengerVerification passenger = new PassengerVerification(user2.getUserId(), user.getName(), LicenseNum, licenseImgLink, licenseImgLink, origin);

		DriverVerification driverTest = null;
		PassengerVerification passengerTest = null;

		try{
			driver = CarpoolDaoDriver.addDriverToDatabases(driver);
			driverTest = CarpoolDaoDriver.getDriverVerificationById(driver.getVerificationId());
			String newLicenseImgLink = "dssssewrewrt2345r5sdfdsfdf3er";			
			driverTest.setLicenseImgLink(newLicenseImgLink);
			driverTest.setUserId(user2.getUserId());
			driverTest.setRealName(user2.getName());
			driverTest = CarpoolDaoDriver.addDriverToDatabases(driverTest);

			passenger = CarpoolDaoPassenger.addPassengerToDatabases(passenger);
			passengerTest = CarpoolDaoPassenger.getPassengerVerificationById(passenger.getVerificationId());
			passengerTest.setBackImgLink(newLicenseImgLink);
			passengerTest.setRecommenderId(100);
			passengerTest.setReviewerId(900);
			passengerTest = CarpoolDaoPassenger.addPassengerToDatabases(passengerTest);		

			CarpoolDaoDriver.deleteDriverVerificationInDatabase(driver.getVerificationId());		
			CarpoolDaoPassenger.deletePassengerVerificationInDatabase(passenger.getVerificationId());


			ArrayList<DriverVerification> dlist = new ArrayList<DriverVerification>();
			ArrayList<PassengerVerification> plist = new ArrayList<PassengerVerification>();

			dlist = CarpoolDaoDriver.getAllDriverVerifications();
			plist = CarpoolDaoPassenger.getAllPassengerVerifications();

			if(dlist.size()==1&&dlist.get(0).equals(driverTest)){
				//Passed;
			}else{
				fail();
			}

			if(plist.size()==1&&plist.get(0).equals(passengerTest)){
				//Passed;
			}else{
				fail();
			}

			CarpoolDaoDriver.deleteDriverVerificationInDatabase(driverTest.getVerificationId());			
			CarpoolDaoPassenger.deletePassengerVerificationInDatabase(passengerTest.getVerificationId());

			dlist = CarpoolDaoDriver.getAllDriverVerifications();
			plist = CarpoolDaoPassenger.getAllPassengerVerifications();

			if(dlist.size()==0){
				//Passed;
			}else{
				fail();
			}

			if(plist.size()==0){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}	

	}

	@Test
	public void testDPVerificationByUserId(){
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);
		User user2 =  new User("xch933h", "c2xioloo.ca", departureLocation, Gender.both);
		try {
			user = CarpoolDaoUser.addUserToDatabase(user);
			user2 = CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}

		if(user.getDriverVerification()==null&&user.getPassengerVerification()==null){
			//Passed;
		}else fail();

		String LicenseNum = "32344fgfdgtert";
		LicenseType lt = EnumConfig.LicenseType.driverLisence_a;
		Calendar licenseIssueDate = Calendar.getInstance();
		String licenseImgLink = "23rhj0eirhf_-=sdfewrf234rewffdsf&8r343gf3rfgerafg4th5yyt45tgerfgtwrvy56";
		PassengerVerificationOrigin origin = EnumConfig.PassengerVerificationOrigin.driver;
		DriverVerification driver = new DriverVerification(user.getUserId(), user.getName(),LicenseNum , lt, licenseIssueDate,licenseImgLink);		
		PassengerVerification passenger = new PassengerVerification(user2.getUserId(), user.getName(), LicenseNum, licenseImgLink, licenseImgLink, origin);

		DriverVerification driverTest = null;
		PassengerVerification passengerTest = null;

		try{
			driver = CarpoolDaoDriver.addDriverToDatabases(driver);
			driverTest = CarpoolDaoDriver.getDriverVerificationById(driver.getVerificationId());
			String newLicenseImgLink = "dssssewrewrt2345r5sdfdsfdf3er";			
			driverTest.setLicenseImgLink(newLicenseImgLink);
			driverTest.setUserId(user2.getUserId());
			driverTest.setRealName(user2.getName());
			driverTest = CarpoolDaoDriver.addDriverToDatabases(driverTest);

			passenger = CarpoolDaoPassenger.addPassengerToDatabases(passenger);
			passengerTest = CarpoolDaoPassenger.getPassengerVerificationById(passenger.getVerificationId());
			passengerTest.setBackImgLink(newLicenseImgLink);
			passengerTest.setRecommenderId(100);
			passengerTest.setReviewerId(900);
			passengerTest = CarpoolDaoPassenger.addPassengerToDatabases(passengerTest);		

			ArrayList<DriverVerification> dList = new ArrayList<DriverVerification>();
			ArrayList<DriverVerification> d2List = new ArrayList<DriverVerification>();
			ArrayList<PassengerVerification> pList = new ArrayList<PassengerVerification>();
			ArrayList<PassengerVerification> p2List = new ArrayList<PassengerVerification>();

			dList = CarpoolDaoDriver.getDriverVerificationsByUserId(user.getUserId());
			d2List = CarpoolDaoDriver.getDriverVerificationsByUserId(user2.getUserId());
			pList = CarpoolDaoPassenger.getPassengerVerificationsByUserId(user.getUserId());
			p2List = CarpoolDaoPassenger.getPassengerVerificationsByUserId(user2.getUserId());

			if(dList.size()==1&&dList.get(0).equals(driver)&&d2List.size()==1&&d2List.get(0).equals(driverTest)){
				//Passed;
			}else fail();

			if(pList.size()==0&&p2List.size()==2&&p2List.get(0).equals(passenger)&&p2List.get(1).equals(passengerTest)){
				//Passed;
			}else fail();

		}catch(Exception ex){
			ex.printStackTrace();
			fail();
		}	

	}


}
