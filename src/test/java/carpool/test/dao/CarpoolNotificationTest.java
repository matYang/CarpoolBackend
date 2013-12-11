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
import carpool.constants.Constants.gender;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Notification;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

public class CarpoolNotificationTest {
	
 @Test
 public void testAdd(){
	 CarpoolDaoBasic.clearBothDatabase();
     User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
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
	    User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
			
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
		    	list = CarpoolDaoNotification.getByUserId(targetUserId);
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
	    User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
			
			try {
				CarpoolDaoUser.addUserToDatabase(user);
			} catch (ValidationException e) {			
				e.printStackTrace();
			}
User user2 =  new User("chenmoling", "chenmolingjb", new LocationRepresentation ("primary1","custom1",1), gender.both);
			
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
			    list = CarpoolDaoNotification.getByUserId(targetUserId);
			    if(list.size()==1 &&list.get(0).equals(notification)){
			    	//Passed;
			    }else{			    	
			    	fail();			    	
			    }
			    list = new ArrayList<Notification>();
			    list = CarpoolDaoNotification.getByUserId(targetUserId2);
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
				    list = CarpoolDaoNotification.getByUserId(targetUserId);
				    if(list.size()==1 &&list.get(0).equals(notification)){
				    	//Passed;
				    }else{				    	
				    	fail();				    	
				    }
				    list = new ArrayList<Notification>();
				    list = CarpoolDaoNotification.getByUserId(targetUserId2);
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
    User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
User user2 =  new User("chenmoling", "chenmolingjb", new LocationRepresentation ("primary1","custom1",1), gender.both);
		
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
public void testUpdate() throws NotificationNotFoundException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException{
	CarpoolDaoBasic.clearBothDatabase();
    User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
    User user2 =  new User("chenmoling", "chenmolingjb", new LocationRepresentation ("primary1","custom1",1), gender.both);
		
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
	     list = CarpoolDaoNotification.getByUserId(targetUserId);	     
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
	     list = CarpoolDaoNotification.getByUserId(targetUserId2);
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
	    User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new LocationRepresentation ("primary","custom",1), gender.both);
			
			try {
				CarpoolDaoUser.addUserToDatabase(user);
			} catch (ValidationException e) {			
				e.printStackTrace();
			}
	    User user2 =  new User("chenmoling", "chenmolingjb", new LocationRepresentation ("primary1","custom1",1), gender.both);
			
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
		    	 list = CarpoolDaoNotification.getByUserId(targetUserId);
		    	 if(list.size()==0){
		    		 //Passed;
		    	 }else{
		    		 fail();
		    	 }
		    	 list = new ArrayList<Notification>();
		    	 list = CarpoolDaoNotification.getByUserId(targetUserId2);
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
 
 

 
 
 

}
