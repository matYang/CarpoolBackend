package carpool.test.dao;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoNotification;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.constants.Constants;
import carpool.constants.Constants.NotificationEvent;
import carpool.constants.Constants.NotificationState;
import carpool.constants.Constants.NotificationStateChangeActon;
import carpool.constants.Constants.Gender;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.User;

public class CarpoolNotificationTest {

	@Test
	public void testAdd(){
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);
		//Test
		try{
			Notification test = CarpoolDaoNotification.addNotificationToDatabase(notification);
			if(!notification.equals(test)){
				fail();
			}
			else{
				//Passed;
			}

		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testAddNotifications(){
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}

		int targetUserId = user.getUserId();
		ArrayList<Notification> list = new ArrayList<Notification>();
		Notification notification = new Notification(Constants.NotificationEvent.tranasctionUnderInvestigation,targetUserId);			 
		list.add(notification);
		Notification notification2 = new Notification(Constants.NotificationEvent.transactionAboutToStart,targetUserId);			 
		list.add(notification2);
		Notification notification3 = new Notification(Constants.NotificationEvent.transactionCancelled,targetUserId);			 
		list.add(notification3);
		Notification notification4 = new Notification(Constants.NotificationEvent.transactionEvaluated,targetUserId);			 
		list.add(notification4);
		Notification notification5 = new Notification(Constants.NotificationEvent.transactionInit,targetUserId);			 
		list.add(notification5);
		Notification notification6 = new Notification(Constants.NotificationEvent.transactionReleased,targetUserId);			 
		list.add(notification6);
		//Test
		try{
			CarpoolDaoNotification.addNotificationsToDatabase(list);
			list = CarpoolDaoNotification.getByUserId(targetUserId,false);
			if(list.size()==6 && list.get(0).getNotificationEvent().equals(notification.getNotificationEvent()) && list.get(1).getNotificationEvent().equals(notification2.getNotificationEvent()) && list.get(2).getNotificationEvent().equals(notification3.getNotificationEvent()) && list.get(3).getNotificationEvent().equals(notification4.getNotificationEvent())&& list.get(4).getNotificationEvent().equals(notification5.getNotificationEvent())&& list.get(5).getNotificationEvent().equals(notification6.getNotificationEvent())){
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
	public void testGetByTargetUserId(){
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user2 =  new User("chenmoling", "chenmolingjb", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification);
		NotificationEvent nt2 = Constants.NotificationEvent.transactionInit;
		int targetUserId2 = user2.getUserId();
		Notification notification2 = new Notification(nt2,targetUserId2);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification2);	 
		//Test
		try{
			ArrayList<Notification> list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getByUserId(targetUserId,false);
			if(list.size()==1 &&list.get(0).equals(notification)){
				//Passed;
			}else{			    	
				fail();			    	
			}
			list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getByUserId(targetUserId2,false);
			if(list.size()==1 && list.get(0).equals(notification2)){
				//Passed;
			}else{
				fail();
			}

		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		Notification notification3 = new Notification(NotificationEvent.transactionInit,targetUserId2);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification3);
		try{
			ArrayList<Notification> list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getByUserId(targetUserId,false);
			if(list.size()==1 &&list.get(0).equals(notification)){
				//Passed;
			}else{				    	
				fail();				    	
			}
			list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getByUserId(targetUserId2,false);
			if(list.size()==2 && list.get(0).equals(notification2)&&list.get(1).equals(notification3)){
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
	public void testGetAll(){
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user2 =  new User("chenmoling", "chenmolingjb", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification);
		NotificationEvent nt2 = Constants.NotificationEvent.transactionInit;
		int targetUserId2 = user2.getUserId();
		Notification notification2 = new Notification(nt2,targetUserId2);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification2);	
		Notification notification3 = new Notification(NotificationEvent.transactionInit,targetUserId);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification3);
		Notification notification4 = new Notification(NotificationEvent.transactionInit,targetUserId2);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification4);
		Notification notification5 = new Notification(NotificationEvent.transactionInit,targetUserId);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification5);
		Notification notification6 = new Notification(NotificationEvent.transactionInit,targetUserId);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification6);
		//Test
		try{
			ArrayList<Notification> list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getAllNotifications();
			if(list.size()==6 && list.get(0).getNotificationId()==notification.getNotificationId()&& list.get(1).getNotificationId()==notification2.getNotificationId()&& list.get(2).getNotificationId()==notification3.getNotificationId()&& list.get(3).getNotificationId()==notification4.getNotificationId()&& list.get(4).getNotificationId()==notification5.getNotificationId()&& list.get(5).getNotificationId()==notification6.getNotificationId()){
				if(list.get(0).getTargetUserId()==user.getUserId()&&list.get(1).getTargetUserId()==user2.getUserId()&&list.get(2).getTargetUserId()==user.getUserId()&&list.get(3).getTargetUserId()==user2.getUserId()&&list.get(4).getTargetUserId()==user.getUserId()&&list.get(5).getTargetUserId()==user.getUserId()){
					//Passed;
				}
				else{
					fail();
				}
			}else{	    		
				fail();	    		 
			}


		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}



	@Test
	public void testUpdate() throws NotificationNotFoundException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user2 =  new User("chenmoling", "chenmolingjb", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification);
		NotificationEvent nt2 = Constants.NotificationEvent.transactionInit;
		int targetUserId2 = user2.getUserId();
		Notification notification2 = new Notification(nt2,targetUserId2);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification2);

