package serviceTest;

import static org.junit.Assert.*;

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
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.dbservice.MessageDaoService;
import carpool.dbservice.TransactionDaoService;
import carpool.dbservice.UserDaoService;
import carpool.exception.ValidationException;
import carpool.model.Message;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

public class TransactionDaoServiceTest {

	@Test
	public void test() {
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
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,2 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		TransactionDirection tD = Constants.TransactionDirection.fromInt(1);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",tD,time,timeSlot,1,time,timeSlot,1);
		Transaction transaction2 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",tD,time,timeSlot,1,time,timeSlot,1);
		Transaction transaction3 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",tD,time,timeSlot,1,time,timeSlot,1);
		//Test
		try{
			transaction = TransactionDaoService.createNewTransaction(transaction);
			message = MessageDaoService.getMessageById(transaction.getMessageId());
			assertTrue(message.getDeparture_seatsBooked() == 1 || message.getArrival_seatsBooked() == 1);
			transaction = TransactionDaoService.getUserTransactionById(transaction.getProviderId(), provider.getUserId());
			
			transaction2 = TransactionDaoService.createNewTransaction(transaction2);
			//message = MessageDaoService.getMessageById(transaction.getMessageId());
			//assertTrue(message.getDeparture_seatsBooked() == 1 || message.getArrival_seatsBooked() == 1);
			TransactionDaoService.cancelTransaction(transaction2.getTransactionId(), customer.getUserId());
			transaction3 = TransactionDaoService.createNewTransaction(transaction3);
			
			ArrayList<Transaction> transactions = TransactionDaoService.getAllTransactions();
			assertTrue(transactions.size() == 3);
			
			CarpoolDaoTransaction.getAllTranscations();
			
			ArrayList<Transaction> transactions2 = MessageDaoService.getTransactionByMessageId(message.getMessageId());
			assertTrue(transactions2.size() == 3);
			
			ArrayList<Transaction> transactions3 = UserDaoService.getTransactionByUserId(provider.getUserId());
			assertTrue(transactions3.size() == 3);
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		
		
		
	}

}
