package badstudent.dao.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.constants.Constants;
import carpool.constants.Constants.paymentMethod;
import carpool.database.DaoBasic;
import carpool.database.DaoTransaction;
import carpool.database.DaoUser;
import carpool.dbservice.EmailDaoService;
import carpool.dbservice.MessageDaoService;
import carpool.dbservice.NotificationDaoService;
import carpool.dbservice.UserDaoService;
import carpool.dbservice.authDaoService;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;


public class DaoUserServiceTest {
	private final Calendar calender = Calendar.getInstance();

	@Test
	public void updateTest(){
		DaoBasic.clearBothDatabase();
		User defaultUser = new User(1, "password", "name", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "email", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		UserDaoService.createNewUser(defaultUser);

		try {
			assertTrue(EmailDaoService.isUserEmailActivated(1)==false);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			defaultUser.setName("name2");
			UserDaoService.updateUser(defaultUser, 1);
			assertTrue(UserDaoService.getUserById(1).getName().equals("name2"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.changePassword(1, "password", "password2");
			assertTrue(UserDaoService.getUserById(1).isPasswordCorrect("password2"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}

		try {
			UserDaoService.resetUserPassword(1, "password");
			assertTrue(UserDaoService.getUserById(1).isPasswordCorrect("password"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.changeSingleLocation(1, new LocationRepresentation("b b b b"));
			assertTrue(UserDaoService.getUserById(1).getLocation().toString().equals("b b b b"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.addLocationToUniversityGroup(1, new LocationRepresentation("a a a a"));
			assertTrue(UserDaoService.getUniversityGroup(1).get(0).toString().equals("a a a a"));
			UserDaoService.removeLocationFromUniversityGroup(1, new LocationRepresentation("a a a a"));
			assertTrue(UserDaoService.getUniversityGroup(1).size()==0);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.changeContactInfo(1, "name3", 100, Constants.gender.female, "phone2", "qq2");
			assertTrue(UserDaoService.getUserById(1).getName().equals("name3"));
			assertTrue(UserDaoService.getUserById(1).getAge()==100);
			assertTrue(UserDaoService.getUserById(1).getGender()==Constants.gender.female);
			assertTrue(UserDaoService.getUserById(1).getPhone().equals("phone2"));
			assertTrue(UserDaoService.getUserById(1).getQq().equals("qq2"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.setImagePath(1, "imgPath2");
			assertTrue(UserDaoService.getImagePath(1).equals("imgPath2"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.toggleEmailNotice(1, true);
			UserDaoService.togglePhoneNotice(1, true);
			assertTrue(UserDaoService.getUserById(1).isEmailNotice());
			assertTrue(UserDaoService.getUserById(1).isPhoneNotice());
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		assertFalse(EmailDaoService.isEmailAvailable("email"));
		assertTrue(UserDaoService.isDBUserExist(1));
		assertTrue(UserDaoService.getAllUsers().get(0).getName().equals("name3"));
		assertTrue(UserDaoService.isDBUserValid(1));
		assertTrue(UserDaoService.searchByInfo("", "", "", "qq2").get(0).getName().equals("name3"));
		
		try {
			assertTrue(UserDaoService.getTopBarUserById(1).isPasswordCorrect(Constants.goofyPasswordTrickHackers));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.deleteUser(1);
			assertTrue(UserDaoService.getAllUsers().size()==0);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void redisSessionTest(){
		DaoBasic.clearBothDatabase();
		User defaultUser = new User(1, "password", "name", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "email", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		UserDaoService.createNewUser(defaultUser);
		
		String str = authDaoService.generateUserSession(1);
		assertTrue(authDaoService.validateUserSession(1, str));
		assertTrue(authDaoService.getUserFromSession(str).getName().equals("name"));
		assertTrue(authDaoService.closeUserSession(str));
		assertFalse(authDaoService.validateUserSession(1, str));
	}
	
	@Test
	public void listsTest(){
		DaoBasic.clearBothDatabase();
		User defaultUser = new User(1, "password", "name", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "email", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		User defaultUser2 = new User(2, "password2", "name2", 1, 1,1, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),21,Constants.gender.female,
				"phone2", "email2", "qq2","imgPath2",new LocationRepresentation("a2 a2 a2 a2"),true,true,true,true,
				Constants.userState.invalid,Constants.userSearchState.regionAsk,
				calender,calender,"paypal2");
		Message default1 = new Message(1,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
				new LocationRepresentation("a a a a"),calender,calender,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
				5,false,true,new ArrayList<Transaction>(),calender);
		Transaction defaultT1 = new Transaction(111, 2, 1, "initUserImgPath", "initUserName",
				1, "targetUserImgPath", "targetUserName", 1,1,1, 1, "messageNote", paymentMethod.offline, 1,
				"requestInfo", "responseInfo", calender, calender, new LocationRepresentation("a a a a"), false, false, Constants.transactionState.init, true, calender);
		UserDaoService.createNewUser(defaultUser);
		UserDaoService.createNewUser(defaultUser2);
		MessageDaoService.createNewMessage(default1);
		DaoTransaction.addTransactionToDatabase(defaultT1);
		try {
			UserDaoService.watchUser(1, 2);
			assertTrue(DaoUser.getUserWhoWatchedUser(2).size()==1);
			assertTrue(NotificationDaoService.getAllNotifications().size()==1);
			assertTrue(UserDaoService.getWatchedUsers(1).get(0).getName().equals("name2"));
			UserDaoService.deWatchUser(1, 2);
			assertTrue(UserDaoService.getWatchedUsers(1).size()==0);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			UserDaoService.watchMessage(1, 1);
			assertTrue(DaoUser.getUserWhoWatchedMessage(1).size()==1);
			assertTrue(NotificationDaoService.getAllNotifications().size()==2);
			assertTrue(UserDaoService.getWatchedMessaegs(1).get(0).getOwnerImgPath().equals("ImgPath"));
			UserDaoService.deWatchMessage(1, 1);
			assertTrue(UserDaoService.getWatchedMessaegs(1).size()==0);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		try {
			assertTrue(UserDaoService.getHistoryMessageByUserId(1).get(0).getOwnerImgPath().equals("ImgPath"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			assertTrue(UserDaoService.getTransactionByUserId(1).size()==1);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
	}
	
	//@Test
	public void EmailActivationTest(){
		DaoBasic.clearBothDatabase();
		User defaultUser = new User(1, "password", "name", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "hahaha", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		UserDaoService.createNewUser(defaultUser);
		try {
			assertTrue(EmailDaoService.changeEmail(1, "shhyfz@hotmail.com", "bla"));
		} catch (UserNotFoundException e1) {
			assertTrue(false);
		}
		assertTrue(EmailDaoService.sendActivationEmail(1, "shhyfz@hotmail.com"));
		assertTrue(EmailDaoService.reSendActivationEmail(1));
		try {
			EmailDaoService.activateUserEmail(1, DaoBasic.getJedis().get(Constants.key_emailActivationAuth+"1"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		try {
			assertTrue(UserDaoService.getUserById(1).isEmailActivated());
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		try {
			assertTrue(EmailDaoService.sendChangePasswordEmail("shhyfz@hotmail.com"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
	}

}
