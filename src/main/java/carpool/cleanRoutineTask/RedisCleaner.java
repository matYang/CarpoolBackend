package carpool.cleanRoutineTask;

import java.util.Set;

import redis.clients.jedis.Jedis;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.constants.CarpoolConfig;

public class RedisCleaner {

	/**
	 * the method that is called by cleaner	
	 */
	public static void Clean(){
		cleanEmailActivationRecords();
		cleanForgotPasswordRecords();
		migrateSearchHistory();
			
	}
	
	/**
	 * clean up all the expired email activation key-value pairs, formatted as: ea<userId>
	 */
	private static void cleanEmailActivationRecords(){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		//this is the set of keys that holds the email activation records, check for the timestamp to see if it is expired, if expired just delete the key-value pair
		Set<String> keyset = jedis.keys(CarpoolConfig.key_emailActivationAuth + "*");
		for (String key : keyset){
			//TODO
		}
		
		
		CarpoolDaoBasic.returnJedis(jedis);
	}
	
	/**
	 * clean up all the expired forgot password key-value pairs, formatted as: fp<userId>
	 */
	private static void cleanForgotPasswordRecords(){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		//this is the set of keys that holds the forgot password records, check for the timestamp to see if it is expired, if expired just delete the key-value pair
		Set<String> keyset = jedis.keys(CarpoolConfig.key_forgetPasswordAuth + "*");
		for (String key : keyset){
			//TODO
		}
		
		
		CarpoolDaoBasic.returnJedis(jedis);
	}
	
	/**
	 *  move all search history of a user to his/her S3 bucket
	 */
	private static void migrateSearchHistory(){
		Jedis jedis = CarpoolDaoBasic.getJedis();
		//this is the set of keys that holds the list of sr, to find the id, extract it from each of the key, migrate all their SRs to their S3 buckets
		//this will be running on a different thread, but assume safe to use the aws here
		Set<String> keyset = jedis.keys(CarpoolConfig.key_forgetPasswordAuth + "*");
		for (String key : keyset){
			//TODO
		}
		
		
		CarpoolDaoBasic.returnJedis(jedis);
	}

}
