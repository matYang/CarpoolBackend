package carpool.test.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoTransaction;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.exception.ValidationException;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

public class CarpoolTransactionTest {
    @Test
	public void testAddTransaction(){
		CarpoolDaoBasic.clearBothDatabase();
		//Users
        User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User user2 =  new User("fangyuan", "fangyuanlucky", new LocationRepresentation ("primary","custom",1));
		
		try {
			CarpoolDaoUser.addUserToDatabase(user2);
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
		Message message=new Message(user.getUserId(),false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,1 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot, 1,priceList,paymentMethod,
				"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		
		
		//Test
		try{
			CarpoolDaoTransaction.addTransactionToDatabase(user.getUserId(), user2.getUserId(),message.getMessageId(), 4);
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
