package carpool.model;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.common.Constants.gender;
import carpool.common.Constants.messageState;
import carpool.common.Constants.messageType;
import carpool.common.Constants.paymentMethod;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;


public class Message implements PseudoModel, PseudoValidatable{
	
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
	private boolean isRoundTrip;
	
	private Location departure_Location;
	private Calendar departure_Time;
	private int departure_seatsNumber;
	private int departure_seatsBooked;
	private ArrayList<Integer> daparture_priceList;
	
	private Location arrival_Location;
	private Calendar arrival_Time;
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
	
	
//	/*****
//	 * 	Grey Zone
//	 * 	The following are for toJSON method specifications, please never use
//	 *****/
//	private String ownerImgPath;
//	private String ownerName;
//	private int ownerLevel;
//	private int ownerAverageScore;
//	private String ownerPhone;
//	private String ownerEmail;
//	private String ownerQq;
//	private int ownerTotalTransactions;
//	private ArrayList<JSONObject> transactionBriefs;


	
//	public String toNotificationSummary(){
//		String typeSufix = (type == messageType.ask ? "求点名" : "帮点名") + "的消息";
//		return Common.getNotificationDateString(this.startTime) + typeSufix;
//	}
	

	
	/**
	 * checks if the existing message is valid, eg, expired or not, content and state
	 * @return
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
			jsonMessage.put("startTime", Common.CalendarToUTCString(this.getStartTime()));
			jsonMessage.put("endTime", Common.CalendarToUTCString(this.getEndTime()));
			jsonMessage.put("creationTime", Common.CalendarToUTCString(this.getCreationTime()));
			
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


	public int getMessageId() {
		return messageId;
	}


	public int ownerId() {
		return ownerId;
	}


	public boolean isRoundTrip() {
		return isRoundTrip;
	}


	public Location getDeparture_Location() {
		return departure_Location;
		
	}


	public Calendar getDeparture_Time() {
		return departure_Time;
	}


	public int getDeparture_seatsNumber() {
		return departure_seatsNumber;
	}


	public int getDeparture_seatsBooked() {
		return departure_seatsBooked;
	}


	public String getDeparture_priceList() {
		return "Not ready";
	}


	public Location getArrival_Location() {
		return arrival_Location;
	}


	public Calendar getArrival_Time() {
		return arrival_Time;
	}


	public int getArrival_seatsNumber() {
		return arrival_seatsNumber;
	}


	public int getArrival_seatsBooked() {
		return arrival_seatsBooked;
	}


	public String getArrival_priceList() {
		// TODO Auto-generated method stub
		return null;
	}


	public paymentMethod getPaymentMethod() {
		return paymentMethod;
	}


	public String getNote() {
		return note;
	}


	public messageType getMessageType() {
		return type;
	}


	public gender getGender() {
		return genderRequirement;
	}


	public messageState getMessageState() {
		return state;
	}


	public Calendar getCreationTime() {
		return creationTime;
	}


	public Calendar getEditTime() {
		return editTime;
	}


	public boolean isHistoryDeleted() {
		return historyDeleted;
	}


	
	
	
	

}

