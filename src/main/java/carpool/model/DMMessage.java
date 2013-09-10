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
import carpool.interfaces.PesudoModel;


public class DMMessage implements PesudoModel{
	
	public final int category = Constants.category_DM;
	
	private int messageId;
	private int ownerId;
	
	private String ownerImgPath;
	private String ownerName;
	private int ownerLevel;
	private int ownerAverageScore;
	private String ownerPhone;
	private String ownerEmail;
	private String ownerQq;
	private paymentMethod paymentMethod;   //refer to common.Constants, though for now we'll be using offline only, it will be guaranteed on API level, allow flexibility in underlying logic
	
	private Location location;
	private Calendar startTime;
	private Calendar endTime;
	
	private String note;
	private messageType type;
	private gender genderRequirement;
	private messageState state;
	
	private int price;
	private boolean active;
	private boolean historyDeleted;

	private ArrayList<Transaction> transactionList;
	private Calendar creationTime;
	

	//default constructor for serialization, internal user only
	public DMMessage(){
		this.messageId = -1;
		this.ownerId = -1;
		
		this.ownerImgPath = "default";
		this.ownerName = "default";
		this.ownerLevel = -1;
		this.ownerAverageScore = -1;
		this.ownerPhone = "default";
		this.ownerEmail = "default";
		this.ownerQq = "default";
		this.paymentMethod = Constants.paymentMethod.offline; 

		this.location = new Location();
		this.startTime = Calendar.getInstance();
		this.endTime = Calendar.getInstance();
		
		this.transactionList = new ArrayList<Transaction>();

		this.note = "deault";
		this.type = Constants.messageType.ask;
		this.genderRequirement = Constants.gender.both;
		this.state = Constants.messageState.normal;
		
		this.price = 0;
		this.active = false;
		this.historyDeleted = false;
		this.creationTime = Calendar.getInstance();
	}
	//used to solely testing 
	public DMMessage(int messageId){
		this.messageId = messageId;
		this.ownerId = -1;
		
		this.ownerImgPath = "default";
		this.ownerName = "default";
		this.ownerLevel = -1;
		this.ownerAverageScore = -1;
		this.ownerPhone = "default";
		this.ownerEmail = "default";
		this.ownerQq = "default";
		this.paymentMethod = Constants.paymentMethod.offline;
		
		this.location = new Location();
		this.startTime = Calendar.getInstance();
		this.endTime = Calendar.getInstance();
		
		this.transactionList = new ArrayList<Transaction>();

		this.note = "deault";
		this.type = Constants.messageType.ask;
		this.genderRequirement = Constants.gender.both;
		this.state = Constants.messageState.normal;
		
		this.price = 0;
		this.active = false;
		this.historyDeleted = false;
		this.creationTime = Calendar.getInstance();
	}
	
	/**
	 * This constructor is used for constructing the abbreviated message objects that will become a parameter
	 *  (watchList) of the last 2 User constructors, and in the Notification messageSummary
	 * name: brief message constructor
	 * @param messageId
	 * @param ownerId
	 * @param ownerImgPath
	 * @param ownerName
	 * @param location
	 * @param startTime
	 * @param type
	 * @param state
	 * @param price
	 * @param active
	 * @param historyDeleted
	 * @param creationTime
	 */
	public DMMessage(int messageId, int ownerId, String ownerImgPath, String ownerName, Location location,
			Calendar startTime, messageType type, messageState state, int price, boolean active,
			boolean historyDeleted, Calendar creationTime) {
		this.messageId = messageId;
		this.ownerId = ownerId;
		this.ownerImgPath = ownerImgPath;
		this.ownerName = ownerName;
		this.location = location;
		this.startTime = startTime;
		this.type = type;
		this.state = state;
		this.price = price;
		this.active = active;
		this.historyDeleted = historyDeleted;
		this.creationTime = creationTime;
		
		//below are fields being filled to provide null-pointer-exception protection
		this.ownerLevel = -1;
		this.ownerAverageScore = -1;
		this.ownerPhone = "default";
		this.ownerEmail = "default";
		this.ownerQq = "default";
		this.paymentMethod = Constants.paymentMethod.offline;  
		this.endTime = Calendar.getInstance();
		this.transactionList = new ArrayList<Transaction>();
		this.note = "deault";
		this.genderRequirement = Constants.gender.both;
	}

	
	/**
	 * This constructor is used for constructing less abbreviated message objects that will become a parameter 
	 * (historyList) of the personal-page-main page User constructor and search Results
	 * name: simple message constructor
	 * @param messageId
	 * @param ownerId
	 * @param ownerImgPath
	 * @param ownerName
	 * @param location
	 * @param startTime
	 * @param endTime
	 * @param note
	 * @param type
	 * @param genderRequirement
	 * @param state
	 * @param price
	 * @param paymentMethod
	 * @param active
	 * @param historyDeleted
	 * @param creationTime 
	 */

