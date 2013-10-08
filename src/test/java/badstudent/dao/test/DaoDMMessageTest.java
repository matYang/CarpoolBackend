package badstudent.dao.test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.paymentMethod;
import carpool.database.carpoolDaoBasic;
import carpool.database.carpoolDaoMessage;
import carpool.database.DaoTransaction;
import carpool.database.carpoolDaoUser;
import carpool.dbservice.MessageDaoService;
import carpool.exception.message.MessageNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;


public class DaoDMMessageTest {

	private final Calendar calender1 = DateUtility.DateToCalendar(new Date(6666666));
	private final Calendar calender2 = DateUtility.DateToCalendar(new Date(7777777));
	private final Calendar calender3 = DateUtility.DateToCalendar(new Date(8888888));
	private final Calendar calender4 = DateUtility.DateToCalendar(new Date(9999999));
	private final Message default1 = new Message(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
			new LocationRepresentation("a a a a"),calender1,calender2,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
			5,false,true,new ArrayList<Transaction>(),calender3);
	private final Message default2 = new Message(1,2,"ImgPath2","Name2",30,40,"phone2","email2","qq2",Constants.paymentMethod.all,
			new LocationRepresentation("a2 a2 a2 a2"),calender2,calender3,"note2",Constants.messageType.help,Constants.gender.both,
			Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender4);

