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

public class AdminStatDatabasesArrivalResource extends PseudoResource{
	@Get
	public Representation statGetDatabasesArrivalService(){
		ArrayList<Entry<Long,Integer>> map = new ArrayList<Entry<Long,Integer>>();
		JSONArray resultArr = new JSONArray();

		try {			

			map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.DatabasesArrival);
			//
			//			//For Test			
			//			HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();			
			//
			//			DatabasesArrival.put((long) 4, 1);			
			//
			//			ArrayList<Entry<Long,Integer>> da = new ArrayList<Entry<Long,Integer>>();
			//			da.add(DatabasesArrival.entrySet().iterator().next());			
			//			map.addAll(da);					

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
