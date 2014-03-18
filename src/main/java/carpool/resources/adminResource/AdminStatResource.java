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
import carpool.configurations.CarpoolConfig;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.AdminRoutineAction;
import carpool.configurations.EnumConfig.DayTimeSlot;
import carpool.configurations.EnumConfig.Gender;
import carpool.configurations.EnumConfig.MessageType;
import carpool.configurations.EnumConfig.PaymentMethod;
import carpool.configurations.EnumConfig.UserState;
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

public class AdminStatResource extends PseudoResource{

	@Get
	public Representation statService(){

		HashMap<String,ArrayList<Entry<Long,Integer>>> entireMap = new HashMap<String,ArrayList<Entry<Long,Integer>>>(); 	
		ArrayList<Entry<Long,Integer>> map = new ArrayList<Entry<Long,Integer>>();
		JSONArray resultArr = new JSONArray();

		try {			
			if(this.getAttribute("type").equals("all")){
				entireMap = StatisticAnalysisOfDataService.GetTheEntireMap();
				//For Test			
				//							HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
				//							HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();
				//							HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();
				//							HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();
				//				
				//							UserSRDeparture.put((long) 1, 4);
				//							UserSRArrival.put((long)2, 3);
				//							DatabasesDeparture.put((long)3, 2);
				//							DatabasesArrival.put((long)4,1);
				//				
				//							ArrayList<Entry<Long,Integer>> usrd = new ArrayList<Entry<Long,Integer>>();
				//							usrd.add(UserSRDeparture.entrySet().iterator().next());
				//							ArrayList<Entry<Long,Integer>> usra = new ArrayList<Entry<Long,Integer>>();
				//							usra.add(UserSRArrival.entrySet().iterator().next());
				//							ArrayList<Entry<Long,Integer>> dd = new ArrayList<Entry<Long,Integer>>();
				//							dd.add(DatabasesDeparture.entrySet().iterator().next());
				//							ArrayList<Entry<Long,Integer>> da = new ArrayList<Entry<Long,Integer>>();
				//							da.add(DatabasesArrival.entrySet().iterator().next());
				//				
				//							entireMap.put(CarpoolConfig.UserSRDeparture, usrd);
				//							entireMap.put(CarpoolConfig.UserSRArrival, usra);
				//							entireMap.put(CarpoolConfig.DatabasesDeparture, dd);
				//							entireMap.put(CarpoolConfig.DatabasesArrival, da);

				resultArr = JSONFactory.toJSON(entireMap);	
			}else if(this.getAttribute("type").equals("usrd")){
				map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.UserSRDeparture);

				//For Test			
				//							HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
				//							UserSRDeparture.put((long) 1, 4);
				//							ArrayList<Entry<Long,Integer>> usrd = new ArrayList<Entry<Long,Integer>>();
				//							usrd.add(UserSRDeparture.entrySet().iterator().next());			
				//							map.addAll(usrd);					

				resultArr = JSONFactory.toJSONArray(map);	
			}else if(this.getAttribute("type").equals("usra")){
				map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.UserSRArrival);

				//For Test			
				//							HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();			
				//				
				//							UserSRArrival.put((long) 2, 3);			
				//				
				//							ArrayList<Entry<Long,Integer>> usra = new ArrayList<Entry<Long,Integer>>();
				//							usra.add(UserSRArrival.entrySet().iterator().next());			
				//							map.addAll(usra);					

				resultArr = JSONFactory.toJSONArray(map);
			}else if(this.getAttribute("type").equals("dd")){
				map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.DatabasesDeparture);

				//For Test			
				//								HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();			
				//				
				//								DatabasesDeparture.put((long) 3, 2);			
				//				
				//								ArrayList<Entry<Long,Integer>> dd = new ArrayList<Entry<Long,Integer>>();
				//								dd.add(DatabasesDeparture.entrySet().iterator().next());			
				//								map.addAll(dd);					

				resultArr = JSONFactory.toJSONArray(map);
			}else if(this.getAttribute("type").equals("da")){
				map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.DatabasesArrival);

				//For Test			
				//							HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();			
				//				
				//							DatabasesArrival.put((long) 4, 1);			
				//				
				//							ArrayList<Entry<Long,Integer>> da = new ArrayList<Entry<Long,Integer>>();
				//							da.add(DatabasesArrival.entrySet().iterator().next());			
				//							map.addAll(da);					

				resultArr = JSONFactory.toJSONArray(map);
			}

		}	

		catch(Exception e){
			return this.doException(e);
		}

		Representation result =  new JsonRepresentation(resultArr);
		this.addCORSHeader();
		return result;
	}

}
