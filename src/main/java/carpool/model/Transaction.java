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
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.TransactionType;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.paymentMethod;
import carpool.constants.Constants.transactionState;
import carpool.exception.validation.ValidationException;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;



public class Transaction implements PseudoModel, PseudoValidatable, Comparable<Transaction>{
		
	public final int category = CarpoolConfig.category_DM;
	
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
	private Location departure_location;
	private long departure_Id;
	private Location arrival_location;
	private long arrival_Id;

	private Calendar departure_time;
	private DayTimeSlot departure_timeSlot;
	private int departure_seatsBooked;
	private ArrayList<Integer> departure_priceList;
	
	private TransactionType type;
	private int totalPrice;
	private transactionState state;
	
	private Calendar creationTime;
	private boolean historyDeleted;

	
	@SuppressWarnings("unused")
	private Transaction(){}
	
	//this constructor is used for transaction initialization
	public Transaction(int providerId, int customerId, int messageId, paymentMethod p, String cNote, String pNote,Calendar d_t, DayTimeSlot d_ts, int d_seats, TransactionType type){
		super();
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
				
		this.departure_time = d_t;
		this.departure_timeSlot = d_ts;
		this.departure_seatsBooked = d_seats;
		this.departure_priceList = new ArrayList<Integer>();
		
		this.type = type;
		this.totalPrice = 0;
		this.state = transactionState.init;
		this.creationTime = DateUtility.getCurTimeInstance();
		this.historyDeleted = false;
	}
	

	
	public Transaction(int transactionId, int providerId, int customerId,
			int messageId,
			carpool.constants.Constants.paymentMethod paymentMethod,
			String customerNote, String providerNote, int customerEvaluation,
			int providerEvaluation, Location departure_location,
			Location arrival_location, Calendar departure_time,
			DayTimeSlot departure_timeSlot, int departure_seatsBooked,
			ArrayList<Integer> departure_priceList, TransactionType type,
			int totalPrice, transactionState state, Calendar creationTime,
			boolean historyDeleted) {
		super();
		this.transactionId = transactionId;
		this.providerId = providerId;
		this.customerId = customerId;
		this.messageId = messageId;
		this.paymentMethod = paymentMethod;
		this.customerNote = customerNote;
		this.providerNote = providerNote;
		this.customerEvaluation = customerEvaluation;
		this.providerEvaluation = providerEvaluation;
		this.departure_location = departure_location;
		this.departure_Id = departure_location.getId();
		this.arrival_location = arrival_location;
		this.arrival_Id = arrival_location.getId();
		this.departure_time = departure_time;
		this.departure_timeSlot = departure_timeSlot;
		this.departure_seatsBooked = departure_seatsBooked;
		this.departure_priceList = departure_priceList;
		this.type = type;
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

	public long getDeparture_Id() {
		return departure_Id;
	}


	public void setDeparture_Id(long departure_Id) {
		this.departure_Id = departure_Id;
	}

	public long getArrival_Id() {
		return arrival_Id;
	}

	public void setArrival_Id(long arrival_Id) {
		this.arrival_Id = arrival_Id;
	}

	public void setDeparture_location(Location departure_location) {
		this.departure_location = departure_location;
	}

	public void setArrival_location(Location arrival_location) {
		this.arrival_location = arrival_location;
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

	public Location getDeparture_location() {
		return departure_location;
	}

	public Location getArrival_location() {
		return arrival_location;
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

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
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
			jsonTransaction.put("provider", this.provider != null ? this.provider.toJSON() : new JSONObject());
			jsonTransaction.put("customer", this.customer != null ? this.customer.toJSON() : new JSONObject());
			jsonTransaction.put("message", this.message != null ? this.message.toJSON() : new JSONObject());
			jsonTransaction.put("paymentMethod", this.paymentMethod.code);
			jsonTransaction.put("customerNote", this.customerNote);
			jsonTransaction.put("providerNote", this.providerNote);
			jsonTransaction.put("customerEvaluation", this.customerEvaluation);
			jsonTransaction.put("providerEvaluation", this.providerEvaluation);
			
			jsonTransaction.put("departure_location", this.departure_location.toJSON());
			jsonTransaction.put("arrival_location", this.arrival_location.toJSON());
			jsonTransaction.put("departure_time", DateUtility.castToAPIFormat(this.departure_time));
			jsonTransaction.put("departure_timeSlot", this.departure_timeSlot.code);
			jsonTransaction.put("departure_seatsBooked", this.departure_seatsBooked);
			jsonTransaction.put("departure_priceList", new JSONArray(this.departure_priceList));

			jsonTransaction.put("type", this.type.code);
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
				+ ", providerEvaluation=" + providerEvaluation
				+ ", departure_location=" + departure_location
				+ ", arrival_location=" + arrival_location
				+ ", departure_time=" + departure_time
				+ ", departure_timeSlot=" + departure_timeSlot
				+ ", departure_seatsBooked=" + departure_seatsBooked
				+ ", departure_priceList=" + departure_priceList + ", type="
				+ type + ", totalPrice=" + totalPrice + ", state=" + state
				+ ", creationTime=" + creationTime + ", historyDeleted="
				+ historyDeleted + "]";
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
					this.departure_location.equals(t.departure_location) &&
					this.departure_time.getTime().toString().equals(t.departure_time.getTime().toString()) &&
					this.departure_timeSlot == t.departure_timeSlot &&
					this.departure_seatsBooked == t.departure_seatsBooked &&
					HelperOperator.isArrayListEqual(this.departure_priceList, t.departure_priceList) && 
					this.arrival_location.equals(t.arrival_location) &&
					this.type == t.type &&
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

