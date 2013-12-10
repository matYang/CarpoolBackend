package carpool.model.representation;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.exception.validation.ValidationException;
import carpool.interfaces.PseudoRepresentation;
import carpool.interfaces.PseudoValidatable;

public class LocationRepresentation implements PseudoRepresentation, PseudoValidatable{
	
	private ArrayList<String> hierarchyNameList = new ArrayList<String>();
	private int customDepthIndex;
	
	@SuppressWarnings("unused")
	private LocationRepresentation(){}
	

	public LocationRepresentation(ArrayList<String> hierarchyNameList, int customDepthIndex) {
		super();
		this.hierarchyNameList = hierarchyNameList;
		this.customDepthIndex = customDepthIndex;
	}

	//String format name_name_name_customDepthIndex
	public LocationRepresentation(String serializedLocationReresentationString){
		String[] representationArray = serializedLocationReresentationString.split("_");
		int i = 0;
		for (i = 0; i < representationArray.length - 1; i++){
			this.hierarchyNameList.add(representationArray[i]);
		}
		this.customDepthIndex = Integer.parseInt(representationArray[i]);
	}
	
	//used for SQL
	public LocationRepresentation(String primaryLocationString, String customLocationString, int customDepthIndex){
		this(primaryLocationString + "_" + customLocationString + "_" + customDepthIndex);
	}
	
	public LocationRepresentation(JSONObject jsonLocationRepresentation) throws JSONException{
		JSONArray nameList = jsonLocationRepresentation.getJSONArray("hierarchyNameList");
		for(int i=0;i < nameList.length();i++){ 
		    this.hierarchyNameList.add(nameList.getString(i));
		}
		this.customDepthIndex = jsonLocationRepresentation.getInt("customDepthIndex");
	}
	
	
	
	public String getName(int index){
		return this.hierarchyNameList.get(index);
	}
	
	public ArrayList<String> getHierarchyNameList() {
		return hierarchyNameList;
	}

	public void setHierarchyNameList(ArrayList<String> hierarchyNameList) {
		this.hierarchyNameList = hierarchyNameList;
	}

	public int getCustomDepthIndex() {
		return customDepthIndex;
	}

	public void setCustomDepthIndex(int customDepthIndex) {
		this.customDepthIndex = customDepthIndex;
	}
	
	public String getPrimaryLocationString(){
		String serializedLocationRepresentationString = "";
		for (int i = 0; i < this.customDepthIndex; i++){
			serializedLocationRepresentationString += this.hierarchyNameList.get(i) + "_";
		}
		serializedLocationRepresentationString = serializedLocationRepresentationString.substring(0, serializedLocationRepresentationString.length()-1);
		return serializedLocationRepresentationString;
	}
	
	public String getCustomLocationString(){
		String serializedLocationRepresentationString = "";
		for (int i = this.customDepthIndex; i < this.hierarchyNameList.size(); i++){
			serializedLocationRepresentationString += this.hierarchyNameList.get(i) + "_";
		}
		serializedLocationRepresentationString = serializedLocationRepresentationString.substring(0, serializedLocationRepresentationString.length()-1);
		return serializedLocationRepresentationString;
	}

	
	
	@Override
	public String toSerializedString(){
		String serializedLocationRepresentationString = "";
		for (int i = 0; i < this.hierarchyNameList.size(); i++){
			serializedLocationRepresentationString += this.hierarchyNameList.get(i) + "_";
		}
		serializedLocationRepresentationString += Integer.toString(this.customDepthIndex);
		return serializedLocationRepresentationString;
	}
	
	@Override
	public JSONObject toJSON(){
		JSONArray nameList = new JSONArray(this.hierarchyNameList);
		JSONObject jsonLocationRepresentation = new JSONObject();
		try{
			jsonLocationRepresentation.put("hierarchyNameList", nameList);
			jsonLocationRepresentation.put("customDepthIndex", this.customDepthIndex);
		} catch (JSONException e){
			e.printStackTrace();
		}
		return jsonLocationRepresentation;
	}


	public boolean equals(LocationRepresentation loc) {
		if (this.hierarchyNameList.size() != loc.getHierarchyNameList().size()){
			return false;
		}
		for (int i = 0; i < this.hierarchyNameList.size(); i++){
			if (!this.hierarchyNameList.get(i).equals(loc.getHierarchyNameList().get(i))){
				return false;
			}
		}
		return this.customDepthIndex == loc.getCustomDepthIndex();
	}


	@Override
	public boolean validate() throws ValidationException {
		// TODO Auto-generated method stub
		return false;
	}

}
