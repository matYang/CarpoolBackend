package badstudent.dao.test;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.constants.Constants;
import carpool.database.carpoolDaoBasic;
import carpool.database.carpoolDaoUser;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

import static org.junit.Assert.*;

public class SocialListTest {
	private final Calendar calender = Calendar.getInstance();
	private final User A = new User(0, "password", "name", 0, 0,0, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone", "email", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private User B = new User(0, "password2", "name2", 1, 1,1, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),21,Constants.gender.female,
			"phone2", "email2", "qq2","imgPath2",new LocationRepresentation("a2 a2 a2 a2"),true,true,true,true,
			Constants.userState.invalid,Constants.userSearchState.regionAsk,
			calender,calender,"paypal2");
	private User C = new User(0, "password3", "name3", 2, 2,2, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),21,Constants.gender.female,
			"phone3", "email3", "qq3","imgPath3",new LocationRepresentation("a3 a3 a3 a3"),true,false,true,false,
			Constants.userState.invalid,Constants.userSearchState.regionAsk,
			calender,calender,"paypal3");
	@Test
	public void init(){
		carpoolDaoBasic.clearBothDatabase();
		//add 3 user to user table (A,B,C) A has no friend B has friend A C has friend B
		ArrayList<User> friend = new ArrayList<User>();
		friend.add(A);
		B.setSocialList(friend);
		try {
			carpoolDaoUser.addUserToDatabase(A);
			carpoolDaoUser.addUserToDatabase(B);
			friend.clear();
			friend.add(B);
			C.setSocialList(friend);
			carpoolDaoUser.addUserToDatabase(C);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		//test the above relationship
		try {
			User userA = carpoolDaoUser.getUserById(1);
			assertTrue(userA.getSocialList().size()==0);
			User userB = carpoolDaoUser.getUserById(2);
			assertTrue(userB.getSocialList().get(0).getName().equals("name"));
			User userC = carpoolDaoUser.getUserById(3);
			assertTrue(userC.getSocialList().get(0).getName().equals("name2"));
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		//Add B,C to A | C to B | now A has friend B,C B has friend A,C C has friend B
		try {
			User userA = carpoolDaoUser.getUserById(1);
			ArrayList<User> AList = userA.getSocialList();
			AList.add(B);
			AList.add(C);
			userA.setSocialList(AList);
			carpoolDaoUser.UpdateUserInDatabase(userA);
			User userB = carpoolDaoUser.getUserById(2);
			ArrayList<User> BList = userB.getSocialList();
			BList.add(C);
			userB.setSocialList(BList);
			carpoolDaoUser.UpdateUserInDatabase(userB);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		//test the above relationship
			try {
				User userA = carpoolDaoUser.getUserById(1);
				assertTrue(userA.getSocialList().get(0).getName().equals("name2"));
				assertTrue(userA.getSocialList().get(1).getName().equals("name3"));
				User userB = carpoolDaoUser.getUserById(2);
				assertTrue(userB.getSocialList().get(0).getName().equals("name"));
				assertTrue(userB.getSocialList().get(1).getName().equals("name3"));
				User userC = carpoolDaoUser.getUserById(3);
				assertTrue(userC.getSocialList().get(0).getName().equals("name2"));
			} catch (UserNotFoundException e) {
				e.printStackTrace();
				assertTrue(false);
			}
	}	
}
