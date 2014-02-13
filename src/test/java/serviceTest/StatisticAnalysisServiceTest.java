package serviceTest;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.junit.Test;

import carpool.aws.AwsMain;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.Gender;
import carpool.constants.Constants.MessageType;
import carpool.constants.Constants.PaymentMethod;
import carpool.dbservice.admin.StatisticAnalysisOfDataService;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;

public class StatisticAnalysisServiceTest {
	
	@Test
	public void testTimeStampConverter(){
		Calendar oldr = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
		oldr.add(Calendar.HOUR_OF_DAY, 16);	
		Calendar test = Calendar.getInstance(TimeZone.getTimeZone(CarpoolConfig.timeZoneStandard));
		test.add(Calendar.HOUR_OF_DAY, 16);
		if(DateUtility.convertToStandard(oldr).HOUR_OF_DAY-test.HOUR_OF_DAY==0){
			//Passed;
		}else{
			fail();
		}
	}
	
	@Test
	public void testTimeStampAnalysis() throws ValidationException{
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
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,departure_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,arrival_Id);
		
		long did3 = 3;
		long aid4 = 4;
		Location departureLocation3= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,did3);
		Location arrivalLocation4 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,aid4);
		long did5 = 5;
		long aid6 = 6;
		Location departureLocation5= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,did5);
		Location arrivalLocation6 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,aid6);
		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);
		User user2 = new User("xdsof", "sdfdsf@wer",arrivalLocation,Gender.female);
		User user3 = new User("ffade","xiongchuhan@hotmail.com",departureLocation,Gender.male);
		User user4 = new User("aeccde","c2xiong@uwaterloo.ca",arrivalLocation,Gender.female);
		CarpoolDaoUser.addUserToDatabase(user);		
		CarpoolDaoUser.addUserToDatabase(user2);	
		CarpoolDaoUser.addUserToDatabase(user3);	
		CarpoolDaoUser.addUserToDatabase(user4);
		int userId = user.getUserId();
		int userId2 = user2.getUserId();
		int userId3 = user3.getUserId();
		int userId4 = user4.getUserId();
		
		SearchRepresentation sr = CarpoolConfig.getDefaultSearchRepresentation();
		sr.setDepartureMatch_Id(did3);
		sr.setArrivalMatch_Id(did5);
		SearchRepresentation sr2 = CarpoolConfig.getDefaultSearchRepresentation();
		sr2.setDepartureMatch_Id(did3);
		sr2.setArrivalMatch_Id(did5);
		SearchRepresentation sr3 = CarpoolConfig.getDefaultSearchRepresentation();
		sr3.setDepartureMatch_Id(did3);		
		sr3.setArrivalMatch_Id(did5);
		SearchRepresentation sr4 = CarpoolConfig.getDefaultSearchRepresentation();
				
		AwsMain.storeSearchHistory(sr, userId);
		AwsMain.storeSearchHistory(sr2, userId2);
		AwsMain.storeSearchHistory(sr3, userId3);
		AwsMain.storeSearchHistory(sr4, userId4);
		
		ArrayList<SearchRepresentation> srlist = new ArrayList<SearchRepresentation>();
		Calendar low = DateUtility.getCurTimeInstance();
		low.add(Calendar.HOUR_OF_DAY, -1);
		Calendar high = DateUtility.getCurTimeInstance();
		high.add(Calendar.HOUR_OF_DAY, 1);
		srlist = StatisticAnalysisOfDataService.getUserSRsBasedOnTimeStamps(low, high);
		if(srlist.size()==4 && srlist.get(0).equals(sr4)&&srlist.get(1).equals(sr3)&&srlist.get(2).equals(sr2)&&srlist.get(3).equals(sr)){
			//Passed;
		}else{
			fail();
		}
		low.add(Calendar.HOUR_OF_DAY, -1);
		high.add(Calendar.HOUR_OF_DAY, -3);
		srlist = StatisticAnalysisOfDataService.getUserSRsBasedOnTimeStamps(low, high);
		if(srlist.size()==0){
			//Passed;
		}else{
			fail();
		}
	}
	//Note: For this test you need to clean all the user SR files on Aws
	@Test
	public void testGetEntireMap() throws ValidationException, LocationNotFoundException{		
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
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,departure_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,arrival_Id);
		
		long did3 = 3;
		long aid4 = 4;
		Location departureLocation3= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,did3);
		Location arrivalLocation4 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,aid4);
		long did5 = 5;
		long aid6 = 6;
		Location departureLocation5= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,did5);
		Location arrivalLocation6 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,aid6);
		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);
		User user2 = new User("xdsof", "sdfdsf@wer",arrivalLocation,Gender.female);
		User user3 = new User("ffade","xiongchuhan@hotmail.com",departureLocation,Gender.male);
		User user4 = new User("aeccde","c2xiong@uwaterloo.ca",arrivalLocation,Gender.female);
		CarpoolDaoUser.addUserToDatabase(user);		
		CarpoolDaoUser.addUserToDatabase(user2);	
		CarpoolDaoUser.addUserToDatabase(user3);	
		CarpoolDaoUser.addUserToDatabase(user4);
		int userId = user.getUserId();
		int userId2 = user2.getUserId();
		int userId3 = user3.getUserId();
		int userId4 = user4.getUserId();
		
		SearchRepresentation sr = CarpoolConfig.getDefaultSearchRepresentation();
		sr.setDepartureMatch_Id(did3);
		sr.setArrivalMatch_Id(did5);
		SearchRepresentation sr2 = CarpoolConfig.getDefaultSearchRepresentation();
		sr2.setDepartureMatch_Id(did3);
		sr2.setArrivalMatch_Id(did5);
		SearchRepresentation sr3 = CarpoolConfig.getDefaultSearchRepresentation();
		sr3.setDepartureMatch_Id(did3);		
		sr3.setArrivalMatch_Id(did5);
		SearchRepresentation sr4 = CarpoolConfig.getDefaultSearchRepresentation();
				
		AwsMain.storeSearchHistory(sr, userId);
		AwsMain.storeSearchHistory(sr2, userId2);
		AwsMain.storeSearchHistory(sr3, userId3);
		AwsMain.storeSearchHistory(sr4, userId4);
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);
		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt3 = DateUtility.getCurTimeInstance();	
		dt3.add(Calendar.DAY_OF_YEAR, -2);
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		MessageType type1 = MessageType.fromInt(1);
		MessageType type2 = MessageType.fromInt(2);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		DayTimeSlot timeSlot1 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(2);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(3);
		DayTimeSlot timeSlot4 = DayTimeSlot.fromInt(4);
		
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		
		Message message2=new Message(userId2,true, new Location(departureLocation3),dt,timeSlot,1 , priceList,new Location(departureLocation5),at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		
		Message message3=new Message(userId3,true, new Location(departureLocation5),dt,timeSlot,1 , priceList,new Location(departureLocation3),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		
		Message message4=new Message(userId4,false,new Location(arrivalLocation4),dt2,timeSlot,1 , priceList,new Location(departureLocation5),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		
		Message message5=new Message(userId,false, new Location(arrivalLocation6),dt3,timeSlot,1 , priceList,new Location(departureLocation3),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId2,true, new Location(departureLocation5),dt3,timeSlot,1 , priceList,new Location(departureLocation3),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId3,false, new Location(departureLocation5),dt3,timeSlot,1 , priceList,new Location(arrivalLocation4),dt2,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId4,true, new Location(departureLocation5),dt3,timeSlot,1 , priceList,new Location(departureLocation3),dt2,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message8);
		//Seats adjust
		Message message9=new Message(userId,false, new Location(departureLocation5),dt,timeSlot,10 , priceList,new Location(departureLocation3),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		message9.setDeparture_seatsBooked(11);
		CarpoolDaoMessage.addMessageToDatabase(message9);
		
		HashMap<String,ArrayList<Entry<Long,Integer>>>map = new HashMap<String,ArrayList<Entry<Long,Integer>>>();
		map = StatisticAnalysisOfDataService.GetTheEntireMap();
		//UserSRDeparture
		ArrayList<Entry<Long,Integer>>list1 = new ArrayList<Entry<Long,Integer>>();
		list1 = map.get(CarpoolConfig.UserSRDeparture);
		//UserSRArrival
		ArrayList<Entry<Long,Integer>>list2 = new ArrayList<Entry<Long,Integer>>();
		list2 = map.get(CarpoolConfig.UserSRArrival);
		//DatabasesDeparture
		ArrayList<Entry<Long,Integer>>list3 = new ArrayList<Entry<Long,Integer>>();
		list3 = map.get(CarpoolConfig.DatabasesDeparture);
		//DatabasesArrival
		ArrayList<Entry<Long,Integer>>list4 = new ArrayList<Entry<Long,Integer>>();
		list4 = map.get(CarpoolConfig.DatabasesArrival);
		if(list1.size()==2&&list1.get(0).getValue()==3&&list1.get(1).getValue()==1){
			//Passed;
		}else{
			fail();
		}
		if(list2.size()==2&&list2.get(0).getValue()==3&&list2.get(1).getValue()==1){
			//Passed;
		}else{
			fail();
		}
		if(list3.size()==5&&list3.get(0).getValue()==5){
			//Passed;
		}else{
			fail();
		}
		if(list4.size()==4&&list4.get(0).getValue()==5){
			//Passed;
		}else{
			fail();
		}
	}
	
	//Note: For this test you need to clean all the user SR files on Aws
	@Test
	public void testGetSpecificList() throws LocationNotFoundException, ValidationException{
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
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,departure_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,arrival_Id);
		
		long did3 = 3;
		long aid4 = 4;
		Location departureLocation3= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,did3);
		Location arrivalLocation4 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,aid4);
		long did5 = 5;
		long aid6 = 6;
		Location departureLocation5= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,did5);
		Location arrivalLocation6 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,aid6);
		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);
		User user2 = new User("xdsof", "sdfdsf@wer",arrivalLocation,Gender.female);
		User user3 = new User("ffade","xiongchuhan@hotmail.com",departureLocation,Gender.male);
		User user4 = new User("aeccde","c2xiong@uwaterloo.ca",arrivalLocation,Gender.female);
		CarpoolDaoUser.addUserToDatabase(user);		
		CarpoolDaoUser.addUserToDatabase(user2);	
		CarpoolDaoUser.addUserToDatabase(user3);	
		CarpoolDaoUser.addUserToDatabase(user4);
		int userId = user.getUserId();
		int userId2 = user2.getUserId();
		int userId3 = user3.getUserId();
		int userId4 = user4.getUserId();
		
		SearchRepresentation sr = CarpoolConfig.getDefaultSearchRepresentation();
		sr.setDepartureMatch_Id(did3);
		sr.setArrivalMatch_Id(did5);
		SearchRepresentation sr2 = CarpoolConfig.getDefaultSearchRepresentation();
		sr2.setDepartureMatch_Id(did3);
		sr2.setArrivalMatch_Id(did5);
		SearchRepresentation sr3 = CarpoolConfig.getDefaultSearchRepresentation();
		sr3.setDepartureMatch_Id(did3);		
		sr3.setArrivalMatch_Id(did5);
		SearchRepresentation sr4 = CarpoolConfig.getDefaultSearchRepresentation();
				
		AwsMain.storeSearchHistory(sr, userId);
		AwsMain.storeSearchHistory(sr2, userId2);
		AwsMain.storeSearchHistory(sr3, userId3);
		AwsMain.storeSearchHistory(sr4, userId4);
		Calendar dt = DateUtility.getCurTimeInstance();		
		Calendar at = DateUtility.getCurTimeInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);
		Calendar dt2 = DateUtility.getCurTimeInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt3 = DateUtility.getCurTimeInstance();	
		dt3.add(Calendar.DAY_OF_YEAR, -2);
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);
		MessageType type1 = MessageType.fromInt(1);
		MessageType type2 = MessageType.fromInt(2);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		DayTimeSlot timeSlot1 = DayTimeSlot.fromInt(1);
		DayTimeSlot timeSlot2 = DayTimeSlot.fromInt(2);
		DayTimeSlot timeSlot3 = DayTimeSlot.fromInt(3);
		DayTimeSlot timeSlot4 = DayTimeSlot.fromInt(4);
		
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		
		Message message2=new Message(userId2,true, new Location(departureLocation3),dt,timeSlot,1 , priceList,new Location(departureLocation5),at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		
		Message message3=new Message(userId3,true, new Location(departureLocation5),dt,timeSlot,1 , priceList,new Location(departureLocation3),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		
		Message message4=new Message(userId4,false,new Location(arrivalLocation4),dt2,timeSlot,1 , priceList,new Location(departureLocation5),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		
		Message message5=new Message(userId,false, new Location(arrivalLocation6),dt3,timeSlot,1 , priceList,new Location(departureLocation3),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId2,true, new Location(departureLocation5),dt3,timeSlot,1 , priceList,new Location(departureLocation3),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId3,false, new Location(departureLocation5),dt3,timeSlot,1 , priceList,new Location(arrivalLocation4),dt2,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId4,true, new Location(departureLocation5),dt3,timeSlot,1 , priceList,new Location(departureLocation3),dt2,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message8);
		//Seats adjust
		Message message9=new Message(userId,false, new Location(departureLocation5),dt,timeSlot,10 , priceList,new Location(departureLocation3),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		message9.setDeparture_seatsBooked(11);
		CarpoolDaoMessage.addMessageToDatabase(message9);
		
		//UserSRDeparture
				ArrayList<Entry<Long,Integer>>list1 = new ArrayList<Entry<Long,Integer>>();
				list1 = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.UserSRDeparture);
				//UserSRArrival
				ArrayList<Entry<Long,Integer>>list2 = new ArrayList<Entry<Long,Integer>>();
				list2 = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.UserSRArrival);
				//DatabasesDeparture
				ArrayList<Entry<Long,Integer>>list3 = new ArrayList<Entry<Long,Integer>>();
				list3 = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.DatabasesDeparture);
				//DatabasesArrival
				ArrayList<Entry<Long,Integer>>list4 = new ArrayList<Entry<Long,Integer>>();
				list4 = StatisticAnalysisOfDataService.getSpecificList(CarpoolConfig.DatabasesArrival);
				if(list1.size()==2&&list1.get(0).getValue()==3&&list1.get(1).getValue()==1){
					//Passed;
				}else{
					fail();
				}
				if(list2.size()==2&&list2.get(0).getValue()==3&&list2.get(1).getValue()==1){
					//Passed;
				}else{
					fail();
				}
				if(list3.size()==5&&list3.get(0).getValue()==5){
					//Passed;
				}else{
					fail();
				}
				if(list4.size()==4&&list4.get(0).getValue()==5){
					//Passed;
				}else{
					fail();
				}
	}
	
	
}
