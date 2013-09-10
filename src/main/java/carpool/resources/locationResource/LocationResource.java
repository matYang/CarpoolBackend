package carpool.resources.locationResource;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.JSONFactory;
import carpool.dbservice.*;
import carpool.mappings.MappingManager;
import carpool.model.*;
import carpool.resources.userResource.UserResource;




public class LocationResource extends ServerResource{

	
	/**
	 * take province and city from query parameter
	 * if there is no province, return all provinces
	 * if there is province but no city, return all cities under that province
	 * if there are both province and city, return all schools under that city
	 * @return
	 */
	@Get
	public Representation searchLocation() {
		//get query parameter _province _city
		String province = getQuery().getValues("province");
		String city = getQuery().getValues("city");
		String region = getQuery().getValues("region");
		String ignoreRegion = getQuery().getValues("ignoreRegion");
		ArrayList<String> searchResult = null;
		ArrayList<JSONObject> searchResult_b = null;
		
		if (!Common.isEntryNull(province) && !Common.isEntryNull(city) && !Common.isEntryNull(ignoreRegion)){
			try{
				searchResult = MappingManager.getAllSchools(province, city);
			}
			catch(Exception e){
				e.printStackTrace();
				Common.d("invalid GETSCHOOL location query with parameter province: " + province + " city: " + city + " region ignored: " + ignoreRegion);
			}
		}
		else if (!Common.isEntryNull(province) && !Common.isEntryNull(city) && !Common.isEntryNull(region)){
			try{
				searchResult = MappingManager.getAllSchools(province, city, region);
			}
			catch(Exception e){
				e.printStackTrace();
				Common.d("invalid GETSCHOOL location query with parameter province: " + province + " city: " + city + " region " + region);
			}
		}
		else if (!Common.isEntryNull(province) && !Common.isEntryNull(city)){
			try{
				searchResult_b = MappingManager.getRegionUniversity(province, city);
			}
			catch(Exception e){
				e.printStackTrace();
				Common.d("invalid GETSCHOOL location query with parameter province: " + province + " city: " + city + " region " + region);
			}
		}
		else if (!Common.isEntryNull(province) && Common.isEntryNull(city)){
			try{
				searchResult = MappingManager.getAllCity(province);
			}
			catch(Exception e){
				e.printStackTrace();
				Common.d("invalid GETCITY location query with parameter province: " + province);
			}
		}
		else if (Common.isEntryNull(province) && Common.isEntryNull(city)){
			searchResult = 	MappingManager.getAllProvince();
		}
		else{
			Common.d("invalid location query format with parameter province: " + province + " city: " + city);
		}

		
		JSONArray jsonArray = searchResult != null ? new JSONArray(searchResult) : new JSONArray (searchResult_b);
		/*try{
			for (int i = 0; i < jsonArray.length(); i++){
				jsonArray.getJSONObject(i).remove("messageIdentifier");
			}
		}
		catch (JSONException e){
			e.printStackTrace();
		}*/
		
		Representation result = new JsonRepresentation(jsonArray);
		//set status
		setStatus(Status.SUCCESS_OK);
		
		try {
			Common.d(result.getText() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		Common.d("@Get::resources::LocationResource query parameters: province " + province + " city         " + city);
		
		
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}


	
}

