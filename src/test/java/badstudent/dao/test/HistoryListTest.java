package badstudent.dao.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.paymentMethod;
import carpool.constants.Constants.userSearchState;
import carpool.database.DaoBasic;
import carpool.database.DaoMessage;
import carpool.database.DaoTransaction;
import carpool.database.DaoUser;
import carpool.dbservice.UserDaoService;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;


public class HistoryListTest {
	
	private Calendar calender = Calendar.getInstance();
	private User defaultUser1 = new User(0, "password", "name", 0, 0,0, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone", "email", "qq","imgPath",new Location("a a a a"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private User defaultUser2 = new User(1, "password", "name1", 0, 0,0, new ArrayList<Message>(),
			new ArrayList<Message>(),new ArrayList<User>(),new ArrayList<Transaction>(),
			new ArrayList<Notification>(),new ArrayList<String>(),20,Constants.gender.male,
			"phone1", "email1", "qq1","imgPath1",new Location("a2 a2 a2 a2"),false,false,false,false,
			Constants.userState.normal,Constants.userSearchState.universityAsk,
			calender,calender,"paypal");
	private Message default1 = new Message(11,1,"ImgPath","Name",3,4,"phone","email","qq",Constants.paymentMethod.offline,
			new Location("a a a a"),calender,calender,"note",Constants.messageType.ask,Constants.gender.male,Constants.messageState.normal,
			5,false,true,new ArrayList<Transaction>(),calender);
	private Message default2 = new Message(1,1,"ImgPath2","Name2",30,40,"phone2","email2","qq2",Constants.paymentMethod.all,
			new Location("a2 a2 a2 a2"),calender,calender,"note2",Constants.messageType.help,Constants.gender.both,
			Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender);
	private Message default3 = new Message(1,2,"ImgPath2","Name3",30,40,"phone3","email3","qq2",Constants.paymentMethod.all,
			new Location("a2 a2 a2 a2"),calender,calender,"note2",Constants.messageType.help,Constants.gender.both,
			Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender);
	private Message default4 = new Message(1,2,"ImgPath2","Name4",30,40,"phone4","email4","qq2",Constants.paymentMethod.all,
			new Location("a2 a2 a2 a2"),calender,calender,"note2",Constants.messageType.help,Constants.gender.both,
			Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender);
	private Transaction defaultT1 = new Transaction(111, 2, 1, "initUserImgPath", "initUserName",
			1, "targetUserImgPath", "targetUserName", 1,1,1, 1, "messageNote", paymentMethod.offline, 1,
			"requestInfo", "responseInfo", calender, calender, new Location("a a a a"), false, false, Constants.transactionState.init, true, calender);
	
    @Test
    public void setup() {
    	DaoBasic.clearBothDatabase();
    	try{
    		DaoUser.addUserToDatabase(defaultUser1);
    		DaoUser.addUserToDatabase(defaultUser2);
    		DaoMessage.addMessageToDatabase(default1);
    		DaoMessage.addMessageToDatabase(default2);
    		DaoMessage.addMessageToDatabase(default3);
    		DaoMessage.addMessageToDatabase(default4);
    	}catch(Exception e){
    		assertTrue(false);
    	}
    	DaoTransaction.addTransactionToDatabase(defaultT1);
    
        try {
			assertTrue(DaoUser.getUserById(2).getHistoryList().size()==3);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
    }

}
