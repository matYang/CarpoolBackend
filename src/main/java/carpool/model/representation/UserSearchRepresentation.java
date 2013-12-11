package carpool.model.representation;

import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.gender;
import carpool.model.Location;


public class UserSearchRepresentation {

	private String name;
	private gender gender;
	private long location_Id;
	
	
	public UserSearchRepresentation(String name, gender g,long l){
		this.name = name;
		this.gender = g;
		this.location_Id = l;
	}
	
	public	UserSearchRepresentation(String serializedUSR){
		String[] strs = serializedUSR.split(CarpoolConfig.urlSeperatorRegx);
		this.name = strs[0];
		this.gender = carpool.constants.Constants.gender.fromInt(Integer.parseInt(strs[1]));
		this.location_Id =Long.parseLong(strs[2]);
	}
	
	public String getName(){
		return this.name;
		
	}
	
	public gender getGender(){
		return this.gender;
	}
	public long getLocationId(){
		return this.location_Id;
	}
	
	public String toSerializedString(){
		return this.name + CarpoolConfig.urlSeperator + this.gender.code + CarpoolConfig.urlSeperator + this.location_Id;
	}

	
	
}
