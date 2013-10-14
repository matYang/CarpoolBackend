package carpool.model.representation;

import carpool.constants.Constants.gender;

public class UserSearchRepresentation {

	private String name;
	private gender Gender;
	private LocationRepresentation location;
	
	
	public  UserSearchRepresentation(String name,gender g,LocationRepresentation l){
		this.name = name;
		this.Gender = g;
		this.location = l;
	}
	public String getName(){
		return this.name;
		
	}
	
	public gender getGender(){
		return this.Gender;
	}
	public LocationRepresentation getLocation(){
		return this.location;
	}
}
