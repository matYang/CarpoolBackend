package carpool.test.dao;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoLetter;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.LetterState;
import carpool.constants.Constants.gender;
import carpool.exception.letter.LetterNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Letter;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

public class CarpoolLetterTest {

	@Test
	public void testAdd() throws ValidationException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		try{
			letter = CarpoolDaoLetter.addLetterToDatabases(letter);
			if(letter.getFrom_userId()==1 && letter.getTo_userId()==2 && letter.getType()==Constants.LetterType.user && letter.getContent().equals("Test")){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Test
	public void testLetterById() throws ValidationException, UserNotFoundException {
		CarpoolDaoBasic.clearBothDatabase();

		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
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
	public void testUpdate() throws ValidationException, UserNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
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
	public void testDelete() throws ValidationException, UserNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
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
	public void testGetAll() throws ValidationException, UserNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);

		Letter letter = new Letter(user.getUserId(),user2.getUserId(),Constants.LetterType.user,"Test");
		Letter letter2 =  new Letter(user2.getUserId(),user2.getUserId(),Constants.LetterType.system,"Test2");
		letter = CarpoolDaoLetter.addLetterToDatabases(letter);
		letter2 = CarpoolDaoLetter.addLetterToDatabases(letter2);

		ArrayList<Letter> list = new ArrayList<Letter>();
		try{
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==2 && list.get(0).equals(letter)&&list.get(1).equals(letter2)){
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
			if(list.size()==1 && list.get(0).equals(letter)){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testGetUserLetters() throws ValidationException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", new LocationRepresentation ("primary2","custom2",1), gender.female);
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
			if(list.size()==1 && list.get(0).equals(letter)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user2.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==1 && list.get(0).equals(letter2)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==1 && list.get(0).equals(letter5)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user2.getUserId(), user3.getUserId(), Constants.LetterType.user, Constants.LetterDirection.outbound);
			if(list.size()==1 && list.get(0).equals(letter7)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user3.getUserId(),user2.getUserId(), Constants.LetterType.user, Constants.LetterDirection.both);
			if(list.size()==2 && list.get(0).equals(letter6)&&list.get(1).equals(letter7)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.system, Constants.LetterDirection.both);
			if(list.size()==2 && list.get(0).equals(letter4)&&list.get(1).equals(letter0)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.system, Constants.LetterDirection.outbound);
			if(list.size()==1 && list.get(0).equals(letter0)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.system, Constants.LetterDirection.inbound);
			if(list.size()==1 && list.get(0).equals(letter4)){
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
			if(list.size()==1 && list.get(0).equals(letter8)){
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
			if(list.size()==2 && list.get(0).equals(letter)&&list.get(1).equals(letter10)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user3.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==2 && list.get(0).equals(letter3)&&list.get(1).equals(letter11)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}

		try{
			list = CarpoolDaoLetter.getUserLetters(user2.getUserId(), user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==2 && list.get(0).equals(letter2)&&list.get(1).equals(letter12)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}
						
		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.inbound);
			if(list.size()==5 && list.get(0).equals(letter2)&&list.get(1).equals(letter3)&&list.get(2).equals(letter5)&&list.get(3).equals(letter11)&&list.get(4).equals(letter12)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}
		
		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.outbound);
			if(list.size()==3 && list.get(0).equals(letter)&&list.get(1).equals(letter5)&&list.get(2).equals(letter10)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}
		
		try{
			list = CarpoolDaoLetter.getUserLetters(-1, user.getUserId(), Constants.LetterType.user, Constants.LetterDirection.both);
			if(list.size()==7 && list.get(0).equals(letter)&&list.get(1).equals(letter2)&&list.get(2).equals(letter3)&&list.get(3).equals(letter5)&&list.get(4).equals(letter10)&&list.get(5).equals(letter11)&&list.get(6).equals(letter12)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){			
			e.printStackTrace();
		}
	}

	@Test
	public void testGetLetterUsers() throws ValidationException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", new LocationRepresentation ("primary2","custom2",1), gender.female);
		CarpoolDaoUser.addUserToDatabase(user3);
		User user4 =  new User("sdfsdfdsfhgfg", "sdfojods@hotmail.com", new LocationRepresentation ("primary3","custom3",1), gender.both);
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
	public void testCheckLetter() throws ValidationException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);
		User user2 =  new User("xchplace", "xiongchuhanplace@hotmail.com", new LocationRepresentation ("primary1","custom1",1), gender.male);
		CarpoolDaoUser.addUserToDatabase(user2);
		User user3 =  new User("sdfjoisdjfi", "sdfoshdf@hotsldfj.com", new LocationRepresentation ("primary2","custom2",1), gender.female);
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
			if(list.size()==12&&list.get(0).getState().code==LetterState.read.code&&list.get(1).getState().code==LetterState.read.code&&list.get(2).getState().code==LetterState.unread.code
					&&list.get(3).getState().code==LetterState.unread.code&&list.get(4).getState().code==LetterState.unread.code&&list.get(5).getState().code==LetterState.unread.code&&list.get(6).getState().code==LetterState.unread.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(8).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.read.code
					&&list.get(10).getState().code==LetterState.unread.code&&list.get(11).getState().code==LetterState.read.code){
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
			if(list.size()==12&&list.get(0).getState().code==LetterState.read.code&&list.get(1).getState().code==LetterState.read.code&&list.get(2).getState().code==LetterState.unread.code
					&&list.get(3).getState().code==LetterState.unread.code&&list.get(4).getState().code==LetterState.unread.code&&list.get(5).getState().code==LetterState.unread.code&&list.get(6).getState().code==LetterState.unread.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(8).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.read.code
					&&list.get(10).getState().code==LetterState.unread.code&&list.get(11).getState().code==LetterState.read.code){
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
			if(list.size()==12&&list.get(0).getState().code==LetterState.read.code&&list.get(1).getState().code==LetterState.read.code&&list.get(2).getState().code==LetterState.unread.code
					&&list.get(3).getState().code==LetterState.unread.code&&list.get(4).getState().code==LetterState.unread.code&&list.get(5).getState().code==LetterState.read.code&&list.get(6).getState().code==LetterState.read.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(8).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.read.code
					&&list.get(10).getState().code==LetterState.unread.code&&list.get(11).getState().code==LetterState.read.code){
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
			if(list.size()==12&&list.get(0).getState().code==LetterState.read.code&&list.get(1).getState().code==LetterState.read.code&&list.get(2).getState().code==LetterState.read.code
					&&list.get(3).getState().code==LetterState.unread.code&&list.get(4).getState().code==LetterState.unread.code&&list.get(5).getState().code==LetterState.read.code&&list.get(6).getState().code==LetterState.read.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(8).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.read.code
					&&list.get(10).getState().code==LetterState.read.code&&list.get(11).getState().code==LetterState.read.code){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			CarpoolDaoLetter.checkLetter(user.getUserId(),user.getUserId());
			list = CarpoolDaoLetter.getAllLetters();
			if(list.size()==12&&list.get(0).getState().code==LetterState.read.code&&list.get(1).getState().code==LetterState.read.code&&list.get(2).getState().code==LetterState.read.code
					&&list.get(3).getState().code==LetterState.unread.code&&list.get(4).getState().code==LetterState.read.code&&list.get(5).getState().code==LetterState.read.code&&list.get(6).getState().code==LetterState.read.code
					&&list.get(7).getState().code==LetterState.unread.code&&list.get(8).getState().code==LetterState.unread.code&&list.get(9).getState().code==LetterState.read.code
					&&list.get(10).getState().code==LetterState.read.code&&list.get(11).getState().code==LetterState.read.code){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
