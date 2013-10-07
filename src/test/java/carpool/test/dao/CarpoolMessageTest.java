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
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.test.mockModel.MockUser;
import static java.lang.System.out;

public class CarpoolMessageTest {
	
	
	
	@Test
	public void testCreate() throws UserNotFoundException {
		carpoolDAOBasic.clearBothDatabase();
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
		carpoolDAOMessage.addMessageToDatabase(message);
		//Has user
		User user = new User("HarryXiong","c2xiong@uwaterloo.ca",new LocationRepresentation("p_c_d_2"));
		Message message2=new Message(1, 2, user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentMethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		carpoolDAOMessage.addMessageToDatabase(message);
	}
	
	@Test
	public void testRead(){
		carpoolDAOBasic.clearBothDatabase();
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
		carpoolDAOMessage.addMessageToDatabase(message);		
		
		//Test
	    try {
	    	Message temp = carpoolDAOMessage.getMessageById(message.getMessageId());
			if(!message.equals(temp)){
				fail();
			}
					
		} catch (MessageNotFoundException | UserNotFoundException e) {
			fail();
		}
			
		
	}
	
	@Test
	public void testUpdate() throws MessageNotFoundException, UserNotFoundException{
		carpoolDAOBasic.clearBothDatabase();
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
				time, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message);
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
	   carpoolDAOMessage.UpdateMessageInDatabase(message);
	
	   //Test
	   if(!message.equals(carpoolDAOMessage.getMessageById(message.getMessageId()))){fail();}
	    
	
	}
	@Test
	public void testDelete(){
		carpoolDAOBasic.clearBothDatabase();
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
		carpoolDAOMessage.addMessageToDatabase(message);
		//Delete
		try {
			carpoolDAOMessage.deleteMessageFromDatabase(message.getMessageId());
		} catch (MessageNotFoundException e) {
			fail();
		}
		try{
			carpoolDAOMessage.getMessageById(message.getMessageId());
		}catch (UserNotFoundException e) {
			fail();
			
		} catch (MessageNotFoundException e) {
			//Passed
		}
		
	}
	
	@Test
	public void testSearch(){
		carpoolDAOBasic.clearBothDatabase();
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
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int messageId=-1;
		int userId=-1;
		//These messages should pass the search	
		//Message	
		Message message=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message);
		//Message2
		Message message2=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message2);
		//Message3
		Message message3=new Message(userId,true, al,dt2,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message3);
		//Message4
		Message message4=new Message(userId,false, al,dt2,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message4);
		//Other messages
		Message message5=new Message(userId,false, al,dt3,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId,true, al,dt3,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId,false, dl,dt3,timeSlot,1 , priceList,al,dt2,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId,true, dl,dt3,timeSlot,1 , priceList,al,dt2,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message8);
		//Seats adjust
		Message message9=new Message(userId,false, dl,dt,timeSlot,10 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		message9.setDeparture_seatsBooked(11);
		carpoolDAOMessage.addMessageToDatabase(message9);
		Message message10=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		message10.setDeparture_seatsBooked(2);
		message10.setArrival_seatsBooked(2);
		carpoolDAOMessage.addMessageToDatabase(message10);
		Message message11=new Message(userId,true, al,dt2,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		message11.setDeparture_seatsBooked(2);
		message11.setArrival_seatsBooked(2);
		carpoolDAOMessage.addMessageToDatabase(message11);
		Message message12=new Message(userId,false, al,dt2,timeSlot,10 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		message12.setDeparture_seatsBooked(11);
		carpoolDAOMessage.addMessageToDatabase(message12);	
		Message message13=new Message(userId,true, al,dt3,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		message13.setDeparture_seatsBooked(2);
		message13.setArrival_seatsBooked(2);
		carpoolDAOMessage.addMessageToDatabase(message13);
		Message message14=new Message(userId,true, dl,dt3,timeSlot,1 , priceList,al,dt2,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		message14.setDeparture_seatsBooked(2);
		message14.setArrival_seatsBooked(2);
		carpoolDAOMessage.addMessageToDatabase(message14);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot,timeSlot);		
		SearchRepresentation SR2 = new SearchRepresentation(true,al,dl,dt2,dt,type,timeSlot,timeSlot);
		SearchRepresentation SR3 = new SearchRepresentation(false,al,dl,dt2,dt,type,timeSlot,timeSlot);
		SearchRepresentation SR4 = new SearchRepresentation(true,dl,al,dt,at,type,timeSlot,timeSlot);
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = carpoolDAOMessage.searchMessage(SR);
			if(mlist !=null && mlist.size()==4 && mlist.get(0).equals(message)&&mlist.get(1).equals(message2)&&mlist.get(2).equals(message3)&&mlist.get(3).equals(message6)){
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
			mlist = carpoolDAOMessage.searchMessage(SR2);
			if(mlist !=null && mlist.size()==4 && mlist.get(0).equals(message)&&mlist.get(1).equals(message3)&&mlist.get(2).equals(message4)&&mlist.get(3).equals(message6)){
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
			mlist = carpoolDAOMessage.searchMessage(SR3);
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
			mlist = carpoolDAOMessage.searchMessage(SR4);
			if(mlist !=null && mlist.size()==2 && mlist.get(0).equals(message)&&mlist.get(1).equals(message2)){
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
		carpoolDAOBasic.clearBothDatabase();
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
		int userId=-1;
		
		//Message	
		Message message=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message);		
		Message message2=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message2);		
		Message message3=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message3);		
		Message message4=new Message(userId,false, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message4);		
		Message message5=new Message(userId,false, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message8);		
		Message message9=new Message(userId,false, dl,dt,timeSlot,10 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message9);
		Message message10=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message10);
		Message message11=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message11);
		Message message12=new Message(userId,false, al,dt,timeSlot,10 , priceList,dl,dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message12);	
		Message message13=new Message(userId,true, al,dt,timeSlot,1 , priceList,dl,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message13);
		Message message14=new Message(userId,true, dl,dt,timeSlot,1 , priceList,al,dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message14);
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = carpoolDAOMessage.getRecentMessages();
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
	public void testIntergrated(){
		//Test a method at a time
	
	}
	
	@Test
	public void testAdvanced(){
		//Test a method at a time
	
	}
	
	//@Test
	public void testBenchmark(){
		carpoolDAOBasic.clearBothDatabase();
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
			carpoolDAOMessage.addMessageToDatabase(message);
			//read
			try {
				if(!carpoolDAOMessage.getMessageById(message.getMessageId()).equals(message)){fail();}
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
				carpoolDAOMessage.UpdateMessageInDatabase(message);
				
			} catch (MessageNotFoundException e) {
				fail();
			}
			  
		  try {
			  
			
			   if(!message.equals(carpoolDAOMessage.getMessageById(message.getMessageId()))){fail();}
			 
		} catch (MessageNotFoundException e) {
			fail();
		} catch (UserNotFoundException e) {
			fail();
		}
		  
		 //delete
		  try {
				carpoolDAOMessage.deleteMessageFromDatabase(message.getMessageId());
			} catch (MessageNotFoundException e) {
				fail();
			}
			try{
				carpoolDAOMessage.getMessageById(message.getMessageId());
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