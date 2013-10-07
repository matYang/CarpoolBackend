package carpool.test.dao;

import static org.junit.Assert.fail;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.junit.Test;

import carpool.carpoolDAO.carpoolDAOBasic;
import carpool.carpoolDAO.carpoolDAOMessage;
import carpool.carpoolDAO.carpoolDAOUser;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.HelperOperator;
import carpool.common.Parser;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.database.DaoBasic;
import carpool.database.DaoUser;
import carpool.dbservice.*;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.representation.LocationRepresentation;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.test.mockModel.MockUser;
import static java.lang.System.out;

public class CarpoolUserTest {
	
	@Test
	public void testCreate() throws ValidationException{
		carpoolDAOBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		//Test
		carpoolDAOUser.addUserToDatabase(user);		
	}
	@Test
	public void testRead(){
		carpoolDAOBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		try {
			carpoolDAOUser.addUserToDatabase(user);
		} catch (ValidationException e) {
				e.printStackTrace();
				fail();
		}
		//Test
		try {
			try {
				if(!user.equals(carpoolDAOUser.getUserById(user.getUserId()))){
					fail();
				}
			} catch (ValidationException e) {				
				e.printStackTrace();
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		try {
			if(!user.equals(carpoolDAOUser.getUserByEmail(user.getEmail()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();	
			fail();
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}
	}
	@Test
	public void testUpdate(){
		carpoolDAOBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		try {
			carpoolDAOUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		
		try {
			user.setPassword("xch93318yeah", "kongfu");
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		user.setEmail("xiongchuhanplace@hotmail.com");
		user.setLocation(new LocationRepresentation ("primary_secondary","customnew",2));
		user.setAccountPass("test1");
		user.setGoogleToken("google");
		user.setAge(100);
		user.setEmailActivated(true);
		try {
			carpoolDAOUser.UpdateUserInDatabase(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test
		try {
			if(!user.equals(carpoolDAOUser.getUserById(user.getUserId()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();		  
		} catch (ValidationException e) {
			
			e.printStackTrace();
			fail();
		}
		try {
			if(!user.equals(carpoolDAOUser.getUserByEmail(user.getEmail()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();		  
		} catch (ValidationException e) {
			
			e.printStackTrace();
			fail();
		}
	}
   @Test
    public void testDelete(){
	   carpoolDAOBasic.clearBothDatabase();
	   User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		try {
			carpoolDAOUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		try {
			carpoolDAOUser.deleteUserFromDatabase(user.getUserId());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		//Test
		try {
			carpoolDAOUser.getUserById(user.getUserId());
		} catch (UserNotFoundException e) {
			//passed();
		}
   }
    @Test
    public void testUpdateSocialList() throws ValidationException{
    	carpoolDAOBasic.clearBothDatabase();
		User user =  new User("password1", "email1", new LocationRepresentation ("primary","custom",1));
		carpoolDAOUser.addUserToDatabase(user);	
		
		User user2 =  new User("password2", "email2", new LocationRepresentation ("primary2","custom2",1));
		carpoolDAOUser.addUserToDatabase(user2);
		carpoolDAOUser.addToSocialList(user,user2);
		
		
		User user3 =  new User("password3", "email3", new LocationRepresentation ("primary3","custom3",1));
		carpoolDAOUser.addUserToDatabase(user3);
		carpoolDAOUser.addToSocialList(user,user3);
		
		User user4 =  new User("password4", "email4", new LocationRepresentation ("primary4","custom4",1));
		carpoolDAOUser.addUserToDatabase(user4);
		carpoolDAOUser.addToSocialList(user,user4);
		
		
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = user.getSocialList();
	    	if(slist !=null && slist.size()==3 && slist.get(0).equals(user2)&&slist.get(1).equals(user3)&&slist.get(2).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		carpoolDAOUser.addToSocialList(user,user2);
		carpoolDAOUser.addToSocialList(user,user3);
		carpoolDAOUser.addToSocialList(user,user4);
		
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = carpoolDAOUser.getSocialListOfUser(user);
	    	if(slist !=null && slist.size()==3 && slist.get(0).equals(user2)&&slist.get(1).equals(user3)&&slist.get(2).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		carpoolDAOUser.addToSocialList(user2,user3);
		carpoolDAOUser.addToSocialList(user2,user4);
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = user2.getSocialList();
	    	if(slist !=null && slist.size()==2 && slist.get(0).equals(user3)&&slist.get(1).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		carpoolDAOUser.deleteFromSocialList(user, user2);
		carpoolDAOUser.deleteFromSocialList(user, user3);
		carpoolDAOUser.deleteFromSocialList(user, user4);
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = user.getSocialList();
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		//Double Check
		carpoolDAOUser.deleteFromSocialList(user, user);		
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = user.getSocialList();
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		carpoolDAOUser.deleteFromSocialList(user2, user3);
		carpoolDAOUser.deleteFromSocialList(user2, user4);
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = user2.getSocialList();
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
    }
    @Test
    public void testgetUserMessageHistory(){
    	carpoolDAOBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		
		try {
			carpoolDAOUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}		
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentmethod =paymentMethod.fromInt(0);		
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		Message message=new Message(1, user.getUserId(), user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		carpoolDAOMessage.addMessageToDatabase(message);
		Message message2=new Message(2, user.getUserId(), user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		carpoolDAOMessage.addMessageToDatabase(message2);
		Message message3=new Message(3, user.getUserId(), user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		carpoolDAOMessage.addMessageToDatabase(message3);
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = carpoolDAOUser.getUserMessageHistory(user);
			if(mlist !=null && mlist.size()==3 && mlist.get(0).equals(message) && mlist.get(1).equals(message2) && mlist.get(2).equals(message3)){
			//Passed;	
			}else{
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
    }
   
   
   
   
}
