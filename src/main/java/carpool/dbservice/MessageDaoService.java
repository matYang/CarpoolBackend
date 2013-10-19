package carpool.dbservice;

import java.util.ArrayList;

import carpool.carpoolDAO.*;
import carpool.common.DebugLog;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;



public class MessageDaoService{
	
	
	public static ArrayList<Message> getAllMessages() {
		return CarpoolDaoMessage.getAllMessages();
	}
	

	public static Message getMessageById(int messageId) throws MessageNotFoundException, UserNotFoundException{
		return CarpoolDaoMessage.getMessageById(messageId);
	}
	
	
	/**
	 * gets the recently posted messages, length specified by Constants.max_recents (currently 10)
	 */
	public static ArrayList<Message> getRecentMessages(){
		return CarpoolDaoMessage.getRecentMessages();
	}
	

	public static ArrayList<Message> primaryMessageSearch(SearchRepresentation userSearch, boolean isLogin, int userId) throws PseudoException{

		ArrayList<Message> searchResult = new ArrayList<Message>();
		searchResult = CarpoolDaoMessage.searchMessage(userSearch);
		if (isLogin){
			UserDaoService.updateUserSearch(userSearch, userId);
		}
		return searchResult;
	}
	

	public static Message createNewMessage(Message newMessage){
		return CarpoolDaoMessage.addMessageToDatabase(newMessage);
	}
	
	
	/**
	 * check if the retrieved message's ownerId matches current message's ownerId, if not, throw MessageOwnerNotMatchException (some for methods below)
	 */
	public static Message updateMessage(Message message) throws MessageNotFoundException, MessageOwnerNotMatchException, UserNotFoundException{
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
	 */
	public static boolean deleteMessage(int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException, ValidationException, UserNotFoundException{
		Message oldMessage = CarpoolDaoMessage.getMessageById(messageId);
		if(oldMessage.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		return true;
//		ArrayList<Transaction> tList = getRelatedTransactions(messageId);
//		for(Transaction t : tList){
//			if(t.getState().code < 5 || t.getState().code == 6){
//				throw new ValidationException("Message Has Active Transactions");
//			}
//		}
//		DaoMessage.deleteMessageFromDatabase(oldMessage.getMessageId());
//		sendMessageDeleteNotification(oldMessage);
//		return true;
	}
	

	
	/**
	 * retrives a list of transactions associated with the target message ID
	 * @param messageId
	 * @return	an arrayList of transactions based on the target message, null if any errors occurred
	 * @throws MessageNotFoundException
	 * @throws UserNotFoundException 
	 */
	public static ArrayList<Transaction> getTransactionByMessageId(int messageId) throws MessageNotFoundException, UserNotFoundException {
		return CarpoolDaoTransaction.getAllTransactionByMessageId(messageId);
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