		notification.setInitUserId(targetUserId2);
		notification.setState(NotificationState.read);
		notification.setNotificationEvent(NotificationEvent.tranasctionUnderInvestigation);
		notification2.setInitUserId(targetUserId);
		notification2.setState(NotificationState.read);
		notification2.setNotificationEvent(NotificationEvent.transactionEvaluated);
		//Test
		try{
			CarpoolDaoNotification.updateNotificationInDatabase(notification);
			CarpoolDaoNotification.updateNotificationInDatabase(notification2);
			ArrayList<Notification> list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getByUserId(targetUserId,false);	     
			if(list.size()==1 && list.get(0).getInitUser().equals(user2)&&list.get(0).getState().code==NotificationState.read.code&&list.get(0).getNotificationEvent().code==NotificationEvent.tranasctionUnderInvestigation.code)
			{
				//Passed;
			}else{	    	
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		ArrayList<Notification> list = new ArrayList<Notification>();
		list = CarpoolDaoNotification.getByUserId(targetUserId2,false);
		try{
			if(list.size()==1 && list.get(0).getInitUser().equals(user)&&list.get(0).getState().equals(NotificationState.read)&&list.get(0).getNotificationEvent().equals(NotificationEvent.transactionEvaluated))
			{
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
	public void testDelete(){
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user2 =  new User("chenmoling", "chenmolingjb",arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification);
		NotificationEvent nt2 = Constants.NotificationEvent.transactionInit;
		int targetUserId2 = user2.getUserId();
		Notification notification2 = new Notification(nt2,targetUserId2);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification2);
		try{
			CarpoolDaoNotification.deleteNotification(notification.getNotificationId());
			ArrayList<Notification> list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getByUserId(targetUserId,false);
			if(list.size()==0){
				//Passed;
			}else{
				fail();
			}
			list = new ArrayList<Notification>();
			list = CarpoolDaoNotification.getByUserId(targetUserId2,false);
			if(list.size()==1&&list.get(0).getTargetUserId()==targetUserId2){
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
	public void testGetUserNotificationUnChecked() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException, NotificationNotFoundException{	
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user2 =  new User("chenmoling", "chenmolingjb",arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);
		notification.setState(NotificationState.unread);
		CarpoolDaoNotification.addNotificationToDatabase(notification);
		NotificationEvent nt2 = Constants.NotificationEvent.transactionInit;
		int targetUserId2 = user2.getUserId();
		Notification notification2 = new Notification(nt2,targetUserId2);			 
		CarpoolDaoNotification.addNotificationToDatabase(notification2);

		ArrayList<Notification> nlist = new ArrayList<Notification>();
		nlist = CarpoolDaoNotification.getByUserId(targetUserId, true);
		if(nlist.size()==1&&nlist.get(0).equals(notification)){
			//Passed;
		}else{
			fail();
		}

		notification.setState(NotificationState.read);
		CarpoolDaoNotification.updateNotificationInDatabase(notification);
		nlist = CarpoolDaoNotification.getByUserId(targetUserId, true);
		if(nlist.size()==0){
			//Passed;
		}else{
			fail();
		}

		notification2.setState(NotificationState.unread);
		CarpoolDaoNotification.updateNotificationInDatabase(notification2);
		nlist = CarpoolDaoNotification.getByUserId(targetUserId2, true);
		if(nlist.size()==1&&nlist.get(0).equals(notification2)){
			//Passed;
		}else{
			fail();
		}

	} 

	@Test
	public void testSortNotifications() throws InterruptedException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user2 =  new User("chenmoling", "chenmolingjb",arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);		

		Thread.sleep(1000);

		NotificationEvent nt2 = Constants.NotificationEvent.transactionInit;
		int targetUserId2 = user2.getUserId();
		Notification notification2 = new Notification(nt2,targetUserId2);	

		CarpoolDaoNotification.addNotificationToDatabase(notification2);
		CarpoolDaoNotification.addNotificationToDatabase(notification);

		ArrayList<Notification> nlist = new ArrayList<Notification>();
		nlist = CarpoolDaoNotification.getAllNotifications();
		nlist = NotificationDaoService.sortNotifications(nlist);
		if(nlist.size()==2 && nlist.get(0).equals(notification)&&nlist.get(1).equals(notification2)){
			//Passed;
		}else{
			fail();
		}
	}

	@Test
	public void testModifyNotificationsByTargetUserId() throws NotificationNotFoundException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
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
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user2 =  new User("chenmoling", "chenmolingjb",arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
		NotificationEvent nt = Constants.NotificationEvent.transactionInit;
		int targetUserId = user.getUserId();
		Notification notification = new Notification(nt,targetUserId);
		notification.setState(NotificationState.unread);
		notification.setInitUser(user2);//user2 send to user
		notification = CarpoolDaoNotification.addNotificationToDatabase(notification);
		NotificationEvent nt2 = Constants.NotificationEvent.transactionInit;
		int targetUserId2 = user2.getUserId();
		Notification notification2 = new Notification(nt2,targetUserId2);	
		notification2.setState(NotificationState.unread);
		notification2 = CarpoolDaoNotification.addNotificationToDatabase(notification2);
		notification2.setInitUser(user);//user send to user2

		ArrayList<Integer> idList = new ArrayList<Integer>();
		idList.add(notification.getNotificationId());
		idList.add(notification2.getNotificationId());

		//Test check
		CarpoolDaoNotification.modifyNotificationByIdList(idList, targetUserId2, NotificationStateChangeActon.check);
		notification = CarpoolDaoNotification.getNotificationById(notification.getNotificationId());
		if(notification.getState().equals(NotificationState.read)){
			fail();			
		}else{
			//Passed;
		}

		CarpoolDaoNotification.modifyNotificationByIdList(idList, targetUserId, NotificationStateChangeActon.check);
		notification = CarpoolDaoNotification.getNotificationById(notification.getNotificationId());
		if(notification.getState().equals(NotificationState.read)){
			//Passed;			
		}else{
			fail();
		}

		//Test delete
		ArrayList<Notification> nlist = new ArrayList<Notification>();
		CarpoolDaoNotification.modifyNotificationByIdList(idList, targetUserId, NotificationStateChangeActon.delete);
		nlist = CarpoolDaoNotification.getAllNotifications();		
		if(nlist.size()==1&&nlist.get(0).getNotificationId()==notification2.getNotificationId()){
			//Passed;			
		}else{
			fail();
		}

		CarpoolDaoNotification.modifyNotificationByIdList(idList, targetUserId2, NotificationStateChangeActon.delete);
		nlist = CarpoolDaoNotification.getAllNotifications();
		if(nlist.size()==0){
			//Passed;			
		}else{
			fail();
		}
	}

}
