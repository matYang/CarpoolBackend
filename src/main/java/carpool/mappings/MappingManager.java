package carpool.mappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.model.Location;


public class MappingManager {

    private static MappingBase getProvinceMappings(String province) {
        return Constants.ALL_PROVINCE.getSubAreaMappings(province);
    }

    private static MappingBase getCityMappings(String province, String city) {
        MappingBase provinceMappings = getProvinceMappings(province);
        if (provinceMappings != null) {
            return provinceMappings.getSubAreaMappings(city);
        } else {
            return null;
        }
    }

    private static MappingBase getRegionMappings(String province, String city, String region) {
        MappingBase cityMappings = getCityMappings(province, city);
        if (cityMappings != null) {
            return cityMappings.getSubAreaMappings(region);
        } else {
            return null;
        }
    }

    public static ArrayList<String> getAllProvince() {
        return Constants.ALL_PROVINCE.getSortedProvinces();
        
    }
    
    /**Modified by Matthew to sort the city regarding the number of universities as default, leaving this crash-able for debugging**/
    public static ArrayList<String> getAllCity(String province) {
        MappingBase provinceMappings = getProvinceMappings(province);
        if (provinceMappings != null) {
        	ArrayList<String> searchResult = new ArrayList<String>(provinceMappings.getAllSubArea());
        	//watch out
			String temp = "";
			for (int i = 0; i < searchResult.size(); i ++){
				for (int j = i+1; j < searchResult.size(); j++){
					if (getAllSchools(province, searchResult.get(i)).size() <= getAllSchools(province, searchResult.get(j)).size()){
						temp = searchResult.get(i);
						searchResult.set(i, searchResult.get(j));
						searchResult.set(j, temp);
					}

				}
			}
				
            return searchResult;
        } 
        else {
            return null;
        }
    }
    
    public static ArrayList<JSONObject> getRegionUniversity(String province, String city) {
    	ArrayList<String> regions = getAllRegion(province, city);
    	ArrayList<JSONObject> groups = new ArrayList<JSONObject>();
    	if (regions == null){
    		return groups;
    	}
    	for (int i = 0; i < regions.size(); i++){
    		ArrayList<String> universities = getAllSchools(province, city, regions.get(i));
    		JSONArray uniArr = new JSONArray(universities);
    		JSONObject group = new JSONObject();
    		try {
				group.put(regions.get(i), uniArr);
				groups.add(group);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	
    	return groups;
    }

    public static ArrayList<String> getAllRegion(String province, String city) {
        MappingBase cityMappings = getCityMappings(province, city);
        if (cityMappings != null) {
            return new ArrayList<String>(cityMappings.getAllSubArea());
        } else {
            return null;
        }
    }

    // Searching purpose
    public static ArrayList<String> getAllSchools(String province, String city, String region) {
        MappingBase regionMappings = getRegionMappings(province, city, region);
        if (regionMappings != null) {
            return new ArrayList<String>(regionMappings.getAllSubArea());
        } else {
            return null;
        }
    }

    // populating purpose
    public static ArrayList<String> getAllSchools(String province, String city) {
        MappingBase cityMapping = getCityMappings(province, city);
        if (cityMapping == null){
            return null;
        }
        ArrayList<String> retVal = new ArrayList<String>();
        for (String region : cityMapping.getAllSubArea()) {
            MappingBase regionMapping = cityMapping.getSubAreaMappings(region);
            if (regionMapping != null){
                retVal.addAll(regionMapping.getAllSubArea());
            }
        }
        Collections.sort(retVal, new Comparator<String>() {
            public int compare(String schoolA, String schoolB) {
                return Collator.getInstance(Locale.CHINESE).compare(schoolA,schoolB);
            }
        });
        return retVal;
    }

    public static boolean isLocationVaild(Location location) {
        String province = location.getProvince();
        String city = location.getCity();
        String school = location.getSchool();
        
        MappingBase provinceMappings = getProvinceMappings(province);
        if(provinceMappings!=null){
            MappingBase cityMappings = getCityMappings(province, city);
            if(cityMappings!=null){
                for(String region : getAllRegion(province, city)){
                    MappingBase regionMappings = getRegionMappings(province, city, region);
                    if(regionMappings.getAllSubArea().contains(school)){
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    public static String determineRegionFromLocation(Location location){
        if(location.getRegion()!="NULL"){
            DebugLog.d("Location has already got region which is " + location.getRegion());
            return location.getRegion();
        }
        if(!isLocationVaild(location)){
            DebugLog.d("Location is not vaild.");
            DebugLog.d(""+location.getSchool());
            DebugLog.d(""+location.getProvince());
            DebugLog.d(""+location.getCity());
            return null;
        }
        for(String region : getAllRegion(location.getProvince(), location.getCity())){
            MappingBase regionMappings = getRegionMappings(location.getProvince(), location.getCity(), region);
            if(regionMappings.getAllSubArea().contains(location.getSchool())){
                return regionMappings.getAreaName();
            }
        }
        DebugLog.d("Can not determine location.");
        return location.getRegion();
    }
    
    public static ArrayList<Location> getLocationsFromUniversities(ArrayList<String> universities){
    	ArrayList<Location> retVal = new ArrayList<Location>();
    	for(String university : universities){
    		for(String provinceName : getAllProvince()){
    			MappingBase province = getProvinceMappings(provinceName);
    			for(String cityName : province.getAllSubArea()){
    				MappingBase city = getCityMappings(provinceName, cityName);
    				for(String regionName : city.getAllSubArea()){
    					MappingBase region = getRegionMappings(provinceName, cityName, regionName);
    					for(String school : region.getAllSubArea()){
    						if(school.equals(university)){
    							retVal.add(new Location(provinceName,cityName,regionName,school));
    						}
    					}
    				}
    			}
    		}
    	}
		return retVal;
    }

}
