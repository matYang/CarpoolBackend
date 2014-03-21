package carpool.test.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.ServerConfig;
import carpool.configurations.EnumConfig.DayTimeSlot;
import carpool.configurations.EnumConfig.Gender;
import carpool.configurations.EnumConfig.MessageState;
import carpool.configurations.EnumConfig.MessageType;
import carpool.configurations.EnumConfig.PaymentMethod;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;

public class CarpoolSearchTest {

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
		
		User user = new User("lol@me.com", "32121", departureLocation, Gender.both);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e1) {
			e1.printStackTrace();
			fail();
		}
		
		//Date
		Calendar dt = DateUtility.getCurTimeInstance();
		Calendar dt1 = DateUtility.getCurTimeInstance();	
		dt1.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -2);
		
		
		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at1 = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 2);
		Calendar at2 = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 3);
		

		
		Calendar dt_sameDay = DateUtility.getCurTimeInstance();

		Calendar at_sameDay = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);
		
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(new Integer(1));
		PaymentMethod p =null;
		p = PaymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		Gender genderRequirement = Gender.fromInt(0);
		MessageState state = MessageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int userId = 1;

		
		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,p,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Message2
		Message message2=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 1,priceList,p,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		//Message3
		Message message3=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 1,priceList,p,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);

		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,new Location(departureLocation).getMatch(),new Location(arrivalLocation).getMatch(),dt_sameDay,at_sameDay,type,timeSlot,timeSlot);
		DebugLog.d(SR.toSerializedString());
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoMessage.searchMessage(SR);
			if(mlist !=null && mlist.size()==3 && mlist.get(0).equals(message) && mlist.get(1).equals(message2)&&mlist.get(2).equals(message3)){
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

}
