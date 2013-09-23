package carpool.model;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.factory.JSONFactory;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;


public class Message implements PseudoModel, PseudoValidatable, Comparable<Message>{
	
	public final int category = Constants.category_DM;
	
	/*****
	 * Message's relations
	 *****/
	private int messageId;
	private int ownerId;
	//use to fill JSONObject specs with 2 concret objects
	private User owner;
	private ArrayList<Transaction> transactionList;
	
	/*****
	 * Carpool Details
	 *****/
	private boolean isRroundTrip;
	
	private Location departure_Location;
	private Calendar departure_Time;
	private int departure_seatsNumber;
	private int departures_seatsBooked;
	private ArrayList<Integer> daparture_priceList;
	
	private Location arrivalLocation;
	private Calendar arrivalTime;
	private int arrival_seatsNumber;
	private int arrival_seatsBooked;
	private ArrayList<Integer> arrival_priceList;
	
	/*****
	 * message details
	 *****/
	private paymentMethod paymentMethod;   //refer to common.Constants, though for now we'll be using offline only, it will be guaranteed on API level, allow flexibility in underlying logic
	private String note;
	private messageType type;
	private gender genderRequirement;
	private messageState state;
	
	private Calendar creationTime;
	private Calendar editTime;
	private boolean historyDeleted;
	

	
//	public String toNotificationSummary(){
//		String typeSufix = (type == messageType.ask ? "求点名" : "帮点名") + "的消息";
//		return Common.getNotificationDateString(this.startTime) + typeSufix;
//	}
	

	
	/**
	 * checks if the existing message is valid, eg, expired or not, content and state
	 */
	public boolean isMessageValid() {
		//TODO
		return false;
	}

	
	/**
	 * checks if this messageType is valid for a message
	 * @param messageType
	 * @return
	 */
	public static boolean isMessageTypeValid(messageType messageType){
		return true;
	}

	/**
	 * TODO: note that, to send a full message back, apply Transaction::prepareBrief to transactlionList
	 */
	
	@Override
	public JSONObject toJSON(){
		JSONObject jsonMessage = new JSONObject(this);
		
		try {
			jsonMessage.put("startTime", Validator.CalendarToUTCString(this.getStartTime()));
			jsonMessage.put("endTime", Validator.CalendarToUTCString(this.getEndTime()));
			jsonMessage.put("creationTime", Validator.CalendarToUTCString(this.getCreationTime()));
			
			jsonMessage.put("location", this.location.toJSON());
			jsonMessage.put("transactionList", JSONFactory.toJSON(this.transactionList));
			
			jsonMessage.put("paymentMethod", this.getPaymentMethod());
			jsonMessage.put("type", this.getType());
			jsonMessage.put("genderRequirement", this.getGenderRequirement());
			jsonMessage.put("state", this.getState());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonMessage;
	}
	
	@Override
	public boolean validate() {
		//TODO
		return false;
	}
	
	

}

