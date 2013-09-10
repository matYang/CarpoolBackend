package badstudent.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.Constants.transactionState;
import badstudent.common.Constants.paymentMethod;
import badstudent.interfaces.PesudoModel;
import badstudent.mappings.MappingManager;



public class Transaction implements PesudoModel{
		
	public final int category = Constants.category_DM;
	
	private int transactionId;
	private int initUserId;
	private int targetUserId;
	
	private String initUserImgPath;
	private String initUserName;
	private int initUserLevel;
	private String targetUserImgPath;
	private String targetUserName;
	private int targetUserLevel;
	
	private int initUserEval;
	private int targetUserEval;
	
	private int messageId;
	private String messageNote;    //the note inside the message
	private paymentMethod paymentMethod;
	private int price;
	private String requestInfo;
	private String responseInfo;

	private Calendar startTime;
	private Calendar endTime;
	private Location location;

	private boolean established;
	private boolean success;
	private transactionState state;
	private boolean historyDeleted;
	private Calendar creationTime;
    
	//this constructor is used for serialization
	public Transaction(){
		this.transactionId = -1;
		this.initUserId = -1;
		this.targetUserId = -1;
		
		this.initUserImgPath = "default";
		this.initUserName = "default";
		this.initUserLevel = -1;
		this.targetUserImgPath = "default";
		this.targetUserName = "default";
		this.targetUserLevel = -1;
		
		this.initUserEval = 0;
		this.targetUserEval = 0;
		
		this.messageId = -1;
		this.messageNote = "default";
		this.paymentMethod = Constants.paymentMethod.offline;
		this.price = -1;
		this.requestInfo  = "default";
		this.responseInfo = "default";

		this.startTime = Calendar.getInstance();
		this.endTime = Calendar.getInstance();
		this.location =  new Location();

		this.established = false;
		this.success = false;
		this.state = Constants.transactionState.init;
		this.historyDeleted = false;
		this.creationTime = Calendar.getInstance();
	}
	
	//this constructor is used for testing
	public Transaction(int transactionId){
		this.transactionId = transactionId;
		this.initUserId = -1;
		this.targetUserId = -1;
		
		this.initUserImgPath = "default";
		this.initUserName = "default";
		this.initUserLevel = -1;
		this.targetUserImgPath = "default";
		this.targetUserName = "default";
		this.targetUserLevel = -1;
		
		this.initUserEval = 0;
		this.targetUserEval = 0;
		
		this.messageId = -1;
		this.messageNote = "default";
		this.paymentMethod = Constants.paymentMethod.offline;
		this.price = -1;
		this.requestInfo  = "default";
		this.responseInfo = "default";

		this.startTime = Calendar.getInstance();
		this.endTime = Calendar.getInstance();
		this.location =  new Location();

		this.established = false;
		this.success = false;
		this.state = Constants.transactionState.init;
		this.historyDeleted = false;
		this.creationTime = Calendar.getInstance();
	}
	
	/**
	 * Summary Constructor
	 * This constructor is used to send a summarized transaction to the front end, only necessary fields need to be filled
	 * name: summarized transaction
	 * @param transactionId
	 * @param initUserId
	 * @param targetUserId
	 * @param messageId
	 * @param messageNote
	 * @param price
	 * @param startTime
	 * @param location
	 * @param established
	 * @param success
	 * @param state
	 * @param historyDeleted
	 * @param creationTime
	 */
	public Transaction(int transactionId, int initUserId, int targetUserId, int messageId, String messageNote, int price, Calendar startTime, Location location, boolean established, boolean success ,transactionState state, boolean historyDeleted, Calendar creationTime){
		this.transactionId = -1;
		this.initUserId = initUserId;
		this.targetUserId = targetUserId;
		this.messageId = messageId;
		this.messageNote = messageNote;
		
		this.initUserEval = 0;
		this.targetUserEval = 0;
		
		this.paymentMethod = Constants.paymentMethod.offline;
		this.price = price;
		this.requestInfo = "default";
		this.responseInfo = "default";

		this.startTime = startTime;
		this.endTime = Calendar.getInstance();
		this.location = location;

		this.established = established;
		this.success = success;
		this.state = state;
		this.historyDeleted = historyDeleted;
		this.creationTime = creationTime;
	}
	
	/**
	 * Initialization Constructor
	 * This constructor is used as a transaction constructor, the first initialization of a transaction
	 * @param initUserId
	 * @param targetUserId
	 * @param messageId
	 * @param paymentMethod
	 * @param price
	 * @param requestInfo
	 * @param responseInfo
	 * @param startTime
	 * @param endTime
	 * @param location
	 */
	public Transaction(int initUserId, int targetUserId, int messageId ,paymentMethod paymentMethod, int price, String requestInfo, Calendar startTime, Calendar endTime, Location location){
		this.transactionId = -1;
		this.initUserId = initUserId;
		this.targetUserId = targetUserId;
		this.messageId = messageId;
		this.messageNote = "default";
		
		this.initUserEval = 0;
		this.targetUserEval = 0;
		
		this.paymentMethod = paymentMethod;
		this.price = price;
		this.requestInfo = requestInfo;
		this.responseInfo = "default";

		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;

		this.established = false;
		this.success = false;
		this.state = Constants.transactionState.init;
		this.historyDeleted = false;
		this.creationTime = Calendar.getInstance();
	}
	
	
	/**
	 * Full Transaction Constructor
	 */
	
