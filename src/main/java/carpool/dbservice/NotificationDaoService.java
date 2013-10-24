package carpool.dbservice;

import java.util.*;

import javax.swing.text.DateFormatter;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.relayTask.NotificationRelayTask;
import carpool.common.*;
import carpool.database.DaoNotification;
import carpool.exception.PseudoException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.notification.NotificationOwnerNotMatchException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;




public class NotificationDaoService{
	
	private static ArrayList<Notification> db_notificationPendingQeue;
	public static void addToNotificationQueue(Notification n){
		db_notificationPendingQeue.add(n);
	}
	public static void addToNotificationQueue(ArrayList<Notification> ns){
		db_notificationPendingQeue.add(ns);
	}
	public static void clearNotificationQueue(){
		db_notificationPendingQeue.clear();
	}
	public static void dispatchNotificationQueue(){
		createNewNotification(db_notificationPendingQeue);
		sendNotification(db_notificationPendingQeue);
		clearNotificationQueue();
	}
	
	/**
	 *  submitting to execution 
	 */
	public static void sendNotification(Notification n){
		ArrayList<Notification> ns = new ArrayList<Notification>();
		ns.add(n);
		sendNotification(ns);
	}
	
	public static void sendNotification(ArrayList<Notification> ns){
		NotificationRelayTask nTask = new NotificationRelayTask(ns);
                ExecutorProvider.executeRelay(nTask);
	}
	
	
	
	/**
	 * get all the notifications from database
	 * @return	
	 */
	public static ArrayList<Notification> getAllNotifications() {
		return DaoNotification.getALL();
	}
	
	
	
	/**
	 * get the full notification by its notificartionId
	 */
	public static ArrayList<Notification> getUserNotification(int userId) throws UserNotFoundException{

		return null;
	}

	/**
	 * created a new notification in SQL, constructed using the notification initialization constructor
	 * @Return the notification just stored, with the notificationId
	 */
	public static Notification createNewNotification(Notification newNotification){
		
		return null;
	}
	
	/**
	 * created new notifications in SQL, note all of these notification will be different
	 * @Return the notifications just stored, with the notificationIds
	 */
	public static ArrayList<Notification> createNewNotification(ArrayList<Notification> newNotifications){
		
		return null;
	}


	/**
	 * deletes the notification from database,(change its historyDeleted to true)
	 * must make sure the given userId matches the notification's targetUserId, targetUserId specifies the owner of the notification
	 * if not found, throw NotificationNotFoundException
	 * if targetUserId does not match userId parameter, throw NotificationOwnerNotMatchException

	 */
	public static void deleteNotification(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException{
		

	}
	
	/**
	 * mark the notification as read(change its state)
	 * if not found, throw NotificationNotFoundException
	 * if targetUserId does not match userId parameter, throw NotificationOwnerNotMatchException
	 * if not in unread state, do nothing
	 */
	public static void checkNotification(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException{


	}
	
}
