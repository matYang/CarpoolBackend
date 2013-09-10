package badstudent.dao.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.Constants.userSearchState;
import badstudent.database.DaoBasic;
import badstudent.database.DaoDMMessage;
import badstudent.database.DaoUser;
import badstudent.dbservice.UserDaoService;
import badstudent.exception.message.MessageNotFoundException;
import badstudent.exception.user.UserNotFoundException;
import badstudent.model.DMMessage;
import badstudent.model.Location;
import badstudent.model.Notification;
import badstudent.model.Transaction;
import badstudent.model.User;

public class WatchListTest {
	
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
	private DMMessage default2 = new DMMessage(1,1,"ImgPath2","Name2",30,40,"phone2","email2","qq2",Constants.paymentMethod.all,
			new Location("a2 a2 a2 a2"),calender,calender,"note2",Constants.messageType.help,Constants.gender.both,
			Constants.messageState.deleted,50,true,false,new ArrayList<Transaction>(),calender);
	
    @Test
    public void setup() {
    	DaoBasic.clearBothDatabase();
    	try{
    		DaoUser.addUserToDatabase(defaultUser1);
    		DaoDMMessage.addMessageToDatabase(default1);
    		DaoDMMessage.addMessageToDatabase(default2);
    	}catch(Exception e){
    		e.printStackTrace();
    		assertTrue(false);
    	}
    
        //addBothMessagesToOneWatchList
    
    	ArrayList<DMMessage> Watch = new ArrayList<DMMessage>();
    	Watch.add(default1);
    	Watch.add(default2);
    	defaultUser2.setWatchList(Watch);
    	User user = null;
    	try{
    		user = DaoUser.addUserToDatabase(defaultUser2);
    	}catch(Exception e){
    		e.printStackTrace();
    		assertTrue(false);
    	}
    	try{
    		user = DaoUser.getUserById(user.getUserId());
    	}catch(Exception e){
    		e.printStackTrace();
    		assertTrue(false);    		
    	}
    	assertTrue(user.getWatchList().get(0).getOwnerImgPath().equals("ImgPath"));
		assertTrue(user.getWatchList().get(1).getOwnerImgPath().equals("ImgPath2"));
		
		
		//MoveMessageFromOnesWatchListToAnother
    	
    	ArrayList<DMMessage> watch1 = new ArrayList<DMMessage>(1);
    	watch1.add(user.getWatchList().get(0));
    	ArrayList<DMMessage> watch2 = new ArrayList<DMMessage>(1);
    	watch2.add(user.getWatchList().get(1));
    	try {
    		user.setWatchList(watch1);
			DaoUser.UpdateUserInDatabase(user);
			user = DaoUser.getUserById(1);
			user.setWatchList(watch2);
			DaoUser.UpdateUserInDatabase(user);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false); 
		}
    	
    	try{
    		user = DaoUser.getUserById(1);
    		assertTrue(user.getWatchList().get(0).getOwnerImgPath().equals("ImgPath2"));
    		user = DaoUser.getUserById(2);
    		assertTrue(user.getWatchList().get(0).getOwnerImgPath().equals("ImgPath"));
    	}catch(Exception e){
    		e.printStackTrace();
			assertTrue(false); 
    	}
    	
    	//do a search on both user and make sure the Watch list is correct
    	
    	assertTrue(DaoUser.searchUser("%", "phone", "%", "%").get(0).getWatchList().get(0).getOwnerImgPath().equals("ImgPath2"));
    	assertTrue(DaoUser.searchUser("%", "%", "qq1", "%").get(0).getWatchList().get(0).getOwnerImgPath().equals("ImgPath2"));
    	
    	//remove a message make sure Watch is removed
    	try {
			DaoDMMessage.deleteMessageFromDatabase(1);
			user = DaoUser.getUserById(2);
			assertTrue(user.getWatchList().size()==0);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false); 
		}
    	
    	//remove a user make sure Watch is removed 
    	
    	try{
    		DaoUser.deleteUserFromDatabase(1);
    	}catch (Exception e){
			e.printStackTrace();
			assertTrue(false);     		
    	}
    }

}
