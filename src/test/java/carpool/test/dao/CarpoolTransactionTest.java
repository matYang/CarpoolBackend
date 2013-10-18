package carpool.test.dao;

import static org.junit.Assert.fail;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoTransaction;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.TransactionDirection;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.exception.ValidationException;
import carpool.model.Message;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

public class CarpoolTransactionTest {
    @Test
	public void testAddTransaction(){
		CarpoolDaoBasic.clearBothDatabase();
		//Users
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User customer =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(customer);
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
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		
				
		//Messages
		Message message=new Message(provider.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,1 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		TransactionDirection tD = Constants.TransactionDirection.fromInt(1);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",tD,time,timeSlot,1,time,timeSlot,1);
		
		//Test
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
    
    @Test
    public void testReadTransaction(){
    	CarpoolDaoBasic.clearBothDatabase();
		//Users
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User customer =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		
		Calendar dt = Calendar.getInstance();
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod p =Constants.paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");	
	    
		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),false
				, dl,dt,timeSlot,dseats , priceList,al,
				at,timeSlot, aseats,priceList,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);		 
		TransactionDirection tD = Constants.TransactionDirection.fromInt(1);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",tD,dt,timeSlot,dseats,at,timeSlot,aseats);
		
		// Test
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());
			
			if(transaction.getProvider().equals(provider)&&transaction.getCustomer().equals(customer)&&transaction.getDeparture_location().equals(dl)&&transaction.getArrival_location().equals(al)&&transaction.getCustomerNote().equals("cNote")&&transaction.getProviderNote().equals("pNote")&&transaction.getPaymentMethod().equals(p)){
				//Passed;
			}else{
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		Transaction t2 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",tD,dt,timeSlot,dseats,at,timeSlot,aseats);
		try{
	    CarpoolDaoTransaction.addTransactionToDatabase(t2);
		t2 = CarpoolDaoTransaction.getTransactionById(t2.getTransactionId());
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		
		System.out.println("---");
		System.out.println(DateUtility.toSQLDateTime(transaction.getArrival_time()));
		System.out.println(DateUtility.toSQLDateTime(t2.getArrival_time()));
		System.out.println("---");
		
//		tlist = CarpoolDaoTransaction.getAllTranscations();	
//		if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
//			//Passed;
//			
//		}else{
//			fail();
//		}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Transaction> tlist = new ArrayList<Transaction>();
			tlist = CarpoolDaoTransaction.getAllTransactionByMessageId(message.getMessageId());
			if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
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
    public void testUpdateTransaction(){
    	CarpoolDaoBasic.clearBothDatabase();
		//Users
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User customer =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		
		Calendar dt = Calendar.getInstance();
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod p =Constants.paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");	
	    
		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),false
				, dl,dt,timeSlot,dseats , priceList,al,
				at,timeSlot, aseats,priceList,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);
		TransactionDirection tD = Constants.TransactionDirection.fromInt(1);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",tD,dt,timeSlot,dseats,at,timeSlot,aseats);
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());		
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		transaction.setCustomerEvaluation(100);
		transaction.setDirection(Constants.TransactionDirection.fromInt(0));
		transaction.setCustomerNote("Good");
		
		//Test
		try{
			CarpoolDaoTransaction.UpdateTransactionInDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());
			if(transaction.getCustomerEvaluation()==100&&transaction.getDirection().equals(Constants.TransactionDirection.fromInt(0))&&transaction.getProvider().equals(provider)&&transaction.getCustomer().equals(customer)&&transaction.getDeparture_location().equals(dl)&&transaction.getArrival_location().equals(al)&&transaction.getCustomerNote().equals("Good")&&transaction.getProviderNote().equals("pNote")&&transaction.getPaymentMethod().equals(p)){
				
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		
		
    }
}
    
    
    