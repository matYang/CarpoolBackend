package carpool.model;
import org.json.JSONException;
import org.json.JSONObject;
import carpool.exception.ValidationException;
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

	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the pointName
	 */
	public String getPointName() {
		return pointName;
	}

	/**
	 * @param pointName the pointName to set
	 */
	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	/**
	 * @return the pointAddress
	 */
	public String getPointAddress() {
		return pointAddress;
	}

	/**
	 * @param pointAddress the pointAddress to set
	 */
	public void setPointAddress(String pointAddress) {
		this.pointAddress = pointAddress;
	}

	/**
	 * @return the lat
	 */
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lng
	 */
	public Double getLng() {
		return lng;
	}

	/**
	 * @param lng the lng to set
	 */
	public void setLng(Double lng) {
		this.lng = lng;
	}

	/**
	 * @return the match
	 */
	public long getMatch() {
		return match;
	}

	/**
	 * @param match the match to set
	 */
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean validate() throws ValidationException {
		// TODO Auto-generated method stub
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
			jsonLocation.put("pointName",this.getPointName()==null?new JSONObject(""):this.getPointName() );
			jsonLocation.put("pointAddress", this.getPointAddress()==null?new JSONObject(""):this.getPointAddress());
			jsonLocation.put("lat", this.getLat());
			jsonLocation.put("lng", this.getLng());			
			jsonLocation.put("match_Id", this.getMatch());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonLocation;
	}
	
	public Location(JSONObject jsonObject) {
		super();
		this.id = jsonObject.getInt("id");
		this.province = jsonObject.getString("province");
		this.city = jsonObject.getString("city");
		this.region = jsonObject.getString("region");
		this.pointName = jsonObject.getString("pointName");
		this.pointAddress = jsonObject.getString("pointAddress");
		this.lat = jsonObject.getDouble("lat");
		this.lng = jsonObject.getDouble("lng");
		this.match = jsonObject.getLong("match_Id");
	}

	public Location(Location departureLocation) {
		super();
		this.id = departureLocation.getId();
		this.province = departureLocation.getProvince();
		this.city = departureLocation.getCity();
		this.region = departureLocation.getRegion();
		this.pointName = departureLocation.getPointName();
		this.pointAddress = departureLocation.getPointAddress();
		this.lat = departureLocation.getLat();
		this.lng = departureLocation.getLng();
		this.match = departureLocation.getMatch();
	}

}
