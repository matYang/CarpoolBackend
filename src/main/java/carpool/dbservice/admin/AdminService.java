package carpool.dbservice.admin;

import java.util.ArrayList;
import java.util.Calendar;

import carpool.carpoolDAO.*;
import carpool.cleanRoutineTask.MessageCleaner;
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.LicenseType;
import carpool.configurations.EnumConfig.MessageState;
import carpool.configurations.EnumConfig.PassengerVerificationOrigin;
import carpool.configurations.EnumConfig.TransactionState;
import carpool.configurations.EnumConfig.UserState;
import carpool.configurations.EnumConfig.VerificationState;
import carpool.configurations.EnumConfig.VerificationType;
import carpool.dbservice.NotificationDaoService;
import carpool.dbservice.UserDaoService;
import carpool.exception.*;
import carpool.exception.identityVerification.identityVerificationNotFound;
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
	
	
	public static ArrayList<PassengerVerification>  getPendingPassengerVerification() throws PseudoException{
		return CarpoolDaoPassenger.getPassengerVerifications(VerificationState.pending);
		
	}
	
	
	public static void rejectDriverVerification(int verificationId, int reviewId) throws identityVerificationNotFound{
		DriverVerification driverVerification = CarpoolDaoDriver.getDriverVerificationById(verificationId);
		//only change state if it is currently pending
		if (driverVerification.getState() == VerificationState.pending){
			driverVerification.setState(VerificationState.rejected);
			driverVerification.setReviewDate(Calendar.getInstance());
			driverVerification.setReviewerId(reviewId);
			CarpoolDaoDriver.updateDriverVerificationInDatabases(driverVerification);
		}
	}
	
	public static void rejectPassengerVerification(int verificationId, int reviewId) throws PseudoException{
		PassengerVerification passengerVerification = CarpoolDaoPassenger.getPassengerVerificationById(verificationId);
		//only change state if it is currently pending
		if (passengerVerification.getState() == VerificationState.pending){
			passengerVerification.setState(VerificationState.rejected);
			passengerVerification.setReviewDate(Calendar.getInstance());
			passengerVerification.setReviewerId(reviewId);
			CarpoolDaoPassenger.updatePassengerVerificationInDatabases(passengerVerification);
		}
	}

	
	//id, issue date, expire date, review id, review date
	public static void verifyDriverVerification(int verificationId, Calendar issueDate, Calendar expireDate, int reviewId) throws PseudoException{
		DriverVerification driverVerification = CarpoolDaoDriver.getDriverVerificationById(verificationId);
		//only change state if it is currently pending
		if (driverVerification.getState() == VerificationState.pending){
			driverVerification.setState(VerificationState.verified);
			
			driverVerification.setLicenseIssueDate(issueDate);
			driverVerification.setExpireDate(expireDate);
			
			driverVerification.setReviewDate(Calendar.getInstance());
			driverVerification.setReviewerId(reviewId);
			CarpoolDaoDriver.updateDriverVerificationInDatabases(driverVerification);
			
			//if verified, check if original user has passenger verification, if not, automatically add the passenger verification
			User user = UserDaoService.getUserById(driverVerification.getUserId());
			if (user.getPassengerVerification() == null || user.getPassengerVerification().getState() == VerificationState.expired || user.getPassengerVerification().getState() == VerificationState.rejected){
				PassengerVerification passengerVerification = new PassengerVerification(driverVerification);
				
				passengerVerification = CarpoolDaoPassenger.addPassengerToDatabases(passengerVerification);
				user.setPassengerVerificationId(passengerVerification.getVerificationId());
				UserDaoService.updateUser(user);
			}
		}
	}
	
	//id, expire date, review id, review date
	public static void verifyPassengerVerification(int verificationId, Calendar expireDate, int reviewId) throws PseudoException{
		PassengerVerification passengerVerification = CarpoolDaoPassenger.getPassengerVerificationById(verificationId);
		//only change state if it is currently pending
		if (passengerVerification.getState() == VerificationState.pending){
			passengerVerification.setState(VerificationState.verified);
			
			passengerVerification.setExpireDate(expireDate);
			
			passengerVerification.setReviewDate(Calendar.getInstance());
			passengerVerification.setReviewerId(reviewId);
			CarpoolDaoPassenger.updatePassengerVerificationInDatabases(passengerVerification);
		}
	}

}
