package badstudent.common;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import badstudent.model.DMMessage;
import badstudent.model.Location;
import badstudent.model.Notification;
import badstudent.model.Transaction;
import badstudent.model.User;

import badstudent.common.Constants.gender;
import badstudent.common.Constants.paymentMethod;
import badstudent.common.Constants.userState;
import badstudent.interfaces.*;


//a class that will make JSONObject or JSONArray based on given classes
//serves as the converter on API data return
public class JSONFactory {
	
	public static JSONObject toJSON(PesudoModel obj){
		if (obj == null){
			Common.d("JSONFactory::toJSON_Model receving null obj");
		}
		return obj != null ? obj.toJSON() : new JSONObject();
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
	
	public static JSONArray toJSON(ArrayList<? extends PesudoModel> objs){
		JSONArray jsonObjs = new JSONArray();
		if (objs == null){
			Common.d("JSONFactory::toJSON_ArrayList receving null objs");
			return new JSONArray();
		}
		for (int i = 0; i < objs.size(); i++){
			jsonObjs.put(toJSON(objs.get(i)));
		}
		
		return jsonObjs;
	}
	
	
	public static JSONArray toJSON_arr_str(ArrayList<String> strings){

		ArrayList<JSONObject> jsonObjs = new ArrayList<JSONObject>();
		for (int i = 0; i < strings.size(); i++){
			jsonObjs.add(toJSON(strings.get(i)));
		}
		return new JSONArray(jsonObjs);
	}

	
	
	/*
	//yeah I should've used an interface and ArrayList<? implements toJSONAble>
	public static JSONObject toJSON(User user){
		JSONObject jsonUser = new JSONObject(user);
		
		try {
			jsonUser.put("lastLogin", Common.CalendarToUTCString(user.getLastLogin()));
			jsonUser.put("creationTime", Common.CalendarToUTCString(user.getCreationTime()));
			
			jsonUser.put("location", toJSON(user.getLocation()));
			jsonUser.put("historyList", toJSON(user.getHistoryList(), new DMMessage()));
			jsonUser.put("watchList", toJSON(user.getWatchList(), new DMMessage()));
			jsonUser.put("socialList", toJSON(user.getSocialList(), new User()));
			jsonUser.put("transactionList", toJSON(user.getTransactionList(), new Transaction()));
			jsonUser.put("notificationList", toJSON(user.getNotificationList(), new Notification()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonUser;
	}
	
	public static JSONObject toJSON(DMMessage message){
		JSONObject jsonMessage = new JSONObject(message);
		
		try {
			jsonMessage.put("startTime", Common.CalendarToUTCString(message.getStartTime()));
			jsonMessage.put("endTime", Common.CalendarToUTCString(message.getEndTime()));
			jsonMessage.put("creationTime", Common.CalendarToUTCString(message.getCreationTime()));
			
			jsonMessage.put("location", toJSON(message.getLocation()));
			jsonMessage.put("transactionList", toJSON(message.getTransactionList(), new Transaction()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonMessage;
	}
	
	public static JSONObject toJSON(Transaction transaction){
		JSONObject jsonTransaction = new JSONObject(transaction);
		
		try {
			jsonTransaction.put("startTime", Common.CalendarToUTCString(transaction.getStartTime()));
			jsonTransaction.put("endTime", Common.CalendarToUTCString(transaction.getEndTime()));
			jsonTransaction.put("creationTime", Common.CalendarToUTCString(transaction.getCreationTime()));
			
			jsonTransaction.put("location", toJSON(transaction.getLocation()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return  jsonTransaction;
	}
	
	public static JSONObject toJSON(Notification notification){
		JSONObject jsonNotification = new JSONObject(notification);
		
		try {
			jsonNotification.put("creationTime", Common.CalendarToUTCString(notification.getCreationTime()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonNotification;
	}
	
	public static JSONObject toJSON(Location location){
		return new JSONObject(location);
	}



	
	public static JSONArray toJSON(ArrayList<User> objs, User type){
		JSONArray jsonObjs = new JSONArray();
		for (int i = 0; i < objs.size(); i++){
			jsonObjs.put(toJSON(objs.get(i)));
		}
		
		return jsonObjs;
	}
	
	public static JSONArray toJSON(ArrayList<DMMessage> objs, DMMessage type){
		JSONArray jsonObjs = new JSONArray();
		for (int i = 0; i < objs.size(); i++){
			jsonObjs.put(toJSON(objs.get(i)));
		}
		
		return jsonObjs;
	}
	
	public static JSONArray toJSON(ArrayList<Transaction> objs, Transaction type){
		JSONArray jsonObjs = new JSONArray();
		for (int i = 0; i < objs.size(); i++){
			jsonObjs.put(toJSON(objs.get(i)));
		}
		
		return jsonObjs;
	}
	
	public static JSONArray toJSON(ArrayList<Notification> objs, Notification type){
		JSONArray jsonObjs = new JSONArray();
		for (int i = 0; i < objs.size(); i++){
			jsonObjs.put(toJSON(objs.get(i)));
		}
		
		return jsonObjs;
	}
	
	public static JSONArray toJSON(ArrayList<Location> objs, Location type){
		return new JSONArray(objs);
	}
	*/
}