	@Test
	public void messageTransaction(){
		init();
		Calendar calender = Calendar.getInstance();
		User defaultUser1 = new User(0, "password", "name", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "email", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		User defaultUser2 = new User(1, "password", "name1", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone1", "email1", "qq1","imgPath1",new LocationRepresentation("a2 a2 a2 a2"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		Message default1 = new Message(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
				new LocationRepresentation("a a a a"),calender,calender,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
				5,false,true,new ArrayList<Transaction>(),calender);
		Message default2 = new Message(1,2,"ImgPath2","Name2",30,40,"phone2","email2","qq2",Constants.paymentMethod.all,
				new LocationRepresentation("a2 a2 a2 a2"),calender,calender,"note2",Constants.messageType.help,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender);
		Message default3 = new Message(1,1,"ImgPath2","Name3",30,40,"phone3","email3","qq2",Constants.paymentMethod.all,
				new LocationRepresentation("a2 a2 a2 a2"),calender,calender,"note2",Constants.messageType.help,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender);
		Transaction defaultT1 = new Transaction(111, 2, 1, "initUserImgPath", "initUserName",
				1, "targetUserImgPath", "targetUserName", 1,1,1, 1, "messageNote", paymentMethod.offline, 1,
				"requestInfo", "responseInfo", calender, calender, new LocationRepresentation("a a a a"), false, false, Constants.transactionState.init, true, calender);
		Transaction defaultT2 = new Transaction(111, 2, 1, "initUserImgPath", "initUserName",
				1, "targetUserImgPath", "targetUserName", 1,1,1, 3, "messageNote", paymentMethod.offline, 1,
				"requestInfo", "responseInfo", calender, calender, new LocationRepresentation("a a a a"), false, false, Constants.transactionState.init, true, calender);
		try {
			carpoolDaoUser.addUserToDatabase(defaultUser1);
			carpoolDaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			assertTrue(false);
		}
		carpoolDaoMessage.addMessageToDatabase(default1);
		carpoolDaoMessage.addMessageToDatabase(default2);
		carpoolDaoMessage.addMessageToDatabase(default3);
		DaoTransaction.addTransactionToDatabase(defaultT1);
		DaoTransaction.addTransactionToDatabase(defaultT2);
		try {
			assertTrue(MessageDaoService.getRelatedTransactions(1).size()==1);
			assertTrue(MessageDaoService.getRelatedTransactions(2).size()==0);
			assertTrue(MessageDaoService.getRelatedTransactions(3).size()==1);
		} catch (MessageNotFoundException e) {
			assertTrue(false);
		}

	}

	@Test
	public void updateMessage(){
		init();
		try {
			carpoolDaoUser.addUserToDatabase(new User());
			User user = new User();
			user.setPhone("aaa");
			user.setEmail("bbb");
			carpoolDaoUser.addUserToDatabase(user);
		} catch (Exception e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		int oldid = carpoolDaoMessage.addMessageToDatabase(default1).getMessageId();
		default2.setMessageId(oldid);
		try {
			carpoolDaoMessage.UpdateMessageInDatabase(default2);
		} catch (MessageNotFoundException e) {
			DebugLog.d("Message Not Found");
			assertTrue(false);
		}
		Message msg = null;
		try {
			msg = carpoolDaoMessage.getMessageById(1);
		} catch (MessageNotFoundException e) {
			DebugLog.d("Message Not Found");
			assertTrue(false);
		}
		assertTrue(msg.getMessageId()==1);
		assertTrue(msg.getOwnerId()==1);
		assertTrue(msg.getOwnerImgPath().equals("ImgPath2"));
		assertTrue(msg.getOwnerName().equals("Name2"));
		assertTrue(msg.getOwnerLevel()==30);
		assertTrue(msg.getOwnerAverageScore()==40);
		assertTrue(msg.getOwnerPhone().equals("phone2"));
		assertTrue(msg.getOwnerEmail().equals("email2"));
		assertTrue(msg.getOwnerQq().equals("qq2"));
		assertTrue(msg.getPaymentMethod()==Constants.paymentMethod.all);
		assertTrue(msg.getLocation().toString().equals("a2 a2 a2 a2"));
		assertTrue(msg.getStartTime().getTime().toString().equals(calender2.getTime().toString()));
		assertTrue(msg.getEndTime().getTime().toString().equals(calender3.getTime().toString()));
		assertTrue(msg.getNote().equals("note2"));
		assertTrue(msg.getType()==Constants.messageType.help);
		assertTrue(msg.getGenderRequirement()==Constants.gender.both);
		assertTrue(msg.getState()==Constants.messageState.deleted);
		assertTrue(msg.getPrice()==50);
		assertTrue(msg.isActive());
		assertTrue(!msg.isHistoryDeleted());
		assertTrue(msg.getTransactionList().size()==0);
		assertTrue(msg.getCreationTime().getTime().toString().equals(calender4.getTime().toString()));
		init();
	}

	@Test
	public void createMessage(){
		init();
		try {
			carpoolDaoUser.addUserToDatabase(new User());
		} catch (Exception e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
		Message msg = carpoolDaoMessage.addMessageToDatabase(default1);
		try {
			msg = carpoolDaoMessage.getMessageById(msg.getMessageId());
		} catch (MessageNotFoundException e) {
			DebugLog.d("Message Not Found");
			assertTrue(false);
		}
		assertTrue(msg.getMessageId()==1);
		assertTrue(msg.getOwnerId()==1);
		assertTrue(msg.getOwnerImgPath().equals("ImgPath"));
		assertTrue(msg.getOwnerName().equals("Name"));
		assertTrue(msg.getOwnerLevel()==3);
		assertTrue(msg.getOwnerAverageScore()==4);
		assertTrue(msg.getOwnerPhone().equals("phone"));
		assertTrue(msg.getOwnerEmail().equals("email"));
		assertTrue(msg.getOwnerQq().equals("qq"));
		assertTrue(msg.getPaymentMethod()==Constants.paymentMethod.offline);
		assertTrue(msg.getLocation().toString().equals("a a a a"));
		assertTrue(msg.getStartTime().getTime().toString().equals(calender1.getTime().toString()));
		assertTrue(msg.getEndTime().getTime().toString().equals(calender2.getTime().toString()));
		assertTrue(msg.getNote().equals("note"));
		assertTrue(msg.getType()==Constants.messageType.ask);
		assertTrue(msg.getGenderRequirement()==Constants.gender.male);
		assertTrue(msg.getState()==Constants.messageState.normal);
		assertTrue(msg.getPrice()==5);
		assertTrue(!msg.isActive());
		assertTrue(msg.isHistoryDeleted());
		assertTrue(msg.getTransactionList().size()==0);
		assertTrue(msg.getCreationTime().getTime().toString().equals(calender3.getTime().toString()));
		init();
	}

	@Test
	public void RegionSearch(){
		init();
		//init user
		ArrayList<String> group = new ArrayList<String>();
		User defaultUser1 = new User(0, "password", "name", 0, 0,0, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "email", "qq","imgPath",new LocationRepresentation("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender2,calender2,"paypal");
		group.add("a a a a");
		defaultUser1.setUniversityGroup(group);
		try {
			carpoolDaoUser.addUserToDatabase(defaultUser1);
		} catch (Exception e) {
			assertTrue(false);
		}
		User defaultUser2 = new User(0, "password3", "name3", 2, 2,2, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),22,Constants.gender.female,
				"phone3", "email3", "qq3","imgPath3",new LocationRepresentation("a3 a3 a3 a3"),true,true,true,true,
				Constants.userState.invalid,Constants.userSearchState.regionAsk,
				calender2,calender2,"paypal3");
		group.clear();
		group.add("a a a b");
		defaultUser2.setUniversityGroup(group);
		try {
			carpoolDaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			assertTrue(false);
		}
		User defaultUser3 = new User(0, "password4", "name4", 3, 3,3, new ArrayList<Message>(),
				new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),23,Constants.gender.female,
				"phone4", "email4", "qq4","imgPath4",new LocationRepresentation("a4 a4 a4 a4"),true,true,true,true,
				Constants.userState.invalid,Constants.userSearchState.regionAsk,
				calender2,calender2,"paypal4");
		group.clear();
		group.add("a a b c");
		defaultUser3.setUniversityGroup(group);
		try {
			carpoolDaoUser.addUserToDatabase(defaultUser3);
		} catch (Exception e) {
			assertTrue(false);
		}
		//init user finish
		//init Message
		Message default1 = new Message(1,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
				new LocationRepresentation("a a a a"),calender2,calender4,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
				5,false,true,new ArrayList<Transaction>(),calender3);
		Message default2 = new Message(2,1,"ImgPath2","Name2",30,40,"phone2","email2","qq2",Constants.paymentMethod.all,
				new LocationRepresentation("a2 a2 a2 a2"),calender1,calender2,"note2",Constants.messageType.ask,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender4);
		Message default3 = new Message(3,2,"ImgPath3","Name3",30,40,"phone3","email3","qq3",Constants.paymentMethod.all,
				new LocationRepresentation("a3 a3 a3 a3"),calender3,calender4,"note3",Constants.messageType.help,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender4);
		Message default4 = new Message(3,3,"ImgPath4","Name4",30,40,"phone4","email4","qq4",Constants.paymentMethod.all,
				new LocationRepresentation("a4 a4 a4 a4"),calender1,calender4,"note4",Constants.messageType.ask,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender4);
		carpoolDaoMessage.addMessageToDatabase(default1);
		carpoolDaoMessage.addMessageToDatabase(default2);
		carpoolDaoMessage.addMessageToDatabase(default3);
		carpoolDaoMessage.addMessageToDatabase(default4);
		//init Message finish
		ArrayList<Message> results = carpoolDaoMessage.searchMessageRegion("a a a bla", DateUtility.toSQLDateTime(calender3), Constants.messageType.ask.code+"");
		assertTrue(results.get(0).getOwnerImgPath().equals("ImgPath"));

		assertTrue(carpoolDaoMessage.searchMessageSingle("%", DateUtility.toSQLDateTime(calender4), "%").size()==4);
		init();
	}

	private void init(){
		carpoolDaoBasic.clearBothDatabase();
	}
}
