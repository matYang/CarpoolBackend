package carpool.test.dao;

import static org.junit.Assert.fail;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.HelperOperator;
import carpool.common.Parser;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.dbservice.*;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.UserSearchRepresentation;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import static java.lang.System.out;

public class CarpoolUserTest {
	
	@Test
	public void testCreate() throws ValidationException{
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		//Test
		CarpoolDaoUser.addUserToDatabase(user);		
	}
	@Test
	public void testRead(){
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
				e.printStackTrace();
				fail();
		}
		//Test
		try {
			try {
				if(!user.equals(CarpoolDaoUser.getUserById(user.getUserId()))){
					fail();
				}
			} catch (ValidationException e) {				
				e.printStackTrace();
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		try {
			if(!user.equals(CarpoolDaoUser.getUserByEmail(user.getEmail()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();	
			fail();
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}
	}
	@Test
	public void testUpdate(){
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		
		try {
			user.setPassword("xch93318yeah", "kongfu");
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		user.setEmail("xiongchuhanplace@hotmail.com");
		user.setLocation(new LocationRepresentation ("primary_secondary","customnew",2));
		user.setAccountPass("test1");
		user.setGoogleToken("google");
		user.setEmailActivated(true);
		try {
			CarpoolDaoUser.UpdateUserInDatabase(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test
		try {
			if(!user.equals(CarpoolDaoUser.getUserById(user.getUserId()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();		  
		} catch (ValidationException e) {
			
			e.printStackTrace();
			fail();
		}
		try {
			if(!user.equals(CarpoolDaoUser.getUserByEmail(user.getEmail()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();		  
		} catch (ValidationException e) {
			
			e.printStackTrace();
			fail();
		}
	}
   @Test
    public void testDelete(){
	   CarpoolDaoBasic.clearBothDatabase();
	   User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		try {
			CarpoolDaoUser.deleteUserFromDatabase(user.getUserId());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		//Test
		try {
			CarpoolDaoUser.getUserById(user.getUserId());
		} catch (UserNotFoundException e) {
			//passed();
		}
   }
    @Test
    public void testUpdateSocialList() throws ValidationException{
    	CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("password1", "email1", new LocationRepresentation ("primary","custom",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user);	
		
		User user2 =  new User("password2", "email2", new LocationRepresentation ("primary2","custom2",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user2);
		CarpoolDaoUser.addToSocialList(user.getUserId(),user2.getUserId());
		
		
		User user3 =  new User("password3", "email3", new LocationRepresentation ("primary3","custom3",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user3);
		CarpoolDaoUser.addToSocialList(user.getUserId(),user3.getUserId());

		
		User user4 =  new User("password4", "email4", new LocationRepresentation ("primary4","custom4",1), gender.both);
		CarpoolDaoUser.addUserToDatabase(user4);
		CarpoolDaoUser.addToSocialList(user.getUserId(),user4.getUserId());

		
		
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==3 && slist.get(0).equals(user2)&&slist.get(1).equals(user3)&&slist.get(2).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		CarpoolDaoUser.addToSocialList(user.getUserId(),user2.getUserId());
		CarpoolDaoUser.addToSocialList(user.getUserId(),user3.getUserId());
		CarpoolDaoUser.addToSocialList(user.getUserId(),user4.getUserId());

		
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==3 && slist.get(0).equals(user2)&&slist.get(1).equals(user3)&&slist.get(2).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		CarpoolDaoUser.addToSocialList(user2.getUserId(), user3.getUserId());
		CarpoolDaoUser.addToSocialList(user2.getUserId(), user4.getUserId());
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user2.getUserId());
	    	if(slist !=null && slist.size()==2 && slist.get(0).equals(user3)&&slist.get(1).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user2.getUserId());
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user3.getUserId());
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user4.getUserId());
		try{
			ArrayList<User> slist = new ArrayList<User>();
			slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		//Double Check
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user.getUserId());		
		try{
			ArrayList<User> slist = new ArrayList<User>();
			slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		CarpoolDaoUser.deleteFromSocialList(user2.getUserId(), user3.getUserId());
		CarpoolDaoUser.deleteFromSocialList(user2.getUserId(), user4.getUserId());
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user2.getUserId());
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
    }
    @Test
    public void testgetUserMessageHistory(){
    	CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}		
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentmethod =paymentMethod.fromInt(0);		
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		Message message=new Message(1, user.getUserId(), user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message);
		Message message2=new Message(2, user.getUserId(), user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(3, user.getUserId(), user,false
				, new LocationRepresentation("p_c_d_2"),time,timeSlot,3,4 , priceList,new LocationRepresentation("p_c_d_2"),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoUser.getUserMessageHistory(user.getUserId());
			if(mlist !=null && mlist.size()==3 && mlist.get(0).equals(message) && mlist.get(1).equals(message2) && mlist.get(2).equals(message3)){
			//Passed;	
			}else{
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
    }
    
    @Test
    public void testUserSearchRepresentation(){
    	
    	CarpoolDaoBasic.clearBothDatabase();
    	//Users
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		user.setName("Harry Xiong");
		user.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User user2 =  new User("yuanFang91", "yuanyuanyuan", new LocationRepresentation ("primary2","custom",1), gender.both);
        user2.setName("Yuan Fang");
		user2.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
       User user3 =  new User("xchxchxch", "xiongchuhan@hotmail.com", new LocationRepresentation ("primary","custom",1), gender.both);
        user3.setName("Harry Xiong");
		user3.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user3);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User user4 =  new User("Yuan", "FangFangFang", new LocationRepresentation ("primary2","custom",1), gender.both);
        user4.setName("Yuan Fang");
		user4.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user4);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user5 =  new User("Matthew", "YangYangYang", new LocationRepresentation ("primary","custom",1), gender.both);
        user5.setName("Yuan Fang");
		user5.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user5);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user6 =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		user6.setName("Chuhan Xiong");
		user6.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user6);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user7 =  new User("Matthew", "YangYangYang", new LocationRepresentation ("primary2","custom",1), gender.both);
        user7.setName("Cristina Fang");
		user7.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user7);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user8 =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		user8.setName("han");
		user8.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user8);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user9 =  new User("Matthew", "YangYangYang", new LocationRepresentation ("primary2","custom",1), gender.both);
        user9.setName("ang");
		user9.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user9);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user10 =  new User("Matthew", "YangYangYang", new LocationRepresentation ("primary2","custom",1), gender.both);
        user10.setName("g");
		user10.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user10);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		
		//USRs
		UserSearchRepresentation usr = new UserSearchRepresentation("Xiong",user.getGender(),user.getLocation());
		UserSearchRepresentation usr2 = new UserSearchRepresentation("Fang",user2.getGender(),user2.getLocation());
		UserSearchRepresentation usr3 = new UserSearchRepresentation("Matthew",Constants.gender.fromInt(1),user5.getLocation());
		UserSearchRepresentation usr4 = new UserSearchRepresentation("g",Constants.gender.fromInt(1),user7.getLocation());
		//Test
		ArrayList<User> ulist = new ArrayList<User>();
		try{
			ulist = CarpoolDaoUser.searchForUser(usr);
			if(ulist !=null && ulist.size()==3 && ulist.get(0).equals(user)&&ulist.get(1).equals(user3)&& ulist.get(2).equals(user6)){
				//Passed;
			}else{
				
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			ulist = CarpoolDaoUser.searchForUser(usr2);
			if(ulist !=null && ulist.size()==3 && ulist.get(0).equals(user2)&&ulist.get(1).equals(user4)&& ulist.get(2).equals(user7)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			ulist = CarpoolDaoUser.searchForUser(usr3);
			if(ulist==null||ulist.size()==0){
				//Passed;
			}else{				
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			ulist = CarpoolDaoUser.searchForUser(usr4);
			if(ulist !=null && ulist.size()==5 && ulist.get(0).equals(user2)&&ulist.get(1).equals(user4)&& ulist.get(2).equals(user7)&&ulist.get(3).equals(user9)&&ulist.get(4).equals(user10)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
   
   
   
   
}
