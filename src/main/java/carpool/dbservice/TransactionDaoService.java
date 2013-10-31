package carpool.dbservice;

import java.util.*;

import javax.swing.text.DateFormatter;

import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoTransaction;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.*;
import carpool.constants.Constants;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.message.MessageOwnerNotMatchException;
import carpool.exception.transaction.TransactionAccessViolationException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.transaction.TransactionOwnerNotMatchException;
import carpool.exception.transaction.TransactionStateViolationException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;



public class TransactionDaoService{
	
	
	/**
	 * get all the transactions from database
	 */
	public static ArrayList<Transaction> getAllTransactions() throws TransactionNotFoundException, UserNotFoundException, MessageNotFoundException {
		return CarpoolDaoTransaction.getAllTranscations();
	}
	
	
	/**
	 * get the full transaction by id
	 * @param transactionId
	 * @return the full transaction object constructed by the full constructor, return null if any error occurs
	 */
	private static Transaction getTransactionById(int transactionId) throws TransactionNotFoundException, UserNotFoundException, MessageNotFoundException {
		return CarpoolDaoTransaction.getTransactionById(transactionId);
	}
	
	/**
	 * get the transaction by id from API call, adding safety by checking userId ownership, since detailed transactions should be viewed by provider or customer only
	 */
	public static Transaction getUserTransactionById(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, UserNotFoundException, MessageNotFoundException{
		Transaction transaction = getTransactionById(transactionId);

		if (transaction.getProviderId() != userId && transaction.getCustomerId() != userId){
			throw new TransactionOwnerNotMatchException();
		}
		return transaction;
	}


	/**
	 * created a new Transaction in SQL
	 * @return	the full Transaction that was just created in database, use the complete constructor for this, including provider, customer, message
	 * @throws ValidationException 
	 */
	public static Transaction createNewTransaction(Transaction newTransaction) throws MessageNotFoundException, UserNotFoundException, ValidationException{
		Transaction t = newTransaction;
		
		Message base = CarpoolDaoMessage.getMessageById(t.getMessageId());
		if (t.getType() == Constants.TransactionType.departure){
			base.setDeparture_seatsBooked(base.getDeparture_seatsBooked() + t.getDeparture_seatsBooked());
		} else{
			base.setArrival_seatsBooked(base.getArrival_seatsBooked() + t.getDeparture_seatsBooked());
		}
		if (base.getDeparture_seatsBooked() > base.getDeparture_seatsNumber() || base.getArrival_seatsBooked() > base.getArrival_seatsNumber()){
			throw new ValidationException("交易发起失败，没有那么多空余位置");
		}
		
		t = CarpoolDaoTransaction.addTransactionToDatabase(newTransaction);
		CarpoolDaoMessage.UpdateMessageInDatabase(base);
		
		//currently transactions are customer -> provider only
		Notification n = new Notification(Constants.NotificationEvent.transactionInit, newTransaction.getProviderId(), t.getCustomerId(), t.getMessageId(), t.getTransactionId());
		NotificationDaoService.sendNotification(n);
		return t;
	}
	
	
	/**
	 * provider or customer cancels the transaction, changing the state of the transaction from confirm to cancelled
	 * Expected Condition: current Transaction state in "Constants -> transactonState.init" && userId matches either providerId or messageId
	 * Action: change state to cancelled, TODO: send notifications, and prompt for explanation
	 */
	public static Transaction cancelTransaction(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionStateViolationException, MessageNotFoundException, UserNotFoundException, ValidationException{
		Transaction t = CarpoolDaoTransaction.getTransactionById(transactionId);

		if(t.getProviderId() == userId || t.getCustomerId() == userId){
			if(t.getState() != Constants.transactionState.init){
				throw new TransactionStateViolationException(t.getState(), Constants.transactionState.init);
			}
			
			Message base = CarpoolDaoMessage.getMessageById(t.getMessageId());
			if (t.getType() == Constants.TransactionType.departure){
				base.setDeparture_seatsBooked(base.getDeparture_seatsBooked() - t.getDeparture_seatsBooked());
			} else{
				base.setArrival_seatsBooked(base.getArrival_seatsBooked() - t.getDeparture_seatsBooked());
			}
			if (base.getDeparture_seatsBooked() < 0 || base.getArrival_seatsBooked() < 0){
				throw new ValidationException("交易取消失败，涉及座位数不匹配");
			}
			
			t.setState(Constants.transactionState.cancelled);
			CarpoolDaoTransaction.updateTransactionInDatabase(t);
			CarpoolDaoMessage.UpdateMessageInDatabase(base);
			//send notifications
			ArrayList<Notification> ns = new ArrayList<Notification>();
			ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, t.getProviderId(), t.getCustomerId(), t.getMessageId(), t.getTransactionId()));
			ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, t.getCustomerId(), t.getProviderId(), t.getMessageId(), t.getTransactionId()));
			NotificationDaoService.sendNotification(ns);
			
		}else{
			throw new TransactionOwnerNotMatchException();
		}
		
		return t;	
	}
	
	
	/**
	 * TODO send notifications
	 * initUser or targetUser reports the transaction for investigation, changing the state of the transaction from finishedToEvaluate to underInvestigation
	 * Expected Condition: current Transaction state in "Constants -> transactonState.finishedToEvaluate" && userId matches either initUser or targetUser
	 * Action: change the transactionState finishedToEvaluate -> underInvestigation
	 */
	public static Transaction reportTransaction(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionStateViolationException, UserNotFoundException, MessageNotFoundException{
		Transaction t = CarpoolDaoTransaction.getTransactionById(transactionId);

		if(t.getProviderId() == userId || t.getCustomerId() == userId){
			if(t.getState() != Constants.transactionState.finished){
				throw new TransactionStateViolationException(t.getState(), Constants.transactionState.finished);
			}else{
				t.setState(Constants.transactionState.underInvestigation);
				CarpoolDaoTransaction.updateTransactionInDatabase(t);
				//send notifications
				ArrayList<Notification> ns = new ArrayList<Notification>();
				ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, t.getProviderId(), t.getCustomerId(), t.getMessageId(), t.getTransactionId()));
				ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, t.getCustomerId(), t.getProviderId(), t.getMessageId(), t.getTransactionId()));
				NotificationDaoService.sendNotification(ns);
			}
		}else{
			throw new TransactionOwnerNotMatchException();
		}

		return t;			
	}
	
	private static void updateUserScore(User user, int score){
		user.setAverageScore(Math.round((user.getAverageScore() * user.getTotalTranscations() + score) / user.getTotalTranscations()));
	}
	
	/**
	 * initUser or targetUser evaluates the transaction， given the transaction is in finished state
	 * @return	the changed transaction, constructed by the full constructor
	 */
	public static Transaction evaluateTransaction(int transactionId, int userId, int score) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionAccessViolationException, TransactionStateViolationException, MessageNotFoundException, UserNotFoundException, ValidationException{
		Transaction t = CarpoolDaoTransaction.getTransactionById(transactionId);
		if(t.getState() != Constants.transactionState.finished){
			throw new TransactionStateViolationException(t.getState(), Constants.transactionState.finished);
		}else{
			try {
				if(userId == t.getProviderId()){
					//check if the user has already evaluated
					if(t.getProviderEvaluation() != 0){
						throw new TransactionAccessViolationException();
					}
					t.setProviderEvaluation(score);
					User customer = CarpoolDaoUser.getUserById(t.getCustomerId());
					updateUserScore(customer, score);
					
					CarpoolDaoTransaction.updateTransactionInDatabase(t);
					CarpoolDaoUser.UpdateUserInDatabase(customer);
					
				}else if(userId == t.getCustomerId()){

					if(t.getCustomerEvaluation() != 0){
						throw new TransactionAccessViolationException();
					}
					t.setCustomerEvaluation(score);
					User provider = CarpoolDaoUser.getUserById(t.getProviderId());
					updateUserScore(provider, score);
					
					CarpoolDaoTransaction.updateTransactionInDatabase(t);
					CarpoolDaoUser.UpdateUserInDatabase(provider);
				}else{
					throw new TransactionOwnerNotMatchException();
				}
				ArrayList<Notification> ns = new ArrayList<Notification>();
				ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, t.getProviderId(), t.getCustomerId(), t.getMessageId(), t.getTransactionId()));
				ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, t.getCustomerId(), t.getProviderId(), t.getMessageId(), t.getTransactionId()));
				NotificationDaoService.sendNotification(ns);
			} catch (UserNotFoundException e) {
				e.printStackTrace();
				throw new TransactionOwnerNotMatchException();
			}
			
		}
		return t;
	}
	
