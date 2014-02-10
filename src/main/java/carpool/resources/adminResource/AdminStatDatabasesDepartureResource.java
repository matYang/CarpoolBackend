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

public class AdminStatDatabasesDepartureResource extends PseudoResource{
	@Get
	public Representation statGetDatabasesDepartureService(){
		ArrayList<Entry<Long,Integer>> map = new ArrayList<Entry<Long,Integer>>();
		JSONArray resultArr = new JSONArray();

		try {			
			
			map = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.DatabasesDeparture);

			//For Test			
//			HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();			
//
//			DatabasesDeparture.put((long) 3, 2);			
//
//			ArrayList<Entry<Long,Integer>> dd = new ArrayList<Entry<Long,Integer>>();
//			dd.add(DatabasesDeparture.entrySet().iterator().next());			
//			map.addAll(dd);					

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
