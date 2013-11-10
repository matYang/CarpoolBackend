package SRStorageTest;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.StoreSearchHistoryTask;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.exception.ValidationException;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;

public class SRStorageTest {
 
@Test	
public void testSRStorage(){
	CarpoolDaoBasic.clearBothDatabase();
	//Users
	User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
	
	try {
		CarpoolDaoUser.addUserToDatabase(user);
	} catch (ValidationException e) {			
		e.printStackTrace();
	}	
   User user2 =  new User("fangyuan&lucyWang", "xiongchuhan@hotmail.com", new LocationRepresentation ("primary","custom",1), gender.both);
	
	try {
		CarpoolDaoUser.addUserToDatabase(user2);
	} catch (ValidationException e) {			
		e.printStackTrace();
	}
	//Date
	Calendar dt = Calendar.getInstance();		
	Calendar at = Calendar.getInstance();
	at.add(Calendar.DAY_OF_YEAR, 1);	
	Calendar at2 = Calendar.getInstance();	
	at2.add(Calendar.DAY_OF_YEAR, -1);	
	Calendar dt2 = Calendar.getInstance();	
	dt2.add(Calendar.DAY_OF_YEAR, -2);	
	//Location
	LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
	LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");			
	messageType type = messageType.fromInt(0);	
	DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
	//SRs
	SearchRepresentation sr = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot,timeSlot);
	SearchRepresentation sr2 = new SearchRepresentation(true,al,dl,dt2,at2,type,timeSlot,timeSlot);
	//Execute	
	StoreSearchHistoryTask ssht = new StoreSearchHistoryTask(sr,user.getUserId());
	ExecutorProvider.executeRelay(ssht);
	ssht = new StoreSearchHistoryTask(sr2,user2.getUserId());
	ExecutorProvider.executeRelay(ssht);
	
}
	
	
	
}
