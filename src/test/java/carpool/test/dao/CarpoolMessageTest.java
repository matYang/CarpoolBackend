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
import carpool.common.Parser;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.database.DaoBasic;
import carpool.database.DaoUser;
import carpool.dbservice.*;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.test.mockModel.MockUser;
import static java.lang.System.out;

public class CarpoolMessageTest {
	
	
	
	@Test
	public void testCreate() throws UserNotFoundException {
		
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		//No user
		Message message=new Message(1, 1, null,false
				, new LocationRepresentation("p c d s"),time,1,1 , priceList,new LocationRepresentation("p c d s"),
				time,1, 1,priceList,paymentMethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		//Has user
		User user = new User("HarryXiong","c2xiong@uwaterloo.ca",new LocationRepresentation("p c d s"));
		Message message2=new Message(1, 2, user,false
				, new LocationRepresentation("p c d s"),time,3,4 , priceList,new LocationRepresentation("p c d s"),
				time,5, 6,priceList,paymentMethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
	
	}
	
	@Test
	public void testRead() throws Exception{
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		int messageId=-1;
		int userId=-1;
				
		//Message	
		Message message=new Message(userId,false
				, new LocationRepresentation("p c d s"),time,1 , priceList,new LocationRepresentation("p c d s"),
				time, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message);
		System.out.println(message.getMessageId()+"xch");
		
		//Test
		if(!carpoolDAOMessage.getMessageById(message.getMessageId()).equals(message)){fail();}
			
		
	}
	
	@Test
	public void testUpdate() throws MessageNotFoundException, UserNotFoundException{
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		int messageId=-1;
		int userId=-1;
				
		//Message	
		Message message=new Message(userId,false
				, new LocationRepresentation("p c d s"),time,1 , priceList,new LocationRepresentation("p c d s"),
				time, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		carpoolDAOMessage.addMessageToDatabase(message);
		
		//Update Location, paymentMethod, and type, state,genderRequirement as well as priceList
	   message.setArrival_Location(new LocationRepresentation("Ontario Waterloo Downtown UniversityOfWaterloo"));
	   message.setGenderRequirement(gender.fromInt(1));
	   message.setType(messageType.fromInt(1));
	   message.setState(messageState.fromInt(-1));
	   message.setPaymentMethod(paymentMethod.fromInt(1));	  
	   priceList.remove(0);
	   priceList.add(2);	   
	   message.setArrival_priceList(priceList);
	   carpoolDAOMessage.UpdateMessageInDatabase(message);
	   ArrayList origList = new ArrayList();
	   origList.add(2);
	   //Test
	   message = carpoolDAOMessage.getMessageById(message.getMessageId());
	   
	   if(message.getArrival_Location().equals(new LocationRepresentation("Ontario Waterloo Downtown UniversityOfWaterloo"))
	     && message.getArrival_priceList().equals(origList) && message.getGenderRequirement().equals(gender.fromInt(1))
	     && message.getType().equals(messageType.fromInt(1)) && message.getState().equals(messageState.fromInt(-1))
	     && message.getPaymentMethod().equals(paymentMethod.fromInt(1))){
	   }else{fail();}   
	
	}
	@Test
	public void testDelete(){
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		int messageId=-1;
		int userId=-1;
				
		//Message	
		Message message=new Message(userId,false
				, new LocationRepresentation("p c d s"),time,1 , priceList,new LocationRepresentation("p c d s"),
				time, 1,priceList,paymentMethod,
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
		//Test a method at a time
	
	}
	
	@Test
	public void testIntergrated(){
		//Test a method at a time
	
	}
	
	@Test
	public void testAdvanced(){
		//Test a method at a time
	
	}
	
	
	public void testBenchmark(){
		Calendar  before = Calendar.getInstance();
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		int messageId=-1;
		int userId=-1;
		int times=1000000;
		while(times > 0){					
			//create	
			Message message=new Message(userId,false
					, new LocationRepresentation("p c d s"),time,1 , priceList,new LocationRepresentation("p c d s"),
					time, 1,priceList,paymentMethod,
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
			   message.setArrival_Location(new LocationRepresentation("Ontario Waterloo Downtown UniversityOfWaterloo"));
			   message.setGenderRequirement(gender.fromInt(1));
			   message.setType(messageType.fromInt(1));
			   message.setState(messageState.fromInt(-1));
			   message.setPaymentMethod(paymentMethod.fromInt(1));
			   message.setArrival_seatsBooked(3);
			   message.setArrival_seatsNumber(100);
			   message.setDeparture_Time(DateUtility.DateToCalendar(new Date(1)));
			   message.setDeparture_Location(new LocationRepresentation("Canada Ontario Waterloo UniversityOfWaterloo"));
			   message.setDeparture_seatsBooked(12);
			   message.setDeparture_seatsNumber(13);
			   message.setHistoryDeleted(true);
			   message.setEditTime(before);
			   message.setNote("xch");
			   message.setRoundTrip(true);
			   message.setEditTime(DateUtility.DateToCalendar(new Date(2)));
			   priceList.remove(0);
			   priceList.add(2);	   
			   message.setArrival_priceList(priceList);
			   try {
				carpoolDAOMessage.UpdateMessageInDatabase(message);
			} catch (MessageNotFoundException e) {
				fail();
			}
			   Message NewMessage = null;
		  try {
			 NewMessage = carpoolDAOMessage.getMessageById(message.getMessageId());
		} catch (MessageNotFoundException e) {
			fail();
		} catch (UserNotFoundException e) {
			fail();
		}
		  if(!NewMessage.equals(message)){fail();}
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
				System.out.println("Passed!");
			}
		  
			
		times--;			
		}
		
		
		Calendar  now = Calendar.getInstance();
		long TimeConsumed =now.getTimeInMillis() - before.getTimeInMillis();
		System.out.println(TimeConsumed);
		
	
	}
}