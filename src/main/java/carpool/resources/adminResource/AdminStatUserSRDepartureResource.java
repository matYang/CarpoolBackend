package carpool.resources.adminResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.constants.CarpoolConfig;
import carpool.dbservice.admin.StatisticAnalysisOfDataService;
import carpool.factory.JSONFactory;
import carpool.resources.PseudoResource;

public class AdminStatUserSRDepartureResource extends PseudoResource{

	@Get
	public Representation statGetUserSRDepartureService(){
		ArrayList<Entry<Long,Integer>> map = new ArrayList<Entry<Long,Integer>>(); 		
		JSONArray resultArr = new JSONArray();

		try {			

			map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.UserSRDeparture);

			//For Test			
			//			HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
			//			UserSRDeparture.put((long) 1, 4);
			//			ArrayList<Entry<Long,Integer>> usrd = new ArrayList<Entry<Long,Integer>>();
			//			usrd.add(UserSRDeparture.entrySet().iterator().next());			
			//			map.addAll(usrd);					

			resultArr = JSONFactory.toJSONArray(map);			
		}	

		catch(Exception e){
			return this.doException(e);
		}

		Representation result =  new JsonRepresentation(resultArr);
		this.addCORSHeader();
		return result;
	}
}
