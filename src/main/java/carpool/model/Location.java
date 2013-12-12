package carpool.model;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.exception.validation.ValidationException;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;

public class Location implements PseudoModel, PseudoValidatable, Comparable<Location>{

	/******
	 * the following stores location's informations
	 ******/
	private long id;
	private String province;
	private String city;
	private String region;
	private String pointName;
	private String pointAddress;
	private Double lat;
	private Double lng;
	private long match;

	/*****
	 * protected constructor to disallow raw initialization and serialization, but allow easier testing
	 *****/
	protected Location(){}

	public Location(String province,String city,String region,String pointName,String pointAddress,Double lat,Double lng,long match){
		super();
		this.id = -1;
		this.province = province;
		this.city = city;
		this.region = region;
		this.pointName = pointName;
		this.pointAddress = pointAddress;
		this.lat = lat;
		this.lng = lng;
		this.match = match;
	}
	/*****
	 * full constructor used for SQL retrieval
	 *****/
	public Location(long id,String province,String city,String region,String pointName,String pointAddress,Double lat,Double lng,long match){
		super();
		this.id = id;
		this.province = province;
		this.city = city;
		this.region = region;
		this.pointName = pointName;
		this.pointAddress = pointAddress;
		this.lat = lat;
		this.lng = lng;
		this.match = match;
	}
	
	public Location(JSONObject jsonObject) {
		super();
		this.id = -1;
		this.province = jsonObject.getString("province");
		this.city = jsonObject.getString("city");
		this.region = jsonObject.getString("region");
		this.pointName = jsonObject.getString("pointName");
		this.pointAddress = jsonObject.getString("pointAddress");
		this.lat = jsonObject.getDouble("lat");
		this.lng = jsonObject.getDouble("lng");
		this.match = jsonObject.getLong("match_Id");
	}

	public Location(Location location) {
		super();
		this.id = location.getId();
		this.province = location.getProvince();
		this.city = location.getCity();
		this.region = location.getRegion();
		this.pointName = location.getPointName();
		this.pointAddress = location.getPointAddress();
		this.lat = location.getLat();
		this.lng = location.getLng();
		this.match = location.getMatch();
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getPointAddress() {
		return pointAddress;
	}

	public void setPointAddress(String pointAddress) {
		this.pointAddress = pointAddress;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public long getMatch() {
		return match;
	}

	public void setMatch(long match) {
		this.match = match;
	}

	public boolean equals(Location location){
		try{
			return location != null && 
					this.id==location.getId()&&
					this.province.equals(location.getProvince())&&
					this.city.equals(location.getCity())&&
					this.region.equals(location.getRegion())&&
					this.pointName.equals(location.getPointName())&&
					this.pointAddress.equals(location.getPointAddress())&&
					this.lat.equals(location.getLat())&&
					this.lng.equals(location.getLng())&&
					this.match==location.getMatch();
		}catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int compareTo(Location o) {
		return 0;
	}

	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject jsonLocation = new JSONObject();
		try {
			jsonLocation.put("id", this.getId());
			jsonLocation.put("province", this.getProvince());
			jsonLocation.put("city", this.getCity());
			jsonLocation.put("region", this.getRegion());
			jsonLocation.put("pointName",this.getPointName() == null ? "" : this.getPointName());
			jsonLocation.put("pointAddress", this.getPointAddress() == null ? "": this.getPointAddress());
			jsonLocation.put("lat", this.getLat());
			jsonLocation.put("lng", this.getLng());			
			jsonLocation.put("match_Id", this.getMatch());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonLocation;
	}
	


}
