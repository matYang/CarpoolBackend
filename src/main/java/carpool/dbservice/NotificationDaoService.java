package carpool.dbservice;

import java.util.*;

import javax.swing.text.DateFormatter;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.relayTask.NotificationRelayTask;
import carpool.carpoolDAO.CarpoolDaoNotification;
import carpool.common.*;
import carpool.constants.Constants.NotificationState;
import carpool.database.DaoNotification;
import carpool.exception.PseudoException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.notification.NotificationOwnerNotMatchException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;




public class NotificationDaoService{
	
	private static ArrayList<Notification> db_notificationPendingQeue;
	public static void addToNotificationQueue(Notification n){
		db_notificationPendingQeue.add(n);
	}
	public static void addToNotificationQueue(ArrayList<Notification> ns){
		db_notificationPendingQeue.addAll(ns);
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
	 * @throws TransactionNotFoundException 
	 * @throws UserNotFoundException 
	 * @throws MessageNotFoundException 
	 */
	public static ArrayList<Notification> getAllNotifications() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException {
		return CarpoolDaoNotification.getAllNotifications();
	}
	
	
	
	/**
	 * get the full notification by its notificartionId
	 * @throws TransactionNotFoundException 
	 * @throws MessageNotFoundException 
	 */
	public static ArrayList<Notification> getUserNotification(int userId) throws UserNotFoundException, MessageNotFoundException, TransactionNotFoundException{

		return CarpoolDaoNotification.getByUserId(userId);
	}

	/**
	 * created a new notification in SQL, constructed using the notification initialization constructor
	 * @Return the notification just stored, with the notificationId
	 */
	public static Notification createNewNotification(Notification newNotification){
		
		return CarpoolDaoNotification.addNotificationToDatabase(newNotification);
	}
	
	/**
	 * created new notifications in SQL, note all of these notification will be different
	 * @Return the notifications just stored, with the notificationIds
	 */
	public static ArrayList<Notification> createNewNotification(ArrayList<Notification> newNotifications){
		
		return CarpoolDaoNotification.addNotificationsToDatabase(newNotifications);
	}


	/**
	 * deletes the notification from database,(change its historyDeleted to true)
	 * must make sure the given userId matches the notification's targetUserId, targetUserId specifies the owner of the notification
	 * if not found, throw NotificationNotFoundException
	 * if targetUserId does not match userId parameter, throw NotificationOwnerNotMatchException
	 * @throws TransactionNotFoundException 
	 * @throws UserNotFoundException 
	 * @throws MessageNotFoundException 

	 */
	public static void deleteNotification(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException{
    		ArrayList<Notification> list = new ArrayList<Notification>();
    		list = CarpoolDaoNotification.getByUserId(userId);
    		for(int i=0; i<list.size(); i++){
    			if(list.get(i).getNotificationId()==notificationId){
    				list.get(i).setHistoryDeleted(true);
    				CarpoolDaoNotification.updateNotificationInDatabase(list.get(i));
    			}
    		}
       
	}
	
	/**
	 * mark the notification as read(change its state)
	 * if not found, throw NotificationNotFoundException
	 * if targetUserId does not match userId parameter, throw NotificationOwnerNotMatchException
	 * if not in unread state, do nothing
	 * @throws TransactionNotFoundException 
	 * @throws UserNotFoundException 
	 * @throws MessageNotFoundException 
	 */
	public static void checkNotification(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException{
		ArrayList<Notification> list = new ArrayList<Notification>();
		list = CarpoolDaoNotification.getByUserId(userId);
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getNotificationId()==notificationId){
				list.get(i).setState(NotificationState.read);
				CarpoolDaoNotification.updateNotificationInDatabase(list.get(i));
			}
		}
       
	}
	
}
