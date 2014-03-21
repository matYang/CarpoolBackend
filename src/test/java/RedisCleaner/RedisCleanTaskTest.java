package RedisCleaner;

import static org.junit.Assert.*;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DateUtility;
import carpool.configurations.DatabaseConfig;
import carpool.cleanRoutineTask.*;

public class RedisCleanTaskTest {

	@Test
	public void testCleanEmailActivationRecords(){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		long time = DateUtility.getCurTime();
		long storeTime = time - DatabaseConfig.emailActivation_expireThreshold - 1;
		String redisSeperatorRegex = "+";
		String RandomString = RandomStringUtils.randomAlphanumeric(DatabaseConfig.emailActivation_sequenceLength);
		String authCode = RandomString + redisSeperatorRegex + storeTime;
		String userKey1 = DatabaseConfig.key_emailActivationAuth + 1;
		jedis.set(userKey1, authCode);
		
		String userKey2 = DatabaseConfig.key_emailActivationAuth + 2;
		jedis.set(userKey2, authCode);
		
		String userKey3 = DatabaseConfig.key_emailActivationAuth + 3;
		jedis.set(userKey3,authCode);
		
		RedisCleaner.cleanEmailActivationRecords();
		Set<String> keyset = jedis.keys(DatabaseConfig.key_emailActivationAuth + "*");
		if(keyset.size()==0){
			//Passed;
		}else{
			fail();
		}
		
		String userKey5 = DatabaseConfig.key_emailActivationAuth + 3;
		jedis.set(userKey5, RandomString + redisSeperatorRegex + (time - 100));	
		
		RedisCleaner.cleanEmailActivationRecords();
		keyset = jedis.keys(DatabaseConfig.key_emailActivationAuth + "*");
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
		long storeTime = time - DatabaseConfig.forgetPassword_expireThreshold - 1;
		String redisSeperatorRegex = "+";
		String RandomString = RandomStringUtils.randomAlphanumeric(DatabaseConfig.forgetPassword_sequenceLength);
		String authCode = RandomString + redisSeperatorRegex + storeTime;
		String userKey1 = DatabaseConfig.key_forgetPasswordAuth + 1;
		jedis.set(userKey1, authCode);
		
		String userKey2 = DatabaseConfig.key_forgetPasswordAuth + 2;
		jedis.set(userKey2, authCode);
		
		String userKey3 = DatabaseConfig.key_forgetPasswordAuth + 3;
		jedis.set(userKey3,authCode);
		
		
		RedisCleaner.cleanForgotPasswordRecords();
		Set<String> keyset = jedis.keys(DatabaseConfig.key_forgetPasswordAuth + "*");
		if(keyset.size()==0){
			//Passed;
		}else{
			fail();
		}
		
		String userKey5 = DatabaseConfig.key_forgetPasswordAuth + 3;
		jedis.set(userKey5, RandomString + redisSeperatorRegex + (time - 100));	
		
		RedisCleaner.cleanForgotPasswordRecords();
		keyset = jedis.keys(DatabaseConfig.key_forgetPasswordAuth + "*");
		if(keyset.size()==1 && keyset.iterator().next().equals(userKey5)){
			//Passed;
		}else{
			fail();
		}
		
		CarpoolDaoBasic.returnJedis(jedis);
	}
	
}
