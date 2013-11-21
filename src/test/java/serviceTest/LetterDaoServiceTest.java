package serviceTest;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.dbservice.LetterDaoService;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Letter;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

public class LetterDaoServiceTest {
	
	@Test
	public void testSendLetter(){
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
		
		Letter letter = new Letter(1,1,Constants.LetterType.user,"Test");
		
		try {
			LetterDaoService.sendLetter(letter);
		} catch (UserNotFoundException e) {
			fail();
		}
	}
	
	
	
}
