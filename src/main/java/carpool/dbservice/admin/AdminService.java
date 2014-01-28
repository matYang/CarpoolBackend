package carpool.dbservice.admin;

import java.util.ArrayList;

import carpool.carpoolDAO.*;
import carpool.cleanRoutineTask.MessageCleaner;
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.constants.Constants;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.transactionState;
import carpool.constants.Constants.userState;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.*;
import carpool.exception.location.LocationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.*;

public class AdminService {

	public static void changeUserState(int userId, userState targetState) throws PseudoException{
		User user = CarpoolDaoUser.getUserById(userId);
		user.setState(targetState);
		CarpoolDaoUser.UpdateUserInDatabase(user);
	}

	public static void changeMessageState(int messageId, messageState targetState) throws PseudoException{
		Message message = CarpoolDaoMessage.getMessageById(messageId);
		message.setState(targetState);
		CarpoolDaoMessage.UpdateMessageInDatabase(message);
	}

	public static void changeTransactionState(int transactionId, transactionState targetState) throws PseudoException{
		Transaction transaction = CarpoolDaoTransaction.getTransactionById(transactionId);
		transaction.setState(targetState);
		CarpoolDaoTransaction.updateTransactionInDatabase(transaction);

		//send notifications
		ArrayList<Notification> ns = new ArrayList<Notification>();
		ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, transaction.getProviderId(), transaction.getCustomerId(), transaction.getMessageId(), transaction.getTransactionId()));
		ns.add(new Notification(Constants.NotificationEvent.transactionCancelled, transaction.getCustomerId(), transaction.getProviderId(), transaction.getMessageId(), transaction.getTransactionId()));
		NotificationDaoService.sendNotification(ns);
	}

	public static void clearBothDatabase(){
		CarpoolDaoBasic.clearBothDatabase();
	}

	public static void forceMessageClean() throws LocationNotFoundException{
		MessageCleaner.Clean();
	}

	public static void forceTransactionMonitoring() throws LocationNotFoundException{
		TransactionCleaner.Clean();
	}

	public static void forceReloadLocation() throws LocationException, ValidationException, LocationNotFoundException{
		CarpoolDaoLocation.reloadDefaultLocations();
	}

}
