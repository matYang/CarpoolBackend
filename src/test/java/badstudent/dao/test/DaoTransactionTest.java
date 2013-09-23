package badstudent.dao.test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.Constants.paymentMethod;
import carpool.common.Constants.transactionState;
import carpool.database.DaoBasic;
import carpool.database.DaoTransaction;
import carpool.dbservice.MessageDaoService;
import carpool.dbservice.UserDaoService;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;


public class DaoTransactionTest {
	
	private final Calendar calender = Calendar.getInstance();
	private final Calendar calender2 = Common.DateToCalendar(new Date(9999999));
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
	private final Message default1 = new Message(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
			new Location("a a a a"),calender,calender2,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
			5,false,true,new ArrayList<Transaction>(),calender);
	
	
	@Test
	public void create(){
		/*
		Transaction t = new Transaction(transactionId, initUserId, targetUserId, initUserImgPath, initUserName, initUserLevel,
				targetUserImgPath, targetUserName, targetUserLevel, messageId, messageNote, paymentMethod, price, requestInfo,
				responseInfo, startTime, endTime, location, established, success, state, historyDeleted, creationTime)
		*/
		DaoBasic.clearBothDatabase();
		UserDaoService.createNewUser(defaultUser);
		UserDaoService.createNewUser(defaultUser2);
		MessageDaoService.createNewMessage(default1);
		//1 published a message 1
		Transaction t = new Transaction(0, 2, 1, "initUserImgPath", "initUserName", 10,
				"targetUserImgPath", "targetUserName", 2,-5,-6, 1, "messageNote", Constants.paymentMethod.offline, 3, "requestInfo",
				"responseInfo", calender, calender2, new Location("a a a a"), false, true, Constants.transactionState.aboutToStart, true, calender);
		DaoTransaction.addTransactionToDatabase(t);
		try {
			t = DaoTransaction.getTransactionById(1);
			assertTrue(t.getTransactionId()==1);
			assertTrue(t.getInitUserId()==2);
			assertTrue(t.getTargetUserId()==1);
			assertTrue(t.getInitUserImgPath().equals("initUserImgPath"));
			assertTrue(t.getInitUserName().equals("initUserName"));
			assertTrue(t.getInitUserLevel()==10);
			assertTrue(t.getTargetUserImgPath().equals("targetUserImgPath"));
			assertTrue(t.getTargetUserName().equals("targetUserName"));
			assertTrue(t.getTargetUserLevel()==2);
			assertTrue(t.getInitUserEval()==-5);
			assertTrue(t.getTargetUserEval()==-6);
			assertTrue(t.getMessageId()==1);
			assertTrue(t.getMessageNote().equals("messageNote"));
			assertTrue(t.getPaymentMethod()==Constants.paymentMethod.offline);
			assertTrue(t.getPrice()==3);
			assertTrue(t.getRequestInfo().equals("requestInfo"));
			assertTrue(t.getResponseInfo().equals("responseInfo"));
			assertTrue(t.getStartTime().getTime().toString().equals(calender.getTime().toString()));
			assertTrue(t.getEndTime().getTime().toString().equals(calender2.getTime().toString()));
			assertTrue(t.getLocation().toString().equals("a a a a"));
			assertTrue(!t.isEstablished());
			assertTrue(t.isSuccess());
			assertTrue(t.getState()==Constants.transactionState.aboutToStart);
			assertTrue(t.isHistoryDeleted());
			assertTrue(t.getCreationTime().getTime().toString().equals(calender.getTime().toString()));
		} catch (TransactionNotFoundException e) {
			assertTrue(false);
		}
		t.setInitUserImgPath("initUserImgPath2");
		t.setInitUserName("initUserName2");
		t.setInitUserLevel(11);
		t.setTargetUserImgPath("targetUserImgPath2");
		t.setTargetUserName("targetUserName2");
		t.setTargetUserLevel(3);
		t.setInitUserEval(5);
		t.setTargetUserEval(6);
		t.setMessageNote("messageNote2");
		t.setPaymentMethod(paymentMethod.paypal);
		t.setPrice(4);
		t.setRequestInfo("requestInfo2");
		t.setResponseInfo("responseInfo2");
		t.setStartTime(calender2);
		t.setEndTime(calender);
		t.setLocation(new Location("a2 a2 a2 a2"));
		t.setEstablished(true);
		t.setSuccess(false);
		t.setState(transactionState.cancelled);
		t.setHistoryDeleted(false);
		t.setCreationTime(calender2);
		try {
			DaoTransaction.UpdateTransactionInDatabase(t);
		} catch (TransactionNotFoundException e) {
			assertTrue(false);
		}
		try {
			t = DaoTransaction.getTransactionById(1);
			assertTrue(t.getTransactionId()==1);
			assertTrue(t.getInitUserId()==2);
			assertTrue(t.getTargetUserId()==1);
			assertTrue(t.getInitUserImgPath().equals("initUserImgPath2"));
			assertTrue(t.getInitUserName().equals("initUserName2"));
			assertTrue(t.getInitUserLevel()==11);
			assertTrue(t.getTargetUserImgPath().equals("targetUserImgPath2"));
			assertTrue(t.getTargetUserName().equals("targetUserName2"));
			assertTrue(t.getTargetUserLevel()==3);
			assertTrue(t.getInitUserEval()==5);
			assertTrue(t.getTargetUserEval()==6);
			assertTrue(t.getMessageId()==1);
			assertTrue(t.getMessageNote().equals("messageNote2"));
			assertTrue(t.getPaymentMethod()==Constants.paymentMethod.paypal);
			assertTrue(t.getPrice()==4);
			assertTrue(t.getRequestInfo().equals("requestInfo2"));
			assertTrue(t.getResponseInfo().equals("responseInfo2"));
			assertTrue(t.getStartTime().getTime().toString().equals(calender2.getTime().toString()));
			assertTrue(t.getEndTime().getTime().toString().equals(calender.getTime().toString()));
			assertTrue(t.getLocation().toString().equals("a2 a2 a2 a2"));
			assertTrue(t.isEstablished());
			assertTrue(!t.isSuccess());
			assertTrue(t.getState()==Constants.transactionState.cancelled);
			assertTrue(!t.isHistoryDeleted());
			assertTrue(t.getCreationTime().getTime().toString().equals(calender2.getTime().toString()));
		} catch (TransactionNotFoundException e) {
			assertTrue(false);
		}
		assertTrue(DaoTransaction.getALL().size()==1);
		try {
			DaoTransaction.deleteTransactionFromDatabase(1);
		} catch (TransactionNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			DaoTransaction.getTransactionById(1);
			assertTrue(false);
		} catch (TransactionNotFoundException e) {
		}
	}
}
