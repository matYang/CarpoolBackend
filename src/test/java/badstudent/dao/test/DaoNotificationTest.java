package badstudent.dao.test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.database.DaoBasic;
import carpool.database.DaoMessage;
import carpool.database.DaoNotification;
import carpool.database.DaoTransaction;
import carpool.database.DaoUser;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;


public class DaoNotificationTest {

	private final Calendar calender = Calendar.getInstance();
	private final Calendar calender1 = Common.DateToCalendar(new Date(6666666));
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
			calender,calender,"paypal2");
	private final Message default1 = new Message(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
			new Location("a a a a"),calender,calender,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
			5,false,true,new ArrayList<Transaction>(),calender);
	private final Message default2 = new Message(1,2,"ImgPath2","Name2",30,40,"phone2","email2","qq2",Constants.paymentMethod.all,
			new Location("a2 a2 a2 a2"),calender,calender,"note2",Constants.messageType.help,Constants.gender.both,
			Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender);
	Transaction t = new Transaction(0, 2, 1, "initUserImgPath", "initUserName", 10,
			"targetUserImgPath", "targetUserName", 2,-5,-6, 1, "messageNote", Constants.paymentMethod.offline, 3, "requestInfo",
			"responseInfo", calender, calender, new Location("a a a a"), false, true, Constants.transactionState.aboutToStart, true, calender);
	
	@Test
	public void create(){
		DaoBasic.clearBothDatabase();
		try {
			DaoUser.addUserToDatabase(defaultUser);
			DaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			assertTrue(false);
		}
		DaoMessage.addMessageToDatabase(default1);
		DaoMessage.addMessageToDatabase(default2);
		DaoTransaction.addTransactionToDatabase(t);
		
		Notification n = new Notification(1, Constants.notificationType.on_message, Constants.notificationEvent.followed,
				1, "initUserName", 1, 1, 1, "summary", calender, false, true);
		DaoNotification.addNotificationToDatabase(n);
		assertTrue(DaoNotification.getALL().size()==1);
		
		assertTrue(n.getNotificationType()==Constants.notificationType.on_message);
		assertTrue(n.getNotificationEvent()==Constants.notificationEvent.followed);
		assertTrue(n.getInitUserId()==1);
		assertTrue(n.getInitUserName().equals("initUserName"));
		assertTrue(n.getMessageId()==1);
		assertTrue(n.getTargetUserId()==1);
		assertTrue(n.getSummary().equals("summary"));
		assertTrue(n.getCreationTime().getTime().toString().equals(calender.getTime().toString()));
		assertTrue(!n.isChecked());
		assertTrue(n.isHistoryDeleted());
		
		try {
			n = DaoNotification.getNotificationById(1);
		} catch (NotificationNotFoundException e) {
			assertTrue(false);
		}
		n.setNotificationType(Constants.notificationType.on_transaction);
		n.setNotificationEvent(Constants.notificationEvent.followerNewPost);
		n.setInitUserId(2);
		n.setInitUserName("initUserName2");
		n.setMessageId(2);
		n.setTargetUserId(2);
		n.setSummary("summary2");
		n.setCreationTime(calender1);
		n.setChecked(true);
		n.setHistoryDeleted(false);
		try {
			DaoNotification.updateNotificationToDatabase(n);
		} catch (NotificationNotFoundException e) {
			assertTrue(false);
		}
		try {
			n = DaoNotification.getNotificationById(1);
		} catch (NotificationNotFoundException e) {
			assertTrue(false);
		}
		
		assertTrue(n.getNotificationType()==Constants.notificationType.on_transaction);
		assertTrue(n.getNotificationEvent()==Constants.notificationEvent.followerNewPost);
		assertTrue(n.getInitUserId()==2);
		assertTrue(n.getInitUserName().equals("initUserName2"));
		assertTrue(n.getMessageId()==2);
		assertTrue(n.getTargetUserId()==2);
		assertTrue(n.getSummary().equals("summary2"));
		assertTrue(n.getCreationTime().getTime().toString().equals(calender1.getTime().toString()));
		assertTrue(n.isChecked());
		assertTrue(!n.isHistoryDeleted());
		
		try {
			DaoNotification.deleteNotificationFromDatabase(1);
		} catch (NotificationNotFoundException e) {
			assertTrue(false);
		}
		assertTrue(DaoNotification.getALL().size()==0);
		try {
			DaoNotification.getNotificationById(1);
			assertTrue(false);
		} catch (NotificationNotFoundException e) {
		}
		
		Notification n1 = new Notification(1, Constants.notificationType.on_message, Constants.notificationEvent.followed,
				1, "initUserName", 1, 1, 1, "summary", calender, false, true);
		Notification n2 = new Notification(1, Constants.notificationType.on_message, Constants.notificationEvent.followed,
				1, "initUserName", 1, 1, 1, "summary", calender, false, true);
		ArrayList<Notification> nList = new ArrayList<Notification>();
		nList.add(n1);
		nList.add(n2);
		DaoNotification.addNotificationToDatabase(nList);
		assertTrue(DaoNotification.getALL().size()==2);
	}
}
