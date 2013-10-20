package carpool.dbservice.admin;

import carpool.carpoolDAO.*;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.transactionState;
import carpool.constants.Constants.userState;
import carpool.exception.*;
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
	}
	
	public static void clearBothDatabase(){
		CarpoolDaoBasic.clearBothDatabase();
	}
	
	public static void forceMessageClean(){
		
	}
	
	public static void forceTransactionMonitoring(){
		
	}

}
