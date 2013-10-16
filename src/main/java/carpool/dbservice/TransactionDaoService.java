package carpool.dbservice;

import java.util.*;

import javax.swing.text.DateFormatter;

import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.*;
import carpool.constants.Constants;
import carpool.database.DaoTransaction;
import carpool.exception.PseudoException;
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
	 * @return	
	 */
	public static ArrayList<Transaction> getAllTransactions() {
		return DaoTransaction.getALL();
	}
	
	
	
	/**
	 * get the full transaction by id
	 * @param transactionId
	 * @return the full transaction object constructed by the full constructor, return null if any error occurs
	 * @throws TransactionNotFoundException if the specified transaction id does not exist
	 */
	private static Transaction getTransactionById(int transactionId) throws TransactionNotFoundException {
		return DaoTransaction.getTransactionById(transactionId);
	}
	
	/**
	 * get the transaction by id from API call, adding safety by checking userId ownership, since detailed transactions should be viewed by provider or customer only
	 * @param transactionId
	 * @param userId
	 * @throws TransactionNotFoundException		//thrown by getTranasctionById
	 * @throws TransactionOwnerNotMatchException	//thrown when userId not match providerId or customerId
	 */
	public static Transaction getUserTransactionById(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException{
		Transaction transaction = getTransactionById(transactionId);
		//TODO add id checking
//		if (transaction.getInitUserId() != userId && transaction.getTargetUserId() != userId){
//			throw new TransactionOwnerNotMatchException();
//		}
		return transaction;
	}


	/**
	 * created a new Transaction in SQL, the transaction passed in is constructed by the full constructor
	 * remember to set the creation time, use date string format specified by Common.parseDateString
	 * @param newTransaction
	 * @return	the full Transaction that was just created in database, use the complete constructor for this, including provider, customer, message
	 */
	public static Transaction createNewTransaction(Transaction newTransaction){
		newTransaction.setCreationTime(Calendar.getInstance());
		Transaction t = DaoTransaction.addTransactionToDatabase(newTransaction);
		// send Transaction Pending Notification
		Notification n = new Notification(-1, Constants.notificationType.on_transaction, Constants.notificationEvent.transactionPending,
				t.getInitUserId(), t.getInitUserName(), 0, t.getTransactionId(), t.getTargetUserId(),
				"XXX start a Transaction XXX with you.", Calendar.getInstance(), false, false);
		NotificationDaoService.createNewNotification(n);
		return t;
	}
	
	
	/**
	 * initUser or targetUser cancels the transaction, changing the state of the transaction from confirm to cancelled
	 * Expected Condition: current Transaction state in "Constants -> transactonState.confirm" && userId matches either initUser or targetUser
	 * Action: change the transactionState confirm -> cancelled, established -> false
	 * @param transactionId
	 * @param userId
	 * @return	the changed transaction, constructed by the full constructor
	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
	 * @throws TransactionOwnerNotMatchException	throw if the initUserId and targetUserId both does not match given userId
	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "confirm"
	 */
	public static Transaction cancelTransaction(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		try {
			User initUser = UserDaoService.getUserById(t.getInitUserId());
			User targetUser = UserDaoService.getUserById(t.getTargetUserId());
			if(targetUser.getUserId()==userId || initUser.getUserId()==userId){
				if(t.getState()!=Constants.transactionState.confirm){
					throw new TransactionStateViolationException(t.getState(), Constants.transactionState.confirm);
				}else{
					t.setState(Constants.transactionState.cancelled);
					t.setEstablished(false);
					DaoTransaction.UpdateTransactionInDatabase(t);
				}
			}else{
				throw new TransactionOwnerNotMatchException();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
		return t;			
	}
	
	
	/**
	 * initUser or targetUser reports the transaction for investigation, changing the state of the transaction from finishedToEvaluate to underInvestigation
	 * Expected Condition: current Transaction state in "Constants -> transactonState.finishedToEvaluate" && userId matches either initUser or targetUser
	 * Action: change the transactionState finishedToEvaluate -> underInvestigation
	 * @param transactionId
	 * @param userId
	 * @return	the changed transaction, constructed by the full constructor
	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
	 * @throws TransactionOwnerNotMatchException	throw if the initUserId and targetUserId both does not match given userId
	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "finishedToEvaluate"
	 */
	public static Transaction reportTransaction(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		try {
			User initUser = UserDaoService.getUserById(t.getInitUserId());
			User targetUser = UserDaoService.getUserById(t.getTargetUserId());
			if(targetUser.getUserId()==userId || initUser.getUserId()==userId){
				if(t.getState()!=Constants.transactionState.finishedToEvaluate){
					throw new TransactionStateViolationException(t.getState(), Constants.transactionState.finishedToEvaluate);
				}else{
					t.setState(Constants.transactionState.underInvestigation);
					t.setEstablished(false);
					DaoTransaction.UpdateTransactionInDatabase(t);
				}
			}else{
				throw new TransactionOwnerNotMatchException();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
		return t;			
	}
	
	
	/**
	 * initUser or targetUser evaluates the transaction, changing the state of the transaction from:
	 * finishedToEvaluate -> success_initUserEvaluated || finishedToEvaluate -> success_targetUserEvaluated || (success_initUserEvaluated || success_targetUserEvaluated -> success)
	 * Expected Conditions and Corresponding Actions: 
	 * 1. if userId == initUserId:
	 * 		a. current state is finishedToEvaluate; action:  finishedToEvaluate -> success_initUserEvaluated
	 * 		b. current state is success_targetUserEvaluated; action: success_targetUserEvaluated -> success
	 * 		
	 * 		then: update this initUserEval record, update the target user's average score
	 * 2. if userId == targetUserId:
	 * 		a. current state is finishedToEvaluate; action:  finishedToEvaluate -> success_targetUserEvaluated
	 * 		b. current state is success_initUserEvaluated; action: success_initUserEvaluated -> success
	 * 
	 * 		then: update this targetUserEval record, update the init user's average score
	 * @param transactionId
	 * @param userId
	 * @return	the changed transaction, constructed by the full constructor
	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
	 * @throws TransactionOwnerNotMatchException	throw if the initUserId and targetUserId both does not match given userId
	 * @throw  TransactionAccessViolationException  throw if userId == initUser and cur-state is initUserEvaluated || userId == targetUserId and curState is targetUserEvaluated
	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the state condition does not match either finishedToEvaluate || success_initUserEvaluated || success_targetUserEvaluated, expected state in this case is always finishedToEvaluate
	 */
	public static Transaction evaluateTransaction(int transactionId, int userId, int score) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionAccessViolationException, TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		if(t.getState()!= Constants.transactionState.finishedToEvaluate && t.getState()!= Constants.transactionState.success_initUserEvaluated
				&& t.getState()!= Constants.transactionState.success_targetUserEvaluated){
			throw new TransactionStateViolationException(t.getState(), Constants.transactionState.finishedToEvaluate);
		}else{
			try {
				User initUser = UserDaoService.getUserById(t.getInitUserId());
				User targetUser = UserDaoService.getUserById(t.getTargetUserId());
				if(userId==initUser.getUserId()){
					if(t.getState()==Constants.transactionState.success_initUserEvaluated){
						throw new TransactionAccessViolationException();
					}else if(t.getState()==Constants.transactionState.finishedToEvaluate){
						t.setState(Constants.transactionState.success_initUserEvaluated);
						t.setInitUserEval(score);
						DaoTransaction.UpdateTransactionInDatabase(t);
						targetUser.setAverageScore((targetUser.getAverageScore()*(targetUser.getTotalTranscations()-1)+score)/targetUser.getTotalTranscations());
						try {
							CarpoolDaoUser.UpdateUserInDatabase(targetUser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{//success_targetUserEvaluated
						t.setState(Constants.transactionState.success);
						t.setInitUserEval(score);
						DaoTransaction.UpdateTransactionInDatabase(t);
						targetUser.setAverageScore((targetUser.getAverageScore()*(targetUser.getTotalTranscations()-1)+score)/targetUser.getTotalTranscations());
						try {
							CarpoolDaoUser.UpdateUserInDatabase(targetUser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else if(userId==targetUser.getUserId()){
					if(t.getState()==Constants.transactionState.success_targetUserEvaluated){
						throw new TransactionAccessViolationException();
					}else if(t.getState()==Constants.transactionState.finishedToEvaluate){
						t.setState(Constants.transactionState.success_targetUserEvaluated);
						t.setTargetUserEval(score);
						DaoTransaction.UpdateTransactionInDatabase(t);
						initUser.setAverageScore((initUser.getAverageScore()*(initUser.getTotalTranscations()-1)+score)/initUser.getTotalTranscations());
						try {
							CarpoolDaoUser.UpdateUserInDatabase(initUser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{//success_initUserEvaluated
						t.setState(Constants.transactionState.success);
						t.setTargetUserEval(score);
						DaoTransaction.UpdateTransactionInDatabase(t);
						initUser.setAverageScore((initUser.getAverageScore()*(initUser.getTotalTranscations()-1)+score)/initUser.getTotalTranscations());
						try {
							CarpoolDaoUser.UpdateUserInDatabase(initUser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					throw new TransactionOwnerNotMatchException();
				}
			} catch (UserNotFoundException e) {
				e.printStackTrace();
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
