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
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.common.DateUtility;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.TransactionType;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.constants.Constants.transactionState;
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
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User customer =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1), gender.both);
		
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
		TransactionType transactiontype = TransactionType.fromInt(0);
				
		//Messages
		Message message=new Message(provider.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,1 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);		
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,transactiontype);
		
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
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User customer =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1), gender.both);
		
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
		TransactionType ttype = TransactionType.fromInt(0);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);
		
		// Test
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());			
			if(transaction.getProvider().equals(provider)&&transaction.getCustomer().equals(customer)&&transaction.getDeparture_location().equals(dl)&&transaction.getArrival_location().equals(al)&&transaction.getCustomerNote().equals("cNote")&&transaction.getProviderNote().equals("pNote")&&transaction.getPaymentMethod().equals(p)&&transaction.getTotalPrice()==1){
				//Passed;
			}else{
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		Transaction t2 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);
		try{
	    CarpoolDaoTransaction.addTransactionToDatabase(t2);
		t2 = CarpoolDaoTransaction.getTransactionById(t2.getTransactionId());		
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();		
		tlist = CarpoolDaoTransaction.getAllTranscations();	
		if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
			//Passed;
			
		}else{
			fail();
		}
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
		try{
			ArrayList<Transaction> tlist = new ArrayList<Transaction>();
			tlist = CarpoolDaoTransaction.getAllTransactionByUserId(provider.getUserId());
			if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
				//Passed;				
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		try{
			ArrayList<Transaction> tlist = new ArrayList<Transaction>();
			tlist = CarpoolDaoTransaction.getAllTransactionByUserId(customer.getUserId());
			if(tlist !=null && tlist.size()==2&& tlist.get(0).equals(transaction)&&tlist.get(1).equals(t2)){
				//Passed;				
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		ArrayList<Integer> alist = new ArrayList<Integer>();
		alist.add(10);
		Message message2=new Message(provider.getUserId(),true
				, dl,dt,timeSlot,dseats , priceList,al,
				at,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message2 = CarpoolDaoMessage.addMessageToDatabase(message2);
		ttype = TransactionType.fromInt(1);
	    transaction = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);
	    try{
	    	transaction = CarpoolDaoTransaction.addTransactionToDatabase(transaction);
	    	if(transaction.getProvider().equals(provider)&& transaction.getCustomer().equals(customer)&&transaction.getMessage().equals(message2)&&transaction.getTotalPrice()==10){
	    		//Passed;
	    	}else{
	    		
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
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User customer =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1), gender.both);
		
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
		ArrayList<Integer> alist = new ArrayList<Integer>();
		alist.add(10);
		
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");	
	    
		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),true
				, dl,dt,timeSlot,dseats , priceList,al,
				at,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);	
		TransactionType ttype = TransactionType.fromInt(0);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",dt,timeSlot,dseats,ttype);
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());		
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		transaction.setCustomerEvaluation(100);
		transaction.setType(TransactionType.fromInt(1));
		transaction.setCustomerNote("Good");
			
		//Test
		try{
			CarpoolDaoTransaction.updateTransactionInDatabase(transaction);
			transaction = CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId());			
			if(transaction.getCustomerEvaluation()==100&&transaction.getType().equals(TransactionType.fromInt(1))&&transaction.getProvider().equals(provider)&&transaction.getCustomer().equals(customer)&&transaction.getDeparture_location().equals(al)&&transaction.getArrival_location().equals(dl)&&transaction.getCustomerNote().equals("Good")&&transaction.getProviderNote().equals("pNote")&&transaction.getPaymentMethod().equals(p)&&transaction.getTotalPrice()==10){
				
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		
		
    }
    
    @Test
    public void testTransactionCleaner(){    	
    	CarpoolDaoBasic.clearBothDatabase();
		//Users
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User customer =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(customer);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		
		Calendar dt = Calendar.getInstance();
		dt.set(Calendar.HOUR_OF_DAY, 20);
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);
		
		Calendar dt2 = Calendar.getInstance();
		dt2.add(Calendar.DAY_OF_YEAR, -1);
		Calendar at2 = Calendar.getInstance();
		at2.add(Calendar.DAY_OF_YEAR, 0);
		
		Calendar dt3 = Calendar.getInstance();
		dt3.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at3 = Calendar.getInstance();
		at3.add(Calendar.DAY_OF_YEAR, 3);
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod p =Constants.paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		ArrayList<Integer> alist = new ArrayList<Integer>();
		alist.add(10);
		
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");	
	    
		int dseats = 4;
		int aseats = 4;
		//Messages
		Message message=new Message(provider.getUserId(),true
				, dl,dt,timeSlot,dseats , priceList,al,
				at,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message = CarpoolDaoMessage.addMessageToDatabase(message);	
		Message message2=new Message(provider.getUserId(),true
				, dl,dt2,timeSlot,dseats , priceList,al,
				at2,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message2 = CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(provider.getUserId(),true
				, dl,dt3,timeSlot,dseats , priceList,al,
				at3,timeSlot, aseats,alist,p,
				"test",  type, genderRequirement);
		message3 = CarpoolDaoMessage.addMessageToDatabase(message3);
		
		
		TransactionType ttype = TransactionType.departure;
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction.setState(transactionState.init);//This should pass the test
		Transaction transaction2 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction2.setState(transactionState.init);
		Transaction transaction3 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction3.setState(transactionState.finished);
		Transaction transaction4 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction4.setState(transactionState.aboutToStart);// This should pass the test
		Transaction transaction5 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
    	transaction5.setState(transactionState.init);
    	Transaction transaction6 = new Transaction(provider.getUserId(),customer.getUserId(),message3.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
    	transaction6.setState(transactionState.aboutToStart);
    	Transaction transaction7 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction7.setState(transactionState.aboutToStart);
		Transaction transaction8 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction8.setState(transactionState.finished);
		Transaction transaction9 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction9.setState(transactionState.init);
		Transaction transaction10 = new Transaction(provider.getUserId(),customer.getUserId(),message2.getMessageId(),p,"cNote","pNote",null,timeSlot,dseats,ttype);
		transaction10.setState(transactionState.finished);
		
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(transaction);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction2);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction3);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction4);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction5);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction6);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction7);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction8);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction9);
			CarpoolDaoTransaction.addTransactionToDatabase(transaction10);
			//Test
			TransactionCleaner.Clean();
			ArrayList<transactionState> list = new ArrayList<transactionState>();
			list.add(CarpoolDaoTransaction.getTransactionById(transaction.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction2.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction3.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction4.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction5.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction6.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction7.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction8.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction9.getTransactionId()).getState());
			list.add(CarpoolDaoTransaction.getTransactionById(transaction10.getTransactionId()).getState());
			if(list !=null && list.size()==10 && list.get(0)==transactionState.aboutToStart && list.get(1)==transactionState.init &&list.get(2)==transactionState.finished && list.get(3)==transactionState.finished && list.get(4)==transactionState.init && list.get(5)==transactionState.finished && list.get(6)==transactionState.finished&& list.get(7)==transactionState.finished && list.get(8)==transactionState.init&&list.get(9)==transactionState.finished){
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
    
    
    