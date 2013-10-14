package carpool.dbservice;

import org.apache.commons.lang3.RandomStringUtils;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.relayTask.EmailRelayTask;
import carpool.common.DebugLog;
import carpool.common.EmailHandler;
import carpool.constants.Constants;
import carpool.carpoolDAO.*;
import carpool.encryption.EmailCrypto;
import carpool.exception.user.UserNotFoundException;
import carpool.model.User;

public class EmailDaoService {

	/**
	 * changes the user's email
	 * 1st: change user's email to newEmail, emailAcvtivated  to false in database, clear userSession in Redis
	 * 2nd: send an activation email to the new Email address
	 * @param userId
	 * @param newEmail
	 * @param sessionString
	 * @return return false if error occurs, eg inside the catch clause
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean changeEmail(int userId, String newEmail, String sessionString) throws UserNotFoundException{
		try {
			User user = CarpoolDaoUser.getUserById(userId);
			user.setEmail(newEmail);
			user.setEmailActivated(false);
			CarpoolDaoUser.UpdateUserInDatabase(user);
			AuthDaoService.closeUserSession(sessionString);
			EmailDaoService.sendActivationEmail(userId, newEmail);
			return true;
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
		}
		return false;
	}

	/**
	 * @param newEmail  the new email that an activation email should be sent to
	 * store  newUserId - authCode key pair in Redis for fast access, use newUserId as key
	 * use Constants.key_emailActivationAuth + userId as Redis key
	 * @return   return true of successful, return false if unsuccessful
	 */
	public static boolean sendActivationEmail(int userId, String newEmail){
		String authCode = RandomStringUtils.randomAlphanumeric(15);
		//clear previous session whatsoever
		CarpoolDaoBasic.getJedis().del(Constants.key_emailActivationAuth + userId);
		//start new session, make sure only one session exists ot a time
		CarpoolDaoBasic.getJedis().set(Constants.key_emailActivationAuth + userId, authCode);
		String encryptedEmailKey = EmailCrypto.encrypt(userId, authCode);
		try {
			EmailRelayTask emailTask = new EmailRelayTask(newEmail, "Activate your email address", "http://"+Constants.domainName+"/api/v1.0/users/emailActivation?key="+encryptedEmailKey);
			ExecutorProvider.executeRelay(emailTask);
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * when user clicks the activation address, an API call made to server will access this method
	 * change emailActivated field of the user in sql to true
	 * delete the id-authCode key pair in Redis
	 * fetch the authcode from Redis and check against the authCode passed in first, if does not match, do not activate email
	 * @param userId
	 * @param authCode
	 * @return topBarUser if operation successful, null if error occurred
	 * @throws user not found exception if the user id does not exist
	 */
	public static User activateUserEmail(int userId, String authCode) throws UserNotFoundException{
		try{
			if(!CarpoolDaoBasic.getJedis().get(Constants.key_emailActivationAuth + userId).equals(authCode)){
				DebugLog.d("anthCode does not match");
				return null;
			}
		}
		catch (NullPointerException e){
			throw new UserNotFoundException();
		}
		
		try {
			User user =  CarpoolDaoUser.getUserById(userId);
			user.setEmailActivated(true);
			CarpoolDaoUser.UpdateUserInDatabase(user);
			CarpoolDaoBasic.getJedis().del(Constants.key_emailActivationAuth +  userId);
			return user;
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
		}
		return null;
	}

	/**
	 * checks if the user's email has already been activated
	 * @param userId
	 * @return true is user's email is activated, false otherwise
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean isUserEmailActivated(int userId) throws UserNotFoundException{
		try {
			User user = CarpoolDaoUser.getUserById(userId);
			return user.isEmailActivated();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * make sure userId exist
	 * resends an activation email to the user
	 * clear the previous authCode session in Redis, use Constants.key_emailActivationAuth + userId as Redis key, generate a new authCode
	 * @param userId
	 * @return true if email sent successfully
	 */
	public static boolean reSendActivationEmail(int userId){
		User user = null;
		try {
			user = CarpoolDaoUser.getUserById(userId);
		} catch (UserNotFoundException e) {
			DebugLog.d("User does not exsit");
			return false;
		}
		//make sure you have the prefix...don't want to debug this
		CarpoolDaoBasic.getJedis().del(Constants.key_emailActivationAuth + userId);
		String authCode = RandomStringUtils.randomAlphanumeric(30);
		CarpoolDaoBasic.getJedis().set(Constants.key_emailActivationAuth + userId, authCode);
		sendActivationEmail(userId, user.getEmail());
		return true;
	}

	/**
	 * send a changedPassWordEmail to the target email, it can be assumed that email passed here is valid and registered
	 * delete  Constants.key_forgetPasswordAuth + id no matter if it exists or not
	 * save Constants.key_forgetPasswordAuth + id : newAuthCode in Redis
	 * use EmailCrypto.encrype(id, authCode) to get a single encrypted key and place it in email, it will be decrypted when users sends it back
	 * @param email
	 * @return isSent
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean sendChangePasswordEmail(String email) throws UserNotFoundException{
		try {
			User user = CarpoolDaoUser.getUserByEmail(email);
			int  userId = user.getUserId();
			CarpoolDaoBasic.getJedis().del(Constants.key_forgetPasswordAuth + userId);
			String authCode = RandomStringUtils.randomAlphanumeric(30);
			CarpoolDaoBasic.getJedis().set(Constants.key_forgetPasswordAuth + userId, authCode);
			String encryptedEmailKey = EmailCrypto.encrypt(userId, authCode);
			EmailHandler.send(email, "Change your password", Constants.domainName+"/forgetPassword?key="+encryptedEmailKey);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isEmailAvailable(String email){
		try {
			CarpoolDaoUser.getUserByEmail(email);
		} catch (UserNotFoundException e){
			return true;
		}
		return false;
	}

}