	public DMMessage(int messageId, int ownerId, String ownerImgPath, String ownerName, Location location,
			Calendar startTime, Calendar endTime, String note, messageType type, gender genderRequirement,
			messageState state, int price, paymentMethod paymentMethod, boolean active, boolean historyDeleted, Calendar creationTime) {
		this.messageId = messageId;
		this.ownerId = ownerId;
		this.ownerImgPath = ownerImgPath;
		this.ownerName = ownerName;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.note = note;
		this.type = type;
		this.genderRequirement = genderRequirement;
		this.state = state;
		this.price = price;
		this.paymentMethod = paymentMethod;
		this.active = active;
		this.historyDeleted = historyDeleted;
		this.creationTime = creationTime;
		
		//below are fields being filled to provide null-pointer-exception protection
		this.ownerLevel = -1;
		this.ownerAverageScore = -1;
		this.ownerPhone = "default";
		this.ownerEmail = "default";
		this.ownerQq = "default";
		this.transactionList = new ArrayList<Transaction>();
	}
	/**
	 * no-id constructor, this will be used for POST, basically when a message is first created
	 * @param ownerId
	 * @param paymentMethod
	 * @param location
	 * @param startTime
	 * @param endTime
	 * @param note
	 * @param type
	 * @param genderRequirement
	 * @param price
	 */
	public DMMessage(int ownerId, paymentMethod paymentMethod, Location location, Calendar startTime,
			Calendar endTime, String note, messageType type, gender genderRequirement, int price) {
		this.messageId = -1;
		this.ownerId = ownerId;
		this.ownerImgPath = "default";
		this.ownerName = "default";
		this.ownerLevel = -1;
		this.ownerAverageScore = -1;
		this.ownerPhone = "default";
		this.ownerEmail = "default";
		this.ownerQq = "default";
		this.paymentMethod = paymentMethod;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.note = note;
		this.type = type;
		this.genderRequirement = genderRequirement;
		this.state = Constants.messageState.normal;
		this.price = price;
		this.active = true;
		this.historyDeleted = false;
		this.transactionList = new ArrayList<Transaction>();
		this.creationTime = Calendar.getInstance();
	}
	
	/**
	 * this constructor is used for PUT only, updating message as a whole
	 * @param messageId
	 * @param ownerId
	 * @param paymentMethod
	 * @param location
	 * @param startTime
	 * @param endTime
	 * @param note
	 * @param type
	 * @param genderRequirement
	 * @param price
	 */
	public DMMessage(int messageId, int ownerId, paymentMethod paymentMethod, Location location,
			Calendar startTime, Calendar endTime, String note, messageType type, gender genderRequirement, int price){
		this.messageId = messageId;
		this.ownerId = ownerId;
		this.ownerImgPath = "default";
		this.ownerName = "default";
		this.ownerLevel = -1;
		this.ownerAverageScore = -1;
		this.ownerPhone = "default";
		this.ownerEmail = "default";
		this.ownerQq = "default";
		this.paymentMethod = paymentMethod;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.note = note;
		this.type = type;
		this.genderRequirement = genderRequirement;
		this.state = Constants.messageState.normal;
		this.price = price;
		this.active = true;
		this.historyDeleted = false;
		this.transactionList = new ArrayList<Transaction>();
		this.creationTime = Calendar.getInstance();
	}
	
	
	/**
	 * full constructor, this will be used for sending back data needed for message detail page
	 * name: full constructor
	 * @param messageId
	 * @param ownerId
	 * @param ownerImgPath
	 * @param ownerName
	 * @param ownerLevel
	 * @param ownerAverageScore
	 * @param ownerPhone
	 * @param ownerEmail
	 * @param ownerQq
	 * @param paymentMethod
	 * @param location
	 * @param startTime
	 * @param endTime
	 * @param note
	 * @param type
	 * @param genderRequirement
	 * @param state
	 * @param price
	 * @param active
	 * @param historyDeleted
	 * @param transactionList
	 * @param creationTime
	 */
	public DMMessage(int messageId, int ownerId, String ownerImgPath, String ownerName, int ownerLevel,
			int ownerAverageScore, String ownerPhone, String ownerEmail, String ownerQq, paymentMethod paymentMethod,
			Location location, Calendar startTime, Calendar endTime, String note, messageType type,
			gender genderRequirement, messageState state, int price, boolean active, boolean historyDeleted,
			ArrayList<Transaction> transactionList, Calendar creationTime) {
		this.messageId = messageId;
		this.ownerId = ownerId;
		this.ownerImgPath = ownerImgPath;
		this.ownerName = ownerName;
		this.ownerLevel = ownerLevel;
		this.ownerAverageScore = ownerAverageScore;
		this.ownerPhone = ownerPhone;
		this.ownerEmail = ownerEmail;
		this.ownerQq = ownerQq;
		this.paymentMethod = paymentMethod;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.note = note;
		this.type = type;
		this.genderRequirement = genderRequirement;
		this.state = state;
		this.price = price;
		this.active = active;
		this.historyDeleted = historyDeleted;
		this.transactionList = transactionList;
		this.creationTime = creationTime;
	}


