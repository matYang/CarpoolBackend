package carpool.factory;

import java.util.Calendar;

import org.apache.commons.lang3.RandomStringUtils;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.CarpoolConfig;

public class AuthFactory {
	
	public static final String emailActivation_getKey(int userId){
		return CarpoolConfig.key_emailActivationAuth + userId;
	}
	
	public static final String emailActivation_setAuthCode(int userId){
		String authCode = RandomStringUtils.randomAlphanumeric(CarpoolConfig.emailActivation_sequenceLength) + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp();
		CarpoolDaoBasic.getJedis().set(emailActivation_getKey(userId), authCode);
		return authCode;
	}
	
	public static final boolean emailActivation_validate(int userId, String challengeAuthCode){
		try{
			if(challengeAuthCode == null || challengeAuthCode.length() == 0){
				return false;
			}else{
				String storedAuthCode = CarpoolDaoBasic.getJedis().get(emailActivation_getKey(userId));
				String separate[] = storedAuthCode.split(CarpoolConfig.redisSeperatorRegex);
				String storedTimeStamp = separate[1];

				//check for expiration
				if((DateUtility.getCurTime() - Long.parseLong(storedTimeStamp)) > CarpoolConfig.emailActivation_expireThreshold){
					//expired, delete record and return false
					CarpoolDaoBasic.getJedis().del(emailActivation_getKey(userId));
					return false;
				}
				
				if (!storedAuthCode.equals(challengeAuthCode)){
					return false;
				}
				else{
					CarpoolDaoBasic.getJedis().del(emailActivation_getKey(userId));
					return true;
				}
			}
		} catch (Exception e){
			DebugLog.d(e);
			return false;
		}
	}
	
	
	private static final String forgetPassword_getKey(int userId){
		return CarpoolConfig.key_forgetPasswordAuth + userId;
	}
	
	public static final String forgetPassword_setAuthCode(int userId){
		String authCode = RandomStringUtils.randomAlphanumeric(CarpoolConfig.forgetPassword_sequenceLength) + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp();;
		CarpoolDaoBasic.getJedis().set(forgetPassword_getKey(userId), authCode);
		return authCode;
	}
	
	public static final boolean forgetPassword_validate(int userId, String challengeAuthCode){
		try{
			if(challengeAuthCode == null || challengeAuthCode.length() == 0){
				return false;
			}else{
				String storedAuthCode = CarpoolDaoBasic.getJedis().get(forgetPassword_getKey(userId));
				String separate[] = storedAuthCode.split(CarpoolConfig.redisSeperatorRegex);
				String storedTimeStamp = separate[1];
				
				//check for expiration
				if((DateUtility.getCurTime() - Long.parseLong(storedTimeStamp )) > CarpoolConfig.forgetPassword_expireThreshold){
					//expired, delete record and return false
					CarpoolDaoBasic.getJedis().del(forgetPassword_getKey(userId));
					return false;
				}
				
				if (!storedAuthCode.equals(challengeAuthCode)){
					return false;
				}
				else{
					CarpoolDaoBasic.getJedis().del(forgetPassword_getKey(userId));
					return true;
				}
			}
		} catch (Exception e){
			DebugLog.d(e);
			return false;
		}
	}
	
	
	/**
	 * session format: ran+userId : userId+timeStamp
	 */
	public static final String session_openSession(int userId){
		String sessionString = RandomStringUtils.randomAlphanumeric(CarpoolConfig.session_sequenceLength) + CarpoolConfig.redisSeperator + userId;
		
		String authCode = userId + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp();
				
		CarpoolDaoBasic.getJedis().set(sessionString, authCode);
		return sessionString;
	}
	
	/**
	 * simply validate if a session string is valid
	 * @param sessionString
	 * @return -1 if not valid, userId if valid
	 */
	public static final int session_validate(String sessionString){
		try{
			String idTimeStamp = CarpoolDaoBasic.getJedis().get(sessionString);
			if(idTimeStamp == null || idTimeStamp.length() == 0){
				//if session does not exist, return -1
				return -1;
			}
			String id = idTimeStamp.split(CarpoolConfig.redisSeperatorRegex)[0];
			String timeStamp = idTimeStamp.split(CarpoolConfig.redisSeperatorRegex)[1];
			if((DateUtility.getCurTime() - Long.parseLong(timeStamp)) > CarpoolConfig.session_expireThreshold){
				//expired
				CarpoolDaoBasic.getJedis().del(sessionString);
				return -1;
			}
			else{
				return Integer.parseInt(id);
			}
		} catch (Exception e){
			DebugLog.d(e);
			return -1;
		}
		
	}
	
	/**
	 * strong validate a session string against a user, and update the time stamp is exceeds update threshold and within expire threshold
	 * if the sessionString - ID+time stamp pair is not found, return false
	 * if the pair exists, separate id and time stamp, if id does not match target id, return false
	 * if time stamp exceeds the maximum age, delete the key - value pair, return false
	 * if the sessionString - ID+time stamp pair exists, ID matches target id, and time stamp is within maximum age, assign the latest time stamp to id, and update the value at random string in Redis if it is older than 3 days, return true
	 */
	public static final boolean session_strongValidate(int challengeUserId, String sessionString){
		try{
			String idTimeStamp = CarpoolDaoBasic.getJedis().get(sessionString);
			if(idTimeStamp == null || idTimeStamp.length() == 0){
				//if session does not exist, return -1
				return false;
			}else{
				String separate[] = idTimeStamp.split(CarpoolConfig.redisSeperatorRegex);
				String id = separate[0];
				String timeStamp = separate[1];
				if(!id.equals(challengeUserId + "")){
					return false;
				}
				if((DateUtility.getCurTime() - Long.parseLong(timeStamp)) > CarpoolConfig.session_expireThreshold){
					//if expired, clean up and return false
					CarpoolDaoBasic.getJedis().del(sessionString);
					return false;
				}
				if ((DateUtility.getCurTime() - Long.parseLong(timeStamp)) > CarpoolConfig.session_updateThreshold){
					//if should update, udpate only the time stamp in the value pair
					CarpoolDaoBasic.getJedis().set(sessionString, id + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp());
				}
				return true;
			}
		} catch (Exception e){
			DebugLog.d(e);
			return false;
		}
		
	}
	
	public static final boolean session_closeSession(String sessionString){
		return CarpoolDaoBasic.getJedis().del(sessionString) == 1;
	}
	
}
