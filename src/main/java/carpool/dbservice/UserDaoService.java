package carpool.dbservice;

import java.util.*;



import carpool.common.*;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.database.*;
import carpool.exception.PseudoException;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.*;

public class UserDaoService{
	
	
	/*****
	 *  User's CRUD
	 *****/

	public static User getUserById(int id) throws UserNotFoundException{
		return DaoUser.getUserById(id);
	}


	public static ArrayList<User> searchByInfo(String name, String phone, String email, String qq){
		if(name==null || phone==null || email==null || qq==null){
			DebugLog.d("User searchByInfo: Parameters are null");
			return null;
		}
		if(name.equals("")){
			name = "%";
		}
		if(phone.equals("")){
			phone = "%";
		}
		if(email.equals("")){
			email = "%";
		}
		if(qq.equals("")){
			qq = "%";
		}
		return DaoUser.searchUser(name, phone, email, qq);
	}
	

	public static ArrayList<User> getAllUsers() {
		ArrayList<User> users = searchByInfo("", "", "", "");
		return users;
	}

	
	public static User createNewUser(User newUser) throws ValidationException{
		return DaoUser.addUserToDatabase(newUser);
	}



	public static User updateUser(User user) throws PseudoException{
		try {
			DaoUser.UpdateUserInDatabase(user);
			return user;
		}catch(Exception e){
			DebugLog.d(e.getMessage());
			throw new PseudoException(e.getMessage());
		}
	}


	public static void deleteUser(int id) throws UserNotFoundException{
		DaoUser.deleteUserFromDatabase(id);
	}


	/*****
	 *  The follows are user's passwords related
	 *****/
	
	public static boolean changePassword(int userId, String oldPassword, String newPassword) throws UserNotFoundException, ValidationException{
		if(oldPassword.equals(newPassword)){
			throw new ValidationException("Old password and new password are equal");
		}
		User user = DaoUser.getUserById(userId);
		user.setPassword(oldPassword, newPassword);
		try {
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			throw new ValidationException("Action failed, please try again later");
		}
		
	}
	
	public static boolean resetUserPassword(int userId, String newPassword) throws UserNotFoundException{
		try {
			User user = DaoUser.getUserById(userId);
			user.setPassword("dontcare", newPassword);
			DaoUser.UpdateUserInDatabase(user);
			user = DaoUser.getUserById(userId);
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
	
	public static User watchUser(int id, int targetUserId) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		User target = DaoUser.getUserById(targetUserId);
		if(!user.getSocialList().contains(target)){
			user.getSocialList().add(target);
		}
		try {
			DaoUser.UpdateUserInDatabase(user);
			//send followed Notification
			Notification n = new Notification(-1, Constants.notificationType.on_user, Constants.notificationEvent.followed,
					id, user.getName(), 0, 0, targetUserId, "You have been followed by XXX", Calendar.getInstance(), false, false);
			NotificationDaoService.createNewNotification(n);
			return target;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static boolean deWatchUser(int userId, int targetUserId) throws UserNotFoundException{
		User user = DaoUser.getUserById(userId);
		User target = DaoUser.getUserById(targetUserId);
		HelperOperator.removeFromSocialList(user.getSocialList(), target.getUserId());
		try {
			DaoUser.UpdateUserInDatabase(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	public static ArrayList<User> getWatchedUsers(int id) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		if(user==null){
			return null;
		}
		return user.getSocialList();
	}
	


	public static ArrayList<Message> getHistoryMessageByUserId(int id) throws UserNotFoundException{
		User user = DaoUser.getUserById(id);
		if(user==null){
			throw new UserNotFoundException();
		}
		return user.getHistoryList();
	}
	
	
	public static ArrayList<Transaction> getTransactionByUserId(int id) throws UserNotFoundException{
		User user = getUserById(id);
		if(user==null){
			throw new UserNotFoundException();
		}
		return user.getTransactionList();
	}
	

	public static ArrayList<Notification> getNotificationByUserId(int id) throws UserNotFoundException{
		User user = getUserById(id);
		return user.getNotificationList();
	}



}
