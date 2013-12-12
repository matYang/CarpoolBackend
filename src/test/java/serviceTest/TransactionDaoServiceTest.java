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
import carpool.constants.Constants.TransactionType;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.dbservice.MessageDaoService;
import carpool.dbservice.TransactionDaoService;
import carpool.dbservice.UserDaoService;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.Transaction;
import carpool.model.User;

public class TransactionDaoServiceTest {

	@Test
	public void test() throws LocationNotFoundException {
		CarpoolDaoBasic.clearBothDatabase();
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
		//Users
        User provider =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new Location(departureLocation), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(provider);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		User customer =  new User("fangyuan", "fangyuanlucky", new Location(arrivalLocation), gender.both);
		
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
				, new Location(departureLocation),time,timeSlot,2 , priceList,new Location(arrivalLocation),
				time,timeSlot, 2,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		TransactionType tD = Constants.TransactionType.fromInt(1);
		Transaction transaction = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,tD);
		Transaction transaction2 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,tD);
		Transaction transaction3 = new Transaction(provider.getUserId(),customer.getUserId(),message.getMessageId(),paymentMethod,"cNote","pNote",time,timeSlot,1,tD);
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
