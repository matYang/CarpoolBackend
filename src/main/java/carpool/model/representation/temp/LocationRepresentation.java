package carpool.model.representation.temp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.interfaces.PseudoRepresentation;

public class LocationRepresentation implements PseudoRepresentation{
	
	private ArrayList<String> hierarchyNameList;
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
		JSONArray nameList = jsonLocationRepresentation.getJSONArray("nameList");
		for(int i=0;i < nameList.length();i++){ 
		    this.hierarchyNameList.add(nameList.getJSONObject(i).getString("names"));
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
		serializedLocationRepresentationString.substring(0, serializedLocationRepresentationString.length()-1);
		return serializedLocationRepresentationString;
	}
	
	public String getCustomLocationString(){
		String serializedLocationRepresentationString = "";
		for (int i = this.customDepthIndex; i < this.hierarchyNameList.size(); i++){
			serializedLocationRepresentationString += this.hierarchyNameList.get(i) + "_";
		}
		serializedLocationRepresentationString.substring(0, serializedLocationRepresentationString.length()-1);
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
	public JSONObject toJSON() throws JSONException{
		JSONArray nameList = new JSONArray(this.hierarchyNameList);
		JSONObject jsonLocationRepresentation = new JSONObject();
		jsonLocationRepresentation.put("nameList", nameList);
		jsonLocationRepresentation.put("customDepthIndex", this.customDepthIndex);
		
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

}
