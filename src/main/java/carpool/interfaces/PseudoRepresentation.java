package carpool.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

public interface PseudoRepresentation extends PseudoModel{
	
	public String toSerializedString();
	
	public JSONObject toJSON();

}
