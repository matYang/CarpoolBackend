package carpool.model;


import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.constants.Constants.notificationEvent;
import carpool.constants.Constants.notificationType;
import carpool.interfaces.PseudoModel;




public class Notification implements PseudoModel{
		
	private int notificationId;
	private notificationType notificationType;
	private notificationEvent notificationEvent;
	
	private int initUserId;
	private String initUserName;
	private int messageId;
	private int transcationId;
	
	private int targetUserId;
	private String summary; 

	private Calendar creationTime;
	private boolean checked;
	private boolean historyDeleted;
	
	//default constructor, used for serialization
	public Notification() {
		this.notificationId = -1;
		this.notificationType = notificationType.on_user;
		this.notificationEvent = notificationEvent.followed;
		this.initUserId = -1;
		this.initUserName = "default";
		this.messageId = -1;
		this.transcationId = -1;
		this.targetUserId = -1;
		this.summary = "default";
		this.creationTime = Calendar.getInstance();
		this.checked = false;
		this.historyDeleted = false;
	}
	
	
	/**
	 * notification initialization constructor, it is guaranteed that when the notification is initialized, all necessary parameters will be present
	 * This constructor for the initialization of the notification, messageId and transactionId are optional parameters whose default value should be -1, passed in depending on notification type
	 * if type == on_user, only user initUserId (on_user only corresponds to followed in notificationEvents), leave mId and tId as -1
	 * if type == on_message, pass in both initUserId and messageId, leave tId as -1
	 * if type == on_transaction, pass in both initUserId and transactionId, leave mId as -1
	 * @param notificationType
	 * @param notificationEvent
	 * @param initUserId
	 * @param targetUserId
	 */
	public Notification(notificationType notificationType, notificationEvent notificationEvent, int initUserId, int messageId, int transactionId, int targetUserId) {
		//TODO not sure if this is even need here, depends on SQL situations
		this.notificationId = -1;
		this.notificationType = notificationType;
		this.notificationEvent = notificationEvent;
		this.initUserId = initUserId;
		this.initUserName = "default";
		this.messageId = messageId;
		this.transcationId = transactionId;
		this.targetUserId = targetUserId;
		this.summary = "default";
		this.creationTime = Calendar.getInstance();
		this.checked = false;
		this.historyDeleted = false;
	}


	/**	
	 * full constructor
	 * This constructor is used for sending notification back to the user, 
	 * @param notificationId
	 * @param notificationType
	 * @param notificationEvent
	 * @param initUserId
	 * @param targetUserId
	 * @param messageId
	 * @param transcationId
	 * @param summary     TODO note this is important, summary is not stored in SQL because notifications might be initialized when the operations are all id-based where objects would have to be fetched from sql which is slow, 
	 * and storing this string parameter will cause problems if user changes his name or what, and it is a waste of space
	 * instead, I think it is a better idea to dynamically generate it, the formats are as follows:
	 * if type == on_user, make the summary empty
	 * if type == on_message, follow the specification of DMMessage::toNotificationSummary
	 * if type == on_transaction, follow the specification of Transaction::toNotificationSummary
	 * note those methods are more like examples, retrieving the entire object out and call the toNotificationSummary method would not be efficient
	 * @param createTime
	 * @param checked
	 * @param state
	 * @param historyDeleted
	 */
	public Notification(int notificationId, notificationType notificationType, notificationEvent notificationEvent, int initUserId, String initUserName, int messageId, int transcationId, int targetUserId, String summary, Calendar creationTime, boolean checked, boolean historyDeleted) {
		this.notificationId = notificationId;
		this.notificationType = notificationType;
		this.notificationEvent = notificationEvent;
		this.initUserId = initUserId;
		this.initUserName = initUserName;
		this.messageId = messageId;
		this.transcationId = transcationId;
		this.targetUserId = targetUserId;
		this.summary = summary;
		this.creationTime = creationTime;
		this.checked = checked;
		this.historyDeleted = historyDeleted;
	}

	
	
	@Override
	public String toString() {
		return "Notification [notificationId=" + notificationId + ", type="
				+ notificationType + ", event=" + notificationEvent + ", initUserId=" + initUserId
				+ ", initUserName=" + initUserName + ", messageId=" + messageId
				+ ", transcationId=" + transcationId + ", targetUserId="
				+ targetUserId + ", summary=" + summary + ", creationTime="
				+ creationTime + ", checked=" + checked + ", historyDeleted="
				+ historyDeleted + "]";
	}


	public int getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
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
	
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
	public int getTranscationId() {
		return transcationId;
	}
	public void setTranscationId(int transcationId) {
		this.transcationId = transcationId;
	}
	
	public notificationType getNotificationType() {
		return notificationType;
	}


	public void setNotificationType(notificationType type) {
		this.notificationType = type;
	}


	public notificationEvent getNotificationEvent() {
		return notificationEvent;
	}


	public void setNotificationEvent(notificationEvent event) {
		this.notificationEvent = event;
	}


	public String getInitUserName() {
		return initUserName;
	}


	public void setInitUserName(String initUserName) {
		this.initUserName = initUserName;
	}


	public String getSummary() {
		return summary;
	}


	public void setSummary(String summary) {
		this.summary = summary;
	}


	public Calendar getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
	public boolean isHistoryDeleted() {
		return historyDeleted;
	}
	public void setHistoryDeleted(boolean historyDeleted) {
		this.historyDeleted = historyDeleted;
	}
	
	
	public JSONObject toJSON(){
		JSONObject jsonNotification = new JSONObject(this);
		
		try {
			jsonNotification.put("creationTime", DateUtility.CalendarToUTCString(this.getCreationTime()));
			
			jsonNotification.put("notificationType", this.getNotificationType());
			jsonNotification.put("notificationEvent", this.getNotificationEvent());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonNotification;
	}


}

