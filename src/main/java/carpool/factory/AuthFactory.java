package carpool.factory;

import java.util.Calendar;

import org.apache.commons.lang3.RandomStringUtils;

import redis.clients.jedis.Jedis;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.CarpoolConfig;

public class AuthFactory {
	
	public static final String emailActivation_getKey(int userId){
		return CarpoolConfig.key_emailActivationAuth + userId;
	}
	
	public static final String emailActivation_setAuthCode(int userId){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		String authCode = RandomStringUtils.randomAlphanumeric(CarpoolConfig.emailActivation_sequenceLength) + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp();
		jedis.set(emailActivation_getKey(userId), authCode);
		CarpoolDaoBasic.returnJedis(jedis);
		return authCode;
	}
	
	public static final boolean emailActivation_validate(int userId, String challengeAuthCode){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		try{
			if(challengeAuthCode == null || challengeAuthCode.length() == 0){
				return false;
			}else{
				String storedAuthCode = jedis.get(emailActivation_getKey(userId));
				String separate[] = storedAuthCode.split(CarpoolConfig.redisSeperatorRegex);
				String storedTimeStamp = separate[1];

				//check for expiration
				if((DateUtility.getCurTime() - Long.parseLong(storedTimeStamp)) > CarpoolConfig.emailActivation_expireThreshold){
					//expired, delete record and return false
					jedis.del(emailActivation_getKey(userId));
					return false;
				}
				
				if (!storedAuthCode.equals(challengeAuthCode)){
					return false;
				}
				else{
					jedis.del(emailActivation_getKey(userId));
					return true;
				}
			}
		} catch (Exception e){
			DebugLog.d(e);
			return false;
		} finally{
			CarpoolDaoBasic.returnJedis(jedis);
		}
	}
	
	
	private static final String forgetPassword_getKey(int userId){
		return CarpoolConfig.key_forgetPasswordAuth + userId;
	}
	
	public static final String forgetPassword_setAuthCode(int userId){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		String authCode = RandomStringUtils.randomAlphanumeric(CarpoolConfig.forgetPassword_sequenceLength) + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp();
		jedis.set(forgetPassword_getKey(userId), authCode);
		CarpoolDaoBasic.returnJedis(jedis);
		return authCode;
	}
	
	public static final boolean forgetPassword_validate(int userId, String challengeAuthCode){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		try{
			if(challengeAuthCode == null || challengeAuthCode.length() == 0){
				return false;
			}else{
				String storedAuthCode = jedis.get(forgetPassword_getKey(userId));
				String separate[] = storedAuthCode.split(CarpoolConfig.redisSeperatorRegex);
				String storedTimeStamp = separate[1];
				
				//check for expiration
				if((DateUtility.getCurTime() - Long.parseLong(storedTimeStamp )) > CarpoolConfig.forgetPassword_expireThreshold){
					//expired, delete record and return false
					jedis.del(forgetPassword_getKey(userId));
					return false;
				}
				
				if (!storedAuthCode.equals(challengeAuthCode)){
					return false;
				}
				else{
					jedis.del(forgetPassword_getKey(userId));
					return true;
				}
			}
		} catch (Exception e){
			DebugLog.d(e);
			return false;
		} finally{
			CarpoolDaoBasic.returnJedis(jedis);
		}
	}
	
	
	/**
	 * session format: ran+userId : userId+timeStamp
	 */
	public static final String session_openSession(int userId){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		String sessionString = RandomStringUtils.randomAlphanumeric(CarpoolConfig.session_sequenceLength) + CarpoolConfig.redisSeperator + userId;
		String authCode = userId + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp();
				
		jedis.set(sessionString, authCode);
		CarpoolDaoBasic.returnJedis(jedis);
		return sessionString;
	}
	
	/**
	 * simply validate if a session string is valid
	 * @param sessionString
	 * @return -1 if not valid, userId if valid
	 */
	public static final int session_validate(String sessionString){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		try{
			String idTimeStamp = jedis.get(sessionString);
			if(idTimeStamp == null || idTimeStamp.length() == 0){
				//if session does not exist, return -1
				return -1;
			}
			String id = idTimeStamp.split(CarpoolConfig.redisSeperatorRegex)[0];
			String timeStamp = idTimeStamp.split(CarpoolConfig.redisSeperatorRegex)[1];
			if((DateUtility.getCurTime() - Long.parseLong(timeStamp)) > CarpoolConfig.session_expireThreshold){
				//expired
				jedis.del(sessionString);
				return -1;
			}
			else{
				return Integer.parseInt(id);
			}
		} catch (Exception e){
			DebugLog.d(e);
			return -1;
		} finally{
			CarpoolDaoBasic.returnJedis(jedis);
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
		Jedis jedis = CarpoolDaoBasic.getJedis();
		try{
			String idTimeStamp = jedis.get(sessionString);
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
					jedis.del(sessionString);
					return false;
				}
				if ((DateUtility.getCurTime() - Long.parseLong(timeStamp)) > CarpoolConfig.session_updateThreshold){
					//if should update, udpate only the time stamp in the value pair
					jedis.set(sessionString, id + CarpoolConfig.redisSeperator + DateUtility.getTimeStamp());
				}
				return true;
			}
		} catch (Exception e){
			DebugLog.d(e);
			return false;
		} finally{
			CarpoolDaoBasic.returnJedis(jedis);
		}
		
	}
	
	public static final boolean session_closeSession(String sessionString){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		boolean result = jedis.del(sessionString) == 1;
		CarpoolDaoBasic.returnJedis(jedis);
		return result;
	}
	
}
