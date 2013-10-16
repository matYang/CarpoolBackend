package carpool.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import carpool.common.DateUtility;
import carpool.common.HelperOperator;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.TransactionDirection;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.paymentMethod;
import carpool.constants.Constants.transactionState;
import carpool.exception.ValidationException;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;
import carpool.model.representation.LocationRepresentation;




public class Transaction implements PseudoModel, PseudoValidatable, Comparable<Transaction>{
		
	public final int category = Constants.category_DM;
	
	private int transactionId;
	
	private int providerId;
	private int customerId;
	private int messageId;
	
	private User provider;
	private User customer;
	private Message message;
	
	private paymentMethod paymentMethod;
	private String customerNote;
	private String providerNote;
	private int customerEvaluation;
	private int providerEvaluation;
	
	
	//transactions have their data set upon initialization, further change to the base message itself will not effect transaction details
	private TransactionDirection direction;
	private LocationRepresentation departure_location;
	private Calendar departure_time;
	private DayTimeSlot departure_timeSlot;
	private int departure_seatsBooked;
	private ArrayList<Integer> departure_priceList;
	
	private LocationRepresentation arrival_location;
	private Calendar arrival_time;
	private DayTimeSlot arrival_timeSlot;
	private int arrival_seatsBooked;
	private ArrayList<Integer> arrival_priceList; 
	
	
	private int totalPrice;
	private transactionState state;
	
	private Calendar creationTime;
	private boolean historyDeleted;

	
	private Transaction(){}
	
	//this contructor is used for transaction initialization
	public Transaction(int providerId, int customerId, int messageId, paymentMethod p, String cNote, String pNote, 
			TransactionDirection tD, Calendar d_t, DayTimeSlot d_ts, int d_seats, Calendar a_t, DayTimeSlot a_ts, int a_seats){
		
		this.providerId = providerId;
		this.customerId = customerId;
		this.messageId = messageId;
		
		this.provider = null;
		this.customer = null;
		this.message = null;
		
		this.paymentMethod = p;
		this.customerNote = cNote;
		this.providerNote = pNote;
		this.customerEvaluation = 0;
		this.providerEvaluation = 0;
		
		this.direction = tD;
		this.departure_location = null;
		this.departure_time = d_t;
		this.departure_timeSlot = d_ts;
		this.departure_seatsBooked = d_seats;
		this.departure_priceList = new ArrayList<Integer>();
		this.arrival_location = null;
		this.arrival_time = a_t;
		this.arrival_timeSlot = a_ts;
		this.arrival_seatsBooked = a_seats;
		this.arrival_priceList = new ArrayList<Integer>();
		
		this.totalPrice = 0;
		this.state = transactionState.init;
		this.creationTime = Calendar.getInstance();
		this.historyDeleted = false;
	}
	

	public Transaction(int transactionId, int providerId, int customerId,
			int messageId, User provider, User customer, Message message,
			carpool.constants.Constants.paymentMethod paymentMethod,
			String customerNote, String providerNote, int customerEvaluation,
			int providerEvaluation, TransactionDirection td,
			LocationRepresentation departure_location, Calendar departure_time,
			DayTimeSlot departure_timeSlot, 
			int departure_seatsBooked, ArrayList<Integer> departure_priceList,
			LocationRepresentation arrival_location, Calendar arrival_time,
			DayTimeSlot arrival_timeSlot, 
			int arrival_seatsBooked, ArrayList<Integer> arrival_priceList,
			int totalPrice, transactionState state, Calendar creationTime,
			boolean historyDeleted) {
		super();
		this.transactionId = transactionId;
		this.providerId = providerId;
		this.customerId = customerId;
		this.messageId = messageId;
		this.provider = provider;
		this.customer = customer;
		this.message = message;
		this.paymentMethod = paymentMethod;
		this.customerNote = customerNote;
		this.providerNote = providerNote;
		this.customerEvaluation = customerEvaluation;
		this.providerEvaluation = providerEvaluation;
		this.direction = td;
		this.departure_location = departure_location;
		this.departure_time = departure_time;
		this.departure_timeSlot = departure_timeSlot;
		this.departure_seatsBooked = departure_seatsBooked;
		this.departure_priceList = departure_priceList;
		this.arrival_location = arrival_location;
		this.arrival_time = arrival_time;
		this.arrival_timeSlot = arrival_timeSlot;
		this.arrival_seatsBooked = arrival_seatsBooked;
		this.arrival_priceList = arrival_priceList;
		this.totalPrice = totalPrice;
		this.state = state;
		this.creationTime = creationTime;
		this.historyDeleted = historyDeleted;
	}
	
	
	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public User getProvider() {
		return provider;
	}

