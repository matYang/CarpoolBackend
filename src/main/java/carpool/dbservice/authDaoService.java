package carpool.dbservice;

import java.util.Calendar;

import org.apache.commons.lang3.RandomStringUtils;

import carpool.constants.Constants;
import carpool.database.DaoBasic;
import carpool.database.DaoUser;
import carpool.exception.ValidationException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.User;

public class authDaoService {

	/**
	 * only checks for login, compares email and password to results from db,
	 * move login states checking to User class, eg isUserValid etc
	 * @return	if validation failed/email not exist, return null, else, return user object constructed from topBarUser constructor
	 * @throws UserNotFoundException 
	 * @throws no need to throw user not found exception here, not found then not log in
	 */
	public static User authenticateUserLogin(String email, String password) throws ValidationException{
	
		User user;
		try {
			user = DaoUser.getUserByEmail(email);
		} catch (UserNotFoundException e) {
			throw new ValidationException("Email/Password Incorrect");
		}
		
		if(!user.validate()){
			throw new ValidationException("Account not valid");
		}
		if(!user.isPasswordCorrect(password)){
			throw new ValidationException("Email/Password Incorrect");
		}
		return user;
	
	}

	/**
	 * check against the key_forgetPasswordAuth+id : authCode pair in Redis
	 */
	public static boolean isResetPasswordValid(int userId, String authCode){
		String key = Constants.key_forgetPasswordAuth+userId;
		String match = DaoBasic.getJedis().get(key);
		return match.equals(authCode);
	}

	/**
	 * gets the user from sessionString: 
	 * 1. try to retrieve id+time stamp 
	 * 2. if not found, return null, else, check time stamp 
	 * 3. if time stamp expired, return null, else, get user using id from SQL
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
	 * @return  a random string of fixed length 15 characters, consisting of a-z, A-Z, 0-9,
	 */
	static String generateRandomString(){
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
		return sessionString;
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

}
