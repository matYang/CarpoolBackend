package carpool.dbservice;

import java.util.*;


import carpool.common.*;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.carpoolDAO.*;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;
import carpool.model.representation.SearchRepresentation;
import carpool.model.representation.UserSearchRepresentation;

public class UserDaoService{
	
	
	/*****
	 *  User's CRUD
	 *****/
	
	public static ArrayList<User> getAllUsers(){
		return CarpoolDaoUser.getAllUsers();
	}

	public static User getUserById(int id) throws UserNotFoundException{
		return CarpoolDaoUser.getUserById(id);
	}
	
	
	public static User createNewUser(User newUser) throws ValidationException{
		return CarpoolDaoUser.addUserToDatabase(newUser);
	}



	public static User updateUser(User user) throws PseudoException{
		try {

			CarpoolDaoUser.UpdateUserInDatabase(user);
			return user;
			
		}catch(Exception e){
			DebugLog.d(e.getMessage());
			throw new PseudoException(e.getMessage());
		}
	}


	public static void deleteUser(int id) throws UserNotFoundException{
		CarpoolDaoUser.deleteUserFromDatabase(id);
	}


	/*****
	 *  The follows are user's passwords related
	 *****/
	
	public static boolean changePassword(int userId, String oldPassword, String newPassword) throws UserNotFoundException, ValidationException{
		if(oldPassword.equals(newPassword)){
			throw new ValidationException("Old password and new password are equal");
		}
		User user = CarpoolDaoUser.getUserById(userId);
		user.setPassword(oldPassword, newPassword);
		try {
			CarpoolDaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			throw new ValidationException("Action failed, please try again later");
		}
		
	}
	
	public static boolean resetUserPassword(int userId, String newPassword) throws UserNotFoundException{
		try {
			User user = CarpoolDaoUser.getUserById(userId);
			user.setPassword("dontcare", newPassword);
			CarpoolDaoUser.UpdateUserInDatabase(user);
			user = CarpoolDaoUser.getUserById(userId);
			if(user.isPasswordCorrect(newPassword)){
				return true;
			}
		} catch (Exception e) {
			DebugLog.d(e.getMessage());
		}
		return false;
	}
	
	
	/*****
	 * The follows are user relations used separately on API 
	 *****/
	
	public static boolean watchUser(int userId, int targetUserId) throws UserNotFoundException{

		try {
			CarpoolDaoUser.addToSocialList(userId, targetUserId);
//			//send followed Notification
//			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.followed,
//					id, user.getName(), 0, 0, targetUserId, "You have been followed by XXX", Calendar.getInstance(), false, false);
//			NotificationDaoService.createNewNotification(n);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public static boolean deWatchUser(int userId, int targetUserId) throws UserNotFoundException{
		try {
			CarpoolDaoUser.deleteFromSocialList(userId, targetUserId);
			//send notifications
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	public static ArrayList<User> getWatchedUsers(int id) throws UserNotFoundException{
		return CarpoolDaoUser.getSocialListOfUser(id);
	}
	
	public static boolean isUserWatched(int userId, int targetUserId){
		return CarpoolDaoUser.hasUserInSocialList(userId, targetUserId);
	}

	public static ArrayList<User> searchForUser(UserSearchRepresentation uSR, boolean isLoggedIn, int userId){
		ArrayList<User> searchResult = CarpoolDaoUser.searchForUser(uSR);
		if (isLoggedIn){
			//maybe log the userSearch
		}
		return searchResult;
	}

	public static ArrayList<Message> getHistoryMessageByUserId(int id) throws UserNotFoundException{
		return CarpoolDaoUser.getUserMessageHistory(id);
	}
	
	
	
	
	
	//TODO
	public static ArrayList<Transaction> getTransactionByUserId(int id) throws UserNotFoundException{
		return CarpoolDaoTransaction.getTransactionByUserId();
	}
	

	public static ArrayList<Notification> getNotificationByUserId(int id) throws UserNotFoundException{
		User user = getUserById(id);
		return user.getNotificationList();
	}

	public static void updateUserSearch(SearchRepresentation userSearch, int id) throws PseudoException {
		User user = getUserById(id);
		user.setSearchRepresentation(userSearch);
		updateUser(user);
	}



}
