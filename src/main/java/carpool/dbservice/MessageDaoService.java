package carpool.dbservice;

import java.util.ArrayList;

import carpool.carpoolDAO.*;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants.messageState;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;
import carpool.constants.*;


public class MessageDaoService{
	
	
	public static ArrayList<Message> getAllMessages() throws LocationNotFoundException {
		return CarpoolDaoMessage.getAllMessages();
	}
	

	public static Message getMessageById(int messageId) throws MessageNotFoundException, UserNotFoundException, LocationNotFoundException{
		return CarpoolDaoMessage.getMessageById(messageId);
	}
	
	
	/**
	 * gets the recently posted messages, length specified by Constants.max_recents (currently 10)
	 * @throws LocationNotFoundException 
	 */
	public static ArrayList<Message> getRecentMessages() throws LocationNotFoundException{
		return CarpoolDaoMessage.getRecentMessages();
	}
	

	public static ArrayList<Message> primaryMessageSearch(SearchRepresentation userSearch, boolean isLogin, int userId) throws PseudoException{
//		userSearch.setDepartureDate(DateUtility.castFromAPIFormat("2013-10-21 21:47:50"));
		ArrayList<Message> searchResult = new ArrayList<Message>();
		searchResult = CarpoolDaoMessage.searchMessage(userSearch);
		if (isLogin){
			UserDaoService.updateUserSearch(userSearch, userId);
		}
		return searchResult;
	}
	

	public static Message createNewMessage(Message newMessage) throws LocationNotFoundException{
		return CarpoolDaoMessage.addMessageToDatabase(newMessage);
	}
	
	
	/**
	 * check if the retrieved message's ownerId matches current message's ownerId, if not, throw MessageOwnerNotMatchException (some for methods below)
	 * @throws LocationNotFoundException 
	 */
	public static Message updateMessage(Message message) throws MessageNotFoundException, MessageOwnerNotMatchException, UserNotFoundException, LocationNotFoundException{
		Message oldMessage = CarpoolDaoMessage.getMessageById(message.getMessageId());
		if(oldMessage.getOwnerId()!=message.getOwnerId()){
			throw new MessageOwnerNotMatchException();
		}
		CarpoolDaoMessage.UpdateMessageInDatabase(message);
		//sendMessageUpDateNotification(message);
		return message;
	}
	

	/**
	 * can not delete if this message has active transactions
	 * @throws LocationNotFoundException 
	 */
	public static boolean deleteMessage(int messageId) throws MessageNotFoundException, MessageOwnerNotMatchException, ValidationException, UserNotFoundException, LocationNotFoundException{
		Message oldMessage = CarpoolDaoMessage.getMessageById(messageId);
//		if(oldMessage.getOwnerId()!=ownerId){
//			throw new MessageOwnerNotMatchException();
//		}
		oldMessage.setState(messageState.deleted);
		CarpoolDaoMessage.UpdateMessageInDatabase(oldMessage);
		return true;
	}

	
	/**
	 * retrives a list of transactions associated with the target message ID
	 * @throws LocationNotFoundException 
	 */
	public static ArrayList<Transaction> getTransactionByMessageId(int messageId) throws MessageNotFoundException, UserNotFoundException, LocationNotFoundException {
		return CarpoolDaoTransaction.getAllTransactionByMessageId(messageId);
	}
	
	
	
	/*
	* get the auto-matching messages, make upper limit 10 messaeges
	*/
	public static ArrayList<Message> autoMatching(int curMsgId, int curUserId) throws MessageNotFoundException, PseudoException{
		Message targetMessage = CarpoolDaoMessage.getMessageById(curMsgId);
		SearchRepresentation sr = new SearchRepresentation(targetMessage.isRoundTrip(), targetMessage.getDeparture_Id(),
                       targetMessage.getArrival_Id(), targetMessage.getDeparture_time(), targetMessage.getArrival_time(),
			targetMessage.getType() == Constants.messageType.ask ? Constants.messageType.help : Constants.messageType.ask,
                        targetMessage.getDeparture_timeSlot(), targetMessage.getArrival_timeSlot());
                
                //use unlogged in state to avoid automatching being recorded in search history   
               	return primaryMessageSearch(sr, false, -1);
	}
	
	
//	
//	private static void sendMessageUpDateNotification(Message msg){
//		ArrayList<Notification> notifications = new ArrayList<Notification>();
//		
////		TODO		
////		for(User user : DaoUser.getUserWhoWatchedMessage(msg.getMessageId())){
////			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.watchingMessageModified,
////					msg.getOwnerId(), msg.getOwnerName(),msg.getMessageId(), 0, user.getUserId(),
////					"The Message you've watched has change",Calendar.getInstance(), false, false);
////			notifications.add(n);
////		}
//		
//		NotificationDaoService.createNewNotificationQueue(notifications);
//	}
//	
//	private static void sendMessageDeleteNotification(Message msg){
//		ArrayList<Notification> notifications = new ArrayList<Notification>();
//		
////		TODO		
////		for(User user : DaoUser.getUserWhoWatchedMessage(msg.getMessageId())){
////			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.watchingMessageModified,
////					msg.getOwnerId(), msg.getOwnerName(),msg.getMessageId(), 0, user.getUserId(),
////					"The Message you've watched no longer exsit",Calendar.getInstance(), false, false);
////			notifications.add(n);
////		}
//		
//		NotificationDaoService.createNewNotificationQueue(notifications);
//	}
//	
//	private static void sendFollowerNewPostNotification(Message msg){
//		ArrayList<Notification> notifications = new ArrayList<Notification>();
//		
////		TODO
////		for(User user : DaoUser.getUserWhoWatchedUser(msg.getOwnerId())){
////			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.followerNewPost,
////					msg.getOwnerId(), msg.getOwnerName(), msg.getMessageId(), 0, user.getUserId(),
////					"The User you followed has just post a new Message.", Calendar.getInstance(), false, false);
////			notifications.add(n);
////		}
//		
//		NotificationDaoService.createNewNotificationQueue(notifications);
//	}

	
}
