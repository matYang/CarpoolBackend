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

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.cleanRoutineTask.MessageCleaner;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.HelperOperator;
import carpool.common.Parser;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.dbservice.*;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import static java.lang.System.out;

public class CarpoolMessageTest {
	
	
	
	@Test
	public void testCreate() throws UserNotFoundException {
		CarpoolDaoBasic.clearBothDatabase();
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		//No user
		Message message=new Message(1, 1, null,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,1,1 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,1, 1,priceList,paymentMethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Has user
		User user = new User("HarryXiong","c2xiong@uwaterloo.ca",new LocationRepresentation("p_c_d_2"), gender.both);
		Message message2=new Message(1, 2, user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentMethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message);
	}
	
	@Test
	public void testRead(){
		CarpoolDaoBasic.clearBothDatabase();
        User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		
				
		//Message	
		Message message=new Message(user.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,1 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);		
		
		//Test
	    try {
	    	Message temp = CarpoolDaoMessage.getMessageById(message.getMessageId());
			if(!message.equals(temp)){
				fail();
			}
					
		} catch (MessageNotFoundException | UserNotFoundException e) {
			e.printStackTrace();
			fail();
		}
			
		
	}
	
	@Test
	public void testUpdate() throws MessageNotFoundException, UserNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
        User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}		
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		
				
