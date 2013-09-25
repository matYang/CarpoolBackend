package carpool.model;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.HelperOperator;
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
	private User owner;
	//transactionList is not pulled from database by default
	private ArrayList<Transaction> transactionList;
	
	/*****
	 * Carpool Details
	 *****/
	private boolean isRoundTrip;
	
	private Location departure_Location;
	private Calendar departure_Time;
	private int departure_seatsNumber;
	private int departure_seatsBooked;
	private ArrayList<Integer> departure_priceList;
	
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
	
	
	private Message(){}
	
	
	/*****
	 * The contructor used for message posting
	 *****/
	public Message(int messageId, int ownerId, boolean isRoundTrip,
			Location departure_Location, Calendar departure_Time,
			int departure_seatsNumber, ArrayList<Integer> departure_priceList,
			Location arrival_Location, Calendar arrival_Time,
			int arrival_seatsNumber, ArrayList<Integer> arrival_priceList,
			carpool.constants.Constants.paymentMethod paymentMethod,
			String note, messageType type, gender genderRequirement) {
		super();
		this.messageId = messageId;
		this.ownerId = ownerId;
		this.isRoundTrip = isRoundTrip;
		this.departure_Location = departure_Location;
		this.departure_Time = departure_Time;
		this.departure_seatsNumber = departure_seatsNumber;
		this.departure_priceList = departure_priceList;
		this.arrival_Location = arrival_Location;
		this.arrival_Time = arrival_Time;
		this.arrival_seatsNumber = arrival_seatsNumber;
		this.arrival_priceList = arrival_priceList;
		this.paymentMethod = paymentMethod;
		this.note = note;
		this.type = type;
		this.genderRequirement = genderRequirement;
		
		//dummy fills
		this.owner = null;
		this.transactionList = new ArrayList<Transaction>();
		this.departure_seatsBooked = 0;
		this.arrival_seatsBooked = 0;
		
		this.state = Constants.messageState.normal;
		this.creationTime = Calendar.getInstance();
		this.editTime = Calendar.getInstance();
		this.historyDeleted = false;
	}




	/*****
	 * full constructor used for SQL retrieval
	 *****/
	public Message(int messageId, int ownerId, User owner,
			boolean isRroundTrip, Location departure_Location,
			Calendar departure_Time, int departure_seatsNumber,
			int departures_seatsBooked, ArrayList<Integer> departure_priceList,
			Location arrival_Location, Calendar arrival_Time,
			int arrival_seatsNumber, int arrival_seatsBooked,
			ArrayList<Integer> arrival_priceList,
			carpool.constants.Constants.paymentMethod paymentMethod,
			String note, messageType type, gender genderRequirement,
			messageState state, Calendar creationTime, Calendar editTime,
			boolean historyDeleted) {
		super();
		this.messageId = messageId;
		this.ownerId = ownerId;
		this.owner = owner;
		this.isRoundTrip = isRoundTrip;
		this.departure_Location = departure_Location;
		this.departure_Time = departure_Time;
		this.departure_seatsNumber = departure_seatsNumber;
		this.departure_seatsBooked = departures_seatsBooked;
		this.departure_priceList = departure_priceList;
		this.arrival_Location = arrival_Location;
		this.arrival_Time = arrival_Time;
		this.arrival_seatsNumber = arrival_seatsNumber;
		this.arrival_seatsBooked = arrival_seatsBooked;
		this.arrival_priceList = arrival_priceList;
		this.paymentMethod = paymentMethod;
		this.note = note;
		this.type = type;
		this.genderRequirement = genderRequirement;
		this.state = state;
		this.creationTime = creationTime;
		this.editTime = editTime;
		this.historyDeleted = historyDeleted;
	}


	public int getMessageId() {
		return messageId;
	}


	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}


	public int getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}


	public User getOwner() {
		return owner;
	}


	public void setOwner(User owner) {
		this.owner = owner;
	}


	public ArrayList<Transaction> getTransactionList() {
		return transactionList;
	}


	public void setTransactionList(ArrayList<Transaction> transactionList) {
		this.transactionList = transactionList;
	}


	public boolean isRoundTrip() {
		return isRoundTrip;
	}


	public void setRoundTrip(boolean isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}


	public Location getDeparture_Location() {
		return departure_Location;
	}


	public void setDeparture_Location(Location departure_Location) {
		this.departure_Location = departure_Location;
	}


	public Calendar getDeparture_Time() {
		return departure_Time;
	}


	public void setDeparture_Time(Calendar departure_Time) {
		this.departure_Time = departure_Time;
	}


	public int getDeparture_seatsNumber() {
		return departure_seatsNumber;
	}


	public void setDeparture_seatsNumber(int departure_seatsNumber) {
		this.departure_seatsNumber = departure_seatsNumber;
	}


	public int getDeparture_seatsBooked() {
		return departure_seatsBooked;
	}


	public void setDeparture_seatsBooked(int departure_seatsBooked) {
		this.departure_seatsBooked = departure_seatsBooked;
	}


	public ArrayList<Integer> getDeparture_priceList() {
		return departure_priceList;
	}


	public void setDeparture_priceList(ArrayList<Integer> departure_priceList) {
		this.departure_priceList = departure_priceList;
	}


	public Location getArrival_Location() {
		return arrival_Location;
	}


	public void setArrival_Location(Location arrival_Location) {
		this.arrival_Location = arrival_Location;
	}


	public Calendar getArrival_Time() {
		return arrival_Time;
	}


	public void setArrival_Time(Calendar arrival_Time) {
		this.arrival_Time = arrival_Time;
	}


	public int getArrival_seatsNumber() {
		return arrival_seatsNumber;
	}


	public void setArrival_seatsNumber(int arrival_seatsNumber) {
		this.arrival_seatsNumber = arrival_seatsNumber;
	}


	public int getArrival_seatsBooked() {
		return arrival_seatsBooked;
	}


	public void setArrival_seatsBooked(int arrival_seatsBooked) {
		this.arrival_seatsBooked = arrival_seatsBooked;
	}


	public ArrayList<Integer> getArrival_priceList() {
		return arrival_priceList;
	}


	public void setArrival_priceList(ArrayList<Integer> arrival_priceList) {
		this.arrival_priceList = arrival_priceList;
	}


	public paymentMethod getPaymentMethod() {
		return paymentMethod;
	}


	public void setPaymentMethod(paymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
	}


	public messageType getType() {
		return type;
	}


	public void setType(messageType type) {
		this.type = type;
	}


	public gender getGenderRequirement() {
		return genderRequirement;
	}


	public void setGenderRequirement(gender genderRequirement) {
		this.genderRequirement = genderRequirement;
	}


	public messageState getState() {
		return state;
	}


	public void setState(messageState state) {
		this.state = state;
	}


	public Calendar getEditTime() {
		return editTime;
	}


	public void setEditTime(Calendar editTime) {
		this.editTime = editTime;
	}


	public boolean isHistoryDeleted() {
		return historyDeleted;
	}


	public void setHistoryDeleted(boolean historyDeleted) {
		this.historyDeleted = historyDeleted;
	}


	public int getCategory() {
		return category;
	}


	public Calendar getCreationTime() {
		return creationTime;
	}


	@Override
	public JSONObject toJSON(){
		JSONObject jsonMessage = new JSONObject(this);
		
		try {
			jsonMessage.put("messageId", this.getMessageId());
			jsonMessage.put("ownerId", this.getOwnerId());
			jsonMessage.put("owner", this.getOwner());
			jsonMessage.put("transactionList", this.getTransactionList());
			
			jsonMessage.put("isRoundTrip", this.isRoundTrip());
			jsonMessage.put("departure_Location", this.getDeparture_Location().toJSON());
			jsonMessage.put("departure_Time", DateUtility.CalendarToUTCString(this.getDeparture_Time()));
			jsonMessage.put("departure_seatsNumber", this.getDeparture_seatsNumber());
			jsonMessage.put("departure_seatsBooked", this.getDeparture_seatsBooked());
			jsonMessage.put("daparture_priceList", new JSONArray(this.getDeparture_priceList()));
			jsonMessage.put("arrival_Location", this.getArrival_Location().toJSON());
			jsonMessage.put("arrival_Time", DateUtility.CalendarToUTCString(this.getArrival_Time()));
			jsonMessage.put("arrival_seatsNumber", this.getArrival_seatsNumber());
			jsonMessage.put("arrival_seatsBooked", this.getArrival_seatsBooked());
			jsonMessage.put("arrival_priceList", new JSONArray(this.getArrival_priceList()));
			
			jsonMessage.put("paymentMethod", this.getPaymentMethod());
			jsonMessage.put("note", this.getNote());
			jsonMessage.put("type", this.getType());
			jsonMessage.put("genderRequirement", this.getGenderRequirement());
			jsonMessage.put("state", this.getState());
			jsonMessage.put("creationTime", DateUtility.CalendarToUTCString(this.getCreationTime()));
			jsonMessage.put("editTime",DateUtility.CalendarToUTCString(this.getEditTime()));
			jsonMessage.put("historyDeleted", this.isHistoryDeleted());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonMessage;
	}



	public boolean equals(Message msg) {
		try{
			return msg != null && this.getArrival_Location().equals(msg.getArrival_Location()) && this.getArrival_Time().equals(msg.getArrival_Time()) &&
					HelperOperator.isArrayListEqual(this.getArrival_priceList(), msg.getArrival_priceList()) && this.getArrival_seatsBooked() == msg.getArrival_seatsBooked() &&
					this.getArrival_seatsNumber() == msg.getArrival_seatsNumber() && this.category == msg.getCategory() && this.getCreationTime().equals(msg.getCreationTime()) &&
					this.departure_Location.equals(msg.getDeparture_Location()) && this.getDeparture_Time().equals(msg.getDeparture_Time()) &&
					HelperOperator.isArrayListEqual(this.getDeparture_priceList(), msg.getDeparture_priceList()) && this.getDeparture_seatsBooked() == msg.getDeparture_seatsBooked() &&
					this.getDeparture_seatsNumber() == msg.getDeparture_seatsNumber() && this.getEditTime().equals(msg.getEditTime()) &&
					this.getGenderRequirement() == msg.getGenderRequirement() && this.isHistoryDeleted() == msg.isHistoryDeleted() &&
					this.isRoundTrip == msg.isRoundTrip && this.getMessageId() == msg.getMessageId() && this.getNote().equals(msg.getNote()) &&
					this.getOwnerId() == msg.getOwnerId() && this.getPaymentMethod() == msg.getPaymentMethod() && this.getState() == msg.getState() &&
					this.getType() == msg.getType();
		}
		catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
		

	}


	@Override
	public String toString() {
		return "Message [category=" + category + ", messageId=" + messageId
				+ ", ownerId=" + ownerId + ", owner=" + owner
				+ ", transactionList=" + transactionList + ", isRroundTrip="
				+ isRoundTrip + ", departure_Location=" + departure_Location
				+ ", departure_Time=" + departure_Time
				+ ", departure_seatsNumber=" + departure_seatsNumber
				+ ", departure_seatsBooked=" + departure_seatsBooked
				+ ", departure_priceList=" + departure_priceList
				+ ", arrival_Location=" + arrival_Location + ", arrival_Time="
				+ arrival_Time + ", arrival_seatsNumber=" + arrival_seatsNumber
				+ ", arrival_seatsBooked=" + arrival_seatsBooked
				+ ", arrival_priceList=" + arrival_priceList
				+ ", paymentMethod=" + paymentMethod + ", note=" + note
				+ ", type=" + type + ", genderRequirement=" + genderRequirement
				+ ", state=" + state + ", creationTime=" + creationTime
				+ ", editTime=" + editTime + ", historyDeleted="
				+ historyDeleted + "]";
	}


	//override Comparator, by default messages will be sorted in departure timing orders
	@Override
	public int compareTo(Message anotherMessage) {
		return this.getDeparture_Time().compareTo(anotherMessage.getDeparture_Time());
	}
	
	
	@Override
	public boolean validate() {
		//TODO
		
		
		return true;
	}

	

}

