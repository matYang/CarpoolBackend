package carpool.aws;

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

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.exception.ValidationException;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;

public class awsTest {

	//@Test
	public void testGetFile(){
		try{
			awsMain.getFileObject();
		} 
		catch(IOException e){
			e.getMessage();
			fail();
		}
	}

	//@Test
	public void testGetImg(){
		try{
			awsMain.getImgObject();
		}
		catch(IOException e){
			e.getMessage();
			fail();
		}
	}
	//@Test
	public void testUploadImg(){
		org.apache.log4j.BasicConfigurator.configure(new NullAppender());
		awsMain.uploadImg(1);
	}

	//@Test
	public void testUploadSearchFile() throws IOException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));

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
		//Location
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_dl1_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_al1_2");	
		LocationRepresentation dl2=new LocationRepresentation("Canada_Ontario_dl2_2");
		LocationRepresentation al2=new LocationRepresentation("Canada_Ontario_al2_2");	

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
		Message message=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation SR2 = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot2,timeSlot2);
		SearchRepresentation SR3 = new SearchRepresentation(true,dl,al,dt,at,type,timeSlot3,timeSlot3);
		SearchRepresentation SR4 = new SearchRepresentation(true,dl2,al2,dt2,at2,type,timeSlot2,timeSlot2);
		SearchRepresentation SR5 = new SearchRepresentation(false,dl2,al2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR6 = new SearchRepresentation(false,dl2,al2,dt2,at2,type,timeSlot3,timeSlot3);

		org.apache.log4j.BasicConfigurator.configure(new NullAppender());
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

	@Test
	public void testGetSearchHistory() throws IOException{

		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1));

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
		//Location
		LocationRepresentation dl=new LocationRepresentation("Canada_Ontario_dl1_2");
		LocationRepresentation al=new LocationRepresentation("Canada_Ontario_al1_2");	
		LocationRepresentation dl2=new LocationRepresentation("Canada_Ontario_dl2_2");
		LocationRepresentation al2=new LocationRepresentation("Canada_Ontario_al2_2");	

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
		Message message=new Message(userId,false, dl,dt,timeSlot,1 , priceList,al,at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot,timeSlot);
		SearchRepresentation SR2 = new SearchRepresentation(false,dl,al,dt,at,type,timeSlot2,timeSlot2);
		SearchRepresentation SR3 = new SearchRepresentation(true,dl,al,dt,at,type,timeSlot3,timeSlot3);
		SearchRepresentation SR4 = new SearchRepresentation(true,dl2,al2,dt2,at2,type,timeSlot2,timeSlot2);
		SearchRepresentation SR5 = new SearchRepresentation(false,dl2,al2,dt2,at2,type,timeSlot3,timeSlot3);
		SearchRepresentation SR6 = new SearchRepresentation(false,dl2,al2,dt2,at2,type,timeSlot3,timeSlot3);
		// In this case, we use 6 to be the upper bound
		org.apache.log4j.BasicConfigurator.configure(new NullAppender());
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		int pre = awsMain.getUserSearchHistory(userId).size();

		awsMain.storeSearchHistory(SR, userId);
		awsMain.storeSearchHistory(SR2, userId);
		awsMain.storeSearchHistory(SR3, userId);
		awsMain.storeSearchHistory(SR4, userId);
		awsMain.storeSearchHistory(SR5, userId);

		list = awsMain.getUserSearchHistory(userId);
		if(list.size()==pre){
			//Passed;
		}else{
			fail();
		}
		awsMain.storeSearchHistory(SR6, userId);
		list = awsMain.getUserSearchHistory(userId);
		if(list.size()==pre+6){
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
}
