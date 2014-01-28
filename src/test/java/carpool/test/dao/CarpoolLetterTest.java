package carpool.test.dao;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoLetter;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.LetterState;
import carpool.constants.Constants.gender;
import carpool.dbservice.LetterDaoService;
import carpool.exception.letter.LetterNotFoundException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Letter;
import carpool.model.Location;
import carpool.model.User;

public class CarpoolLetterTest {

	private boolean letterEquals(Letter l1, Letter l2){		
		try {
			if(l1.getFrom_user()==null&&l1.getTo_user()!=null){
				return  l1.getFrom_userId() == l2.getFrom_userId() && l1.getTo_userId() == l2.getTo_userId() &&
						l1.getType().code == l2.getType().code && l2.getFrom_user()==null && l1.getTo_user().equals(l2.getTo_user()) && 
						l1.getContent().equals(l2.getContent()) && l1.getState().code == LetterState.read.code && l1.isHistoryDeleted() == l2.isHistoryDeleted() &&
						l1.getSend_time().getTime().toString().equals(l2.getSend_time().getTime().toString()) &&
						l1.getCheck_time().getTime().toString().equals(l2.getCheck_time().getTime().toString());
			}else if(l1.getFrom_user()!=null&&l1.getTo_user()!=null){
				return  l1.getFrom_userId() == l2.getFrom_userId() && l1.getTo_userId() == l2.getTo_userId() &&
						l1.getType().code == l2.getType().code && l1.getFrom_user().equals(l2.getFrom_user()) && l1.getTo_user().equals(l2.getTo_user()) && 
						l1.getContent().equals(l2.getContent()) && l1.getState().code == LetterState.read.code && l1.isHistoryDeleted() == l2.isHistoryDeleted() &&
						l1.getSend_time().getTime().toString().equals(l2.getSend_time().getTime().toString()) &&
						l1.getCheck_time().getTime().toString().equals(l2.getCheck_time().getTime().toString());
			}else if(l1.getFrom_user()!=null&&l1.getTo_user()==null){
				return  l1.getFrom_userId() == l2.getFrom_userId() && l1.getTo_userId() == l2.getTo_userId() &&
						l1.getType().code == l2.getType().code && l1.getFrom_user().equals(l2.getFrom_user()) && l2.getTo_user()==null && 
						l1.getContent().equals(l2.getContent()) && l1.getState().code == LetterState.read.code && l1.isHistoryDeleted() == l2.isHistoryDeleted() &&
						l1.getSend_time().getTime().toString().equals(l2.getSend_time().getTime().toString()) &&
						l1.getCheck_time().getTime().toString().equals(l2.getCheck_time().getTime().toString());
			}else{
				return false;
			}

		} catch (ValidationException e) {
			return false;
		}
	}

	@Test
	public void testAdd() throws ValidationException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		try{
			letter = CarpoolDaoLetter.addLetterToDatabases(letter);
			if(letter.getFrom_userId()==1 && letter.getOwnder_id()==2 && letter.getTo_userId()==2 && letter.getType()==Constants.LetterType.user && letter.getContent().equals("Test")){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testLetterById() throws ValidationException, UserNotFoundException, LocationNotFoundException {
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca",departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user2.getUserId(),Constants.LetterType.system,"Test2");
		letter = CarpoolDaoLetter.addLetterToDatabases(letter);
		letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);

		int letterId = letter.getLetterId();
		int letterId2 = letter2.getLetterId();
		try{
			Letter test = CarpoolDaoLetter.getLetterById(letterId);
			if(letter.equals(test)){
				//Passed;
			}else{
				fail();
			}
			Letter test2 = CarpoolDaoLetter.getLetterById(letterId2);
			if(letter2.equals(test2)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testUpdate() throws ValidationException, UserNotFoundException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com",arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user2.getUserId(),Constants.LetterType.system,"Test2");
		letter = CarpoolDaoLetter.addLetterToDatabases(letter);
		letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);

		letter.setTo_userId(user.getUserId());
		letter.setType(Constants.LetterType.system);
		letter.setContent("Test2");
		try{
			CarpoolDaoLetter.updateLetterInDatabases(letter);
		}catch(Exception e){
			e.printStackTrace();
		}
		letter2.setFrom_userId(user.getUserId());
		letter2.setType(Constants.LetterType.user);
		letter2.setContent("Test");
		try{
			CarpoolDaoLetter.updateLetterInDatabases(letter2);
		}catch(Exception e){
			e.printStackTrace();
		}

		if(letter.getFrom_userId()==letter.getTo_userId()&&letter2.getFrom_userId()==letter.getFrom_userId()){
			//Passed;
		}else{
			fail();
		}
		if(letter.getType().equals(Constants.LetterType.system)&&letter2.getType().equals(Constants.LetterType.user)&&letter.getContent().equals("Test2")&&letter2.getContent().equals("Test")){
			//Passed;
		}else{
			fail();
		}
	}

	@Test
	public void testDelete() throws ValidationException, UserNotFoundException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user2.getUserId(),Constants.LetterType.system,"Test2");
		letter = CarpoolDaoLetter.addLetterToDatabases(letter);
		letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);

