package carpool.dbservice.admin;

import java.util.ArrayList;

import carpool.carpoolDAO.*;
import carpool.cleanRoutineTask.MessageCleaner;
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.MessageState;
import carpool.configurations.EnumConfig.TransactionState;
import carpool.configurations.EnumConfig.UserState;
import carpool.configurations.EnumConfig.VerificationState;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.*;
import carpool.exception.location.LocationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.*;
import carpool.model.identityVerification.DriverVerification;
import carpool.model.identityVerification.PassengerVerification;

public class AdminService {

	public static void changeUserState(int userId, UserState targetState) throws PseudoException{
		User user = CarpoolDaoUser.getUserById(userId);
		user.setState(targetState);
		CarpoolDaoUser.UpdateUserInDatabase(user);
	}

	public static void changeMessageState(int messageId, MessageState targetState) throws PseudoException{
		Message message = CarpoolDaoMessage.getMessageById(messageId);
		message.setState(targetState);
		CarpoolDaoMessage.UpdateMessageInDatabase(message);
	}

	public static void changeTransactionState(int transactionId, TransactionState targetState) throws PseudoException{
		Transaction transaction = CarpoolDaoTransaction.getTransactionById(transactionId);
		transaction.setState(targetState);
		CarpoolDaoTransaction.updateTransactionInDatabase(transaction);

		//send notifications
		ArrayList<Notification> ns = new ArrayList<Notification>();
		ns.add(new Notification(EnumConfig.NotificationEvent.transactionCancelled, transaction.getProviderId(), transaction.getCustomerId(), transaction.getMessageId(), transaction.getTransactionId()));
		ns.add(new Notification(EnumConfig.NotificationEvent.transactionCancelled, transaction.getCustomerId(), transaction.getProviderId(), transaction.getMessageId(), transaction.getTransactionId()));
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
	
	public static ArrayList<DriverVerification> getPendingDriverVerification() throws PseudoException{
		return CarpoolDaoDriver.getDriverVerifications(VerificationState.pending);
	}
	
	public static void decideDriverVerification(int verificationId, boolean isVerified) throws PseudoException{
		DriverVerification driverVerification = CarpoolDaoDriver.getDriverVerificationById(verificationId);
		//only change state if it is currently pending
		if (driverVerification.getState() == VerificationState.pending){
			driverVerification.setState(isVerified ? VerificationState.verified : VerificationState.rejected);
			CarpoolDaoDriver.updateDriverVerificationInDatabases(driverVerification);
		}
	}
	
	public static ArrayList<PassengerVerification>  getPendingPassengerVerification() throws PseudoException{
		return CarpoolDaoPassenger.getPassengerVerifications(VerificationState.pending);
		
	}
	
	public static void decidePassengerVerification(int verificationId, boolean isVerified) throws PseudoException{
		PassengerVerification passengerVerification = CarpoolDaoPassenger.getPassengerVerificationById(verificationId);
		//only change state if it is currently pending
		if (passengerVerification.getState() == VerificationState.pending){
			passengerVerification.setState(isVerified ? VerificationState.verified : VerificationState.rejected);
			CarpoolDaoPassenger.updatePassengerVerificationInDatabases(passengerVerification);
		}
	}

}
