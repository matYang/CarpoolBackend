package serviceTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.common.DebugLog;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.dbservice.MessageDaoService;
import carpool.exception.PseudoException;
import carpool.model.Message;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;

public class MessageDaoServiceTest {

	@Test
	public void test() {
		
		Calendar dt = Calendar.getInstance();		
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);	
		Calendar dt2 = Calendar.getInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt3 = Calendar.getInstance();	
		dt3.add(Calendar.DAY_OF_YEAR, -2);
		
		//Location
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Waterloo_DC library_3");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_DC library_3");	
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		SearchRepresentation SR = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation sr = new SearchRepresentation("false+Canada_Ontario_Waterloo_DC Library_3+Canada_Ontario_Waterloo_DC Library_3+2013-10-21 21:47:50+2013-10-21 22:01:24+0+0+0");
		
		try {
			Message message=new Message(1,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
			CarpoolDaoMessage.addMessageToDatabase(message);
			assertTrue(MessageDaoService.primaryMessageSearch(SR, false, -1).size() > 0);
			DebugLog.d(SR.toSerializedString());
			
			SearchRepresentation SR_dul = new SearchRepresentation(SR.toSerializedString());
			assertTrue(MessageDaoService.primaryMessageSearch(SR_dul, false, -1).size() > 0);
			
			assertTrue(MessageDaoService.primaryMessageSearch(sr, false, -1).size() > 0);
			
		} catch (PseudoException e) {
			e.printStackTrace();
			fail();
		}
	}

}