//	
//	/**
//	 * TODO (admin?) deletes the transaction from database (for testing and admin use only)
//	 * first retrieve the transaction from db using the given transaction id
//	 * decrement both initUser and targetUsers' total transaction number
//	 * if not found, throw TransactionNotFoundException
//	 * then check if the retrieved transaction's initUserId or targetUserId matches userId parameter, if not, throw TransactionOwnerNotMatchException
//	 * @param transactionId
//	 * @param userId
//	 * @return true if transaction exists and deleted
//	 * @throws PseudoException 
//	 */
//	public static boolean deleteTransaction(int transactionId, int userId) throws PseudoException{
//		Transaction t = DaoTransaction.getTransactionById(transactionId);
//		try {
//			User initUser = UserDaoService.getUserById(t.getInitUserId());
//			User targetUser = UserDaoService.getUserById(t.getTargetUserId());
//			if(initUser.getUserId()==userId || targetUser.getUserId()==userId){
//				//only finished transactions can be deleted
//				if(t.getState()!=Constants.transactionState.success && t.getState()!=Constants.transactionState.success_initUserEvaluated && t.getState()!=Constants.transactionState.success_noEvaluation && t.getState()!=Constants.transactionState.success_targetUserEvaluated){
//					throw new TransactionStateViolationException(t.getState(), Constants.transactionState.success);
//				}else{
//					initUser.setTotalTranscations(initUser.getTotalTranscations()-1);
//					targetUser.setTotalTranscations(targetUser.getTotalTranscations()-1);
//					DaoTransaction.deleteTransactionFromDatabase(transactionId);
//					UserDaoService.updateUser(initUser);
//					UserDaoService.updateUser(targetUser);
//				}
//			}else{
//				throw new TransactionOwnerNotMatchException();
//			}
//		} catch (UserNotFoundException e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	
//	/**TODO API
//	 * admin found a problem with a transaction, and moving it to cancelled
//	 * Expected Condition: current Transaction state in "Constants -> transactonState.underInvestigation"
//	 * Action: change the transactionState underInvestigation -> cancelled
//	 * @param transactionId
//	 * @return	the changed transaction, constructed by the full constructor
//	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
//	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "underInvestigation"
//	 */
//	public static Transaction investigationCancelTransaction(int transactionId) throws TransactionNotFoundException,  TransactionStateViolationException{
//		Transaction t = DaoTransaction.getTransactionById(transactionId);
//		if(t.getState()==Constants.transactionState.underInvestigation){
//			t.setState(Constants.transactionState.cancelled);
//			DaoTransaction.UpdateTransactionInDatabase(t);
//		}else{
//			throw new TransactionStateViolationException(t.getState(), Constants.transactionState.underInvestigation);
//		}
//		return t;
//	}
//	
//	/**TODO API
//	 * admin did not find a problem with a transaction, and moving it to back to finishedToEvaluated
//	 * Expected Condition: current Transaction state in "Constants -> transactonState.underInvestigation"
//	 * Action: change the transactionState underInvestigation -> finishedToEvaluate
//	 * @param transactionId
//	 * @param userId
//	 * @return	the changed transaction, constructed by the full constructor
//	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
//	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "underInvestigation"
//	 */
//	public static Transaction investigationReleaseTransaction(int transactionId) throws TransactionNotFoundException,  TransactionStateViolationException{
//		Transaction t = DaoTransaction.getTransactionById(transactionId);
//		if(t.getState()==Constants.transactionState.underInvestigation){
//			t.setState(Constants.transactionState.finishedToEvaluate);
//			DaoTransaction.UpdateTransactionInDatabase(t);
//			// send Transaction released notification
//			Notification n = new Notification(-1, Constants.notificationType.on_transaction, Constants.notificationEvent.transactionReleased,
//					0, "", 0, t.getTransactionId(),t.getInitUserId(),
//					"Your transaction XXX has been released", Calendar.getInstance(), false, false);
//			NotificationDaoService.createNewNotification(n);
//			n.setTargetUserId(t.getTargetUserId());
//			NotificationDaoService.createNewNotification(n);
//		}else{
//			throw new TransactionStateViolationException(t.getState(), Constants.transactionState.underInvestigation);
//		}
//		return t;
//	}
}
