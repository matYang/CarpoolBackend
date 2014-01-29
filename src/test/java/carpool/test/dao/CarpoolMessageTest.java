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
import carpool.constants.Constants.Gender;
import carpool.constants.Constants.MessageState;
import carpool.constants.Constants.MessageType;
import carpool.constants.Constants.PaymentMethod;
import carpool.dbservice.*;
import carpool.exception.validation.ValidationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.representation.SearchRepresentation;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import static java.lang.System.out;

public class CarpoolMessageTest {



	@Test
	public void testCreate() throws UserNotFoundException, LocationNotFoundException {
		CarpoolDaoBasic.clearBothDatabase();
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		//No user
		Message message=new Message(1, 1, null,false
				, departureLocation,time,timeSlot,1,1 , priceList,arrivalLocation,
				time,timeSlot,1, 1,priceList,paymentMethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Has user
		User user = new User("HarryXiong","c2xiong@uwaterloo.ca",departureLocation, Gender.both);
		Message message2=new Message(1, 2, user,false
				, arrivalLocation,time,timeSlot,3,4 , priceList,departureLocation,
				time,timeSlot,5, 6,priceList,paymentMethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message);
	}

	@Test
	public void testRead() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();		
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		Location userLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", userLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		//Message	
		Message message=new Message(user.getUserId(),false
				, departureLocation,time,timeSlot,1 , priceList,arrivalLocation,
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);		

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
	public void testUpdate() throws MessageNotFoundException, UserNotFoundException, LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();			
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		//Message	
		Message message=new Message(user.getUserId(),false
				, arrivalLocation,time,timeSlot,1 , priceList,arrivalLocation,
				time, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Update Location, paymentMethod, and type, state,genderRequirement,timeSlot as well as priceList
		message.setArrival_Location(departureLocation);
		message.setGenderRequirement(Gender.fromInt(1));
		message.setType(MessageType.fromInt(1));
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
	public void testDelete() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int messageId=-1;
		int userId=-1;
		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		//Message	
		Message message=new Message(userId,false
				, departureLocation,time,timeSlot,1 , priceList,arrivalLocation,
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
	public void testSearch() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();		
		//Date
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt3 = DateUtility.getCurTimeInstance();	
		dt3.add(Calendar.DAY_OF_YEAR, -2);

		//Location
		long departure_Id = 1L;
		long arrival_Id = 2L;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		MessageType type1 = MessageType.fromInt(1);
		MessageType type2 = MessageType.fromInt(2);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		DayTimeSlot timeSlot1 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(2);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(3);
		DayTimeSlot timeSlot4 = DayTimeSlot.fromInt(4);
		int userId=user.getUserId();
		//These messages should pass the search	
		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Message2
		Message message2=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		//Message3
		Message message3=new Message(userId,true, new Location(arrivalLocation),dt2,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		//Message4
		Message message4=new Message(userId,false,new Location(arrivalLocation),dt2,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		//Other messages
		Message message5=new Message(userId,false, new Location(arrivalLocation),dt3,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId,true, new Location(arrivalLocation),dt3,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId,false, new Location(departureLocation),dt3,timeSlot,1 , priceList,new Location(arrivalLocation),dt2,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId,true, new Location(departureLocation),dt3,timeSlot,1 , priceList,new Location(arrivalLocation),dt2,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message8);
		//Seats adjust
		Message message9=new Message(userId,false, new Location(departureLocation),dt,timeSlot,10 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		message9.setDeparture_seatsBooked(11);
		CarpoolDaoMessage.addMessageToDatabase(message9);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,new Location(departureLocation).getMatch(),new Location(arrivalLocation).getMatch(),dt,at,type,timeSlot,timeSlot);		
		SearchRepresentation SR2 = new SearchRepresentation(true,new Location(arrivalLocation).getMatch(),new Location(departureLocation).getMatch(),dt2,dt,type2,timeSlot,timeSlot);
		SearchRepresentation SR3 = new SearchRepresentation(false,new Location(arrivalLocation).getMatch(),new Location(departureLocation).getMatch(),dt2,dt,type2,timeSlot,timeSlot);
		SearchRepresentation SR4 = new SearchRepresentation(true,new Location(departureLocation).getMatch(),new Location(arrivalLocation).getMatch(),dt,at,type,timeSlot,timeSlot);
		//New SRs
		Calendar dtime = DateUtility.getCurTimeInstance();		
		dtime.set(Calendar.HOUR_OF_DAY, 24);
		dtime.set(Calendar.MINUTE, 0);
		dtime.set(Calendar.SECOND, 0);
		Calendar atime = DateUtility.getCurTimeInstance();
		atime.add(Calendar.DAY_OF_YEAR, 1);
		atime.set(Calendar.HOUR_OF_DAY, 18);
		SearchRepresentation SR5 = new SearchRepresentation(false,new Location(departureLocation).getMatch(),new Location(arrivalLocation).getMatch(),dtime,atime,type,timeSlot1,timeSlot2);
		Calendar dtime2 = DateUtility.getCurTimeInstance();		
		dtime2.set(Calendar.HOUR_OF_DAY, 9);
		dtime2.set(Calendar.MINUTE, 30);
		dtime2.set(Calendar.SECOND, 0);
		Calendar atime2 = DateUtility.getCurTimeInstance();		
		atime2.set(Calendar.HOUR_OF_DAY, 18);
		atime2.set(Calendar.MINUTE, 30);
		atime2.set(Calendar.SECOND, 0);
		Calendar dtime3 = DateUtility.getCurTimeInstance();		
		dtime3.set(Calendar.HOUR_OF_DAY, 21);
		dtime3.set(Calendar.MINUTE, 30);
		dtime3.set(Calendar.SECOND, 0);
		Calendar atime3 = DateUtility.getCurTimeInstance();		
		atime3.set(Calendar.HOUR_OF_DAY, 23);
		atime3.set(Calendar.MINUTE, 59);
		atime3.set(Calendar.SECOND, 59);
		Calendar dtime4 = DateUtility.getCurTimeInstance();
		dtime4.add(Calendar.DAY_OF_YEAR, -1);
		Message message10=new Message(userId,true, new Location(departureLocation),dtime3,timeSlot,1 , priceList,new Location(arrivalLocation),atime3,timeSlot, 1,priceList,paymentMethod,"test",  type1, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message10);
		Message message11=new Message(userId,true, new Location(departureLocation),dtime3,timeSlot,1 , priceList,new Location(arrivalLocation),atime3,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message11);
		SearchRepresentation SR6 = new SearchRepresentation(false,new Location(departureLocation).getMatch(),new Location(arrivalLocation).getMatch(),dtime2,atime2,type2,timeSlot1,timeSlot2);
		SearchRepresentation SR7 = new SearchRepresentation(true,new Location(departureLocation).getMatch(),new Location(arrivalLocation).getMatch(),dtime2,atime2,type1,timeSlot1,timeSlot2);
		SearchRepresentation SR8 = new SearchRepresentation(true,new Location(arrivalLocation).getMatch(),new Location(departureLocation).getMatch(),dtime4,dtime2,type,timeSlot1,timeSlot2);
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
			if(mlist.size()==7&&mlist.get(0).equals(message)&&mlist.get(1).equals(message2)&&mlist.get(2).equals(message3)&&mlist.get(3).equals(message4)&mlist.get(4).equals(message6)&&mlist.get(5).equals(message8)&&mlist.get(6).equals(message11)){
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
			if(mlist !=null && mlist.size()==8 && mlist.get(0).equals(message)&&mlist.get(1).equals(message2)&&mlist.get(2).equals(message3)&&mlist.get(3).equals(message4)&&mlist.get(4).equals(message6)&&mlist.get(5).equals(message8)&&mlist.get(6).equals(message10)&&mlist.get(7).equals(message11)){
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

	}

	@Test
	public void getRecentMessages() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();		
		//Date
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.getRecentMessages();
			if(mlist !=null && mlist.size()==0){
				//Passed;
			}else{				
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca",departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);		
		int userId=user.getUserId();
		
		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);		
		Message message2=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);		
		Message message3=new Message(userId,true, new Location(arrivalLocation),dt,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);		
		Message message4=new Message(userId,false, new Location(arrivalLocation),dt,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);		
		Message message5=new Message(userId,false, new Location(arrivalLocation),dt,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId,true, new Location(arrivalLocation),dt,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message8);		
		Message message9=new Message(userId,false, new Location(departureLocation),dt,timeSlot,10 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message9);
		Message message10=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message10);
		Message message11=new Message(userId,true, new Location(arrivalLocation),dt,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message11);
		Message message12=new Message(userId,false, new Location(arrivalLocation),dt,timeSlot,10 , priceList,new Location(departureLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message12);	
		Message message13=new Message(userId,true, new Location(arrivalLocation),dt,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message13);
		Message message14=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
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
	public void testMessageCleaner() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		Calendar dt1 = DateUtility.getCurTimeInstance();	
		Calendar at1 = DateUtility.getCurTimeInstance();

		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR,-1);
		Calendar at2 = DateUtility.getCurTimeInstance();
		at2.add(Calendar.DAY_OF_YEAR,-1);

		Calendar dt3 = DateUtility.getCurTimeInstance();			
		dt3.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at3 = DateUtility.getCurTimeInstance();
		at3.add(Calendar.DAY_OF_YEAR, 1);

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(30);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(2);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);

		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		//Message: message2	, message5 , message7 and message8 shouldn't pass
		Message message=new Message(user.getUserId(),false
				, departureLocation,dt1,timeSlot,1 , priceList,departureLocation,
				at1, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		Message message2=new Message(user.getUserId(),false
				, arrivalLocation,dt2,timeSlot,1 , priceList,arrivalLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(user.getUserId(),false
				, departureLocation,dt1,timeSlot,1 , priceList,arrivalLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		Message message4=new Message(user.getUserId(),true
				, departureLocation,dt2,timeSlot,1 , priceList,departureLocation,
				at1, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		Message message5=new Message(user.getUserId(),true
				, arrivalLocation,dt2,timeSlot,1 , priceList,arrivalLocation,
				at2, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(user.getUserId(),true
				, arrivalLocation,dt3,timeSlot,1 , priceList,departureLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(user.getUserId(),true
				, arrivalLocation,dt3,timeSlot,1 , priceList,arrivalLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		message7.setState(Constants.MessageState.fromInt(1));
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(user.getUserId(),true
				, departureLocation,dt3,timeSlot,1 , priceList,arrivalLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		message8.setState(Constants.MessageState.fromInt(0));
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
			DebugLog.d(e);
		}

		if(list !=null && list.size()==8 && list.get(0)==2&&list.get(1)==1&& list.get(2)==2&&list.get(3)==2&& list.get(4)==1&&list.get(5)==2&& list.get(6)==1&&list.get(7)==0){
			//Passed;
		}else{			
			fail();
		}
	}
	
	@Test
	public void testMessageIsOpen() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		Calendar dt1 = DateUtility.getCurTimeInstance();	
		Calendar at1 = DateUtility.getCurTimeInstance();

		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR,-1);
		Calendar at2 = DateUtility.getCurTimeInstance();
		at2.add(Calendar.DAY_OF_YEAR,-1);

		Calendar dt3 = DateUtility.getCurTimeInstance();			
		dt3.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at3 = DateUtility.getCurTimeInstance();
		at3.add(Calendar.DAY_OF_YEAR, 1);

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(30);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(2);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);

		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		//Message: message2	, message5 , message7 and message8 shouldn't pass
		Message message=new Message(user.getUserId(),false
				, departureLocation,dt1,timeSlot,1 , priceList,departureLocation,
				at1, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		Message message2=new Message(user.getUserId(),false
				, arrivalLocation,dt2,timeSlot,1 , priceList,arrivalLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(user.getUserId(),false
				, departureLocation,dt1,timeSlot,1 , priceList,arrivalLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		Message message4=new Message(user.getUserId(),true
				, departureLocation,dt2,timeSlot,1 , priceList,departureLocation,
				at1, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		Message message5=new Message(user.getUserId(),true
				, arrivalLocation,dt2,timeSlot,1 , priceList,arrivalLocation,
				at2, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(user.getUserId(),true
				, arrivalLocation,dt3,timeSlot,1 , priceList,departureLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(user.getUserId(),true
				, arrivalLocation,dt3,timeSlot,1 , priceList,arrivalLocation,
				at3, timeSlot,1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		message7.setState(Constants.MessageState.fromInt(1));
		CarpoolDaoMessage.addMessageToDatabase(message7);
		
		//
		//Set Message states and Dt
		message.setState(carpool.constants.Constants.MessageState.closed);
		message2.setState(carpool.constants.Constants.MessageState.deleted);
		message3.setState(carpool.constants.Constants.MessageState.open);
		message3.setDeparture_time(DateUtility.getCurTimeInstance());
		//This will pass
		message4.setState(carpool.constants.Constants.MessageState.open);
		message4.setDeparture_time(dt3);
		//Set Message states and Dt
		message5.setState(carpool.constants.Constants.MessageState.open);
		message5.setDeparture_time(dt2);
		message6.setState(carpool.constants.Constants.MessageState.deleted);
		message6.setDeparture_time(DateUtility.getCurTimeInstance());
		message7.setState(carpool.constants.Constants.MessageState.closed);
		message7.setDeparture_time(DateUtility.getCurTimeInstance());
		
		if(CarpoolDaoMessage.isOpen(message)||CarpoolDaoMessage.isOpen(message2)||CarpoolDaoMessage.isOpen(message3)||CarpoolDaoMessage.isOpen(message5)||CarpoolDaoMessage.isOpen(message6)||CarpoolDaoMessage.isOpen(message7)){
			fail();
		}else{
			if(CarpoolDaoMessage.isOpen(message4)){
				//Passed;
			}else{
				fail();
			}
		}	
	}

	//@Test
	public void testBenchmark() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		Calendar  before = DateUtility.getCurTimeInstance();
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int messageId=-1;
		int userId=-1;
		int times=1000000;
		//Location
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		while(times > 0){					
			//create	
			Message message=new Message(userId,false
					, departureLocation,time,timeSlot,1 , priceList,arrivalLocation,
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
			message.setArrival_Location(departureLocation);
			message.setGenderRequirement(Gender.fromInt(1));
			message.setType(MessageType.fromInt(1));
			message.setState(MessageState.fromInt(1));
			message.setPaymentMethod(paymentMethod.fromInt(1));
			message.setArrival_seatsBooked(3);
			message.setArrival_seatsNumber(100);
			message.setDeparture_time(DateUtility.DateToCalendar(new Date(1)));
			message.setDeparture_Location(arrivalLocation);
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


		Calendar  now = DateUtility.getCurTimeInstance();
		long TimeConsumed =now.getTimeInMillis() - before.getTimeInMillis();
		System.out.println(TimeConsumed);


	}
}