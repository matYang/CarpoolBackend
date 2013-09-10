package badstudent.dao.test;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.Constants;
import carpool.database.DaoBasic;
import carpool.database.DaoUser;
import carpool.exception.user.UserNotFoundException;
import carpool.model.DMMessage;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;

import static org.junit.Assert.*;

public class SocialListTest {
	private final Calendar calender = Calendar.getInstance();
	private final User A = new User(0, "password", "name", 0, 0,0, new ArrayList<DMMessage>(),
			new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone", "email", "qq","imgPath",new Location("a a a a"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private User B = new User(0, "password2", "name2", 1, 1,1, new ArrayList<DMMessage>(),
			new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),21,Constants.gender.female,
			"phone2", "email2", "qq2","imgPath2",new Location("a2 a2 a2 a2"),true,true,true,true,
			Constants.userState.invalid,Constants.userSearchState.regionAsk,
			calender,calender,"paypal2");
	private User C = new User(0, "password3", "name3", 2, 2,2, new ArrayList<DMMessage>(),
			new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),21,Constants.gender.female,
			"phone3", "email3", "qq3","imgPath3",new Location("a3 a3 a3 a3"),true,false,true,false,
			Constants.userState.invalid,Constants.userSearchState.regionAsk,
			calender,calender,"paypal3");
	@Test
	public void init(){
		DaoBasic.clearBothDatabase();
		//add 3 user to user table (A,B,C) A has no friend B has friend A C has friend B
		ArrayList<User> friend = new ArrayList<User>();
		friend.add(A);
		B.setSocialList(friend);
		try {
			DaoUser.addUserToDatabase(A);
			DaoUser.addUserToDatabase(B);
			friend.clear();
			friend.add(B);
			C.setSocialList(friend);
			DaoUser.addUserToDatabase(C);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		//test the above relationship
		try {
			User userA = DaoUser.getUserById(1);
			assertTrue(userA.getSocialList().size()==0);
			User userB = DaoUser.getUserById(2);
			assertTrue(userB.getSocialList().get(0).getName().equals("name"));
			User userC = DaoUser.getUserById(3);
			assertTrue(userC.getSocialList().get(0).getName().equals("name2"));
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		//Add B,C to A | C to B | now A has friend B,C B has friend A,C C has friend B
		try {
			User userA = DaoUser.getUserById(1);
			ArrayList<User> AList = userA.getSocialList();
			AList.add(B);
			AList.add(C);
			userA.setSocialList(AList);
			DaoUser.UpdateUserInDatabase(userA);
			User userB = DaoUser.getUserById(2);
			ArrayList<User> BList = userB.getSocialList();
			BList.add(C);
			userB.setSocialList(BList);
			DaoUser.UpdateUserInDatabase(userB);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		//test the above relationship
			try {
				User userA = DaoUser.getUserById(1);
				assertTrue(userA.getSocialList().get(0).getName().equals("name2"));
				assertTrue(userA.getSocialList().get(1).getName().equals("name3"));
				User userB = DaoUser.getUserById(2);
				assertTrue(userB.getSocialList().get(0).getName().equals("name"));
				assertTrue(userB.getSocialList().get(1).getName().equals("name3"));
				User userC = DaoUser.getUserById(3);
				assertTrue(userC.getSocialList().get(0).getName().equals("name2"));
			} catch (UserNotFoundException e) {
				e.printStackTrace();
				assertTrue(false);
			}
	}	
}
