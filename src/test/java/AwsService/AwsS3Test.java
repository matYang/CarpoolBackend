package AwsService;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import carpool.aws.awsMain;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;

public class AwsS3Test {

	//Note: All the tests have been commented out since some of them may influence the test results of others

	//@Test
	public void testGetFile(){
		CarpoolDaoBasic.clearBothDatabase();
		int userId=1;					
		awsMain.getFileObject(userId);
		 
		
	}

	//@Test
	public void testGetImg(){
		CarpoolDaoBasic.clearBothDatabase();
		int userId=1;			
		awsMain.getImgObject(userId);
		
		
	}
	//@Test
	public void testUploadImg() throws IOException{
		CarpoolDaoBasic.clearBothDatabase();
		int userId = 1;			
		awsMain.uploadProfileImg(userId);
	}

	//@Test
	public void testUploadSearchFile() throws IOException, LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		Location departureLocation2= new Location(province,city1,region1,"Test12","Test111",lat1,lng1,arrival_Id*2);
		Location arrivalLocation2 = new Location(province,city2,region2,"Test22","Test222",lat2,lng2,departure_Id+arrival_Id);
		long dm = departureLocation.getMatch();
		long am = arrivalLocation.getMatch();
		long dm2 = departureLocation2.getMatch();
		long am2 = arrivalLocation2.getMatch();
		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = Calendar.getInstance();		
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = Calendar.getInstance();
		dt2.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at2 = Calendar.getInstance();
		at2.add(Calendar.DAY_OF_YEAR, 2);		

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);			
		gender genderRequirement = gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(2);
		int userId=user.getUserId();

		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation SR2 = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot2,timeSlot2);
		SearchRepresentation SR3 = new SearchRepresentation(true,dm,am,dt,at,type,timeSlot3,timeSlot3);
		SearchRepresentation SR4 = new SearchRepresentation(true,dm,am2,dt2,at2,type,timeSlot2,timeSlot2);
		SearchRepresentation SR5 = new SearchRepresentation(false,dm,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR6 = new SearchRepresentation(false,dm,am2,dt2,at2,type,timeSlot3,timeSlot3);

		Jedis redis = carpool.carpoolDAO.CarpoolDaoBasic.getJedis();
		String rediskey = carpool.constants.CarpoolConfig.redisSearchHistoryPrefix+userId;
		int upper = carpool.constants.CarpoolConfig.redisSearchHistoryUpbound;
		//For this test, we set the upper to be 6

		awsMain.storeSearchHistory(SR, userId);			
		awsMain.storeSearchHistory(SR2, userId);
		awsMain.storeSearchHistory(SR3, userId);
		awsMain.storeSearchHistory(SR4, userId);
		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==5){
			//Pass
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR5, userId);

		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==1){
			//Pass
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==2){
			//Pass
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==3){
			//Pass
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==4){
			//Pass
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==5){
			//Pass
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR5, userId);

		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==1){
			//Pass
		}else{
			fail();
		}   
		// Try to save more than upper bound
		redis.lpush(rediskey, SR.toSerializedString());
		redis.lpush(rediskey, SR2.toSerializedString());
		redis.lpush(rediskey, SR3.toSerializedString());
		redis.lpush(rediskey, SR4.toSerializedString());
		redis.lpush(rediskey, SR5.toSerializedString());
		redis.lpush(rediskey, SR6.toSerializedString());
		awsMain.storeSearchHistory(SR5, userId);
		if(redis.lrange(rediskey, 0,upper).size()==0){
			//Pass
		}else{
			fail();
		}   

	}

	//@Test
	public void testGetSearchHistory() throws IOException, LocationNotFoundException{

		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		Location departureLocation2= new Location(province,city1,region1,"Test12","Test111",lat1,lng1,arrival_Id*2);
		Location arrivalLocation2 = new Location(province,city2,region2,"Test22","Test222",lat2,lng2,departure_Id+arrival_Id);
		long dm = departureLocation.getMatch();
		long am = arrivalLocation.getMatch();
		long dm2 = departureLocation2.getMatch();
		long am2 = arrivalLocation2.getMatch();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		//Date
		Calendar dt = Calendar.getInstance();		
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = Calendar.getInstance();
		dt2.add(Calendar.DAY_OF_YEAR, 1);
		Calendar at2 = Calendar.getInstance();
		at2.add(Calendar.DAY_OF_YEAR, 2);		

		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		messageType type = messageType.fromInt(0);			
		gender genderRequirement = gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);	
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(2);
		int userId=user.getUserId();

		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation SR2 = new SearchRepresentation(false,dm,am,dt,at,type,timeSlot2,timeSlot2);
		SearchRepresentation SR3 = new SearchRepresentation(true,dm,am,dt,at,type,timeSlot3,timeSlot3);
		SearchRepresentation SR4 = new SearchRepresentation(true,dm2,am2,dt2,at2,type,timeSlot2,timeSlot2);
		SearchRepresentation SR5 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR6 = new SearchRepresentation(false,dm2,am2,dt2,at2,type,timeSlot3,timeSlot3);
		// In this case, we use 6 to be the upper bound
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		int pre = awsMain.getUserSearchHistory(userId).size();

		awsMain.storeSearchHistory(SR, userId);
		awsMain.storeSearchHistory(SR2, userId);
		awsMain.storeSearchHistory(SR3, userId);
		awsMain.storeSearchHistory(SR4, userId);
		awsMain.storeSearchHistory(SR5, userId);

		String rediskey = carpool.constants.CarpoolConfig.redisSearchHistoryPrefix+userId;
		int upper = carpool.constants.CarpoolConfig.redisSearchHistoryUpbound;
		int storage = carpool.carpoolDAO.CarpoolDaoBasic.getJedis().lrange(rediskey, 0, upper-1).size();

		list = awsMain.getUserSearchHistory(userId);
		if(list.size()==(pre+storage)){
			//Passed;
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR6, userId);
		list = awsMain.getUserSearchHistory(userId);
		if(list.size()==(pre+storage+1)){
			//Passed;
			//				for(int i=0; i<list.size(); i++){
			//	                 System.out.println(list.get(i).toSerializedString());					
			//					}
		}else{
			//				for(int i=0; i<list.size(); i++){
			//                 System.out.println(list.get(i).toSerializedString());					
			//				}
			fail();
		}

		awsMain.storeSearchHistory(SR6, userId);
		list = awsMain.getUserSearchHistory(userId);
		if(list.size()==(pre+storage+2)){
			//Passed;
			//				for(int i=0; i<list.size(); i++){
			//	                 System.out.println(list.get(i).toSerializedString());					
			//					}
		}else{
			//				for(int i=0; i<list.size(); i++){
			//                 System.out.println(list.get(i).toSerializedString());					
			//				}
			fail();
		}
	}
	
	//@Test
	public void testCreateUserFile(){
		int userId = 1;
		awsMain.createUserFile(userId);
		//Check amw management console
	}
}