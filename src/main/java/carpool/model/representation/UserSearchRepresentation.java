package carpool.model.representation;

import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.gender;


public class UserSearchRepresentation {

	private String name;
	private gender gender;
	private LocationRepresentation location;
	
	
	public UserSearchRepresentation(String name, gender g,LocationRepresentation l){
		this.name = name;
		this.gender = g;
		this.location = l;
	}
	
	public	UserSearchRepresentation(String serializedUSR){
		String[] strs = serializedUSR.split(CarpoolConfig.urlSeperatorRegx);
		this.name = strs[0];
		this.gender = carpool.constants.Constants.gender.fromInt(Integer.parseInt(strs[1]));
		this.location = new LocationRepresentation(strs[2]);
	}
	
	public String getName(){
		return this.name;
		
	}
	
	public gender getGender(){
		return this.gender;
	}
	public LocationRepresentation getLocation(){
		return this.location;
	}
	
	public String toSerializedString(){
		return this.name + CarpoolConfig.urlSeperator + this.gender.code + CarpoolConfig.urlSeperator + this.location.toSerializedString();
	}
	
}
