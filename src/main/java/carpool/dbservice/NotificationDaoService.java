package carpool.dbservice;

import java.util.*;
import java.util.Map.Entry;

import javax.swing.text.DateFormatter;

import carpool.asyncRelayExecutor.ExecutorProvider;
import carpool.asyncTask.relayTask.EmailRelayTask;
import carpool.asyncTask.relayTask.NotificationRelayTask;
import carpool.asyncTask.relayTask.SESRelayTask;
import carpool.carpoolDAO.CarpoolDaoNotification;
import carpool.common.*;
import carpool.constants.Constants.EmailEvent;
import carpool.constants.Constants.NotificationState;
import carpool.exception.PseudoException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.notification.NotificationOwnerNotMatchException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;



public class NotificationDaoService{
	
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
		createNewNotification(ns);
        ExecutorProvider.executeRelay(nTask);
        
        
        //trying to send out emai notifications
        Map<Integer, ArrayList<Notification>> bufferMap = emailRelayBufferMapMaker(ns);
        for (Entry<Integer, ArrayList<Notification>> entry : bufferMap.entrySet()){
        	try {
				User user = UserDaoService.getUserById(entry.getKey());
				if (user.isEmailNotice()){
					SESRelayTask eTask = new SESRelayTask(user.getEmail(), EmailEvent.notification, JSONFactory.toJSON(entry.getValue()).toString());
					ExecutorProvider.executeRelay(eTask);
				}
			} catch (UserNotFoundException | LocationNotFoundException e) {
				DebugLog.d(e);
			}
        }
	}
	
	private static HashMap<Integer, ArrayList<Notification>> emailRelayBufferMapMaker(ArrayList<Notification> ns){
		HashMap<Integer, ArrayList<Notification>> bufferMap = new HashMap<Integer, ArrayList<Notification>>();
		
		for (Notification n : ns){
			if (bufferMap.get(n.getTargetUserId()) == null){
				ArrayList<Notification> nList = new ArrayList<Notification>();
				nList.add(n);
				bufferMap.put(n.getTargetUserId(), nList);
			}
			else {
				bufferMap.get(n.getTargetUserId()).add(n);
			}
		}
		
		
		return bufferMap;
	}
	
	
	
	/**
	 * get all the notifications from database
	 * @throws LocationNotFoundException 
	 */
	public static ArrayList<Notification> getAllNotifications() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException {
		return CarpoolDaoNotification.getAllNotifications();
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
	 * @throws LocationNotFoundException 

	 */
	public static void deleteNotification(int notificationId) throws NotificationNotFoundException, NotificationOwnerNotMatchException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		Notification notification = CarpoolDaoNotification.getNotificationById(notificationId);
		if(notification==null){
			throw new NotificationNotFoundException("对不起，您想要删除的提醒不存在，可能它已被删除");
		}
		
		CarpoolDaoNotification.deleteNotification(notificationId);
       
	}
	
	/**
	 * mark the notification as read(change its state)
	 * if not found, throw NotificationNotFoundException
	 * if targetUserId does not match userId parameter, throw NotificationOwnerNotMatchException
	 * if not in unread state, do nothing
	 * @throws LocationNotFoundException 
	 */
	public static Notification checkNotification(int notificationId, int userId) throws NotificationNotFoundException, NotificationOwnerNotMatchException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		Notification notification = CarpoolDaoNotification.getNotificationById(notificationId);
		if(notification==null){
			throw new NotificationNotFoundException("对不起，您想要查看的提醒不存在，可能它已被删除");
		}else if(notification.getTargetUserId()==userId){
			notification.setState(NotificationState.read);
			CarpoolDaoNotification.updateNotificationInDatabase(notification);
		}
		return notification;
       
	}
	
}
