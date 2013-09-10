package badstudent.dbservice;

import java.util.*;

import javax.swing.text.DateFormatter;

import badstudent.model.*;
import badstudent.relayTask.NotificationRelayTask;
import badstudent.asyncRelayExecutor.ExecutorProvider;
import badstudent.common.*;
import badstudent.database.DaoNotification;
import badstudent.database.DaoUser;
import badstudent.exception.notification.NotificationNotFoundException;
import badstudent.exception.notification.NotificationOwnerNotMatchException;



public class NotificationDaoService{
	
	
	/**
	 * get all the notifications from database
	 * used for testing only, do not have to generate summary
	 * @return	
	 */
	public static ArrayList<Notification> getAllNotifications() {
		return DaoNotification.getALL();
	}
	
	
	
	/**
	 * get the full notification by its notificartionId
	 * note: must qualify the notification full constructor, note the requirements on the summary field
	 * @param notificationId 
	 * @param userId
	 * @return the full notification object constructed by the its full constructor, return null if any error occurs
	 * @throws NotificationNotFoundException if the specified notification id does not exist
	 */
	public static Notification getUserNotificationById(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException{
		Notification notification =  DaoNotification.getNotificationById(notificationId);
		if(notification.getTargetUserId() != userId){
			throw new NotificationOwnerNotMatchException();
		}
		return notification;
	}

	/**
	 * created a new notification in SQL, constructed using the notification initialization constructor
	 * remember to set the creation time, use date string format specified by Common.parseDateString
	 * TODO must call createNewNotification to all the User, DMMessage, Transaction operations specified in Constants.notificationEvents, 
	 * it can be assumed that when the initialization constructor is called, all necessary parameters will be present
	 * @param newNotification
	 * @param userId
	 * @return	the full Notification that was just created in database, use the complete constructor for this, return null if any errors occurred
	 */
	public static Notification createNewNotification(Notification newNotification){
		ArrayList<Notification> notes = new ArrayList<Notification>();
		notes.add(newNotification);
		NotificationRelayTask note = new NotificationRelayTask(notes);
		ExecutorProvider.executeRelay(note);
		return DaoNotification.addNotificationToDatabase(newNotification);
	}
	
	
	/**
	 * created a bunch of new notification in single SQL query, all constructed using the notification initialization constructor
	 * remember to set the creation time, use date string format specified by Common.parseDateString
	 * TODO must call createNewNotification to all the User, DMMessage, Transaction operations specified in Constants.notificationEvents on at a DaoService level!
	 * some operations might generate only one notification, in this case, use the above method(This is important)
	 * @param notifications
	 * @return
	 */
	public static ArrayList<Notification> createNewNotificationQueue(ArrayList<Notification> notifications){
		NotificationRelayTask note = new NotificationRelayTask(notifications);
		ExecutorProvider.executeRelay(note);
		return DaoNotification.addNotificationToDatabase(notifications);
	}
	

	/**
	 * deletes the notification from database,(change its historyDeleted to true, same applies to Transaction, DMMessage, User)
	 * must make sure the given userId matches the notification's targetUserId, targetUserId specifies the owner of the notification
	 * if not found, throw NotificationNotFoundException
	 * if targetUserId does not match userId parameter, throw NotificationOwnerNotMatchException
	 * return false if unsuccessful
	 * @param notificationId
	 * @param userId
	 * @return true if notification exists and deleted
	 */
	public static boolean deleteNotification(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException{
		Notification n = getUserNotificationById(notificationId, userId);
		if(n.getTargetUserId()!=userId){
			throw new NotificationOwnerNotMatchException();
		}
		n.setHistoryDeleted(true);
		DaoNotification.updateNotificationToDatabase(n);
		return getUserNotificationById(notificationId, userId).isHistoryDeleted();
	}
	
	/**
	 * mark the notification as read(change its checked to true, no matter if its true or not), this happens when user clicks on the notification and thus opens the link
	 * if not found, throw NotificationNotFoundException
	 * if targetUserId does not match userId parameter, throw NotificationOwnerNotMatchException
	 * @param notificationId
	 * @param userId
	 * @return
	 * @throws NotificationNotFoundException
	 * @throws NotificationOwnerNotMatchException
	 */
	public static boolean checkNotification(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException{
		Notification n = getUserNotificationById(notificationId, userId);
		if(n.getTargetUserId()!=userId){
			throw new NotificationOwnerNotMatchException();
		}
		n.setChecked(true);
		DaoNotification.updateNotificationToDatabase(n);
		return getUserNotificationById(notificationId, userId).isChecked();
	}
	
}
