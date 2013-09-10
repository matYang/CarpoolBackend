package badstudent.dao.test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.database.DaoBasic;
import carpool.database.DaoDMMessage;
import carpool.database.DaoUser;
import carpool.dbservice.DMMessageDaoService;
import carpool.dbservice.NotificationDaoService;
import carpool.dbservice.UserDaoService;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.DMMessage;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;


public class DMMessageDaoServiceTest {

	@Test
	public void updateTest(){
		DaoBasic.clearBothDatabase();
		Calendar calender = Calendar.getInstance();
		Calendar calender2 = Common.DateToCalendar(new Date(9999999));
		User defaultUser = new User(0, "password", "name", 0, 0,0, new ArrayList<DMMessage>(),
				new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "email", "qq","imgPath",new Location("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calender,calender,"paypal");
		try {
			DaoUser.addUserToDatabase(defaultUser);
		} catch (Exception e1) {
			assertTrue(false);
		}
		DMMessage default1 = new DMMessage(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
				new Location("a a a a"),calender,calender,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
				5,false,true,new ArrayList<Transaction>(),calender);
		try {
			UserDaoService.watchUser(1, 1);
		} catch (UserNotFoundException e2) {
			e2.printStackTrace();
		}
		DMMessageDaoService.createNewMessage(default1);
		assertTrue(NotificationDaoService.getAllNotifications().size()==2);
		try {
			UserDaoService.watchMessage(1, 1);
		} catch (UserNotFoundException e1) {
			assertTrue(false);
		}
		try {
			default1 = DMMessageDaoService.getMessageById(1);
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		try {
			DMMessageDaoService.updateGender(Constants.gender.female, 1, 1);
			default1 = DMMessageDaoService.getMessageById(1);
			assertTrue(default1.getGenderRequirement()==Constants.gender.female);
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		try {
			DMMessageDaoService.updateLocation(new Location("a2 a2 a2 a2"), 1, 1);
			default1 = DMMessageDaoService.getMessageById(1);
			assertTrue(default1.getLocation().toString().equals("a2 a2 a2 a2"));
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		default1.setOwnerImgPath("ImgPath2");
		
		try {
			DMMessageDaoService.updateMessage(default1);
			default1 = DMMessageDaoService.getMessageById(1);
			assertTrue(default1.getOwnerImgPath().equals("ImgPath2"));
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		try {
			DMMessageDaoService.updateNote("note2", 1, 1);
			default1 = DMMessageDaoService.getMessageById(1);
			assertTrue(default1.getNote().equals("note2"));
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		try {
			DMMessageDaoService.updatePaymentMethod(Constants.paymentMethod.paypal, 1, 1);
			default1 = DMMessageDaoService.getMessageById(1);
			assertTrue(default1.getPaymentMethod()==Constants.paymentMethod.paypal);
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		try {
			DMMessageDaoService.updatePrice(88, 1, 1);
			default1 = DMMessageDaoService.getMessageById(1);
			assertTrue(default1.getPrice()==88);
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		try {
			DMMessageDaoService.updateTime(calender2, calender2, 1, 1);
			assertTrue(NotificationDaoService.getAllNotifications().size()==10);
			default1 = DMMessageDaoService.getMessageById(1);
			assertTrue(default1.getStartTime().getTime().toString().equals(calender2.getTime().toString()));
			assertTrue(default1.getEndTime().getTime().toString().equals(calender2.getTime().toString()));
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			assertTrue(false);
		}
		assertTrue(DMMessageDaoService.getAllMessages().get(0).getOwnerPhone().equals("phone"));
		try {
			assertTrue(DMMessageDaoService.deleteMessage(1, 1));
			assertTrue(NotificationDaoService.getAllNotifications().size()==11);
		} catch (MessageNotFoundException | MessageOwnerNotMatchException e) {
			assertTrue(false);
		}
		assertTrue(DMMessageDaoService.getAllMessages().size()==1);
		try {
			assertTrue(DMMessageDaoService.getMessageById(1).getState()==Constants.messageState.deleted);
			assertTrue(DMMessageDaoService.getMessageById(1).isHistoryDeleted());
		} catch (MessageNotFoundException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void searchTest(){
		DaoBasic.clearBothDatabase();
		Calendar calenderx = Calendar.getInstance();
		//init user
		ArrayList<String> group = new ArrayList<String>();
		User defaultUser1 = new User(0, "password", "name", 0, 0,0, new ArrayList<DMMessage>(),
				new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
				"phone", "email", "qq","imgPath",new Location("a a a a"),false,false,false,false,
				Constants.userState.normal,Constants.userSearchState.universityAsk,
				calenderx,calenderx,"paypal");
		group.add("a a a a");
		defaultUser1.setUniversityGroup(group);
		try {
			DaoUser.addUserToDatabase(defaultUser1);
		} catch (Exception e) {
			assertTrue(false);
		}
		User defaultUser2 = new User(0, "password3", "name3", 2, 2,2, new ArrayList<DMMessage>(),
				new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),22,Constants.gender.female,
				"phone3", "email3", "qq3","imgPath3",new Location("a3 a3 a3 a3"),true,true,true,true,
				Constants.userState.invalid,Constants.userSearchState.regionAsk,
				calenderx,calenderx,"paypal3");
		group.clear();
		group.add("a a a b");
		defaultUser2.setUniversityGroup(group);
		try {
			DaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			assertTrue(false);
		}
		User defaultUser3 = new User(0, "password4", "name4", 3, 3,3, new ArrayList<DMMessage>(),
				new ArrayList<DMMessage>(),new ArrayList<User>(),new ArrayList<Transaction>(),
				new ArrayList<Notification>(),new ArrayList<String>(),23,Constants.gender.female,
				"phone4", "email4", "qq4","imgPath4",new Location("a4 a4 a4 a4"),true,true,true,true,
				Constants.userState.invalid,Constants.userSearchState.regionAsk,
				calenderx,calenderx,"paypal4");
		group.clear();
		group.add("a a b c");
		defaultUser3.setUniversityGroup(group);
		try {
			DaoUser.addUserToDatabase(defaultUser3);
		} catch (Exception e) {
			assertTrue(false);
		}
		//init user finish
		//init Message
		Calendar calender1 = Common.DateToCalendar(new Date(6666666));
		Calendar calender2 = Common.DateToCalendar(new Date(7777777));
		Calendar calender3 = Common.DateToCalendar(new Date(8888888));
		Calendar calender4 = Common.DateToCalendar(new Date(9999999));
		DMMessage default1 = new DMMessage(1,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
				new Location("a a a a"),calender2,calender4,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
				5,false,true,new ArrayList<Transaction>(),calenderx);
		DMMessage default2 = new DMMessage(2,1,"ImgPath2","Name2",30,40,"phone2","email2","qq2",Constants.paymentMethod.all,
				new Location("a2 a2 a2 a2"),calender1,calender2,"note2",Constants.messageType.ask,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calenderx);
		DMMessage default3 = new DMMessage(3,2,"ImgPath3","Name3",30,40,"phone3","email3","qq3",Constants.paymentMethod.all,
				new Location("a a a a3"),calender3,calender4,"note3",Constants.messageType.ask,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calenderx);
		DMMessage default4 = new DMMessage(3,3,"ImgPath4","Name4",30,40,"phone4","email4","qq4",Constants.paymentMethod.all,
				new Location("a a b c"),calender1,calender4,"note4",Constants.messageType.ask,Constants.gender.both,
				Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calenderx);
		DaoDMMessage.addMessageToDatabase(default1);
		DaoDMMessage.addMessageToDatabase(default2);
		DaoDMMessage.addMessageToDatabase(default3);
		DaoDMMessage.addMessageToDatabase(default4);
		//init Message finish
		try {
			assertTrue(DMMessageDaoService.extendedMessageSearch(new Location("a a b c"), calender3, Constants.userSearchState.universityAsk, 1000).get(0).getOwnerImgPath().equals("ImgPath4"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		try {
			ArrayList<DMMessage> result = DMMessageDaoService.extendedMessageSearch(new Location("a a a shit"), calender3, Constants.userSearchState.regionAsk, 1000);
			assertTrue(result.get(0).getOwnerImgPath().equals("ImgPath"));
			assertTrue(result.get(1).getOwnerImgPath().equals("ImgPath3"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		try {
			assertTrue(DMMessageDaoService.extendedMessageSearch(new Location("bla bla bla bla"), calender3, Constants.userSearchState.universityGroupAsk, 3).get(0).getOwnerImgPath().equals("ImgPath4"));
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		
	}
}
