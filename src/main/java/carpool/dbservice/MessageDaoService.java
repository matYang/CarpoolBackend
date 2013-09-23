package carpool.dbservice;

import java.util.ArrayList;
import java.util.Calendar;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.Constants.gender;
import carpool.common.Constants.messageType;
import carpool.common.Constants.paymentMethod;
import carpool.common.Constants.userSearchState;
import carpool.database.DaoMessage;
import carpool.database.DaoTransaction;
import carpool.database.DaoUser;
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
	
	
	/**
	 * get all the DM messages from dataBase, used for testing only
	 * @return	if any error occurs, return null
	 */
	public static ArrayList<Message> getAllMessages() {
		return DaoMessage.getAll();
	}
	
	/**
	 * gets the recently posted messages, length specified by Constants.max_recents (currently 3)
	 * @return if any error occurs, return null
	 */
	public static ArrayList<Message> getRecentMessages() {
		ArrayList<Message> all = DaoMessage.getAll();
		ArrayList<Message> retVal = new ArrayList<Message>();
		for(int i=0;i<Constants.max_recents;i++){
			retVal.add(all.get(all.size()-1-i));
		}
		return retVal;
	}
	
	/**
	 * the primary DMMessage searching method
	 * search by location, date, searchState, it can be assumed that all of these parameters are valid
	 * note that, seachState contains both target search type and message type
	 * note that only use year-month-date in this calendar instance to search for matching messages, not the matching of entire date strings
	 * ** note that the search state here can not be circle-related and must be valid
	 * @param location
	 * @param date
	 * @param searchState
	 * @return ArrayList of DMMessage search results, return null if any error or exceptions occur
	 * @throws UnacceptableSearchStateException
	 */
	public static ArrayList<Message> primaryMessageSearch(Location location,Calendar date, userSearchState searchState) throws UnacceptableSearchStateException{
		//universityAsk(0), universityHelp(1), regionAsk(2), regionHelp(3), universityGroupAsk(4), universityGroupHelp(5);
		int remainder = searchState.code % 2;
		messageType type = messageType.ask;
		if(remainder==1){
			type = messageType.help;
		}
		if(searchState.code<2){
			return DaoMessage.searchMessageSingle(location.toString(),Common.toSQLDateTime(date),type.code+"");
		}else if(searchState.code<4){
			return DaoMessage.searchMessageRegion(location.toString(),Common.toSQLDateTime(date),type.code+"");
		}else{
			throw new UnacceptableSearchStateException();
		}
	}
	
	/**
	 * the secured and extended version of primary DMMessage searching method
	 * ** note that the search state here can be any valid search states, if it is circle related, ignore location, ready universityGroup from user, but must be valid first
	 * ** remember to update User::searchState after each user searches
	 * @param location
	 * @param date
	 * @param searchState
	 * @return ArrayList of DMMessage search results, return null if any error or exceptions occur
	 * @throws UnacceptableSearchStateException
	 * @throws UserNotFoundException
	 */
	public static ArrayList<Message> extendedMessageSearch(Location location,Calendar date, userSearchState searchState, int userId) throws UserNotFoundException{
		//universityAsk(0), universityHelp(1), regionAsk(2), regionHelp(3), universityGroupAsk(4), universityGroupHelp(5);
		if(searchState.code<4){
			try {
				return primaryMessageSearch(location, date, searchState);
			} catch (UnacceptableSearchStateException e) {
				Common.d("IMPOSSIBLE TO HAPPEN");
				return null;
			}
		}else{
			int remainder = searchState.code % 2;
			messageType type = messageType.ask;
			if(remainder==1){
				type = messageType.help;
			}
			User user = DaoUser.getUserById(userId);
			String group = user.getUniversityGroupString();
			ArrayList<Message> retVal = new ArrayList<Message>();
			for(String str : group.split("-")){
				retVal.addAll(DaoMessage.searchMessageSingle(str, Common.toSQLDateTime(date), type.code+""));
			}
			return retVal;
		}
	}
	

	/**
	 * created a new DM message in SQL, the newMessage passed in is constructed by the constructor for POST in DMMessage
	 * remember to set the creation time, use date string format specified by Common.parseDateString
	 * note that do not store additional owner information other than owner id in DMMessage table, as information in user table can be consistently changed
	 * to keep owner info updated, fetch owner-related info from user table with user id(ownerId) in other methods
	 * @param newMessage
	 * @param userId
	 * @return	the full DMMessage that is just created in database, use  the full DMMessage constructor for this, null if any errors occurred
	 */
	public static Message createNewMessage(Message newMessage){
		newMessage.setCreationTime(Calendar.getInstance());
		//sent follower New Post Notification
		sendFollowerNewPostNotification(newMessage);
		return DaoMessage.addMessageToDatabase(newMessage);
	}
	
	/**
	 * gets the whole message from DB, user the whole message constructor specified in the DMMessage class
	 * @param messageId
	 * @return	null if operation fails, eg id does not exist, otherwise return the message
	 * @throws MessageNotFoundException throw this if messageId is not found in database
	 */
	public static Message getMessageById(int messageId) throws MessageNotFoundException{
		return DaoMessage.getMessageById(messageId);
	}
	
	/**
	 * update the existing message
	 * first retrieve the message from db using the id of current message
	 * if not found, throw messageNotFoundException
	 * then check if the retrieved message's ownerId matches current message's ownerId, if not, throw MessageOwnerNotMatchException (some for methods below)
	 * then keep error checking and updating
	 * @param message
	 * @param id
	 * @return null if error occurred, otherwise return the newly updated DMMessage
	 * @throws MessageNotFoundException
	 * @throws MessageOwnerNotMatchException
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
	 * when delete, do not remove from DB, change messageState state to deleted, and historyDeleted to true
	 * can not delete if this message has transactions that are not finished yet
	 * note user could edit the message after a transaction is initialized, so the startTime and endTime of the DMMessage does not necessarily tell if there are active transactions
	 * deletes the DMMessage from database
	 * first retrieve the message from db using the id of current message
	 * if not found, throw messageNotFoundException
	 * then check if the retrieved message's ownerId matches ownerId parameter, if not, throw MessageOwnerNotMatchException
	 * @param messageId
	 * @param ownerId
	 * @return true if message exists and deleted, false if message does not exist or delete failed
	 */
	public static boolean deleteMessage(int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException{
		Message oldMessage = DaoMessage.getMessageById(messageId);
		if(oldMessage.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		ArrayList<Transaction> tList = getRelatedTransactions(messageId);
		for(Transaction t : tList){
			if(t.getState().code < 5 || t.getState().code == 6){
				//Transactions not finished, can not delete
				return false;
			}
		}
		oldMessage.setHistoryDeleted(true);
		oldMessage.setState(Constants.messageState.deleted);
		DaoMessage.UpdateMessageInDatabase(oldMessage);
		// send watching message deleted Notification
		sendMessageDeleteNotification(oldMessage);
		return true;
	}
	
	
	/**
	 * update the note field of the DMMessage object
	 * @param newNote
	 * @param messageId
	 * @param ownerId 
	 * @return	updated string if operation successful, null if any error occurred
	 * @throws MessageNotFoundException
	 * @throws MessageOwnerNotMatchException
	 */
	public static String updateNote(String newNote, int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException{
		Message oldMessge = DaoMessage.getMessageById(messageId);
		if(oldMessge.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		oldMessge.setNote(newNote);
		DaoMessage.UpdateMessageInDatabase(oldMessge);
		sendMessageUpDateNotification(oldMessge);
		return newNote;
	}
	
	/**
	 * updates the gender of the DMMessage object
	 * @param newGender
	 * @param messageId
	 * @param ownerId 
	 * @return	updated gender if operation successful, null if any error occurred
	 * @throws MessageNotFoundException
	 * @throws MessageOwnerNotMatchException
	 */
	public static gender updateGender(gender newGender, int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException{
		Message oldMessge = DaoMessage.getMessageById(messageId);
		if(oldMessge.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		oldMessge.setGenderRequirement(newGender);
		DaoMessage.UpdateMessageInDatabase(oldMessge);
		sendMessageUpDateNotification(oldMessge);
		return newGender;
	}
	
	/**
	 * updates the location of the message
	 * @param newLocation
	 * @param messageId
	 * @param ownerid 
	 * @return the newly updated location if everything is right, return null if error occurs
	 * @throws MessageNotFoundException
	 * @throws MessageOwnerNotMatchException
	 */
	public static Location updateLocation(Location newLocation, int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException{
		Message oldMessge = DaoMessage.getMessageById(messageId);
		if(oldMessge.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		oldMessge.setLocation(newLocation);
		DaoMessage.UpdateMessageInDatabase(oldMessge);
		sendMessageUpDateNotification(oldMessge);
		return newLocation;
	}
	
	/**
	 * updates the price of the message
	 * @param newPrice
	 * @param messageId
	 * @param ownerId
	 * @return	the updated price, return -1 if any error occurs
	 * @throws MessageNotFoundException
	 * @throws MessageOwnerNotMatchException
	 */
	public static int updatePrice(int newPrice, int messageId, int ownerId) throws MessageNotFoundException,MessageOwnerNotMatchException{
		Message oldMessge = DaoMessage.getMessageById(messageId);
		if(oldMessge.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		oldMessge.setPrice(newPrice);
		DaoMessage.UpdateMessageInDatabase(oldMessge);
		sendMessageUpDateNotification(oldMessge);
		return newPrice;
	}
	
	
	/**
	 * updates the start time and end time of the message
	 * all the calendars are parsed by Common.parseDateString from String to Calendar instances
	 * @param calendar startTime
	 * @param calendar endTime
	 * @param messageId
	 * @param ownerId
	 * @return	an ArrayList of Calendar instance, the first one is updated start time; second one is updated end time, return null if any error occurs
	 * @throws MessageNotFoundException
	 * @throws MessageOwnerNotMatchException
	 */
	public static ArrayList<Calendar> updateTime(Calendar startTime, Calendar endTime, int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException{
		Message oldMessge = DaoMessage.getMessageById(messageId);
		if(oldMessge.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		oldMessge.setStartTime(startTime);
		oldMessge.setEndTime(endTime);
		DaoMessage.UpdateMessageInDatabase(oldMessge);
		ArrayList<Calendar> retVal = new ArrayList<Calendar>();
		retVal.add(startTime);
		retVal.add(endTime);
		sendMessageUpDateNotification(oldMessge);
		return retVal;
	}
	
	/**
	 * updates the payment method of the message
	 * @param newPaymentMethod
	 * @param messageId
	 * @param ownerId
	 * @return updated payment method if successful, null if any error occurred
	 * @throws MessageNotFoundException
	 * @throws MessageOwnerNotMatchException
	 */
	public static paymentMethod updatePaymentMethod(paymentMethod newPaymentMethod, int messageId, int ownerId) throws MessageNotFoundException, MessageOwnerNotMatchException{
		Message oldMessge = DaoMessage.getMessageById(messageId);
		if(oldMessge.getOwnerId()!=ownerId){
			throw new MessageOwnerNotMatchException();
		}
		oldMessge.setPaymentMethod(newPaymentMethod);
		DaoMessage.UpdateMessageInDatabase(oldMessge);
		sendMessageUpDateNotification(oldMessge);
		return newPaymentMethod;
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
		//send watching message modified Notification
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		for(User user : DaoUser.getUserWhoWatchedMessage(msg.getMessageId())){
			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.watchingMessageModified,
					msg.getOwnerId(), msg.getOwnerName(),msg.getMessageId(), 0, user.getUserId(),
					"The Message XXX you've watched has change",Calendar.getInstance(), false, false);
			notifications.add(n);
		}
		NotificationDaoService.createNewNotificationQueue(notifications);
	}
	
	private static void sendMessageDeleteNotification(Message msg){
		//send watching message modified Notification
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		for(User user : DaoUser.getUserWhoWatchedMessage(msg.getMessageId())){
			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.watchingMessageModified,
					msg.getOwnerId(), msg.getOwnerName(),msg.getMessageId(), 0, user.getUserId(),
					"The Message XXX you've watched no longer exsit",Calendar.getInstance(), false, false);
			notifications.add(n);
		}
		NotificationDaoService.createNewNotificationQueue(notifications);
	}
	
	private static void sendFollowerNewPostNotification(Message msg){
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		for(User user : DaoUser.getUserWhoWatchedUser(msg.getOwnerId())){
			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.followerNewPost,
					msg.getOwnerId(), msg.getOwnerName(), msg.getMessageId(), 0, user.getUserId(),
					"The User XXX you followed has just post a new Message XXX.", Calendar.getInstance(), false, false);
			notifications.add(n);
		}
		NotificationDaoService.createNewNotificationQueue(notifications);
	}

	
}
