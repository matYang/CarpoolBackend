package badstudent.dao.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.util.Series;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.database.DaoBasic;
import carpool.database.DaoUser;
import carpool.dbservice.EmailDaoService;
import carpool.dbservice.UserDaoService;
import carpool.dbservice.authDaoService;
import carpool.encryption.SessionCrypto;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.resources.userResource.UserCookieResource;


public class SessionControlTest {

	@Test
	public void test() {
		DaoBasic.clearBothDatabase();
		
		Calendar calender = Calendar.getInstance();
		
		User defaultUser = new User(1, "1", "name", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "uwse@me.com", "qq","imgPath",new Location("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		
		UserDaoService.createNewUser(defaultUser);
		EmailDaoService.sendActivationEmail(defaultUser.getUserId(), defaultUser.getEmail());
		
		try {
			EmailDaoService.activateUserEmail(defaultUser.getUserId(), DaoBasic.getJedis().get(Constants.key_emailActivationAuth + defaultUser.getUserId()));
			
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		User topBarUser;
		try {
			topBarUser = authDaoService.authenticateUserLogin("uwse@me.com", "1");
			assertTrue(topBarUser != null);
			if (topBarUser != null && topBarUser.isAbleToLogin()){
				CookieSetting newCookie = UserCookieResource.openCookieSession(topBarUser.getUserId());
	
//				Common.d("sacnning: " + newCookie.getValue());
//				Common.d("decrpting: " + SessionCrypto.decrypt(newCookie.getValue()));
				
				Series<Cookie> cookies = new Series<Cookie>(null);
				cookies.add(newCookie);
				User newUser1 = authDaoService.getUserFromSession(UserCookieResource.getSessionString(cookies));

//				Common.d(newUser1.toString());
				
				UserCookieResource.closeCookieSession(cookies);

				assertTrue(newUser1.getUserId() == 1 && newUser1.getEmail().equals("uwse@me.com"));
				return;
			}
			fail();
			
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		fail();
		
	}
}
