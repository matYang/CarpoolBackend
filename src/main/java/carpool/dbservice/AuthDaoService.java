package carpool.dbservice;

import java.util.Calendar;

import org.apache.commons.lang3.RandomStringUtils;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.CarpoolConfig;
import carpool.exception.PseudoException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.factory.AuthFactory;
import carpool.model.User;

public class AuthDaoService {

	/**
	 * only checks for login, compares email and password to results from db,
	 * move login states checking to User class, eg isUserValid etc
	 * @return	if validation failed/email not exist, return null, else, return user object constructed from topBarUser constructor
	 * @throws UserNotFoundException 
	 * @throws no need to throw user not found exception here, not found then not log in
	 */
	public static User authenticateUserLogin(String email, String password) throws PseudoException{
	
		User user;
		try {
			user = CarpoolDaoUser.getUserByEmail(email);
			if (!user.isEmailActivated()){
				throw new ValidationException("请先激活账号邮箱");
			}
			if(!user.validate()){
				throw new ValidationException("用户账号信息不符合要求，请联系我们");
			}
			if(!user.isPasswordCorrect(password)){
				throw new ValidationException("您输入的密码不正确");
			}
			user.setLastLogin(DateUtility.getCurTimeInstance());
			UserDaoService.updateUser(user);
		} catch (UserNotFoundException e) {
			throw new ValidationException("您输入的邮箱不存在");
		}
		
		return user;
	
	}


	public static boolean isResetPasswordValid(int userId, String authCode){
		return AuthFactory.forgetPassword_validate(userId, authCode);
	}

	/**
	 * gets the user from sessionString: 
	 */
	public static User getUserFromSession(String sessionString) {
		int id = AuthFactory.session_validate(sessionString);
		if (id < 0){
			return null;
		}
		try {
			User user = CarpoolDaoUser.getUserById(id);
			return user;
		} catch (Exception e) {
			DebugLog.d(e);
			return null;
		}
	}


	/**
	 * take in the authenticated userId, creates a session string, consisting of "random string" + "id" 
	 * store session string - ID+time stamp pair in Redis
	 */
	public static String generateUserSession(int id){
		String sessionString = AuthFactory.session_openSession(id);
		return sessionString;
	}

	/**
	 * @return authentication status
	 */
	public static boolean validateUserSession(int id, String sessionString){
		return AuthFactory.session_strongValidate(id, sessionString);
	}

	/**
	 * delete the (random string+ID) + time stamp pair no matter if it exists or not, and return false
	 */
	public static boolean closeUserSession(String sessionString){
		return AuthFactory.session_closeSession(sessionString);
	}

}
