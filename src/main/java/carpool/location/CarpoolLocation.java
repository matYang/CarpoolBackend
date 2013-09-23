package carpool.location;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import carpool.exception.location.LocationException;

public class CarpoolLocation {
	
	public static enum LocationDepth {country, province, city, point}
	
	private CarpoolLocation parent;
	private LinkedHashMap<String, CarpoolLocation> subLocations;
	private LinkedHashMap<String, CarpoolLocation> neighbours;
	private String name;
	//TODO
	private int curDepth;
	private String postalCode;
	private String address;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private BigDecimal radius;
	
	@SuppressWarnings("unused")
	private CarpoolLocation(){
		this.parent = null;
		this.subLocations = null;
		this.neighbours = null;
		this.name = "";
		this.curDepth = -1;
		this.postalCode = "";
		this.address = "";
		this.latitude = BigDecimal.valueOf(0);
		this.longitude = BigDecimal.valueOf(0);
		this.radius = BigDecimal.valueOf(0);
	}
	
	public CarpoolLocation(String name, int depth, String postalCode, String address, BigDecimal latitude, BigDecimal longitude){
		this.parent = null;
		this.subLocations = null;
		this.neighbours = null;
		this.name = name;
		this.curDepth = depth;
		this.postalCode = postalCode;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = BigDecimal.valueOf(0);
	}
	
	public CarpoolLocation getParent(){
		return this.parent;
	}
	
	public CarpoolLocation getSubLocation(String name){
		return subLocations.get(name);
	}
	
	public LinkedHashMap<String, CarpoolLocation> getSubLocations(){
		return this.subLocations;
	}
	
	public CarpoolLocation getNeighbour(String name){
		return this.neighbours.get(name);
	}
	
	public LinkedHashMap<String, CarpoolLocation> getNeighbours(){
		return this.neighbours;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getDepth(){
		return this.curDepth;
	}
	
	public String getPostalCode(){
		return this.postalCode;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public BigDecimal getLat(){
		return this.latitude;
	}
	
	public BigDecimal getLon(){
		return this.longitude;
	}
	
	public BigDecimal getRadius(){
		return this.radius;
	}
	
	
	/**
	 * here only checks for sub areas
	 */
	public boolean hasLocation(String name){
		return this.subLocations.containsKey(name);
	}
	
	public boolean hasNeighbour(String name){
		return this.neighbours.containsKey(name);
	}
	
	
	public boolean equals(CarpoolLocation loc){
		boolean prep = true;
		if (this.curDepth > 0 && loc.getDepth() > 0){
			prep = this.parent.equals(loc.getParent());
		}
		//not comparing radius
		return prep && this.name.equalsIgnoreCase(loc.getName()) &&
				this.curDepth == loc.getDepth() &&
				this.postalCode.equalsIgnoreCase(loc.getPostalCode()) &&
				this.address.equalsIgnoreCase(loc.getAddress()) &&
				this.latitude.compareTo(loc.getLat()) == 0 &&
				this.longitude.compareTo(loc.getLon()) == 0;
	}
	
	//TODO
	public boolean isInRange(BigDecimal lat, BigDecimal lon){
		BigDecimal testDecimal = BigDecimal.valueOf(0);
		//if redius is not specified, do not compare
		if (this.getRadius().compareTo(testDecimal) == 0){
			return true;
		}
		return ( (lat.subtract(this.getLat())).pow(2) ).add( (lon.subtract(this.getLon())).pow(2) ) .compareTo( this.getRadius().pow(2) ) <= 0;
	}
	
	public boolean isInRange (CarpoolLocation loc){
		return this.hasLocation(loc.getName()) && this.isInRange(loc.getLat(), loc.getLon());
	}
	
	

	/******************************************
	 * 
	 *     GERY ZONE
	 *     The radius is left here for implementations of range search, it could be changed dynamically by user behavior or custom location data
	 *     implementations awaits
	 * 
	 ******************************************/
	
	public void setRadius(BigDecimal radius){
		this.radius = radius;
	}

	
	
	
	/******************************************
	 * 
	 *     DANGER ZONE
	 *     the following are Location's setters, which shall only be used by location loader when  all location data are first loaded
	 *     server will not accept HTTP connections before all locations are loaded, server will not load locations from location file once HTTP connection opens
	 *     all methods below have default visibility, locationLoader placed in same package has access
	 * 
	 ******************************************/
	
	void setParent(CarpoolLocation parent, Object accessor) throws LocationException{
		if (accessor instanceof CarpoolLocationLoader){
			this.parent = parent;
		}
		else{
			throw new LocationException("Access Violation");
		}
	}
	
	void addSubLocation(CarpoolLocation loc, Object accessor)throws LocationException{
		if (accessor instanceof CarpoolLocationLoader){
			if (this.subLocations.containsKey(loc.getName())){
				throw new LocationException("SubLocation Already Exist");
			}
			this.subLocations.put(loc.getName(), loc);
		}
		else{
			throw new LocationException("Access Violation");
		}
	}
	
	void setSubLocations( LinkedHashMap<String, CarpoolLocation> subLocations, Object accessor)throws LocationException{
		if (accessor instanceof CarpoolLocationLoader){
			this.subLocations = subLocations;
		}
		else{
			throw new LocationException("Access Violation");
		}
	}
	
	void addNeighbour(CarpoolLocation loc, Object accessor)throws LocationException{
		if (accessor instanceof CarpoolLocationLoader){
			if (this.neighbours.containsKey(loc.getName())){
				throw new LocationException("Neighbour Already Exist");
			}
			this.neighbours.put(loc.getName(), loc);
		}
		else{
			throw new LocationException("Access Violation");
		}
	}
	
	void setNeighbours( LinkedHashMap<String, CarpoolLocation> neighbours, Object accessor)throws LocationException{
		if (accessor instanceof CarpoolLocationLoader){
			this.neighbours = neighbours;
		}
		else{
			throw new LocationException("Access Violation");
		}
	}
	
}