		//Message	
		Message message=new Message(user.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,1 , priceList,new LocationRepresentation("p_c_d_2"),
				time, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Update Location, paymentMethod, and type, state,genderRequirement,timeSlot as well as priceList
	   message.setArrival_location(new LocationRepresentation("Ontario_Waterloo_UniversityOfWaterloo_2"));
	   message.setGenderRequirement(gender.fromInt(1));
	   message.setType(messageType.fromInt(1));
	   message.setPaymentMethod(paymentMethod.fromInt(1));	  
	   priceList.remove(0);
	   priceList.add(2);	   
	   message.setArrival_priceList(priceList);
	   message.setArrival_timeSlot(DayTimeSlot.fromInt(1));
	   message.setDeparture_timeSlot(DayTimeSlot.fromInt(2));
	   CarpoolDaoMessage.UpdateMessageInDatabase(message);
	
	   //Test
	   try{
		   if(!message.equals(CarpoolDaoMessage.getMessageById(message.getMessageId()))){
			   fail();
		   }
	   }
	   catch(Exception e){
		   e.printStackTrace();
	   }
	    
	
	}
	@Test
	public void testDelete(){
		CarpoolDaoBasic.clearBothDatabase();
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int messageId=-1;
		int userId=-1;
				
		//Message	
		Message message=new Message(userId,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,1 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Delete
		try {
			CarpoolDaoMessage.deleteMessageFromDatabase(message.getMessageId());
		} catch (MessageNotFoundException e) {
			fail();
		}
		try{
			CarpoolDaoMessage.getMessageById(message.getMessageId());
		}catch (UserNotFoundException e) {
			fail();
			
		} catch (MessageNotFoundException e) {
			//Passed
		}
		
	}
	
	@Test
	public void testSearch(){
		CarpoolDaoBasic.clearBothDatabase();
User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = Calendar.getInstance();		
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = Calendar.getInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt3 = Calendar.getInstance();	
		dt3.add(Calendar.DAY_OF_YEAR, -2);
		
		//Location
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");		
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		messageType type1 = messageType.fromInt(1);
		messageType type2 = messageType.fromInt(2);
		gender genderRequirement = gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		DayTimeSlot timeSlot1 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(2);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(3);
		DayTimeSlot timeSlot4 = DayTimeSlot.fromInt(4);
		int userId=user.getUserId();
		//These messages should pass the search	
		//Message	
		Message message=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Message2
		Message message2=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		//Message3
		Message message3=new Message(userId,true, al,dt2,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		//Message4
		Message message4=new Message(userId,false, al,dt2,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		//Other messages
		Message message5=new Message(userId,false, al,dt3,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId,true, al,dt3,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId,false, dl,dt3,timeSlot,1 , priceList,al,dt2,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId,true, dl,dt3,timeSlot,1 , priceList,al,dt2,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message8);
		//Seats adjust
		Message message9=new Message(userId,false, dl,dt,timeSlot,10 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		message9.setDeparture_seatsBooked(11);
		CarpoolDaoMessage.addMessageToDatabase(message9);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot,timeSlot);		
		SearchRepresentation SR2 = new SearchRepresentation(true,al,dl,dt2,dt,type2,timeSlot,timeSlot);
		SearchRepresentation SR3 = new SearchRepresentation(false,al,dl,dt2,dt,type2,timeSlot,timeSlot);
		SearchRepresentation SR4 = new SearchRepresentation(true,dl,al,dt,at,type,timeSlot,timeSlot);
	    //New SRs
		Calendar dtime = Calendar.getInstance();		
		dtime.set(Calendar.HOUR_OF_DAY, 24);
		dtime.set(Calendar.MINUTE, 0);
		dtime.set(Calendar.SECOND, 0);
		Calendar atime = Calendar.getInstance();
		atime.add(Calendar.DAY_OF_YEAR, 1);
		atime.set(Calendar.HOUR_OF_DAY, 18);
		SearchRepresentation SR5 = new SearchRepresentation(false,dl,al,dtime,atime,type,timeSlot1,timeSlot2);
		Calendar dtime2 = Calendar.getInstance();		
		dtime2.set(Calendar.HOUR_OF_DAY, 9);
		dtime2.set(Calendar.MINUTE, 30);
		dtime2.set(Calendar.SECOND, 0);
		Calendar atime2 = Calendar.getInstance();		
		atime2.set(Calendar.HOUR_OF_DAY, 18);
		dtime2.set(Calendar.MINUTE, 30);
		dtime2.set(Calendar.SECOND, 0);
		Calendar dtime3 = Calendar.getInstance();		
		dtime3.set(Calendar.HOUR_OF_DAY, 21);
		dtime3.set(Calendar.MINUTE, 30);
		dtime3.set(Calendar.SECOND, 0);
		Calendar atime3 = Calendar.getInstance();		
		atime3.set(Calendar.HOUR_OF_DAY, 23);
		dtime3.set(Calendar.MINUTE, 59);
		dtime3.set(Calendar.SECOND, 59);
		Calendar dtime4 = Calendar.getInstance();
		dtime4.add(Calendar.DAY_OF_YEAR, -1);
		Message message10=new Message(userId,true, dl,dtime3,timeSlot,1 , priceList,al,atime3,timeSlot, 1,priceList,paymentMethod,"test",  type1, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message10);
		Message message11=new Message(userId,true, dl,dtime3,timeSlot,1 , priceList,al,atime3,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message11);
		SearchRepresentation SR6 = new SearchRepresentation(false,dl,al,dtime2,atime2,type2,timeSlot1,timeSlot2);
		SearchRepresentation SR7 = new SearchRepresentation(true,dl,al,dtime2,atime2,type1,timeSlot1,timeSlot2);
		SearchRepresentation SR8 = new SearchRepresentation(true,al,dl,dtime4,dtime2,type,timeSlot1,timeSlot2);
		//New SR Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR5);
			if(mlist.size()==5&&mlist.get(0).equals(message)&&mlist.get(1).equals(message2)&&mlist.get(2).equals(message3)&&mlist.get(3).equals(message6)&&mlist.get(4).equals(message11)){
				//Passed;
			}else{				
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR6);
			if(mlist.size()==6&&mlist.get(0).equals(message)&&mlist.get(1).equals(message2)&&mlist.get(2).equals(message3)&&mlist.get(3).equals(message6)&&mlist.get(4).equals(message10)&&mlist.get(5).equals(message11)){
				//Passed;
			}else{			
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR7);
			if(mlist.size()==1&&mlist.get(0).equals(message10)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR8);
			if(mlist.size()==5&&mlist.get(0).equals(message)&&mlist.get(1).equals(message3)&&mlist.get(2).equals(message4)&mlist.get(3).equals(message6)&&mlist.get(4).equals(message11)){
				//Passed;
			}else{
				System.out.println(mlist.size());
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
			fail();
		}
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR);
			if(mlist.size()==5 && mlist.get(0).equals(message)&&mlist.get(1).equals(message2)&&mlist.get(2).equals(message3)&&mlist.get(3).equals(message6)&&mlist.get(4).equals(message11)){
				//Passed;				
			}else{
				System.out.println(mlist.size());
				fail();
				}			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR2);
			if(mlist !=null && mlist.size()==6 && mlist.get(0).equals(message)&&mlist.get(1).equals(message3)&&mlist.get(2).equals(message4)&&mlist.get(3).equals(message6)&&mlist.get(4).equals(message10)&&mlist.get(5).equals(message11)){
				//Passed;
			}else{
				System.out.println(mlist.size());
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR3);
			if(mlist !=null && mlist.size()==3 && mlist.get(0).equals(message3)&&mlist.get(1).equals(message4)&&mlist.get(2).equals(message8)){
				//Passed;
			}else{
				System.out.println(mlist.size());				
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR4);
			if(mlist.size()==3 && mlist.get(0).equals(message)&&mlist.get(1).equals(message2)&&mlist.get(2).equals(message11)){
				//Passed;
			}else{
				System.out.println(mlist.size());
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void getRecentMessages(){
		CarpoolDaoBasic.clearBothDatabase();
User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = Calendar.getInstance();		
		Calendar at = Calendar.getInstance();			
		//Location
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");		
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int messageId=-1;
		int userId=user.getUserId();
		
		//Message	
		Message message=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);		
		Message message2=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);		
		Message message3=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);		
		Message message4=new Message(userId,false, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);		
		Message message5=new Message(userId,false, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message8);		
		Message message9=new Message(userId,false, dl,dt,timeSlot,10 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message9);
		Message message10=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message10);
		Message message11=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message11);
		Message message12=new Message(userId,false, al,dt,timeSlot,10 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message12);	
		Message message13=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message13);
		Message message14=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message14);
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.getRecentMessages();
			if(mlist !=null && mlist.size()==10 && mlist.get(0).equals(message)&& mlist.get(1).equals(message2)&& mlist.get(2).equals(message3)&& mlist.get(3).equals(message4)&& mlist.get(4).equals(message5)&& mlist.get(5).equals(message6)&& mlist.get(6).equals(message7)&& mlist.get(7).equals(message8)&& mlist.get(8).equals(message9)&& mlist.get(9).equals(message10)){
			//Passed;
			}else{				
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testMessageCleaner(){
		CarpoolDaoBasic.clearBothDatabase();
        User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}		
		
		
		Calendar dt1 = Calendar.getInstance();	
		Calendar at1 = Calendar.getInstance();
		
		Calendar dt2 = Calendar.getInstance();	
	    dt2.add(Calendar.DAY_OF_YEAR,-1);
		Calendar at2 = Calendar.getInstance();
		at2.add(Calendar.DAY_OF_YEAR,-1);
		
		Calendar dt3 = Calendar.getInstance();			
		dt3.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at3 = Calendar.getInstance();
		at3.add(Calendar.DAY_OF_YEAR, 1);
				
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(30);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(2);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		
				
		//Message: message2	, message5 , message7 and message8 shouldn't pass
		Message message=new Message(user.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),dt1,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at1, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		Message message2=new Message(user.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),dt2,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(user.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),dt1,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		Message message4=new Message(user.getUserId(),true
				, new LocationRepresentation("p_c_d_2"),dt2,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at1, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		Message message5=new Message(user.getUserId(),true
				, new LocationRepresentation("p_c_d_2"),dt2,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at2, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(user.getUserId(),true
				, new LocationRepresentation("p_c_d_2"),dt3,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(user.getUserId(),true
				, new LocationRepresentation("p_c_d_2"),dt3,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		message7.setState(Constants.messageState.fromInt(1));
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(user.getUserId(),true
				, new LocationRepresentation("p_c_d_2"),dt3,timeSlot,1 , priceList,new LocationRepresentation("p_c_a_2"),
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		message8.setState(Constants.messageState.fromInt(0));
		CarpoolDaoMessage.addMessageToDatabase(message8);
		MessageCleaner.Clean();
		ArrayList<Integer> list = new ArrayList<Integer>();
		String query = "SELECT * from carpoolDAOMessage WHERE ownerId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, message.getOwnerId());
			ResultSet rs = stmt.executeQuery();			
				while(rs.next()){	
					list.add(rs.getInt("messageState"));
					}			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		
		if(list !=null && list.size()==8 && list.get(0)==2&&list.get(1)==1&& list.get(2)==2&&list.get(3)==2&& list.get(4)==1&&list.get(5)==2&& list.get(6)==1&&list.get(7)==0){
			//Passed;
		}else{			
			fail();
		}
	}

	
	//@Test
	public void testBenchmark(){
		CarpoolDaoBasic.clearBothDatabase();
		Calendar  before = Calendar.getInstance();
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int messageId=-1;
		int userId=-1;
		int times=1000000;
		while(times > 0){					
			//create	
			Message message=new Message(userId,false
					, new LocationRepresentation("p_c_d_2"),time,timeSlot,1 , priceList,new LocationRepresentation("p_c_d_2"),
					time,timeSlot, 1,priceList,paymentMethod,
					"test",  type, genderRequirement);
			//add
			CarpoolDaoMessage.addMessageToDatabase(message);
			//read
			try {
				if(!CarpoolDaoMessage.getMessageById(message.getMessageId()).equals(message)){fail();}
			} catch (MessageNotFoundException e) {
				e.printStackTrace();
				fail();
			} catch (UserNotFoundException e) {
				e.printStackTrace();
				fail();
			}
			//update			
			   message.setArrival_location(new LocationRepresentation("Ontario_Waterloo_UniversityOfWaterloo_2"));
			   message.setGenderRequirement(gender.fromInt(1));
			   message.setType(messageType.fromInt(1));
			   message.setState(messageState.fromInt(1));
			   message.setPaymentMethod(paymentMethod.fromInt(1));
			   message.setArrival_seatsBooked(3);
			   message.setArrival_seatsNumber(100);
			   message.setDeparture_time(DateUtility.DateToCalendar(new Date(1)));
			   message.setDeparture_location(new LocationRepresentation("Canada_Ontario_UniversityOfWaterloo_2"));
			   message.setDeparture_seatsBooked(12);
			   message.setDeparture_seatsNumber(13);
			   message.setHistoryDeleted(true);
			   message.setNote("xch");
			   message.setRoundTrip(true);
			   message.setEditTime(DateUtility.DateToCalendar(new Date(2)));
			   priceList.remove(0);
			   priceList.add(2);	   
			   message.setArrival_priceList(priceList);
			   message.setArrival_timeSlot(DayTimeSlot.fromInt(1));
			   message.setDeparture_timeSlot(DayTimeSlot.fromInt(2));
			   
			   try {
				CarpoolDaoMessage.UpdateMessageInDatabase(message);
				
			} catch (MessageNotFoundException e) {
				fail();
			}
			  
		  try {
			  
			
			   if(!message.equals(CarpoolDaoMessage.getMessageById(message.getMessageId()))){fail();}
			 
		} catch (MessageNotFoundException e) {
			fail();
		} catch (UserNotFoundException e) {
			fail();
		}
		  
		 //delete
		  try {
				CarpoolDaoMessage.deleteMessageFromDatabase(message.getMessageId());
			} catch (MessageNotFoundException e) {
				fail();
			}
			try{
				CarpoolDaoMessage.getMessageById(message.getMessageId());
			}catch (UserNotFoundException e) {
				fail();
				
			} catch (MessageNotFoundException e) {
				//System.out.println("Passed!");
			}
		  
			
		times--;			
		}
		
		
		Calendar  now = Calendar.getInstance();
		long TimeConsumed =now.getTimeInMillis() - before.getTimeInMillis();
		System.out.println(TimeConsumed);
		
	
	}
}