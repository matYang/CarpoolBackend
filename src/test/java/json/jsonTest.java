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
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_Toronto_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_Waterloo_2");		
		
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
		Message message = new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		
		
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