	@Override
	public String toString() {
		return "DMMessage [category=" + category + ", messageId=" + messageId
				+ ", ownerId=" + ownerId + ", ownerImgPath=" + ownerImgPath
				+ ", ownerName=" + ownerName + ", ownerLevel=" + ownerLevel
				+ ", ownerAverageScore=" + ownerAverageScore + ", ownerPhone="
				+ ownerPhone + ", ownerEmail=" + ownerEmail + ", ownerQq="
				+ ownerQq + ", paymentMethod=" + paymentMethod + ", location="
				+ location + ", startTime=" + startTime + ", endTime="
				+ endTime + ", note=" + note + ", type=" + type
				+ ", genderRequirement=" + genderRequirement + ", state="
				+ state + ", price=" + price + ", active=" + active
				+ ", historyDeleted=" + historyDeleted + ", transactionList="
				+ transactionList + ", creationTime=" + creationTime + "]";
	}
	
	public String toNotificationSummary(){
		String typeSufix = (type == messageType.ask ? "求点名" : "帮点名") + "的消息";
		return Common.getNotificationDateString(this.startTime) + typeSufix;
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

	public String getOwnerImgPath() {
		return ownerImgPath;
	}

	public void setOwnerImgPath(String ownerImgPath) {
		this.ownerImgPath = ownerImgPath;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public int getOwnerLevel() {
		return ownerLevel;
	}

	public void setOwnerLevel(int ownerLevel) {
		this.ownerLevel = ownerLevel;
	}

	public int getOwnerAverageScore() {
		return ownerAverageScore;
	}

	public void setOwnerAverageScore(int ownerAverageScore) {
		this.ownerAverageScore = ownerAverageScore;
	}

	public String getOwnerPhone() {
		return ownerPhone;
	}

	public void setOwnerPhone(String ownerPhone) {
		this.ownerPhone = ownerPhone;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getOwnerQq() {
		return ownerQq;
	}

	public void setOwnerQq(String ownerQq) {
		this.ownerQq = ownerQq;
	}

	public paymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(paymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isHistoryDeleted() {
		return historyDeleted;
	}

	public void setHistoryDeleted(boolean historyDeleted) {
		this.historyDeleted = historyDeleted;
	}

	public ArrayList<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(ArrayList<Transaction> transactionList) {
		this.transactionList = transactionList;
	}

	public int getCategory() {
		return category;
	}
	public Calendar getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}
	
	
	/**
	 * checks if the existing message is valid, eg, expired or not, content and state
	 * @return
	 */
	public boolean isMessageValid() {
		return isNoteValid(this.note) && isPriceValid(this.price, this.startTime, this.endTime) && isTimeValid(this.startTime, this.endTime) && isPaymentMethodValid(this.paymentMethod) && isMessageTypeValid(this.type) && this.state == Constants.messageState.normal && !(this.historyDeleted);
	}
	
	/**
	 * checks if the target message's new note is fine
	 * @param newNote
	 * @return
	 */
	public static boolean isNoteValid(String newNote) {
		// TODO
		return true;
	}
	
	/**
	 * checks of the new price is valid, eg less than minimum, not more than maximum
	 * @param newPrice
	 * @return
	 */
	public static boolean isPriceValid(int newPrice, Calendar startTime, Calendar endTime) {
		int hourPrice = newPrice / Common.getHourDifference(startTime, endTime);
		return hourPrice > Constants.min_DMMessageHourPrice && hourPrice < Constants.max_DMMessageHourPrice;
	}

	/**
	 * checks if the start time and end time are both in valid formats, valid range
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isTimeValid(Calendar startTime, Calendar endTime) {
		return Common.isTimeValid(startTime, endTime);
	}
	
	/**
	 * checks if the current payment method is valid and satisfies all conditions, in v1.0 it can only be offline
	 * @param newPaymentMethod
	 * @return
	 */
	public static boolean isPaymentMethodValid(paymentMethod newPaymentMethod) {
		return newPaymentMethod==Constants.paymentMethod.offline;
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
	 * TODO: (corresponding to the brief message constructor specified above)
	 * ***this is used for sending brief messages, apply this to messages in User::watchList and Notification::messageSummary
	 * required fields:
	 * @param messageId
	 * @param ownerId
	 * @param ownerImgPath
	 * @param ownerName
	 * @param location
	 * @param startTime
	 * @param type
	 * @param state
	 * @param price
	 * @param active
	 * @param historyDeleted
	 * @param creationTime
	 */
	public void prepareBrief(){
		
	}
	
	/**
	 * TODO: (corresponding to the simple message constructor specified above)
	 * *** this is used for sending snap shots of messages, apply this to messages in User::historyList, any message searching results/recent messages in DMDaoService
	 * required fields:
	 * @param messageId
	 * @param ownerId
	 * @param ownerImgPath
	 * @param ownerName
	 * @param location
	 * @param startTime
	 * @param endTime
	 * @param note
	 * @param type
	 * @param genderRequirement
	 * @param state
	 * @param price
	 * @param paymentMethod
	 * @param active
	 * @param historyDeleted
	 * @param creationTime
	 */
	public void prepareSimple(){
		
	}
	
	/**
	 * TODO: note that, to send a full message back, apply Transaction::prepareBrief to transactlionList
	 */
	
	
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

}

