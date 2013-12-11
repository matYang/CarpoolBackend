package carpool.factory;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.paymentMethod;
import carpool.constants.Constants.userState;
import carpool.interfaces.*;
import carpool.model.Letter;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.DefaultLocationRepresentation;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;



//a class that will make JSONObject or JSONArray based on given classes
//serves as the converter on API data return
public class JSONFactory {
	
	public static JSONObject toJSON(PseudoModel obj){
		if (obj == null){
			DebugLog.d("JSONFactory::toJSON_Model receving null obj");
			return new JSONObject();
		}
		else if (obj instanceof User){
			return ((User)obj).toJSON();
		}
		else if (obj instanceof Message){
			return ((Message)obj).toJSON();
		}
		else if (obj instanceof Transaction){
			return ((Transaction)obj).toJSON();
		}
		else if (obj instanceof Notification){
			return ((Notification)obj).toJSON();
		}
		else if (obj instanceof Letter){
			return ((Letter)obj).toJSON();
		}
		else if (obj instanceof Location){
			return ((Location)obj).toJSON();
		}
		else if (obj instanceof LocationRepresentation){
			return ((LocationRepresentation)obj).toJSON();
		}
		else if (obj instanceof SearchRepresentation){
			return ((SearchRepresentation)obj).toJSON();
		}
		else if (obj instanceof DefaultLocationRepresentation){
			return ((DefaultLocationRepresentation)obj).toJSON();
		}
		else{
			return new JSONObject();
		}
		
	}
	
	public static JSONObject toJSON(String s){
		JSONObject json = new JSONObject();
		try {
			json.put("val", s);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static JSONObject toJSON(boolean b){
		JSONObject json = new JSONObject();
		try {
			json.put("val", b);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static JSONObject toJSON(gender g){
		JSONObject json = new JSONObject();
		try {
			json.put("val", g);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
	public static JSONObject toJSON(paymentMethod p){
		JSONObject json = new JSONObject();
		try {
			json.put("val", p);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static JSONObject toJSON(int i){
		JSONObject json = new JSONObject();
		try {
			json.put("val", i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static JSONObject toJSON(long i){
		JSONObject json = new JSONObject();
		try {
			json.put("val", i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static JSONArray toJSON(ArrayList<? extends PseudoModel> objs){
		ArrayList<JSONObject> temps = new ArrayList<JSONObject>();
		if (objs == null){
			DebugLog.d("JSONFactory::toJSON_ArrayList receving null objs");
			return new JSONArray();
		}
		for (int i = 0; i < objs.size(); i++){
			if (objs.get(i) != null){
				JSONObject jsonResult = toJSON(objs.get(i));
				temps.add(jsonResult);
				
			}
		}
		return new JSONArray(temps);
	}
	

}
