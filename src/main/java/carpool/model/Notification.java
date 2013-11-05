package carpool.model;


import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.HelperOperator;
import carpool.constants.Constants.NotificationEvent;
import carpool.constants.Constants.NotificationState;
import carpool.exception.ValidationException;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;

public class Notification implements PseudoModel, PseudoValidatable, Comparable<Notification>{
		
	private int notificationId;
	private NotificationEvent notificationEvent;
	private int targetUserId;
	
	private int initUserId;
	private int messageId;
	private int transactionId;
	
	private User initUser;
	private Message message;
	private Transaction transaction;
	
	private NotificationState state;
	private Calendar creationTime;
	private boolean historyDeleted;
	
	
	@SuppressWarnings("unused")
	private Notification(){}
	
	
	//default constructor, used for serialization/testing
	public Notification(NotificationEvent event, int targetUserId) {
		this.notificationId = -1;
		this.notificationEvent = NotificationEvent.transactionInit;
		this.targetUserId = targetUserId;
		
		this.initUserId = -1;
		this.messageId = -1;
		this.transactionId = -1;
		
		this.initUser = null;
		this.message = null;
		this.transaction = null;
		
		this.state = NotificationState.unread;
		this.creationTime = Calendar.getInstance();
		this.historyDeleted = false;
	}
	
	//init constructor, used for initiation
	public Notification(NotificationEvent event, int targetUserId, int initUserId, int messageId, int transactionId) {
		this.notificationId = -1;
		this.notificationEvent = event;
		this.targetUserId = targetUserId;
		
		this.initUserId = initUserId;
		this.messageId = messageId;
		this.transactionId = transactionId;
		
		this.initUser = null;
		this.message = null;
		this.transaction = null;
		
		this.state = NotificationState.unread;
		this.creationTime = Calendar.getInstance();
		this.historyDeleted = false;
	}
	
	//full constructor, used for SQL
	public Notification(int notificationId,
			NotificationEvent notificationEvent, int targetUserId,
			int initUserId, int messageId, int transactionId,
			NotificationState state, Calendar creationTime,
			boolean historyDeleted) {
		super();
		this.notificationId = notificationId;
		this.notificationEvent = notificationEvent;
		this.targetUserId = targetUserId;
		
		this.initUserId = initUserId;
		this.messageId = messageId;
		this.transactionId = transactionId;
		
		this.initUser = null;
		this.message = null;
		this.transaction = null;
		
		this.state = state;
		this.creationTime = creationTime;
		this.historyDeleted = historyDeleted;
	}
	
	
	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public NotificationEvent getNotificationEvent() {
		return notificationEvent;
	}

	public void setNotificationEvent(NotificationEvent notificationEvent) {
		this.notificationEvent = notificationEvent;
	}


	public int getTargetUserId() {
		return targetUserId;
	}


	public void setTargetUserId(int targetUserId) {
		this.targetUserId = targetUserId;
	}


	public int getInitUserId() {
		return initUserId;
	}


	public void setInitUserId(int initUserId) {
		this.initUserId = initUserId;
	}


	public int getMessageId() {
		return messageId;
	}


	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}


	public int getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}


	public User getInitUser() {
		return initUser;
	}


	public void setInitUser(User initUser) {
		this.initUser = initUser;
	}


	public Message getMessage() {
		return message;
	}


	public void setMessage(Message message) {
		this.message = message;
	}


	public Transaction getTransaction() {
		return transaction;
	}


	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}


	public NotificationState getState() {
		return state;
	}


	public void setState(NotificationState state) {
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

	public JSONObject toJSON(){
		JSONObject jsonNotification = new JSONObject();
		
		try {
			
			jsonNotification.put("notificationId", this.notificationId);
			jsonNotification.put("notificationEvent", this.notificationEvent.code);
			jsonNotification.put("targetUserId", this.targetUserId);
			
			jsonNotification.put("initUserId",this.initUserId);
			jsonNotification.put("messageId", this.messageId);
			jsonNotification.put("transactionId", this.transactionId);
			
			jsonNotification.put("initUser", this.initUser != null ? this.initUser.toJSON() : new JSONObject());
			jsonNotification.put("message", this.message != null ? this.message.toJSON() : new JSONObject());
			jsonNotification.put("transaction", this.transaction != null ? this.transaction.toJSON() : new JSONObject());
			
			
			jsonNotification.put("state", this.state.code);
			jsonNotification.put("creationTime", DateUtility.castToAPIFormat(this.getCreationTime()));
			jsonNotification.put("historyDeleted", this.historyDeleted);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonNotification;
	}
	

	@Override
	public String toString() {
		return "Notification [notificationId=" + notificationId
				+ ", notificationEvent=" + notificationEvent
				+ ", targetUserId=" + targetUserId + ", initUserId="
				+ initUserId + ", messageId=" + messageId + ", transcationId="
				+ transactionId + ", user=" + initUser + ", message=" + message
				+ ", transaction=" + transaction + ", state=" + state
				+ ", creationTime=" + creationTime + ", historyDeleted="
				+ historyDeleted + "]";
	}
	
	public boolean equals(Notification n){
		return n != null &&
				this.notificationId == n.notificationId &&
				this.notificationEvent == n.notificationEvent &&
				this.targetUserId == n.targetUserId &&
				
				this.initUserId == n.initUserId &&
				this.messageId == n.messageId &&
				this.transactionId == n.transactionId &&
				
				this.state == n.state &&
				this.historyDeleted == n.historyDeleted;
	}


	@Override
	public int compareTo(Notification o) {
		return this.creationTime.compareTo(o.creationTime);
	}

	@Override
	public boolean validate() throws ValidationException {
		// TODO
		
		return false;
	}

}

