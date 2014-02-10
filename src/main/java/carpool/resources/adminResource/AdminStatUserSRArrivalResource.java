package carpool.resources.adminResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.constants.CarpoolConfig;
import carpool.dbservice.admin.StatisticAnalysisOfDataService;
import carpool.factory.JSONFactory;
import carpool.resources.PseudoResource;

public class AdminStatUserSRArrivalResource extends PseudoResource{
	@Get
	public Representation statGetUserSRArrivalService(){
		ArrayList<Entry<Long,Integer>> map = new ArrayList<Entry<Long,Integer>>();
		JSONArray resultArr = new JSONArray();

		try {			

			map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.UserSRArrival);

			//For Test			
			//			HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();			
			//
			//			UserSRArrival.put((long) 2, 3);			
			//
			//			ArrayList<Entry<Long,Integer>> usra = new ArrayList<Entry<Long,Integer>>();
			//			usra.add(UserSRArrival.entrySet().iterator().next());			
			//			map.addAll(usra);					

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
