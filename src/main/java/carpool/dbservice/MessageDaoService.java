package carpool.dbservice;

import java.util.ArrayList;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.StoreSearchHistoryTask;
import carpool.aws.AwsMain;
import carpool.carpoolDAO.*;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants.messageState;
import carpool.exception.PseudoException;

import carpool.exception.validation.ValidationException;
import carpool.exception.location.LocationNotFoundException;

import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
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
	

	public static ArrayList<Message> getRecentMessages() throws LocationNotFoundException{
		return CarpoolDaoMessage.getRecentMessages();
	}
	

	public static ArrayList<Message> primaryMessageSearch(SearchRepresentation userSearch, boolean isLogin, int userId) throws PseudoException{
//		userSearch.setDepartureDate(DateUtility.castFromAPIFormat("2013-10-21 21:47:50"));
		ArrayList<Message> searchResult = new ArrayList<Message>();
		searchResult = CarpoolDaoMessage.searchMessage(userSearch);
		if (isLogin){
			UserDaoService.updateUserSearch(userSearch, userId);
			//StoreSearchHistoryTask ssht = new StoreSearchHistoryTask(userSearch, userId);
			//ExecutorProvider.executeRelay(ssht);
			AwsMain.storeSearchHistory(userSearch, userId);
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
			throw new MessageOwnerNotMatchException("对不起，您想更改的信息不存在，可能它已被删除");
		}
		CarpoolDaoMessage.UpdateMessageInDatabase(message);
		//sendMessageUpDateNotification(message);
		return message;
	}
	

	public static boolean closeMessage(int messageId) throws MessageNotFoundException, MessageOwnerNotMatchException, ValidationException, UserNotFoundException, LocationNotFoundException{
		Message oldMessage = CarpoolDaoMessage.getMessageById(messageId);
//		if(oldMessage.getOwnerId()!=ownerId){
//			throw new MessageOwnerNotMatchException();
//		}
		oldMessage.setState(messageState.closed);
		CarpoolDaoMessage.UpdateMessageInDatabase(oldMessage);
		return true;
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

}