		int letterId = letter.getLetterId();
		int letterId2 = letter2.getLetterId();

		CarpoolDaoLetter.deleteLetter(letterId);
		try{
			letter = CarpoolDaoLetter.getLetterById(letterId);			
			fail();

		}catch(LetterNotFoundException not){
			//Passed;
		}

		CarpoolDaoLetter.deleteLetter(letterId2);
		try{
			letter2 = CarpoolDaoLetter.getLetterById(letterId2);		
			fail();

		}catch(LetterNotFoundException not){
			//Passed;
		}
	}

	@Test
	public void testGetAll() throws ValidationException, UserNotFoundException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user2.getUserId(),Constants.LetterType.system,"Test2");
		letter = CarpoolDaoLetter.addLetterToDatabases(letter);
		letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);

		ArrayList<Letter> list = new ArrayList<Letter>();
		try{
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==3 && list.get(1).equals(letter)&&list.get(2).equals(letter2)){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		CarpoolDaoLetter.deleteLetter(letter2.getLetterId());

		try{
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==2 && list.get(1).equals(letter)){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testGetUserLetters() throws ValidationException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", departureLocation, gender.female);
		CarpoolDaoUser.addUserToDatabase(user3);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test2");
		Letter letter3 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test3");
		Letter letter4 =  new Letter(-1,user.getUserId(),Constants.LetterType.system,"Test4");
		Letter letter5 =  new Letter(user.getUserId(),user.getUserId(),Constants.LetterType.user,"Test5");
		Letter letter6 =  new Letter(user2.getUserId(),user3.getUserId(),Constants.LetterType.user,"Test6");
		Letter letter7 =  new Letter(user3.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test7");
		Letter letter8 =  new Letter(-1,user3.getUserId(),Constants.LetterType.system,"Test8");
		Letter letter9 =  new Letter(-1,user2.getUserId(),Constants.LetterType.system,"Test9");
		Letter letter0 =  new Letter(user.getUserId(),-1,Constants.LetterType.system,"Test0");
		try{
			letter = CarpoolDaoLetter.addLetterToDatabases(letter);
			letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);
			letter3 = CarpoolDaoLetter.addLetterToDatabases(letter3);
			letter4 = CarpoolDaoLetter.addLetterToDatabases(letter4);
			letter5 = CarpoolDaoLetter.addLetterToDatabases(letter5);
			letter6 = CarpoolDaoLetter.addLetterToDatabases(letter6);
			letter7 = CarpoolDaoLetter.addLetterToDatabases(letter7);
			letter8 = CarpoolDaoLetter.addLetterToDatabases(letter8);
			letter9 = CarpoolDaoLetter.addLetterToDatabases(letter9);
			letter0 = CarpoolDaoLetter.addLetterToDatabases(letter0);
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		ArrayList<Letter> list = new ArrayList<Letter>();

		try{
			list = CarpoolDaoLetter.getUserLetters(user.getUserId(), user2.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==1 && letterEquals(list.get(0),letter)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user2.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==1 && letterEquals(list.get(0),letter2)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==1 && letterEquals(list.get(0),letter5)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user2.getUserId(), user3.getUserId(), Constants.LetterType.user, Constants.LetterDirection.outbound);
			if(list.size()==1 && letterEquals(list.get(0),letter7)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user3.getUserId(),user2.getUserId(), Constants.LetterType.user, Constants.LetterDirection.both);
			if(list.size()==2 && letterEquals(list.get(0),letter6) && letterEquals(list.get(1),letter7)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.system, Constants.LetterDirection.both);
			if(list.size()==2 && letterEquals(list.get(0),letter4)&&letterEquals(list.get(1),letter0)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.system, Constants.LetterDirection.outbound);
			if(list.size()==1 && letterEquals(list.get(0),letter0)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.system, Constants.LetterDirection.inbound);
			if(list.size()==1 && letterEquals(list.get(0),letter4)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user2.getUserId(), Constants.LetterType.system, Constants.LetterDirection.outbound);
			if(list.size()==0){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user3.getUserId(), Constants.LetterType.system, Constants.LetterDirection.both);
			if(list.size()==1 && letterEquals(list.get(0),letter8)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		Letter letter10 = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test10");
		Letter letter11 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test11");
		Letter letter12 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test12");

		try{
			letter10 = CarpoolDaoLetter.addLetterToDatabases(letter10);
			letter11 = CarpoolDaoLetter.addLetterToDatabases(letter11);
			letter12 = CarpoolDaoLetter.addLetterToDatabases(letter12);
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user.getUserId(), user2.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==2 && letterEquals(list.get(0),letter)&&letterEquals(list.get(1),letter10)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user3.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==2 && letterEquals(list.get(0),letter3)&&letterEquals(list.get(1),letter11)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user2.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==2 && letterEquals(list.get(0),letter2)&&letterEquals(list.get(1),letter12)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==5 && letterEquals(list.get(0),letter2)&&letterEquals(list.get(1),letter3)&&letterEquals(list.get(2),letter5)&&letterEquals(list.get(3),letter11)&&letterEquals(list.get(4),letter12)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.outbound);
			if(list.size()==3 &&letterEquals(list.get(0),letter)&&letterEquals(list.get(1),letter5)&&letterEquals(list.get(2),letter10)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.both);
			if(list.size()==7 && letterEquals(list.get(0),letter)&&letterEquals(list.get(1),letter2)&&letterEquals(list.get(2),letter3)&&letterEquals(list.get(3),letter5)&&letterEquals(list.get(4),letter10)&&letterEquals(list.get(5),letter11)&&letterEquals(list.get(6),letter12)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}
	}

	@Test
	public void testGetLetterUsers() throws ValidationException, LocationNotFoundException{
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
		Location departureLocation1= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation1 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		Location departureLocation2= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation2 = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation1, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation1, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", departureLocation2, gender.female);
		CarpoolDaoUser.addUserToDatabase(user3);
		User user4 =  new User("sdfsdfdsfhgfg", "sdfojods@hotmail.com", arrivalLocation2, gender.both);
		CarpoolDaoUser.addUserToDatabase(user4);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test2");
		Letter letter3 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test3");
		Letter letter4 =  new Letter(-1,user.getUserId(),Constants.LetterType.system,"Test4");
		Letter letter5 =  new Letter(user.getUserId(),user.getUserId(),Constants.LetterType.user,"Test5");
		Letter letter6 =  new Letter(user2.getUserId(),user3.getUserId(),Constants.LetterType.user,"Test6");
		Letter letter7 =  new Letter(user3.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test7");
		Letter letter8 =  new Letter(-1,user3.getUserId(),Constants.LetterType.system,"Test8");
		Letter letter9 =  new Letter(-1,user2.getUserId(),Constants.LetterType.system,"Test9");
		Letter letter10 = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test10");
		Letter letter11 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test11");
		Letter letter12 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test12");
		Letter letter13 =  new Letter(user4.getUserId(),user4.getUserId(),Constants.LetterType.user,"Test13");		
		try{
			letter = CarpoolDaoLetter.addLetterToDatabases(letter);
			letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);
			letter3 = CarpoolDaoLetter.addLetterToDatabases(letter3);
			letter4 = CarpoolDaoLetter.addLetterToDatabases(letter4);
			letter5 = CarpoolDaoLetter.addLetterToDatabases(letter5);
			letter6 = CarpoolDaoLetter.addLetterToDatabases(letter6);
			letter7 = CarpoolDaoLetter.addLetterToDatabases(letter7);
			letter8 = CarpoolDaoLetter.addLetterToDatabases(letter8);
			letter9 = CarpoolDaoLetter.addLetterToDatabases(letter9);
			letter10 = CarpoolDaoLetter.addLetterToDatabases(letter10);
			letter11 = CarpoolDaoLetter.addLetterToDatabases(letter11);
			letter12 = CarpoolDaoLetter.addLetterToDatabases(letter12);
			letter13 = CarpoolDaoLetter.addLetterToDatabases(letter13);			
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		ArrayList<User> list = new ArrayList<User>();

		try{
			list = CarpoolDaoLetter.getLetterUsers(user.getUserId());
			if(list.size()==3&&list.get(0).equals(user)&&list.get(1).equals(user2)&&list.get(2).equals(user3)){
				//Passed;
			}else{
				fail();
			}
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getLetterUsers(user2.getUserId());
			if(list.size()==2&&list.get(0).equals(user)&&list.get(1).equals(user3)){
				//Passed;
			}else{
				fail();
			}
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getLetterUsers(user3.getUserId());
			if(list.size()==2&&list.get(0).equals(user)&&list.get(1).equals(user2)){
				//Passed;
			}else{
				fail();
			}
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

	}

	@Test
	public void testCheckLetter() throws ValidationException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", departureLocation, gender.female);
		CarpoolDaoUser.addUserToDatabase(user3);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test2");
		Letter letter3 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test3");
		Letter letter4 =  new Letter(-1,user.getUserId(),Constants.LetterType.system,"Test4");
		Letter letter5 =  new Letter(user.getUserId(),user3.getUserId(),Constants.LetterType.user,"Test5");
		Letter letter6 =  new Letter(user2.getUserId(),user3.getUserId(),Constants.LetterType.user,"Test6");
		Letter letter7 =  new Letter(user3.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test7");
		Letter letter8 =  new Letter(-1,user3.getUserId(),Constants.LetterType.system,"Test8");
		Letter letter9 =  new Letter(-1,user2.getUserId(),Constants.LetterType.system,"Test9");
		Letter letter10 = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test10");
		Letter letter11 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test11");
		Letter letter12 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test12");
		try{
			letter = CarpoolDaoLetter.addLetterToDatabases(letter);
			letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);
			letter3 = CarpoolDaoLetter.addLetterToDatabases(letter3);
			letter4 = CarpoolDaoLetter.addLetterToDatabases(letter4);
			letter5 = CarpoolDaoLetter.addLetterToDatabases(letter5);
			letter6 = CarpoolDaoLetter.addLetterToDatabases(letter6);
			letter7 = CarpoolDaoLetter.addLetterToDatabases(letter7);
			letter8 = CarpoolDaoLetter.addLetterToDatabases(letter8);
			letter9 = CarpoolDaoLetter.addLetterToDatabases(letter9);
			letter10 = CarpoolDaoLetter.addLetterToDatabases(letter10);
			letter11 = CarpoolDaoLetter.addLetterToDatabases(letter11);
			letter12 = CarpoolDaoLetter.addLetterToDatabases(letter12);
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		ArrayList<Letter> list = new ArrayList<Letter>();
		try{
			CarpoolDaoLetter.checkLetter(user.getUserId(),user2.getUserId());
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==24&&list.get(1).getState().code==LetterState.read.code&&list.get(3).getState().code==LetterState.read.code&&list.get(5).getState().code==LetterState.unread.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.unread.code&&list.get(11).getState().code==LetterState.unread.code&&list.get(13).getState().code==LetterState.unread.code
					&&list.get(15).getState().code==LetterState.unread.code&&list.get(17).getState().code==LetterState.unread.code&&list.get(19).getState().code==LetterState.read.code
					&&list.get(21).getState().code==LetterState.unread.code&&list.get(23).getState().code==LetterState.read.code){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			CarpoolDaoLetter.checkLetter(user2.getUserId(),user.getUserId());
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==24&&list.get(1).getState().code==LetterState.read.code&&list.get(3).getState().code==LetterState.read.code&&list.get(5).getState().code==LetterState.unread.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.unread.code&&list.get(11).getState().code==LetterState.unread.code&&list.get(13).getState().code==LetterState.unread.code
					&&list.get(15).getState().code==LetterState.unread.code&&list.get(17).getState().code==LetterState.unread.code&&list.get(19).getState().code==LetterState.read.code
					&&list.get(21).getState().code==LetterState.unread.code&&list.get(23).getState().code==LetterState.read.code){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			CarpoolDaoLetter.checkLetter(user2.getUserId(),user3.getUserId());
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==24&&list.get(1).getState().code==LetterState.read.code&&list.get(3).getState().code==LetterState.read.code&&list.get(5).getState().code==LetterState.unread.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.unread.code&&list.get(11).getState().code==LetterState.read.code&&list.get(13).getState().code==LetterState.read.code
					&&list.get(15).getState().code==LetterState.unread.code&&list.get(17).getState().code==LetterState.unread.code&&list.get(19).getState().code==LetterState.read.code
					&&list.get(21).getState().code==LetterState.unread.code&&list.get(23).getState().code==LetterState.read.code){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			CarpoolDaoLetter.checkLetter(user.getUserId(),user3.getUserId());
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==24&&list.get(1).getState().code==LetterState.read.code&&list.get(3).getState().code==LetterState.read.code&&list.get(5).getState().code==LetterState.read.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.read.code&&list.get(11).getState().code==LetterState.read.code&&list.get(13).getState().code==LetterState.read.code
					&&list.get(15).getState().code==LetterState.unread.code&&list.get(17).getState().code==LetterState.unread.code&&list.get(19).getState().code==LetterState.read.code
					&&list.get(21).getState().code==LetterState.read.code&&list.get(23).getState().code==LetterState.read.code){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testSortLetters() throws ValidationException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", departureLocation, gender.female);
		CarpoolDaoUser.addUserToDatabase(user3);

		ArrayList<Letter> list = new ArrayList<Letter>();

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		list.add(letter);
		Letter letter2 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test2");
		list.add(letter2);
		Letter letter3 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test3");
		list.add(letter3);
		Letter letter4 =  new Letter(-1,user.getUserId(),Constants.LetterType.system,"Test4");
		list.add(letter4);
		Letter letter5 =  new Letter(user.getUserId(),user.getUserId(),Constants.LetterType.user,"Test5");
		list.add(letter5);
		Letter letter6 =  new Letter(user2.getUserId(),user3.getUserId(),Constants.LetterType.user,"Test6");
		list.add(letter6);

		try{
			letter = CarpoolDaoLetter.addLetterToDatabases(letter);
			letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);
			letter3 = CarpoolDaoLetter.addLetterToDatabases(letter3);
			letter4 = CarpoolDaoLetter.addLetterToDatabases(letter4);
			letter5 = CarpoolDaoLetter.addLetterToDatabases(letter5);
			letter6 = CarpoolDaoLetter.addLetterToDatabases(letter6);			
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		Calendar dt = Calendar.getInstance();
		Calendar at = (Calendar) dt.clone();
		at.add(Calendar.DAY_OF_YEAR, 1);
		Calendar dt2 = (Calendar) dt.clone();
		dt2.set(Calendar.HOUR_OF_DAY, dt.get(Calendar.HOUR_OF_DAY)-1);
		Calendar at2 = (Calendar) dt.clone();
		at2.add(Calendar.DAY_OF_YEAR, -1);
		Calendar dt3 = (Calendar) dt.clone();
		dt3.add(Calendar.DAY_OF_YEAR, 2);
		Calendar at3 = (Calendar) dt.clone();
		at3.add(Calendar.DAY_OF_YEAR, -2);

		letter.setSend_time(dt3);
		letter2.setSend_time(dt);
		letter3.setSend_time(at2);
		letter4.setSend_time(at3);
		letter5.setSend_time(at);
		letter6.setSend_time(dt2);

		ArrayList<Letter> testResult = new ArrayList<Letter>();
		testResult = LetterDaoService.sortLetters(list);
		if(testResult.size()==list.size() && testResult.get(0).equals(letter4) && testResult.get(1).equals(letter3) && testResult.get(2).equals(letter6)&&testResult.get(3).equals(letter2)&&testResult.get(4).equals(letter5)&&testResult.get(5).equals(letter)){
			//Passed;
		}else{
			fail();
		}

	}

	@Test
	public void testGetUncheckedLettersByUserId() throws ValidationException, LocationNotFoundException, UserNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", arrivalLocation, gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", departureLocation, gender.female);
		CarpoolDaoUser.addUserToDatabase(user3);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test2");
		Letter letter3 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test3");
		Letter letter4 =  new Letter(-1,user.getUserId(),Constants.LetterType.system,"Test4");
		Letter letter5 =  new Letter(user.getUserId(),user.getUserId(),Constants.LetterType.user,"Test5");
		Letter letter6 =  new Letter(user2.getUserId(),user3.getUserId(),Constants.LetterType.user,"Test6");
		Letter letter7 =  new Letter(user3.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test7");
		Letter letter8 =  new Letter(-1,user3.getUserId(),Constants.LetterType.system,"Test8");
		Letter letter9 =  new Letter(-1,user2.getUserId(),Constants.LetterType.system,"Test9");
		Letter letter10 = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test10");
		Letter letter11 =  new Letter(user3.getUserId(),user.getUserId(),Constants.LetterType.user,"Test11");
		Letter letter12 =  new Letter(user2.getUserId(),user.getUserId(),Constants.LetterType.user,"Test12");

		//user
		letter2.setState(LetterState.read);
		letter3.setState(LetterState.unread);
		letter4.setState(LetterState.read);
		letter5.setState(LetterState.read);
		letter11.setState(LetterState.invalid);
		letter12.setState(LetterState.invalid);
		//user2
		letter.setState(LetterState.unread);
		letter7.setState(LetterState.unread);
		letter9.setState(LetterState.unread);
		letter10.setState(LetterState.unread);
		//user3
		letter6.setState(LetterState.read);		
		letter8.setState(LetterState.read);

		try{
			letter = CarpoolDaoLetter.addLetterToDatabases(letter);
			letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);
			letter3 = CarpoolDaoLetter.addLetterToDatabases(letter3);
			letter4 = CarpoolDaoLetter.addLetterToDatabases(letter4);
			letter5 = CarpoolDaoLetter.addLetterToDatabases(letter5);
			letter6 = CarpoolDaoLetter.addLetterToDatabases(letter6);
			letter7 = CarpoolDaoLetter.addLetterToDatabases(letter7);
			letter8 = CarpoolDaoLetter.addLetterToDatabases(letter8);
			letter9 = CarpoolDaoLetter.addLetterToDatabases(letter9);
			letter10 = CarpoolDaoLetter.addLetterToDatabases(letter10);
			letter11 = CarpoolDaoLetter.addLetterToDatabases(letter11);
			letter12 = CarpoolDaoLetter.addLetterToDatabases(letter12);
		}catch(UserNotFoundException e){
			e.printStackTrace();
		}

		ArrayList<Letter> list = new ArrayList<Letter>();
		list = CarpoolDaoLetter.getUncheckedLettersByUserId(user.getUserId());
		if(list.size()==1&&list.get(0).equals(letter3)){
			//Passed;
		}else{
			fail();
		}

		list = CarpoolDaoLetter.getUncheckedLettersByUserId(user2.getUserId());
		if(list.size()==4&&list.get(0).equals(letter)
				&& list.get(1).equals(letter7)&&list.get(2).equals(letter9)
				&& list.get(3).equals(letter10)){
			//Passed;
		}else{
			fail();
		}		

		list = CarpoolDaoLetter.getUncheckedLettersByUserId(user3.getUserId());
		if(list.size()==0){
			//Passed;
		}else{
			fail();
		}

	}

}
