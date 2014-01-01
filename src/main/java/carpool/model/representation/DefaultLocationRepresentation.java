package carpool.model.representation;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.exception.validation.ValidationException;
import carpool.interfaces.PseudoRepresentation;
import carpool.interfaces.PseudoValidatable;
import carpool.model.Location;

public class DefaultLocationRepresentation implements PseudoRepresentation, PseudoValidatable{
	
	private long id;
	private long referenceId;
	private Location location;
	private int radius;
	private String synonyms;
	
	
	public DefaultLocationRepresentation(Location location, int radius, String synonyms) {
		super();
		this.id = -1;
		this.referenceId = -1;
		this.location = location;
		this.radius = radius;
		this.synonyms = synonyms;
	}
	
	public DefaultLocationRepresentation(long id, long referenceId, Location location, int radius, String synonyms) {
		super();
		this.id = id;
		this.referenceId = referenceId;
		this.location = location;
		this.radius = radius;
		this.synonyms = synonyms;
	}

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public boolean equals(DefaultLocationRepresentation dlr){
		return this.id==dlr.getId() && this.referenceId==dlr.getReferenceId() && this.radius == dlr.getRadius() && this.synonyms.equals(dlr.getSynonyms());
	}
	@Override
	public boolean validate() throws ValidationException {
		return true;
	}

	@Override
	public String toString() {
		return "DefaultLocationRepresentation [id=" + id + ", referenceId=" + referenceId + ", location=" + location + ", radius=" + radius + ", synonyms=" + synonyms + "]";
	}

	@Override
	public String toSerializedString() {
		return this.getId()+"-"+this.getLocation().toString()+"-"+this.getRadius()+"-"+this.getSynonyms();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject jsonDefaultLocation = new JSONObject();
		try {
			jsonDefaultLocation.put("defaultId", this.getId());
			jsonDefaultLocation.put("radius", this.getRadius());
			jsonDefaultLocation.put("synonyms", this.getSynonyms());
			
			JSONObject jsonLocation = this.getLocation().toJSON();
			for (Object key : jsonLocation.keySet()){
				String strKey = (String)key;
				jsonDefaultLocation.put(strKey, jsonLocation.get(strKey));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonDefaultLocation;
	}

}
