package serviceTest;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.dbservice.LetterDaoService;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Letter;
import carpool.model.Location;
import carpool.model.User;

public class LetterDaoServiceTest {
	
	@Test
	public void testSendLetter() throws LocationNotFoundException, ValidationException{
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
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user);
		CarpoolDaoUser.addUserToDatabase(user2);
		Letter letter = new Letter(1,1,Constants.LetterType.user,"Test");
		
		try {
			LetterDaoService.sendLetter(letter);
		} catch (UserNotFoundException e) {
			fail();
		}
	}
	
	
	
}
