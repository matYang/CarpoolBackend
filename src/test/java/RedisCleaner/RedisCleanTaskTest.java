package RedisCleaner;

import static org.junit.Assert.*;
import java.util.Set;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DateUtility;
import carpool.constants.CarpoolConfig;
import carpool.cleanRoutineTask.*;

public class RedisCleanTaskTest {

	@Test
	public void testCleanEmailActivationRecords(){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		long time = DateUtility.getCurTime();
		long storeTime = time - CarpoolConfig.emailActivation_expireThreshold - 1;
		String redisSeperatorRegex = "+";
		String RandomString = "MatthewEa";
		String authCode = RandomString + redisSeperatorRegex + storeTime;
		String userKey1 = CarpoolConfig.key_emailActivationAuth + 1;
		jedis.lpush(userKey1, authCode);
		
		String userKey2 = CarpoolConfig.key_emailActivationAuth + 2;
		jedis.lpush(userKey2, authCode);
		
		String userKey3 = CarpoolConfig.key_emailActivationAuth + 3;
		jedis.lpush(userKey3,authCode);
		
		RedisCleaner.cleanEmailActivationRecords();
		Set<String> keyset = jedis.keys(CarpoolConfig.key_emailActivationAuth + "*");
		if(keyset.size()==0){
			//Passed;
		}else{
			fail();
		}
		
		String userKey5 = CarpoolConfig.key_emailActivationAuth + 3;
		jedis.lpush(userKey5, RandomString + redisSeperatorRegex + (time - 100));	
		
		RedisCleaner.cleanEmailActivationRecords();
		keyset = jedis.keys(CarpoolConfig.key_emailActivationAuth + "*");
		if(keyset.size()==1 && keyset.iterator().next().equals(userKey5)){
			//Passed;
		}else{
			fail();
		}
		
		CarpoolDaoBasic.returnJedis(jedis);
	}
	
	@Test
	public void testCleanForgotPasswordRecords(){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		long time = DateUtility.getCurTime();
		long storeTime = time - CarpoolConfig.forgetPassword_expireThreshold - 1;
		String redisSeperatorRegex = "+";
		String RandomString = "MatthewEa";
		String authCode = RandomString + redisSeperatorRegex + storeTime;
		String userKey1 = CarpoolConfig.key_forgetPasswordAuth + 1;
		jedis.lpush(userKey1, authCode);
		
		String userKey2 = CarpoolConfig.key_forgetPasswordAuth + 2;
		jedis.lpush(userKey2, authCode);
		
		String userKey3 = CarpoolConfig.key_forgetPasswordAuth + 3;
		jedis.lpush(userKey3,authCode);
		
		
		RedisCleaner.cleanForgotPasswordRecords();
		Set<String> keyset = jedis.keys(CarpoolConfig.key_forgetPasswordAuth + "*");
		if(keyset.size()==0){
			//Passed;
		}else{
			fail();
		}
		
		String userKey5 = CarpoolConfig.key_forgetPasswordAuth + 3;
		jedis.lpush(userKey5, RandomString + redisSeperatorRegex + (time - 100));	
		
		RedisCleaner.cleanForgotPasswordRecords();
		keyset = jedis.keys(CarpoolConfig.key_forgetPasswordAuth + "*");
		if(keyset.size()==1 && keyset.iterator().next().equals(userKey5)){
			//Passed;
		}else{
			fail();
		}
		
		CarpoolDaoBasic.returnJedis(jedis);
	}
	
}
