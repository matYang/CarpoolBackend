package json;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DebugLog;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.factory.JSONFactory;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.representation.LocationRepresentation;

public class jsonTest {

	@Test
	public void test() {
		CarpoolDaoBasic.clearBothDatabase();
		//Date
		Calendar dt = Calendar.getInstance();		
		Calendar at = Calendar.getInstance();			
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
		
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		int messageId=-1;
		int userId=-1;
		
		//Message	
		Message message = new Message(userId,false, departureLocation,dt,timeSlot,1 , priceList,arrivalLocation,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		
		
		assertTrue(message.toJSON() != null);
		assertTrue(JSONFactory.toJSON(message) != null);
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(message.toJSON());
		jsonArray.put(JSONFactory.toJSON(message));
		try {
			assertTrue(jsonArray.get(0) != null);
			assertTrue(jsonArray.get(1) != null);
			DebugLog.d(jsonArray.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			fail();
		}
	}

}
