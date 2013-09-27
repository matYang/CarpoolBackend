package carpool.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

public interface PseudoRepresentation {
	
	public String toSerializedString();
	
	public JSONObject toJSON() throws JSONException;

}