	public void setProvider(User provider) {
		this.provider = provider;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public paymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(paymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCustomerNote() {
		return customerNote;
	}

	public void setCustomerNote(String customerNote) {
		this.customerNote = customerNote;
	}

	public String getProviderNote() {
		return providerNote;
	}

	public void setProviderNote(String providerNote) {
		this.providerNote = providerNote;
	}

	public int getCustomerEvaluation() {
		return customerEvaluation;
	}

	public void setCustomerEvaluation(int customerEvaluation) {
		this.customerEvaluation = customerEvaluation;
	}

	public int getProviderEvaluation() {
		return providerEvaluation;
	}

	public void setProviderEvaluation(int providerEvaluation) {
		this.providerEvaluation = providerEvaluation;
	}

	public TransactionDirection getDirection() {
		return direction;
	}

	public void setDirection(TransactionDirection direction) {
		this.direction = direction;
	}

	public LocationRepresentation getDeparture_location() {
		return departure_location;
	}

	public void setDeparture_location(LocationRepresentation departure_location) {
		this.departure_location = departure_location;
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

	public LocationRepresentation getArrival_location() {
		return arrival_location;
	}

	public void setArrival_location(LocationRepresentation arrival_location) {
		this.arrival_location = arrival_location;
	}

	public Calendar getArrival_time() {
		return arrival_time;
	}

	public void setArrival_time(Calendar arrival_time) {
		this.arrival_time = arrival_time;
	}

	public DayTimeSlot getArrival_timeSlot() {
		return arrival_timeSlot;
	}

	public void setArrival_timeSlot(DayTimeSlot arrival_timeSlot) {
		this.arrival_timeSlot = arrival_timeSlot;
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

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public transactionState getState() {
		return state;
	}

	public void setState(transactionState state) {
		this.state = state;
	}

	public boolean isHistoryDeleted() {
		return historyDeleted;
	}

	public void setHistoryDeleted(boolean historyDeleted) {
		this.historyDeleted = historyDeleted;
	}

	public Calendar getCreationTime() {
		return creationTime;
	}

	@Override
	public JSONObject toJSON(){
		JSONObject jsonTransaction = new JSONObject();
		
		try {
			jsonTransaction.put("transactionId", this.transactionId);
			jsonTransaction.put("providerId", this.providerId);
			jsonTransaction.put("customerId", this.customerId);
			jsonTransaction.put("messageId", this.messageId);
			jsonTransaction.put("provider", this.provider.toJSON());
			jsonTransaction.put("customer", this.customer.toJSON());
			jsonTransaction.put("message", this.message.toJSON());
			jsonTransaction.put("paymentMethod", this.paymentMethod.code);
			jsonTransaction.put("customerNote", this.customerNote);
			jsonTransaction.put("providerNote", this.providerNote);
			jsonTransaction.put("customerEvaluation", this.customerEvaluation);
			jsonTransaction.put("providerEvaluation", this.providerEvaluation);
			jsonTransaction.put("direction", this.direction.code);
			jsonTransaction.put("departure_location", this.departure_location.toJSON());
			jsonTransaction.put("departure_time", DateUtility.castToAPIFormat(this.departure_time));
			jsonTransaction.put("departure_timeSlot", this.departure_timeSlot.code);
			jsonTransaction.put("departure_seatsBooked", this.departure_seatsBooked);
			jsonTransaction.put("daparture_priceList", new JSONArray(this.departure_priceList));
			jsonTransaction.put("arrival_location", this.arrival_location.toJSON());
			jsonTransaction.put("arrival_time", DateUtility.castToAPIFormat(this.arrival_time));
			jsonTransaction.put("arrival_timeSlot", this.arrival_timeSlot.code);
			jsonTransaction.put("arrival_seatsBooked", this.arrival_seatsBooked);
			jsonTransaction.put("arrival_priceList", new JSONArray(this.arrival_priceList));
			jsonTransaction.put("totalPrice", this.totalPrice);
			jsonTransaction.put("state", this.state.code);
			jsonTransaction.put("creationTime", DateUtility.castToAPIFormat(this.creationTime));
			jsonTransaction.put("historyDeleted", this.historyDeleted);

			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return  jsonTransaction;
	}
	
	
	@Override
	public String toString() {
		return "Transaction [category=" + category + ", transactionId="
				+ transactionId + ", providerId=" + providerId
				+ ", customerId=" + customerId + ", messageId=" + messageId
				+ ", provider=" + provider + ", customer=" + customer
				+ ", message=" + message + ", paymentMethod=" + paymentMethod
				+ ", customerNote=" + customerNote + ", providerNote="
				+ providerNote + ", customerEvaluation=" + customerEvaluation
				+ ", providerEvaluation=" + providerEvaluation + ", direction="
				+ direction + ", departure_location=" + departure_location
				+ ", departure_time=" + departure_time
				+ ", departure_timeSlot=" + departure_timeSlot
				+ ", departure_seatsBooked=" + departure_seatsBooked
				+ ", departure_priceList=" + departure_priceList
				+ ", arrival_location=" + arrival_location + ", arrival_time="
				+ arrival_time + ", arrival_timeSlot=" + arrival_timeSlot
				+ ", arrival_seatsBooked=" + arrival_seatsBooked
				+ ", arrival_priceList=" + arrival_priceList + ", totalPrice="
				+ totalPrice + ", state=" + state + ", creationTime="
				+ creationTime + ", historyDeleted=" + historyDeleted + "]";
	}

	public boolean equals(Transaction t){
		try {
			return t != null &&
					this.transactionId == t.transactionId &&
					this.providerId == t.providerId &&
					this.customerId == t.customerId &&
					this.messageId == t.messageId &&
					this.provider.equals(t.provider) &&
					this.customer.equals(t.customer) &&
					this.message.equals(t.message) &&
					this.paymentMethod == t.paymentMethod &&
					this.customerNote.equals(t.customerNote) &&
					this.providerNote.equals(t.providerNote) &&
					this.customerEvaluation == t.customerEvaluation &&
					this.providerEvaluation == t.providerEvaluation &&
					this.direction == t.direction &&
					this.departure_location.equals(t.departure_location) &&
					this.departure_time.getTime().toString().equals(t.departure_time.getTime().toString()) &&
					this.departure_timeSlot == t.departure_timeSlot &&
					this.departure_seatsBooked == t.departure_seatsBooked &&
					HelperOperator.isArrayListEqual(this.departure_priceList, t.departure_priceList) && 
					this.arrival_location.equals(t.arrival_location) &&
					this.arrival_time.getTime().toString().equals(t.arrival_time.getTime().toString()) &&
					this.arrival_timeSlot == t.arrival_timeSlot &&
					this.arrival_seatsBooked == t.arrival_seatsBooked &&
					HelperOperator.isArrayListEqual(this.arrival_priceList, t.arrival_priceList) && 
					this.totalPrice == t.totalPrice &&
					this.state == t.state &&
					this.historyDeleted == t.historyDeleted;
		} catch (ValidationException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public int compareTo(Transaction t) {
		return this.creationTime.compareTo(t.creationTime);
	}

	@Override
	public boolean validate() throws ValidationException {
		//TODO
		
		return true;
	}
}

