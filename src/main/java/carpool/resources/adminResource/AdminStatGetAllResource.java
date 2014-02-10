package carpool.resources.adminResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.aws.AwsMain;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.constants.Constants.AdminRoutineAction;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.Gender;
import carpool.constants.Constants.MessageType;
import carpool.constants.Constants.PaymentMethod;
import carpool.constants.Constants.UserState;
import carpool.dbservice.admin.AdminService;
import carpool.dbservice.admin.StatisticAnalysisOfDataService;
import carpool.exception.PseudoException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.factory.JSONFactory;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;
import carpool.resources.PseudoResource;

public class AdminStatGetAllResource extends PseudoResource{

	@Get
	public Representation statGetAllService(){

		HashMap<String,ArrayList<Entry<Long,Integer>>> entireMap = new HashMap<String,ArrayList<Entry<Long,Integer>>>(); 	
		JSONArray resultArr = new JSONArray();

		try {			

			entireMap = StatisticAnalysisOfDataService.GetTheEntireMap();

			//For Test			
			//			HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
			//			HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();
			//			HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();
			//			HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();
			//
			//			UserSRDeparture.put((long) 1, 4);
			//			UserSRArrival.put((long)2, 3);
			//			DatabasesDeparture.put((long)3, 2);
			//			DatabasesArrival.put((long)4,1);
			//
			//			ArrayList<Entry<Long,Integer>> usrd = new ArrayList<Entry<Long,Integer>>();
			//			usrd.add(UserSRDeparture.entrySet().iterator().next());
			//			ArrayList<Entry<Long,Integer>> usra = new ArrayList<Entry<Long,Integer>>();
			//			usra.add(UserSRArrival.entrySet().iterator().next());
			//			ArrayList<Entry<Long,Integer>> dd = new ArrayList<Entry<Long,Integer>>();
			//			dd.add(DatabasesDeparture.entrySet().iterator().next());
			//			ArrayList<Entry<Long,Integer>> da = new ArrayList<Entry<Long,Integer>>();
			//			da.add(DatabasesArrival.entrySet().iterator().next());
			//
			//			entireMap.put(CarpoolConfig.UserSRDeparture, usrd);
			//			entireMap.put(CarpoolConfig.UserSRArrival, usra);
			//			entireMap.put(CarpoolConfig.DatabasesDeparture, dd);
			//			entireMap.put(CarpoolConfig.DatabasesArrival, da);		

			resultArr = JSONFactory.toJSON(entireMap);			
		}	

		catch(Exception e){
			return this.doException(e);
		}

		Representation result =  new JsonRepresentation(resultArr);
		this.addCORSHeader();
		return result;
	}

}
