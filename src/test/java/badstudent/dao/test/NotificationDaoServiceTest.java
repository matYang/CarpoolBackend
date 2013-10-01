package badstudent.dao.test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.database.DaoBasic;
import carpool.database.DaoNotification;
import carpool.dbservice.MessageDaoService;
import carpool.dbservice.NotificationDaoService;
import carpool.dbservice.TransactionDaoService;
import carpool.dbservice.UserDaoService;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.notification.NotificationOwnerNotMatchException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;


public class NotificationDaoServiceTest {
	
	private final Calendar calender = Calendar.getInstance();
	private final User defaultUser = new User(0, "password", "name", 0, 0,0, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone", "email", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private User defaultUser2 = new User(0, "password2", "name2", 1, 1,1, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),21,Constants.gender.female,
			"phone2", "email2", "qq2","imgPath2",new LocationRepresentation("a2 a2 a2 a2"),true,true,true,true,
			Constants.userState.invalid,Constants.userSearchState.regionAsk,
			calender,calender,"paypal2");
	private final Message default1 = new Message(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
			new LocationRepresentation("a a a a"),calender,calender,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
			5,false,true,new ArrayList<Transaction>(),calender);
	private Transaction t = new Transaction(0, 2, 1, "initUserImgPath", "initUserName", 10,
			"targetUserImgPath", "targetUserName", 2,-5,-6, 1, "messageNote", Constants.paymentMethod.offline, 3, "requestInfo",
			"responseInfo", calender, calender, new LocationRepresentation("a a a a"), false, true, Constants.transactionState.aboutToStart, true, calender);
	private Notification n = new Notification(1, Constants.notificationType.on_message, Constants.notificationEvent.followed,
			1, "initUserName", 1, 1, 1, "summary", calender, false, false);
	private Notification n2 = new Notification(1, Constants.notificationType.on_message, Constants.notificationEvent.followed,
			1, "initUserName", 1, 1, 1, "summary", calender, false, false);
	
	@Test
	public void create(){
		DaoBasic.clearBothDatabase();
		UserDaoService.createNewUser(defaultUser);
		UserDaoService.createNewUser(defaultUser2);
		MessageDaoService.createNewMessage(default1);
		TransactionDaoService.createNewTransaction(t);
		NotificationDaoService.createNewNotification(n);
		assertTrue(NotificationDaoService.getAllNotifications().size()==2);
		try {
			assertTrue(NotificationDaoService.getUserNotificationById(1, 1).getInitUserName().equals("initUserName"));
		} catch (NotificationNotFoundException
				| NotificationOwnerNotMatchException e) {
			assertTrue(false);
		}
		try {
			assertTrue(NotificationDaoService.checkNotification(1, 1));
		} catch (NotificationNotFoundException
				| NotificationOwnerNotMatchException e) {
			assertTrue(false);
		}
		try {
			assertTrue(NotificationDaoService.getUserNotificationById(1, 1).isChecked());
		} catch (NotificationNotFoundException
				| NotificationOwnerNotMatchException e) {
			assertTrue(false);
		}
		
		try {
			assertTrue(NotificationDaoService.deleteNotification(1, 1));
		} catch (NotificationNotFoundException
				| NotificationOwnerNotMatchException e) {
			assertTrue(false);
		}
		try {
			assertTrue(NotificationDaoService.getUserNotificationById(1, 1).isHistoryDeleted());
		} catch (NotificationNotFoundException
				| NotificationOwnerNotMatchException e) {
			assertTrue(false);
		}
		ArrayList<Notification> nList = new ArrayList<Notification>();
		nList.add(n2);
		NotificationDaoService.createNewNotificationQueue(nList);
		assertTrue(NotificationDaoService.getAllNotifications().size()==3);
	}

}
