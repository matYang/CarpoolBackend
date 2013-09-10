package badstudent.dao.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import badstudent.common.Constants;
import badstudent.database.DaoBasic;
import badstudent.database.DaoDMMessage;
import badstudent.database.DaoNotification;
import badstudent.database.DaoTransaction;
import badstudent.database.DaoUser;
import badstudent.dbservice.UserDaoService;
import badstudent.exception.user.UserNotFoundException;
import badstudent.model.DMMessage;
import badstudent.model.Location;
import badstudent.model.Notification;
import badstudent.model.Transaction;
import badstudent.model.User;

public class NotificationListTest {
	
	private Calendar calender = Calendar.getInstance();
	private User defaultUser1 = new User(0, "password", "name", 0, 0,0, new ArrayList<DMMessage>(),
			new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone", "email", "qq","imgPath",new Location("a a a a"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private User defaultUser2 = new User(1, "password", "name1", 0, 0,0, new ArrayList<DMMessage>(),
			new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone1", "email1", "qq1","imgPath1",new Location("a2 a2 a2 a2"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private DMMessage default1 = new DMMessage(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
			new Location("a a a a"),calender,calender,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
			5,false,true,new ArrayList<Transaction>(),calender);
	private Transaction t = new Transaction(0, 2, 1, "initUserImgPath", "initUserName", 10,
			"targetUserImgPath", "targetUserName", 2,-5,-6, 1, "messageNote", Constants.paymentMethod.offline, 3, "requestInfo",
			"responseInfo", calender, calender, new Location("a a a a"), false, true, Constants.transactionState.aboutToStart, true, calender);
	private Notification n1 = new Notification(1, Constants.notificationType.on_message, Constants.notificationEvent.followed,
			1, "initUserName", 1, 1, 1, "summary", calender, false, true);
	private Notification n2 = new Notification(1, Constants.notificationType.on_message, Constants.notificationEvent.followed,
			1, "initUserName", 1, 1, 1, "summary", calender, false, true);

	
	@Test
	public void create(){
		DaoBasic.clearBothDatabase();
		try {
			DaoUser.addUserToDatabase(defaultUser1);
			DaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			assertTrue(false);
		}
		DaoDMMessage.addMessageToDatabase(default1);
		DaoTransaction.addTransactionToDatabase(t);
		DaoNotification.addNotificationToDatabase(n1);
		DaoNotification.addNotificationToDatabase(n2);
		
		try {
			User user = DaoUser.getUserById(1);
			assertTrue(user.getNotificationList().size()==2);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			assertTrue(UserDaoService.getNotificationByUserId(1).size()==2);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			User user = DaoUser.getUserById(2);
			assertTrue(user.getNotificationList().size()==0);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
	}
}
