package carpool.dbservice;

import java.util.ArrayList;
import java.util.Calendar;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.constants.Constants.userSearchState;
import carpool.database.DaoMessage;
import carpool.database.DaoTransaction;
import carpool.database.DaoUser;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.UnacceptableSearchStateException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;



public class MessageDaoService{
	
	
	public static ArrayList<Message> getAllMessages() {
		return DaoMessage.getAll();
	}
	

	public static Message getMessageById(int messageId) throws MessageNotFoundException{
		return DaoMessage.getMessageById(messageId);
	}
	
	
	/**
	 * gets the recently posted messages, length specified by Constants.max_recents (currently 3)
	 */
	public static ArrayList<Message> getRecentMessages() {
		//TODO can not use getAll
		ArrayList<Message> all = DaoMessage.getAll();
		ArrayList<Message> retVal = new ArrayList<Message>();
		for(int i=0; i< Constants.max_recents;i++){
			retVal.add(all.get(all.size()-1-i));
		}
		return retVal;
	}
	
	
	/**
	 * note that, should include both types of messages
	 * note that only use year-month-date in this calendar instance to search for matching messages, not the matching of entire date strings
	 */
	public static ArrayList<Message> primaryMessageSearch(Location location,Calendar date, userSearchState searchState) throws UnacceptableSearchStateException{
		//universityAsk(0), universityHelp(1), regionAsk(2), regionHelp(3), universityGroupAsk(4), universityGroupHelp(5);
		int remainder = searchState.code % 2;
		messageType type = messageType.ask;
		if(remainder==1){
			type = messageType.help;
		}
		if(searchState.code<2){
			return DaoMessage.searchMessageSingle(location.toString(),DateUtility.toSQLDateTime(date),type.code+"");
		}else if(searchState.code<4){
			return DaoMessage.searchMessageRegion(location.toString(),DateUtility.toSQLDateTime(date),type.code+"");
		}else{
			throw new UnacceptableSearchStateException();
		}
	}

	public static Message createNewMessage(Message newMessage){
		sendFollowerNewPostNotification(newMessage);
		return DaoMessage.addMessageToDatabase(newMessage);
	}
	
	
	/**
	 * check if the retrieved message's ownerId matches current message's ownerId, if not, throw MessageOwnerNotMatchException (some for methods below)
	 */
	public static Message updateMessage(Message message) throws MessageNotFoundException, MessageOwnerNotMatchException{
		Message oldMessage = DaoMessage.getMessageById(message.getMessageId());
		if(oldMessage.getOwnerId()!=message.getOwnerId()){
			throw new MessageOwnerNotMatchException();
		}
		DaoMessage.UpdateMessageInDatabase(message);
		sendMessageUpDateNotification(message);
		return message;
	}
	

	/**
	 * can not delete if this message has active transactions
	 */
	public static boolean deleteMessage(int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException, ValidationException{
		Message oldMessage = DaoMessage.getMessageById(messageId);
		if(oldMessage.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		ArrayList<Transaction> tList = getRelatedTransactions(messageId);
		for(Transaction t : tList){
			if(t.getState().code < 5 || t.getState().code == 6){
				throw new ValidationException("Message Has Active Transactions");
			}
		}
		DaoMessage.deleteMessageFromDatabase(oldMessage.getMessageId());
		sendMessageDeleteNotification(oldMessage);
		return true;
	}
	

	
	/**
	 * retrives a list of transactions associated with the target message ID
	 * @param messageId
	 * @return	an arrayList of transactions based on the target message, null if any errors occurred
	 * @throws MessageNotFoundException
	 */
	public static ArrayList<Transaction> getRelatedTransactions(int messageId) throws MessageNotFoundException {
		MessageDaoService.getMessageById(messageId);
		return DaoTransaction.getTransactionByMessage(messageId);
	}
	
	
	
	private static void sendMessageUpDateNotification(Message msg){
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		
//		TODO		
//		for(User user : DaoUser.getUserWhoWatchedMessage(msg.getMessageId())){
//			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.watchingMessageModified,
//					msg.getOwnerId(), msg.getOwnerName(),msg.getMessageId(), 0, user.getUserId(),
//					"The Message you've watched has change",Calendar.getInstance(), false, false);
//			notifications.add(n);
//		}
		
		NotificationDaoService.createNewNotificationQueue(notifications);
	}
	
	private static void sendMessageDeleteNotification(Message msg){
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		
//		TODO		
//		for(User user : DaoUser.getUserWhoWatchedMessage(msg.getMessageId())){
//			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.watchingMessageModified,
//					msg.getOwnerId(), msg.getOwnerName(),msg.getMessageId(), 0, user.getUserId(),
//					"The Message you've watched no longer exsit",Calendar.getInstance(), false, false);
//			notifications.add(n);
//		}
		
		NotificationDaoService.createNewNotificationQueue(notifications);
	}
	
	private static void sendFollowerNewPostNotification(Message msg){
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		
//		TODO
//		for(User user : DaoUser.getUserWhoWatchedUser(msg.getOwnerId())){
//			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.followerNewPost,
//					msg.getOwnerId(), msg.getOwnerName(), msg.getMessageId(), 0, user.getUserId(),
//					"The User you followed has just post a new Message.", Calendar.getInstance(), false, false);
//			notifications.add(n);
//		}
		
		NotificationDaoService.createNewNotificationQueue(notifications);
	}

	
}