	public Transaction(int transactionId,int initUserId,int targetUserId,String initUserImgPath,String initUserName,int initUserLevel,
			String targetUserImgPath,String targetUserName,int targetUserLevel,int initUserEval, int targetUserEval, int messageId,String messageNote,Constants.paymentMethod paymentMethod,
			int price,String requestInfo,String responseInfo,Calendar startTime,Calendar endTime,Location location,boolean established,
			boolean success,Constants.transactionState state,boolean historyDeleted,Calendar creationTime){
		this.transactionId = transactionId;
		this.initUserId = initUserId;
		this.targetUserId = targetUserId;
		
		this.initUserImgPath = initUserImgPath;
		this.initUserName = initUserName;
		this.initUserLevel = initUserLevel;
		this.targetUserImgPath = targetUserImgPath;
		this.targetUserName = targetUserName;
		this.targetUserLevel = targetUserLevel;
		
		this.initUserEval = initUserEval;
		this.targetUserEval = targetUserEval;
		
		this.messageId = messageId;
		this.messageNote = messageNote;
		this.paymentMethod = paymentMethod;
		this.price = price;
		this.requestInfo = requestInfo;
		this.responseInfo = responseInfo;

		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;

		this.established = established;
		this.success = success;
		this.state = state;
		this.historyDeleted = historyDeleted;
		this.creationTime = creationTime;
	}
	 


	@Override
	public String toString() {
		return "Transaction [category=" + category + ", transactionId="
				+ transactionId + ", initUserId=" + initUserId
				+ ", targetUserId=" + targetUserId + ", initUserImgPath="
				+ initUserImgPath + ", initUserName=" + initUserName
				+ ", intUserLevel=" + initUserLevel + ", targetUserImgPath="
				+ targetUserImgPath + ", targetUserName=" + targetUserName
				+ ", targetUserLevel=" + targetUserLevel + ", messageId="
				+ messageId + ", messageNote=" + messageNote
				+ ", paymentMethod=" + paymentMethod + ", price=" + price
				+ ", requestInfo=" + requestInfo + ", responseInfo="
				+ responseInfo + ", startTime=" + startTime + ", endTime="
				+ endTime + ", location=" + location + ", established="
				+ established + ", success=" + success + ", state=" + state
				+ ", historyDeleted=" + historyDeleted + ", creationTime="
				+ creationTime + "]";
	}
	
	public String toNotificationSummary(){
		return Common.getNotificationDateString(this.startTime) + "与您的交易";
	}

	public String getMessageNote() {
		return messageNote;
	}

	public void setMessageNote(String messageNote) {
		this.messageNote = messageNote;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getInitUserId() {
		return initUserId;
	}

	public void setInitUserId(int initUserId) {
		this.initUserId = initUserId;
	}

	public int getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(int targetUserId) {
		this.targetUserId = targetUserId;
	}

	public String getInitUserImgPath() {
		return initUserImgPath;
	}

	public void setInitUserImgPath(String initUserImgPath) {
		this.initUserImgPath = initUserImgPath;
	}

	public String getInitUserName() {
		return initUserName;
	}

	public void setInitUserName(String initUserName) {
		this.initUserName = initUserName;
	}

	public int getInitUserLevel() {
		return initUserLevel;
	}

	public void setInitUserLevel(int intUserLevel) {
		this.initUserLevel = intUserLevel;
	}

	public String getTargetUserImgPath() {
		return targetUserImgPath;
	}

	public void setTargetUserImgPath(String targetUserImgPath) {
		this.targetUserImgPath = targetUserImgPath;
	}

	public String getTargetUserName() {
		return targetUserName;
	}

	public void setTargetUserName(String targetUserName) {
		this.targetUserName = targetUserName;
	}

	public int getTargetUserLevel() {
		return targetUserLevel;
	}

	public void setTargetUserLevel(int targetUserLevel) {
		this.targetUserLevel = targetUserLevel;
	}

	public int getInitUserEval() {
		return initUserEval;
	}

	public void setInitUserEval(int initUserEval) {
		this.initUserEval = initUserEval;
	}

	public int getTargetUserEval() {
		return targetUserEval;
	}

	public void setTargetUserEval(int targetUserEval) {
		this.targetUserEval = targetUserEval;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public paymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(paymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(String requestInfo) {
		this.requestInfo = requestInfo;
	}

	public String getResponseInfo() {
		return responseInfo;
	}

	public void setResponseInfo(String responseInfo) {
		this.responseInfo = responseInfo;
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isEstablished() {
		return established;
	}

	public void setEstablished(boolean established) {
		this.established = established;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
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
	 * TODO: (this is corresponding to he summarized transaction constructor specified above)
	 * required fields"
	 * @param transactionId
	 * @param initUserId
	 * @param targetUserId
	 * @param messageId
	 * @param messageNote
	 * @param price
	 * @param startTime
	 * @param location
	 * @param established
	 * @param success
	 * @param state
	 * @param historyDeleted
	 * @param creationTime
	 */
	public void prepareBrief(){
		
	}
	
	
	public JSONObject toJSON(){
		JSONObject jsonTransaction = new JSONObject(this);
		
		try {
			jsonTransaction.put("startTime", Common.CalendarToUTCString(this.getStartTime()));
			jsonTransaction.put("endTime", Common.CalendarToUTCString(this.getEndTime()));
			jsonTransaction.put("creationTime", Common.CalendarToUTCString(this.getCreationTime()));
			
			jsonTransaction.put("location", this.location.toJSON());
			
			jsonTransaction.put("paymentMethod", this.getPaymentMethod());
			jsonTransaction.put("state", this.getState());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return  jsonTransaction;
	}
}

