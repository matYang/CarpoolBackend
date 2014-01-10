package carpool.model;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import carpool.common.DateUtility;
import carpool.common.HelperOperator;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.factory.JSONFactory;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;



public class Message implements PseudoModel, PseudoValidatable, Comparable<Message>{

	public final int category = CarpoolConfig.category_DM;

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

	private long departure_Id;
	private Location departure_Location;
	private long departureMatch_Id;
	private Calendar departure_time;
	private DayTimeSlot departure_timeSlot;
	private int departure_seatsNumber;
	private int departure_seatsBooked;
	private ArrayList<Integer> departure_priceList;

	private long arrival_Id;
	private Location arrival_Location;
	private long arrivalMatch_Id;
	private Calendar arrival_time;
	private DayTimeSlot arrival_timeSlot;
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


	@SuppressWarnings("unused")
	private Message(){}


	/*****
	 * The contructor used for message posting/updating
	 *****/
	public Message(int ownerId, boolean isRoundTrip,
			Location departure_Location, Calendar departure_time, DayTimeSlot departure_timeSlot,
			int departure_seatsNumber, ArrayList<Integer> departure_priceList,
			Location arrival_Location, Calendar arrival_time, DayTimeSlot arrival_timeSlot,
			int arrival_seatsNumber, ArrayList<Integer> arrival_priceList,
			paymentMethod paymentMethod, String note, messageType type, gender genderRequirement) {
		super();
		this.messageId = -1;
		this.ownerId = ownerId;
		this.isRoundTrip = isRoundTrip;
		this.departure_Location = departure_Location;
		this.departure_Id = departure_Location.getId();
		this.departureMatch_Id = departure_Location.getMatch();
		this.departure_time = departure_time;
		this.departure_timeSlot = departure_timeSlot;
		this.departure_seatsNumber = departure_seatsNumber;
		this.departure_priceList = departure_priceList;
		this.arrival_Location = arrival_Location;
		this.arrival_Id = arrival_Location.getId();
		this.arrivalMatch_Id = arrival_Location.getMatch();
		this.arrival_time = arrival_time;
		this.arrival_timeSlot  = arrival_timeSlot;
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
			boolean isRoundTrip, Location departure_Location,
			Calendar departure_time, DayTimeSlot departure_timeSlot, int departure_seatsNumber,
			int departures_seatsBooked, ArrayList<Integer> departure_priceList,
			Location arrival_Location, Calendar arrival_time, DayTimeSlot arrival_timeSlot,
			int arrival_seatsNumber, int arrival_seatsBooked,
			ArrayList<Integer> arrival_priceList,
			paymentMethod paymentMethod, String note, messageType type, gender genderRequirement,
			messageState state, Calendar creationTime, Calendar editTime, boolean historyDeleted) {
		super();
		this.messageId = messageId;
		this.ownerId = ownerId;
		this.owner = owner;
		this.isRoundTrip = isRoundTrip;
		this.departure_Location = departure_Location;
		this.departure_Id = departure_Location.getId();
		this.departureMatch_Id = departure_Location.getMatch();
		this.departure_time = departure_time;
		this.departure_timeSlot = departure_timeSlot;
		this.departure_seatsNumber = departure_seatsNumber;
		this.departure_seatsBooked = departures_seatsBooked;
		this.departure_priceList = departure_priceList;
		this.arrival_Location = arrival_Location;
		this.arrival_Id = arrival_Location.getId();
		this.arrivalMatch_Id = arrival_Location.getMatch();
		this.arrival_time = arrival_time;
		this.arrival_timeSlot = arrival_timeSlot;
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
		this.departureMatch_Id = departure_Location.getMatch();
	}


	public Calendar getDeparture_time() {
		return departure_time;
	}


	public void setDeparture_time(Calendar departure_time) {
		this.departure_time = departure_time;
	}


	public DayTimeSlot getDeparture_timeSlot() {
		return departure_timeSlot;
	}


	public void setDeparture_timeSlot(DayTimeSlot departure_timeSlot) {
		this.departure_timeSlot = departure_timeSlot;
	}


	public DayTimeSlot getArrival_timeSlot() {
		return arrival_timeSlot;
	}


	public void setArrival_timeSlot(DayTimeSlot arrival_timeSlot) {
		this.arrival_timeSlot = arrival_timeSlot;
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
		this.arrivalMatch_Id = arrival_Location.getMatch();
	}


	public Calendar getArrival_time() {
		return arrival_time;
	}


