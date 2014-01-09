package emailServiceTest;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.constants.Constants.gender;
import carpool.dbservice.EmailDaoService;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.User;

public class SESRelayTaskTest {
	
	@Test
	public void testActivateEmail(){
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
		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);		
		String email = "xiongchuhan@hotmail.com";
		user.setEmail(email);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			fail();
			e.printStackTrace();
		}
		String newEmail = "xiongchuhanplace@hotmail.com";
		if(EmailDaoService.sendActivationEmail(user.getUserId(), newEmail)){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			fail();
		}
	}
	
	@Test
	public void testForgotPassword(){
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
		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);		
		String email = "xiongchuhan@hotmail.com";
		user.setEmail(email);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			fail();
			e.printStackTrace();
		}
		
		try {
			if(EmailDaoService.sendForgotPasswordEmail(email)){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				fail();
			}
		} catch (UserNotFoundException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	
}
