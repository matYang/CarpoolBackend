package serviceTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.EnumConfig.DayTimeSlot;
import carpool.configurations.EnumConfig.Gender;
import carpool.configurations.EnumConfig.MessageType;
import carpool.configurations.EnumConfig.PaymentMethod;
import carpool.dbservice.MessageDaoService;
import carpool.exception.PseudoException;
import carpool.exception.location.LocationNotFoundException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.representation.SearchRepresentation;

public class MessageDaoServiceTest {

	@Test
	public void test() throws LocationNotFoundException {
		CarpoolDaoBasic.clearBothDatabase();
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);	
		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt3 = DateUtility.getCurTimeInstance();	
		dt3.add(Calendar.DAY_OF_YEAR, -2);
		
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
		long dm = departureLocation.getMatch();
		long am = arrivalLocation.getMatch();
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(2);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		System.out.println();
		Message message=new Message(1,false, departureLocation,dt,timeSlot,1 , priceList,arrivalLocation,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		SearchRepresentation SR = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot,timeSlot);		
		
		try {
			
			assertTrue(MessageDaoService.primaryMessageSearch(SR, false, -1).size() > 0);
					
			SearchRepresentation SR_dul = new SearchRepresentation(SR.toSerializedString());
			assertTrue(MessageDaoService.primaryMessageSearch(SR_dul, false, -1).size() > 0);			
			
			
		} catch (PseudoException e) {
			e.printStackTrace();
			fail();
		}
	}

}
