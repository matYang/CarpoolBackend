package carpool.dbservice;

import java.util.*;


import org.apache.commons.lang3.RandomStringUtils;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.common.*;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.database.*;
import carpool.encryption.EmailCrypto;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;
import carpool.relayTask.EmailRelayTask;

public class UserDaoService{


	/**
	 * return null if fails safe checking, return empty ArrayList if no result found
	 */
	public static ArrayList<User> searchByInfo(String name, String phone, String email, String qq){
		if(name==null || phone==null || email==null || qq==null){
			DebugLog.d("User searchByInfo: Parameters are null");
			return null;
		}
		if(name.equals("")){
			name = "%";
		}
		if(phone.equals("")){
			phone = "%";
		}
		if(email.equals("")){
			email = "%";
		}
		if(qq.equals("")){
			qq = "%";
		}
		return DaoUser.searchUser(name, phone, email, qq);
	}
	

	/**
	 * return all users, used to testing only
	 */
	public static ArrayList<User> getAllUsers() {
		ArrayList<User> users = searchByInfo("", "", "", "");
		return users;
	}

	/**
	 * @param newUser   the newUser just received in POST, constructed by the specified constructor in User
	 * error checking on User have been made on API level, add moderate safe checking if necessary (e.g. not null),
	 * @return the newUser with UserId in place, null if error occurred		
	 */
	public static User createNewUser(User newUser){

		try {
			return DaoUser.addUserToDatabase(newUser);
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
			return null;
		}
	}


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
			User user = DaoUser.getUserById(userId);
			user.setEmail(newEmail);
			user.setEmailActivated(false);
			DaoUser.UpdateUserInDatabase(user);
			closeUserSession(sessionString);
			sendActivationEmail(userId, newEmail);
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
		DaoBasic.getJedis().del(Constants.key_emailActivationAuth + userId);
		//start new session, make sure only one session exists ot a time
		DaoBasic.getJedis().set(Constants.key_emailActivationAuth + userId, authCode);
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
			if(!DaoBasic.getJedis().get(Constants.key_emailActivationAuth + userId).equals(authCode)){
				DebugLog.d("anthCode does not match");
				return null;
			}
		}
		catch (NullPointerException e){
			throw new UserNotFoundException();
		}
		
		try {
			User user =  DaoUser.getUserById(userId);
			user.setEmailActivated(true);
			DaoUser.UpdateUserInDatabase(user);
			DaoBasic.getJedis().del(Constants.key_emailActivationAuth +  userId);
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
			User user = DaoUser.getUserById(userId);
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
			user = DaoUser.getUserById(userId);
		} catch (UserNotFoundException e) {
			DebugLog.d("User does not exsit");
			return false;
		}
		//make sure you have the prefix...don't want to debug this
		DaoBasic.getJedis().del(Constants.key_emailActivationAuth + userId);
		String authCode = RandomStringUtils.randomAlphanumeric(30);
		DaoBasic.getJedis().set(Constants.key_emailActivationAuth + userId, authCode);
		sendActivationEmail(userId, user.getEmail());
		return true;
	}

	/**
	 * send a changedPassWordEmail to the target email, it can be assumed that email passed here is valid and registered
	 * get userId by email
	 * delete  Constants.key_forgetPasswordAuth + id no matter if it exists or not
	 * save Constants.key_forgetPasswordAuth + id : newAuthCode in Redis
	 * use EmailCrypto.encrype(id, authCode) to get a single encrypted key and place it in email, it will be decrypted when users sends it back
	 * @param email
	 * @return isSent
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean sendChangePasswordEmail(String email) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserByEmail(email);
			int  userId = user.getUserId();
			DaoBasic.getJedis().del(Constants.key_forgetPasswordAuth + userId);
			String authCode = RandomStringUtils.randomAlphanumeric(30);
			DaoBasic.getJedis().set(Constants.key_forgetPasswordAuth + userId, authCode);
			String encryptedEmailKey = EmailCrypto.encrypt(userId, authCode);
			EmailHandler.send(email, "Change your password", Constants.domainName+"/forgetPassword?key="+encryptedEmailKey);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * only checks for login, compares email and password to results from db,
	 * move login states checking to User class, eg isUserValid etc
	 * @param email
	 * @param password
	 * @return	if validation failed/email not exist, return null, else, return user object constructed from topBarUser constructor
	 * @throws UserNotFoundException 
	 * @throws no need to throw user not found exception here, not found then not log in
	 */
	public static User isLoginUserValid(String email, String password) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserByEmail(email);
			if(!user.validate()){
				DebugLog.d("user not valid");
				return null;
			}
			if(!user.isPasswordCorrect(password)){
				DebugLog.d("login password failed with paramter " + password);
				return null;
			}
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				DebugLog.d(UserDaoService.getUserById(1).getEmail() + " paramter " + password);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}



	/**
	 * check against the key_forgetPasswordAuth+id : authCode pair in Redis
	 * @param userId
	 * @param authCode
	 * @return true if match, false if not
	 */
	public static boolean isResetPasswordValid(int userId, String authCode){
		String key = Constants.key_forgetPasswordAuth+userId;
		String match = DaoBasic.getJedis().get(key);
		return match.equals(authCode);
	}


	/**
	 * generally will be used as a helper function, this method checks sql
	 * eg: if user's email has been activated
	 * if user's state is valid
	 * @param id   the id of the target existing user
	 * @return  true if user id exist in database and is valid (as of this stage, we may assume any user in database is valid)
	 * @throws if user not found, return false
	 */
	public static boolean isDBUserValid(int id){
		try {
			User user = DaoUser.getUserById(id);
			return user.getState()==Constants.userState.normal;
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
			return false;
		}
	}



	/** use the full constructor specified in User here
	 * error checking is needed here, with only one read request
	 * @param id   the id of the existing user
	 * @return the whole user object, including all its Message, Transaction, Notifications and etc, use the last Contructor (specified for sending data back) for this user object, return null if any error occurred
	 * @throws user not found exception if the user id does not exist
	 */
	public static User getUserById(int id) throws UserNotFoundException{
		return DaoUser.getUserById(id);
	}

	/**
	 * gets the user object for the top bar, refer to the corresponding constructor in User class
	 * @param id	the id of the topBar user to be fetched
	 * @return	the user object constructed with the topBar-specific constructor, return null if any error occurred
	 * @throws user not found exception if the user id does not exist
	 */
	public static User getTopBarUserById(int id) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		return user;
	}


	/**
	 * gets the user from sessionString: 
	 * 1. try to retrieve id+time stamp 
	 * 2. if not found, return null, else, check time stamp 
	 * 3. if time stamp expired, return null, else, get topBarUser using id from sql
	 * 4. if topBarUser retrieved successfully, return it, else, return null if any error occurs
	 * @param sessionString
	 * @return	the topBarUser currently associated with this sessionString
	 */
	public static User getUserFromSession(String sessionString) {
		String idTimeStamp = DaoBasic.getJedis().get(sessionString);
		if(idTimeStamp==null){
			return null;
		}
		String id = idTimeStamp.split("\\+")[0];
		String timeStamp = idTimeStamp.split("\\+")[1];
		if((Calendar.getInstance().getTimeInMillis()-Long.parseLong(timeStamp))>Constants.session_expireThreshould){
			return null;
		}
		try {
			User user = DaoUser.getUserById(Integer.parseInt(id));
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * similar to getUserById by returns a boolean
	 * @param id	id of the user
	 * @return		true if exist, false if not
	 */
	public static boolean isDBUserExist(int id) {
		try {
			DaoUser.getUserById(id);
			return true;
		} catch (UserNotFoundException e) {
			return false;
		}
	}



	/**
	 * update entire user object, minimize DB interactions
	 * @param user   the  target user to be updated
	 * @param id     the id of the target user, passed here as double assurance
	 * @return  the newly updated user, null if error occurred, eg id does not exist
	 * @throws user not found exception if the user id does not exist
	 */
	public static User updateUser(User user, int id) throws PseudoException{
		if(id != user.getUserId()){
			throw new ValidationException("id does not match");
		}
		try {
			DaoUser.UpdateUserInDatabase(user);
			return user;
		}catch(Exception e){
			DebugLog.d(e.getMessage());
			return null;
		}
	}



	/**
	 * delete user object minimize DB interactions
	 * @param id  
	 * @throws user not found exception if the user id does not exist
	 */
	public static void deleteUser(int id) throws UserNotFoundException{
		DaoUser.deleteUserFromDatabase(id);
	}



	/**
	 * changes user's password, verify user's old password, if verified, change it to new password
	 * it can be assumed that all passwords passed here have the right format, no need for formatting checking
	 * @param userId  the id of the target user
	 * @param oldPassword   the old password
	 * @param newPassword   the new password
	 * @return true of password changed, false of not
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean changePassword(int userId, String oldPassword, String newPassword) throws UserNotFoundException{
		if(oldPassword.equals(newPassword)){
			DebugLog.d("old pasword and new pasword are the same.");
			return false;
		}
		try {
			User user = DaoUser.getUserById(userId);
			user.setPassword(oldPassword, newPassword);
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
			return false;
		}
	}


	/**
	 * change the password of the matching user to newPassword
	 * @param userId
	 * @param newPassword
	 * @return	true if changed, false if not
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean resetUserPassword(int userId, String newPassword) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(userId);
			user.setPassword("dontcare", newPassword);
			DaoUser.UpdateUserInDatabase(user);
			user = DaoUser.getUserById(userId);
			if(user.isPasswordCorrect(newPassword)){
				return true;
			}
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
		}
		return false;
	}

	/**
	 * changes user's location
	 * @param userId
	 * @param location
	 * @return the newly updated location, return null if error occurred during operation, eg id does not exist
	 * @throws user not found exception if the user id does not exist
	 */
	public static Location changeSingleLocation(int userId, Location location) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(userId);
			if(user.getLocation().equals(location)){
				return null;
			}
			user.setLocation(location);
			DaoUser.UpdateUserInDatabase(user);
			return location;
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
		}
		return null;
	}


	/**
	 * changes user's contact information, it can be assumed all info entries here are valid
	 * @throws user not found exception if the user id does not exist
	 */
	public static User changeContactInfo(int userId, String name, int age, gender gender, String phone, String qq) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(userId);
			String oldName = user.getName();
			int oldAge = user.getAge();
			gender oldGender = user.getGender();
			String oldPhone = user.getPhone();
			String oldQq = user.getQq();
			if(oldName.equals(name) && oldAge==age && oldGender==gender && oldPhone.equals(phone) && oldQq.equals(qq)){
				return null;
			}
			user.setName(name);
			user.setAge(age);
			user.setGender(gender);
			user.setPhone(phone);
			user.setQq(qq);
			DaoUser.UpdateUserInDatabase(user);
			return user;
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
		}
		return null;
	}

	/**
	 * @return  a random string of fixed length 15 characters, consisting of a-z, A-Z, 0-9,
	 */
	private static String generateRandomString(){
		return RandomStringUtils.randomAlphanumeric(15);
	}


	/**
	 * take in the authenticated userId, creates a session string, consisting of "random string" + "id" 
	 * store session string - ID+time stamp pair in Redis
	 * @param id  the id of the user that is about to be logged in
	 * @return  the latest session string (random string + id)
	 */
	public static String generateUserSession(int id){
		Calendar c = Calendar.getInstance();
		String timeStamp = c.getTimeInMillis()+"";
		String randomString = generateRandomString();
		String sessionString = randomString + "+" + id;
		DaoBasic.getJedis().set(sessionString, id+"+"+timeStamp);
		updateUserLastLogin(id);
		return sessionString;
	}

	/**
	 * take in the user Id, update its last login field with current server time, eg Calendar.getInstance()
	 * it can be assumed that the id passed here exists, is valid, and has passed checking of states errors
	 * @param id
	 */
	private static void updateUserLastLogin(int id){
		try {
			User user = DaoUser.getUserById(id);
			user.setLastLogin(Calendar.getInstance());
			DaoUser.UpdateUserInDatabase(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * query Redis using the random string in the session string, get Id+time stamp value
	 * if the sessionString - ID+time stamp pair is not found, return false
	 * if the pair exists, separate id and time stamp, if id does not match target id, return false, if time stamp exceeds the maximum age, delete the key - value pair, return false
	 * if the sessionString - ID+time stamp pair exists, ID matches target id, and time stamp is within maximum age, assign the latest time stamp to id, and update the value at random string in Redis if it is older than 3 days, return true
	 * @param id id
	 * @param userSessionString
	 * @return authentication status
	 */
	public static boolean validateUserSession(int id, String userSessionString){
		String IDTimeStamp = DaoBasic.getJedis().get(userSessionString);
		if(IDTimeStamp==null){
			return false;
		}else{
			String separate[] = IDTimeStamp.split("\\+");
			String ID = separate[0];
			String timeStamp = separate[1];
			if(!ID.equals(id+"")){
				return false;
			}
			//Maximum age
			if((Calendar.getInstance().getTimeInMillis()-Long.parseLong(timeStamp))>Constants.session_expireThreshould){
				DaoBasic.getJedis().del(userSessionString);
				return false;
			}
			if ((Calendar.getInstance().getTimeInMillis()-Long.parseLong(timeStamp))>Constants.session_updateThreshould){
				Calendar c = Calendar.getInstance();
				timeStamp = c.getTimeInMillis()+"";
				DaoBasic.getJedis().set(userSessionString, ID+"+"+timeStamp);
			}
		}
		return true;
	}



	/**
	 * delete the (random string+ID) + time stamp pair no matter if it exists or not, and return false
	 * @param sessionString the session string of the current user (random string + time stamp, remember to separate to get the random string)
	 * @return  true if delete successfully
	 */
	public static boolean closeUserSession(String sessionString){
		return DaoBasic.getJedis().del(sessionString)==1;
	}


	/**
	 * Set the imgPath of the target user in sql
	 * @param id
	 * @param imgPath
	 * @return true if set, false if not
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean setImagePath(int id, String imgPath) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(id);
			user.setImgPath(imgPath);
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * gets the imgPath of the target user
	 * @param id
	 * @return	return the path, return null if id does not exit
	 * @throws user not found exception if the user id does not exist
	 */
	public static String getImagePath(int id) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(id);
			return user.getImgPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * return an array list of users that are watched by the current user
	 * make sure each user is valid, and use the right user constructor is used
	 * @param id
	 * @return	null if id does not exist, return empty ArrayList if no one watched, nulls are always for invalid situations
	 * @throws user not found exception if the user id does not exist
	 */
	public static ArrayList<User> getWatchedUsers(int id) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		if(user==null){
			return null;
		}
		return user.getSocialList();
	}

	/**
	 * watch the user, make sure user exist and no duplicate
	 * user the topBarUser constructor
	 * @param id
	 * @param targetUserId
	 * @return return the watched user if the user is already in the watch list or it was not in the watch list but now added, null if any errors
	 * @throws user not found exception if the user id does not exist
	 */
	public static User watchUser(int id, int targetUserId) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		User target = DaoUser.getUserById(targetUserId);
		if(!user.getSocialList().contains(target)){
			user.getSocialList().add(target);
		}
		try {
			DaoUser.UpdateUserInDatabase(user);
			//send followed Notification
			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.followed,
					id, user.getName(), 0, 0, targetUserId, "You have been followed by XXX", Calendar.getInstance(), false, false);
			NotificationDaoService.createNewNotification(n);
			return target;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * stop the current user from watching the target user
	 * remember to check things like nulls and use the right user constructor
	 * @param userId
	 * @param targetUserId
	 * @return	true if target user was not in the in the watched list or is now removed from current user's watching list, false if any error occurs
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean deWatchUser(int userId, int targetUserId) throws UserNotFoundException{
		User user = DaoUser.getUserById(userId);
		User target = DaoUser.getUserById(targetUserId);
		HelperOperator.removeFromSocialList(user.getSocialList(), target.getUserId());
		try {
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * check if email is still available, this has to look in database
	 * @param email
	 * @return true if available, false if not
	 */
	public static boolean isEmailAvailable(String email){
		try {
			DaoUser.getUserByEmail(email);
		} catch (UserNotFoundException e){
			return true;
		}
		return false;
	}

	/**
	 * changes target user's email notice state to desired state
	 * @param userId
	 * @param emailNotice
	 * @return	true if operation successful, false if error occurred, eg target user not found, exception caught
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean toggleEmailNotice(int userId, boolean emailNotice) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(userId);
			user.setEmailNotice(emailNotice);
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * changes target user's phone notice state to desired state
	 * @param userId
	 * @param phoneNotice
	 * @return	true if operation successful, false if error occurred, eg target user not found, exception caught
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean togglePhoneNotice(int userId, boolean phoneNotice) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(userId);
			user.setPhoneNotice(phoneNotice);
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * return an array list of messages that are watched by the current user
	 * make sure each message is valid, and use the right user constructor is used (format indicated in DMMessage as part of the mainPageUser message field)
	 * @param id
	 * @return	null if there are any errors, eg. id does not exist, return empty ArrayList if no message watched
	 * @throws user not found exception if the user id does not exist
	 */
	public static ArrayList<Message> getWatchedMessaegs(int id) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		if(user==null){
			return null;
		}
		return user.getWatchList();
	}


	/**
	 * watch the message, make sure user exist and no duplicate
	 * use the brief message constructor
	 * @param id
	 * @param targetMessageId
	 * @return	return the watched message if the message is already in the watch list or it was not in the watch list but now added, null if any errors
	 * @throws user not found exception if the user id does not exist
	 */
	public static Message watchMessage(int id, int targetMessageId) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		try {
			Message msg = DaoMessage.getMessageById(targetMessageId);
			if(user.getWatchList().contains(msg)){
				return msg;
			}
			user.getWatchList().add(msg);
			DaoUser.UpdateUserInDatabase(user);
			//sent Message Watched Notification
			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.messageWatched,
					id, user.getName(), targetMessageId, 0, msg.getOwnerId(), "Your Message XXX has beed watched by XXX",
					Calendar.getInstance(), false, false);
			NotificationDaoService.createNewNotification(n);
			return msg;
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * stop the current user from watching the target message
	 * remember to check things like nulls
	 * @param userId
	 * @param targetMessageId
	 * @return true if target message was not in the in the watched list or is now removed from current user's watching list, false if any error occurs
	 * @throws user not found exception if the user id does not exist
	 */
	public static boolean deWatchMessage(int userId, int targetMessageId) throws UserNotFoundException{
		User user = DaoUser.getUserById(userId);
		try {
			Message msg = DaoMessage.getMessageById(targetMessageId);
			HelperOperator.removeFromWatchList(user.getWatchList(), msg.getMessageId());
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * gets the list of message histories from users, return an ArrayList list of all messages that user posted or has been involved in a transaction with
	 * @param user id
	 * @return	null if any errors occurs, if nothing is wrong, return an ArrayList of DMMessage Objects constructed by the specified constructor in DMMessage
	 * @throws UserNotFoundException
	 */
	public static ArrayList<Message> getHistoryMessageByUserId(int id) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		if(user==null){
			throw new UserNotFoundException();
		}
		return user.getHistoryList();
	}
	
	
	/**
	 * gets the complete list of transactions from the user
	 * @param user id
	 * @return an ArrayList of Transactions constructed by the Summary Constructor specified in Transaction class, null if any errors occur
	 * @throws UserNotFoundException
	 */
	public static ArrayList<Transaction> getTransactionByUserId(int id) throws UserNotFoundException{
		User user = getUserById(id);
		if(user==null){
			throw new UserNotFoundException();
		}
		return user.getTransactionList();
	}
	
	/**
	 * gets the complete list of notifications from the user, use the notification full constructor
	 * the ownership of a notification is defined solely by the notification's targetUserId, initUserId is used to fill in the content and set the link for the notification
	 * @param id
	 * @return
	 * @throws UserNotFoundException
	 */
	public static ArrayList<Notification> getNotificationByUserId(int id) throws UserNotFoundException{
		User user = getUserById(id);
		return user.getNotificationList();
	}
	
	
}
