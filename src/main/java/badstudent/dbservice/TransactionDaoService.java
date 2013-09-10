package badstudent.dbservice;

import java.util.*;

import javax.swing.text.DateFormatter;

import badstudent.model.*;
import badstudent.common.*;
import badstudent.database.DaoTransaction;
import badstudent.database.DaoUser;
import badstudent.exception.message.MessageNotFoundException;
import badstudent.exception.message.MessageOwnerNotMatchException;
import badstudent.exception.transaction.TransactionAccessViolationException;
import badstudent.exception.transaction.TransactionNotFoundException;
import badstudent.exception.transaction.TransactionOwnerNotMatchException;
import badstudent.exception.transaction.TransactionStateViolationException;
import badstudent.exception.user.UserNotFoundException;


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
	public static Transaction getTransactionById(int transactionId) throws TransactionNotFoundException {
		return DaoTransaction.getTransactionById(transactionId);
	}
	
	/**
	 * get the transaction by id from API call, adding safety by checking userId ownership since unlink messages, detailed transactions are private data
	 * @param transactionId
	 * @param userId
	 * @return
	 * @throws TransactionNotFoundException
	 * @throws TransactionOwnerNotMatchException
	 */
	public static Transaction getUserTransactionById(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException{
		Transaction transaction = getTransactionById(transactionId);
		if (transaction.getInitUserId() != userId && transaction.getTargetUserId() != userId){
			throw new TransactionOwnerNotMatchException();
		}
		return transaction;
	}


	/**
	 * created a new Transaction in SQL, the transaction passed in is constructed by the full constructor
	 * remember to set the creation time, use date string format specified by Common.parseDateString
	 * transaction slot checking will be conducted at API level, eg checking for list of transactions already taking place based on a message and determine whether a new transaction fits in
	 * @param newTransaction
	 * @param userId
	 * @return	the full Transaction that was just created in database, use the complete constructor for this, return null if any errors occurred
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
	
	//below are transaction state inter=changes, aka Transaction State Matchine manipulator
	/**
	 * target user confirms the transaction, changing the state of the transaction from init to confirm
	 * Expected Condition: current Transaction state in "Constants -> transactonState.init" && userId matches the targetUserId of the transaction
	 * Action: change the transactionState init -> confirm, established -> false, increment both initUser and targetUsers' total transaction number
	 * @param transactionId
	 * @param userId
	 * @return	the changed transaction, constructed by the full constructor
	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
	 * @throws TransactionOwnerNotMatchException	throw if the initUserId and targetUserId both does not match given userId
	 * @throws TransactionAccessViolationException	throw if the userId is actually the initUserId, but not targetUserId
	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "init", expected state if "init"
	 */
	public static Transaction confirmTransaction(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionAccessViolationException, TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		try {
			User initUser = UserDaoService.getUserById(t.getInitUserId());
			User targetUser = UserDaoService.getUserById(t.getTargetUserId());
			if(initUser.getUserId()==userId){
				throw new TransactionAccessViolationException();
			}else if(targetUser.getUserId()==userId){
				if(t.getState()!=Constants.transactionState.init){
					throw new TransactionStateViolationException(t.getState(), Constants.transactionState.init);
				}else{
					t.setState(Constants.transactionState.confirm);
					t.setEstablished(false);
					initUser.setTotalTranscations(initUser.getTotalTranscations()+1);
					targetUser.setTotalTranscations(targetUser.getTotalTranscations()+1);
					DaoTransaction.UpdateTransactionInDatabase(t);
					UserDaoService.updateUser(initUser, initUser.getUserId());
					UserDaoService.updateUser(targetUser, targetUser.getUserId());
					// send transaction Comfirmed notification
					Notification n = new Notification(-1, Constants.notificationType.on_transaction, Constants.notificationEvent.transactionConfrimed,
							t.getTargetUserId(), t.getTargetUserName(), 0, t.getTransactionId(), t.getInitUserId(),
							"Your transaction XXX has been accepted by XXX", Calendar.getInstance(), false, false);
					NotificationDaoService.createNewNotification(n);
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
	 * target user refuses the transaction, changing the state of the transaction from init to refused
	 * Expected Condition: current Transaction state in "Constants -> transactonState.init" && userId matches the targetUserId of the transaction
	 * Action: change the transactionState init -> refused, established -> false
	 * @param transactionId
	 * @param userId
	 * @return	the changed transaction, constructed by the full constructor
	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
	 * @throws TransactionOwnerNotMatchException	throw if the initUserId and targetUserId both does not match given userId
	 * @throws TransactionAccessViolationException	throw if the userId is actually the initUserId, but not targetUserId
	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "init", expected state is "init"
	 */
	public static Transaction refuseTransaction(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionAccessViolationException, TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		try {
			User initUser = UserDaoService.getUserById(t.getInitUserId());
			User targetUser = UserDaoService.getUserById(t.getTargetUserId());
			if(initUser.getUserId()==userId){
				throw new TransactionAccessViolationException();
			}else if(targetUser.getUserId()==userId){
				if(t.getState()!=Constants.transactionState.init){
					throw new TransactionStateViolationException(t.getState(), Constants.transactionState.init);
				}else{
					t.setState(Constants.transactionState.refused);
					t.setEstablished(false);
					DaoTransaction.UpdateTransactionInDatabase(t);
					// send transaction refused notification
					Notification n = new Notification(-1, Constants.notificationType.on_transaction, Constants.notificationEvent.transactionRefused,
							t.getTargetUserId(), t.getTargetUserName(), 0, t.getTransactionId(), t.getInitUserId(),
							"Your transaction XXX has been refused by XXX", Calendar.getInstance(), false, false);
					NotificationDaoService.createNewNotification(n);
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
							DaoUser.UpdateUserInDatabase(targetUser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{//success_targetUserEvaluated
						t.setState(Constants.transactionState.success);
						t.setInitUserEval(score);
						DaoTransaction.UpdateTransactionInDatabase(t);
						targetUser.setAverageScore((targetUser.getAverageScore()*(targetUser.getTotalTranscations()-1)+score)/targetUser.getTotalTranscations());
						try {
							DaoUser.UpdateUserInDatabase(targetUser);
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
							DaoUser.UpdateUserInDatabase(initUser);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{//success_initUserEvaluated
						t.setState(Constants.transactionState.success);
						t.setTargetUserEval(score);
						DaoTransaction.UpdateTransactionInDatabase(t);
						initUser.setAverageScore((initUser.getAverageScore()*(initUser.getTotalTranscations()-1)+score)/initUser.getTotalTranscations());
						try {
							DaoUser.UpdateUserInDatabase(initUser);
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
	
	
	/**
	 * TODO (admin?) deletes the transaction from database (for testing and admin use only)
	 * first retrieve the transaction from db using the given transaction id
	 * decrement both initUser and targetUsers' total transaction number
	 * if not found, throw TransactionNotFoundException
	 * then check if the retrieved transaction's initUserId or targetUserId matches userId parameter, if not, throw TransactionOwnerNotMatchException
	 * @param transactionId
	 * @param userId
	 * @return true if transaction exists and deleted
	 */
	public static boolean deleteTransaction(int transactionId, int userId) throws TransactionNotFoundException, TransactionOwnerNotMatchException, TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		try {
			User initUser = UserDaoService.getUserById(t.getInitUserId());
			User targetUser = UserDaoService.getUserById(t.getTargetUserId());
			if(initUser.getUserId()==userId || targetUser.getUserId()==userId){
				//only finished transactions can be deleted
				if(t.getState()!=Constants.transactionState.success && t.getState()!=Constants.transactionState.success_initUserEvaluated && t.getState()!=Constants.transactionState.success_noEvaluation && t.getState()!=Constants.transactionState.success_targetUserEvaluated){
					throw new TransactionStateViolationException(t.getState(), Constants.transactionState.success);
				}else{
					initUser.setTotalTranscations(initUser.getTotalTranscations()-1);
					targetUser.setTotalTranscations(targetUser.getTotalTranscations()-1);
					DaoTransaction.deleteTransactionFromDatabase(transactionId);
					UserDaoService.updateUser(initUser, initUser.getUserId());
					UserDaoService.updateUser(targetUser, targetUser.getUserId());
				}
			}else{
				throw new TransactionOwnerNotMatchException();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**TODO API
	 * admin found a problem with a transaction, and moving it to cancelled
	 * Expected Condition: current Transaction state in "Constants -> transactonState.underInvestigation"
	 * Action: change the transactionState underInvestigation -> cancelled
	 * @param transactionId
	 * @return	the changed transaction, constructed by the full constructor
	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "underInvestigation"
	 */
	public static Transaction investigationCancelTransaction(int transactionId) throws TransactionNotFoundException,  TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		if(t.getState()==Constants.transactionState.underInvestigation){
			t.setState(Constants.transactionState.cancelled);
			DaoTransaction.UpdateTransactionInDatabase(t);
		}else{
			throw new TransactionStateViolationException(t.getState(), Constants.transactionState.underInvestigation);
		}
		return t;
	}
	
	/**TODO API
	 * admin did not find a problem with a transaction, and moving it to back to finishedToEvaluated
	 * Expected Condition: current Transaction state in "Constants -> transactonState.underInvestigation"
	 * Action: change the transactionState underInvestigation -> finishedToEvaluate
	 * @param transactionId
	 * @param userId
	 * @return	the changed transaction, constructed by the full constructor
	 * @throws TransactionNotFoundException	 throw if the transaction is not found in the first place
	 * @throws TransactionStateViolationException(currentState, expected state)	throw if the current state of the transaction is not "underInvestigation"
	 */
	public static Transaction investigationReleaseTransaction(int transactionId) throws TransactionNotFoundException,  TransactionStateViolationException{
		Transaction t = DaoTransaction.getTransactionById(transactionId);
		if(t.getState()==Constants.transactionState.underInvestigation){
			t.setState(Constants.transactionState.finishedToEvaluate);
			DaoTransaction.UpdateTransactionInDatabase(t);
			// send Transaction released notification
			Notification n = new Notification(-1, Constants.notificationType.on_transaction, Constants.notificationEvent.transactionReleased,
					0, "", 0, t.getTransactionId(),t.getInitUserId(),
					"Your transaction XXX has been released", Calendar.getInstance(), false, false);
			NotificationDaoService.createNewNotification(n);
			n.setTargetUserId(t.getTargetUserId());
			NotificationDaoService.createNewNotification(n);
		}else{
			throw new TransactionStateViolationException(t.getState(), Constants.transactionState.underInvestigation);
		}
		return t;
	}
}