	public void setArrival_time(Calendar arrival_time) {
		this.arrival_time = arrival_time;
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
	
	public long getDeparture_Id(){
		return departure_Id;
	}
	
	public void setDeparture_Id(long id){
		this.departure_Id = id;
	}
	
	public long getArrival_Id(){
		return arrival_Id;
	}
	
	public void setArrvial_Id(long id){
		this.arrival_Id = id;
	}
	
	public long getDepartureMatch_Id(){
		return this.departureMatch_Id;
	}
	
	public void setDepartureMatch_Id(long id){
		this.departureMatch_Id = id;
	}
	
	public long getArrivalMatch_Id(){
		return this.arrivalMatch_Id;
	}
	
	public void setArrivalMatch_Id(long id){
		this.arrivalMatch_Id = id;
	}

	@Override
	public JSONObject toJSON(){
		JSONObject jsonMessage = new JSONObject();

		try {
			jsonMessage.put("messageId", this.getMessageId());
			jsonMessage.put("ownerId", this.getOwnerId());
			jsonMessage.put("owner", this.owner != null ? this.getOwner().toJSON() : new JSONObject());
			jsonMessage.put("transactionList", this.transactionList != null ? JSONFactory.toJSON(this.getTransactionList()) : new JSONArray());

			jsonMessage.put("isRoundTrip", this.isRoundTrip());
			jsonMessage.put("departure_location", this.getDeparture_Location().toJSON());
			jsonMessage.put("departure_time", DateUtility.castToAPIFormat(this.getDeparture_time()));
			jsonMessage.put("departure_timeSlot", this.departure_timeSlot.code);
			jsonMessage.put("departure_seatsNumber", this.getDeparture_seatsNumber());
			jsonMessage.put("departure_seatsBooked", this.getDeparture_seatsBooked());
			jsonMessage.put("departure_priceList", new JSONArray(this.getDeparture_priceList()));
			jsonMessage.put("arrival_location", this.getArrival_Location().toJSON());
			jsonMessage.put("arrival_time", DateUtility.castToAPIFormat(this.getArrival_time()));
			jsonMessage.put("arrival_timeSlot", this.arrival_timeSlot.code);
			jsonMessage.put("arrival_seatsNumber", this.getArrival_seatsNumber());
			jsonMessage.put("arrival_seatsBooked", this.getArrival_seatsBooked());
			jsonMessage.put("arrival_priceList", new JSONArray(this.getArrival_priceList()));

			jsonMessage.put("paymentMethod", this.getPaymentMethod().code);
			jsonMessage.put("note", this.getNote());
			jsonMessage.put("type", this.getType().code);
			jsonMessage.put("genderRequirement", this.getGenderRequirement().code);
			jsonMessage.put("state", this.getState().code);
			jsonMessage.put("creationTime", DateUtility.castToAPIFormat(this.getCreationTime()));
			jsonMessage.put("editTime",DateUtility.castToAPIFormat(this.getEditTime()));
			jsonMessage.put("historyDeleted", this.isHistoryDeleted());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonMessage;
	}



	public boolean equals(Message msg) {
		try{
			return msg != null
					&& this.getArrival_Location().equals(msg.getArrival_Location()) 
					&& this.getArrival_time().getTime().toString().equals(msg.getArrival_time().getTime().toString()) 
					&& HelperOperator.isArrayListEqual(this.getArrival_priceList(), msg.getArrival_priceList()) && this.getArrival_seatsBooked() == msg.getArrival_seatsBooked()
					&& this.getArrival_seatsNumber() == msg.getArrival_seatsNumber() && this.category == msg.getCategory() 
					&& this.getCreationTime().getTime().toString().equals(msg.getCreationTime().getTime().toString())
					&& this.getDeparture_Location().equals(msg.getDeparture_Location()) 
					&& this.getDeparture_time().getTime().toString().equals(msg.getDeparture_time().getTime().toString())
					&& HelperOperator.isArrayListEqual(this.getDeparture_priceList(), msg.getDeparture_priceList()) && this.getDeparture_seatsBooked() == msg.getDeparture_seatsBooked() 
					&& this.getDeparture_seatsNumber() == msg.getDeparture_seatsNumber() 
					&& this.getEditTime().getTime().toString().equals(msg.getEditTime().getTime().toString()) 
					&& this.getGenderRequirement().code == msg.getGenderRequirement().code && this.isHistoryDeleted() == msg.isHistoryDeleted()
					&& this.isRoundTrip == msg.isRoundTrip && this.getMessageId() == msg.getMessageId() && this.getNote().equals(msg.getNote()) 
					&& this.getOwnerId() == msg.getOwnerId() && this.getPaymentMethod().code == msg.getPaymentMethod().code 
					&& this.getState().code == msg.getState().code 
					&& this.getType().code == msg.getType().code
					&& this.getDeparture_timeSlot() == msg.getDeparture_timeSlot()
					&& this.getArrival_timeSlot() == msg.getArrival_timeSlot();

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
				+ ", transactionList=" + transactionList + ", isRoundTrip="
				+ isRoundTrip + ", departure_location=" + departure_Location
				+ ", departure_time=" + departure_time
				+ ", departure_timeSlot=" + departure_timeSlot
				+ ", departure_seatsNumber=" + departure_seatsNumber
				+ ", departure_seatsBooked=" + departure_seatsBooked
				+ ", departure_priceList=" + departure_priceList
				+ ", arrival_location=" + arrival_Location + ", arrival_time="
				+ arrival_time + ", arrival_timeSlot=" + arrival_timeSlot
				+ ", arrival_seatsNumber=" + arrival_seatsNumber
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
		return this.getDeparture_time().compareTo(anotherMessage.getDeparture_time());
	}


	@Override
	public boolean validate() {
		//TODO

		return true;
	}



}

