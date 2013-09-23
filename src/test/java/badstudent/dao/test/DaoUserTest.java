package badstudent.dao.test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.DateUtility;
import carpool.constants.Constants;
import carpool.database.DaoBasic;
import carpool.database.DaoUser;
import carpool.dbservice.UserDaoService;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;


public class DaoUserTest {
	
	private final Calendar calender = Calendar.getInstance();
	private final Calendar calender2 = DateUtility.DateToCalendar(new Date(9999999));
	private final User defaultUser = new User(0, "password", "name", 0, 0,0, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone", "email", "qq","imgPath",new Location("a a a a"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private User defaultUser2 = new User(0, "password2", "name2", 1, 1,1, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),21,Constants.gender.female,
			"phone2", "email2", "qq2","imgPath2",new Location("a2 a2 a2 a2"),true,true,true,true,
			Constants.userState.invalid,Constants.userSearchState.regionAsk,
			calender2,calender2,"paypal2");

	@Test
	public void search(){
		init();
		try {
			DaoUser.addUserToDatabase(defaultUser);
			DaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		ArrayList<User> set = DaoUser.searchUser("name", "phon2", "%", "%");
		assertTrue(set.size()==2);
		
		try {
			DaoUser.deleteUserFromDatabase(1);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		init();
	}
	
	@Test
	public void createUserAndUpdateAndDelete(){
		init();
		User user = null;
		try {
			user = DaoUser.addUserToDatabase(defaultUser);
			int oldid = user.getUserId();
			user = DaoUser.getUserByEmail("email");
			assertTrue(user.getUserId()==oldid);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		try {
			user = DaoUser.getUserById(user.getUserId());
			assertTrue(user.isPasswordCorrect("password"));
			assertTrue(user.getName().equals("name"));
			assertTrue(user.getLevel()==0);
			assertTrue(user.getAverageScore()==0);
			assertTrue(user.getTotalTranscations()==0);
			assertTrue(user.getHistoryList().size()==0);
			assertTrue(user.getWatchList().size()==0);
			assertTrue(user.getSocialList().size()==0);
			assertTrue(user.getTransactionList().size()==0);
			assertTrue(user.getNotificationList().size()==0);
			assertTrue(user.getUniversityGroup().size()==0);
			assertTrue(user.getAge()==20);
			assertTrue(user.getGender()==Constants.gender.male);
			assertTrue(user.getPhone().equals("phone"));
			assertTrue(user.getEmail().equals("email"));
			assertTrue(user.getQq().equals("qq"));
			assertTrue(user.getImgPath().equals("imgPath"));
			assertTrue(user.getLocation().toString().equals("a a a a"));
			assertTrue(!user.isEmailActivated());
			assertTrue(!user.isPhoneActivated());
			assertTrue(!user.isEmailNotice());
			assertTrue(!user.isPhoneNotice());
			assertTrue(user.getState()==Constants.userState.normal);
			assertTrue(user.getSearchState()==Constants.userSearchState.universityAsk);
			assertTrue(user.getLastLogin().getTime().toString().equals(calender.getTime().toString()));
			assertTrue(user.getCreationTime().getTime().toString().equals(calender.getTime().toString()));
			assertTrue(user.getPaypal().equals("paypal"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		defaultUser2.setUserId(user.getUserId());
		try {
			DaoUser.UpdateUserInDatabase(defaultUser2);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		try {
			user = DaoUser.getUserById(defaultUser2.getUserId());
			assertTrue(user.isPasswordCorrect("password2"));
			assertTrue(user.getName().equals("name2"));
			assertTrue(user.getLevel()==1);
			assertTrue(user.getAverageScore()==1);
			assertTrue(user.getTotalTranscations()==1);
			assertTrue(user.getHistoryList().size()==0);
			assertTrue(user.getWatchList().size()==0);
			assertTrue(user.getSocialList().size()==0);
			assertTrue(user.getTransactionList().size()==0);
			assertTrue(user.getNotificationList().size()==0);
			assertTrue(user.getUniversityGroup().size()==0);
			assertTrue(user.getAge()==21);
			assertTrue(user.getGender()==Constants.gender.female);
			assertTrue(user.getPhone().equals("phone2"));
			assertTrue(user.getEmail().equals("email2"));
			assertTrue(user.getQq().equals("qq2"));
			assertTrue(user.getImgPath().equals("imgPath2"));
			assertTrue(user.getLocation().toString().equals("a2 a2 a2 a2"));
			assertTrue(user.isEmailActivated());
			assertTrue(user.isPhoneActivated());
			assertTrue(user.isEmailNotice());
			assertTrue(user.isPhoneNotice());
			assertTrue(user.getState()==Constants.userState.invalid);
			assertTrue(user.getSearchState()==Constants.userSearchState.regionAsk);
			assertTrue(user.getLastLogin().getTime().toString().equals(calender2.getTime().toString()));
			assertTrue(user.getCreationTime().getTime().toString().equals(calender2.getTime().toString()));
			assertTrue(user.getPaypal().equals("paypal2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		try {
			DaoUser.deleteUserFromDatabase(user.getUserId());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void init(){
		DaoBasic.clearBothDatabase();
	}
}
