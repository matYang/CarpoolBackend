package badstudent.dao.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.paymentMethod;
import carpool.database.carpoolDaoBasic;
import carpool.database.carpoolDaoMessage;
import carpool.database.DaoTransaction;
import carpool.database.carpoolDaoUser;
import carpool.dbservice.NotificationDaoService;
import carpool.dbservice.TransactionDaoService;
import carpool.dbservice.UserDaoService;
import carpool.exception.transaction.TransactionAccessViolationException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.transaction.TransactionOwnerNotMatchException;
import carpool.exception.transaction.TransactionStateViolationException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;


public class TransactionDaoServiceTest {

	private Calendar calender = Calendar.getInstance();

	@Test
	public void basic(){
		carpoolDaoBasic.clearBothDatabase();
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
		Transaction defaultT1 = new Transaction(111, 2, 1, "initUserImgPath", "initUserName",
				1, "targetUserImgPath", "targetUserName", 1,1,1, 1, "messageNote", paymentMethod.offline, 1,
				"requestInfo", "responseInfo", calender, calender, new LocationRepresentation("a a a a"), false, false, Constants.transactionState.success, true, calender);
		try {
			carpoolDaoUser.addUserToDatabase(defaultUser1);
			carpoolDaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			assertTrue(false);
		}
		carpoolDaoMessage.addMessageToDatabase(default1);
		TransactionDaoService.createNewTransaction(defaultT1);
		assertTrue(NotificationDaoService.getAllNotifications().size()==1);
		assertTrue(TransactionDaoService.getAllTransactions().size()==1);
		try {
			assertTrue(TransactionDaoService.getTransactionById(1).getInitUserImgPath().equals("initUserImgPath"));
		} catch (TransactionNotFoundException e) {
			assertTrue(false);
		}
		
		try {
			assertTrue(TransactionDaoService.deleteTransaction(1, 1));
		} catch (TransactionNotFoundException| TransactionOwnerNotMatchException | TransactionStateViolationException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void functional(){
		carpoolDaoBasic.clearBothDatabase();
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
		Transaction defaultT1 = new Transaction(111, 2, 1, "initUserImgPath", "initUserName",
				1, "targetUserImgPath", "targetUserName", 1,1,1, 1, "messageNote", paymentMethod.offline, 1,
				"requestInfo", "responseInfo", calender, calender, new LocationRepresentation("a a a a"), false, false, Constants.transactionState.init, true, calender);
		try {
			carpoolDaoUser.addUserToDatabase(defaultUser1);
			carpoolDaoUser.addUserToDatabase(defaultUser2);
		} catch (Exception e) {
			assertTrue(false);
		}
		carpoolDaoMessage.addMessageToDatabase(default1);
		TransactionDaoService.createNewTransaction(defaultT1);
		try {
			assertTrue(TransactionDaoService.refuseTransaction(1, 1).getState()==Constants.transactionState.refused);
			assertTrue(NotificationDaoService.getAllNotifications().size()==2);
		} catch (TransactionNotFoundException
				| TransactionOwnerNotMatchException
				| TransactionAccessViolationException
				| TransactionStateViolationException e) {
			assertTrue(false);
		}
		try {
			Transaction t = DaoTransaction.getTransactionById(1);
			t.setState(Constants.transactionState.success);
			DaoTransaction.UpdateTransactionInDatabase(t);
			TransactionDaoService.deleteTransaction(1, 1);
		} catch (TransactionNotFoundException
				| TransactionOwnerNotMatchException | TransactionStateViolationException e) {
			assertTrue(false);
		}
		assertTrue(TransactionDaoService.getAllTransactions().size()==0);
		TransactionDaoService.createNewTransaction(defaultT1);
		try {
			TransactionDaoService.confirmTransaction(2, 1);
			assertTrue(NotificationDaoService.getAllNotifications().size()==4);
		} catch (TransactionNotFoundException
				| TransactionOwnerNotMatchException
				| TransactionAccessViolationException
				| TransactionStateViolationException e) {
			assertTrue(false);
		}
		try {
			assertTrue(UserDaoService.getUserById(1).getTotalTranscations()==0);
			assertTrue(UserDaoService.getUserById(2).getTotalTranscations()==0);
		} catch (UserNotFoundException e) {
			assertTrue(false);
		}
		try {
			assertTrue(TransactionDaoService.cancelTransaction(2, 1).getState()==Constants.transactionState.cancelled);
		} catch (TransactionNotFoundException
				| TransactionOwnerNotMatchException
				| TransactionStateViolationException e) {
			assertTrue(false);
		}
		Transaction defaultT2 = new Transaction(111, 2, 1, "initUserImgPath", "initUserName",
				1, "targetUserImgPath", "targetUserName", 1,1,1, 1, "messageNote", paymentMethod.offline, 1,
				"requestInfo", "responseInfo", calender, calender, new LocationRepresentation("a a a a"), false, false, Constants.transactionState.finishedToEvaluate, true, calender);
		TransactionDaoService.createNewTransaction(defaultT2);//ID 3
		try {
			TransactionDaoService.reportTransaction(3, 1);
			TransactionDaoService.investigationReleaseTransaction(3);
			assertTrue(NotificationDaoService.getAllNotifications().size()==7);
			TransactionDaoService.reportTransaction(3, 1);
			TransactionDaoService.investigationCancelTransaction(3);
		} catch (TransactionNotFoundException
				| TransactionOwnerNotMatchException
				| TransactionStateViolationException e) {
			e.printStackTrace();
		}
		try {
			Transaction t = DaoTransaction.getTransactionById(3);
			t.setState(Constants.transactionState.success);
			DaoTransaction.UpdateTransactionInDatabase(t);
			TransactionDaoService.deleteTransaction(3, 1);
		} catch (TransactionNotFoundException
				| TransactionOwnerNotMatchException | TransactionStateViolationException e) {
			
			assertTrue(false);
		}
		TransactionDaoService.createNewTransaction(defaultT2);//ID 4
		
		try {
			TransactionDaoService.evaluateTransaction(4, 1, 10);
			assertTrue(TransactionDaoService.evaluateTransaction(4, 2, 100).getState()==Constants.transactionState.success);
		} catch (TransactionNotFoundException
				| TransactionOwnerNotMatchException
				| TransactionAccessViolationException
				| TransactionStateViolationException e) {
			assertTrue(false);
		}
		try {
			assertTrue(TransactionDaoService.getTransactionById(4).getInitUserEval()==100);
			assertTrue(TransactionDaoService.getTransactionById(4).getTargetUserEval()==10);
		} catch (TransactionNotFoundException e) {
			assertTrue(false);
		}
		try {
			assertTrue(UserDaoService.getUserById(1).getAverageScore()==-100);
			assertTrue(UserDaoService.getUserById(2).getAverageScore()==-10);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
	}
}
