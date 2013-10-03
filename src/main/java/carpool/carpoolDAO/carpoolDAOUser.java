package carpool.carpoolDAO;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Parser;
import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.userSearchState;
import carpool.constants.Constants.userState;
import carpool.database.DaoBasic;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;


public class carpoolDAOUser {

	public static ArrayList<User> searchUser(String name, String phone, String email, String qq){
		ArrayList<User> retVal = new ArrayList<User>();
		String query = "SELECT * FROM carpoolDAOUser WHERE name LIKE ? OR phone LIKE ? OR email LIKE ? OR qq LIKE ?;";
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, name);
			stmt.setString(2, phone);
			stmt.setString(3, email);
			stmt.setString(4, qq);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				User user = createUserByResultSet(rs);
//				user = addHistoryListToUser(user);
//				user = addWatchListToUser(user);
//				user = addSocialListToUser(user);
//				user = addTransactionListToUser(user);
//				user = addNotificationListToUser(user);
				retVal.add(user);
			}
		} catch (SQLException e) {
			DebugLog.d(e.getMessage());
		}
		return retVal;
	}

	public static User addUserToDatabase(User user) throws ValidationException{
		String query = "INSERT INTO carpoolDAOUser (password,name,email,phone,qq,age,gender,birthday,"+
	            "imgPath,user_primaryLocation,user_customLocation,user_customDepthIndex,lastLogin,creationTime,"+
				"emailActivated,phoneActivated,emailNotice,phoneNotice,state,searchState,"+
	            "level,averageScore,totalTranscations,verifications,googleToken,facebookToken,twitterToken,"+
				"paypalToken,id_docType,id_docNum,id_path,id_vehicleImgPath,accountId,accountPass,accountToken,accountValue)"+
	            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setString(1, user.getPassword());
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPhone());
			stmt.setString(5, user.getQq());
			stmt.setInt(6, user.getAge());
			stmt.setInt(7, user.getGender().code);
			stmt.setString(8,  DateUtility.toSQLDateTime(user.getBirthday()));
			stmt.setString(9, user.getImgPath());
			stmt.setString(10, user.getLocation().getPrimaryLocationString());
			stmt.setString(11, user.getLocation().getCustomLocationString());
			stmt.setInt(12, user.getLocation().getCustomDepthIndex());
			stmt.setString(13,  DateUtility.toSQLDateTime(user.getLastLogin()));
			stmt.setString(14, DateUtility.toSQLDateTime(user.getCreationTime()));
			stmt.setInt(15, user.isEmailActivated() ? 1:0);
			stmt.setInt(16, user.isPhoneActivated() ? 1:0);
			stmt.setInt(17, user.isEmailNotice() ? 1:0);
			stmt.setInt(18, user.isPhoneNotice() ? 1:0);
			stmt.setInt(19, user.getState().code);
			stmt.setInt(20, user.getSearchState().code);
			stmt.setInt(21, user.getLevel());
			stmt.setInt(22, user.getAverageScore());
			stmt.setInt(23, user.getTotalTranscations());
			stmt.setString(24, Parser.listToString(user.getVerifications()));
			stmt.setString(25,user.getGoogleToken());
			stmt.setString(26, user.getFacebookToken());
			stmt.setString(27, user.getTwitterToken());
			stmt.setString(28,user.getPaypalToken());
			stmt.setString(29, user.getId_docType());
			stmt.setString(30,user.getId_docNum());
			stmt.setString(31, user.getId_path());
			stmt.setString(32, user.getId_vehicleImgPath());
			stmt.setString(33, user.getAccountId());
			stmt.setString(34, user.getAccountPass());
			stmt.setString(35, user.getAccountToken());		
			stmt.setString(36, user.getAccountValue().toString());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			user.setUserId(rs.getInt(1));
		}catch(SQLException e){
			if(e.getMessage().contains("Duplicate")){
				throw new ValidationException("Some user field already exists");
			}else{
				DebugLog.d(e.getMessage());
			}
		}
	//	updateWatchList(user);
	//	updateSocialList(user);
		return user;
	}

	public static void deleteUserFromDatabase(int id) throws UserNotFoundException{
		String query = "DELETE from WatchList where User_userId = '" + id +"'";
		String query2 = "DELETE from carpoolDAOUser where userId = '" + id + "'";
		String query3 = "DELETE from Message where ownerId = '" + id +"'";
		String query4 = "DELETE from SocialList where mainUser = '" + id +"'";
		String query5 = "DELETE FROM Transaction WHERE initUserId="+id+" OR targetUserId = "+id;
		try(Statement stmt = carpoolDAOBasic.getSQLConnection().createStatement()){
			stmt.addBatch(query);
			stmt.addBatch(query2);
			stmt.addBatch(query3);
			stmt.addBatch(query4);
			stmt.addBatch(query5);
			if(stmt.executeBatch()[1]==0){
				throw new UserNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
	}

	public static void UpdateUserInDatabase(User user) throws Exception{
		String query = "UPDATE carpoolDAOUser SET password=?,name=?,email=?,phone=?,qq=?,age=?,gender=?,birthday=?," +
	            "imgPath=?,user_primaryLocation=?,user_customLocation=?,user_customDepthIndex=?,lastLogin=?,"+
				"creationTime=?,emailActivated = ?,phoneActivated = ?,emailNotice = ?,phoneNotice = ?,state = ?,searchState = ?," +
				"level=?,averageScore=?,totalTranscations=?,verifications=?,googleToken=?,facebookToken=?,twitterToken=?,paypalToken=?,"+
				"id_docType=?,id_docNum=?,id_path=?,id_vehicleImgPath=?,accountId=?,accountPass=?,accountToken=?,accountValue=? WHERE userId = ?";
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, user.getPassword());
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPhone());
			stmt.setString(5, user.getQq());
			stmt.setInt(6, user.getAge());
			stmt.setInt(7, user.getGender().code);
			stmt.setString(8,  DateUtility.toSQLDateTime(user.getBirthday()));
			stmt.setString(9, user.getImgPath());
			stmt.setString(10, user.getLocation().getPrimaryLocationString());
			stmt.setString(11, user.getLocation().getCustomLocationString());
			stmt.setInt(12, user.getLocation().getCustomDepthIndex());
			stmt.setString(13,  DateUtility.toSQLDateTime(user.getLastLogin()));
			stmt.setString(14, DateUtility.toSQLDateTime(user.getCreationTime()));
			stmt.setInt(15, user.isEmailActivated() ? 1:0);
			stmt.setInt(16, user.isPhoneActivated() ? 1:0);
			stmt.setInt(17, user.isEmailNotice() ? 1:0);
			stmt.setInt(18, user.isPhoneNotice() ? 1:0);
			stmt.setInt(19, user.getState().code);
			stmt.setInt(20, user.getSearchState().code);
			stmt.setInt(21, user.getLevel());
			stmt.setInt(22, user.getAverageScore());
			stmt.setInt(23, user.getTotalTranscations());
			stmt.setString(24, Parser.listToString(user.getVerifications()));
			stmt.setString(25,user.getGoogleToken());
			stmt.setString(26, user.getFacebookToken());
			stmt.setString(27, user.getTwitterToken());
			stmt.setString(28,user.getPaypalToken());
			stmt.setString(29, user.getId_docType());
			stmt.setString(30,user.getId_docNum());
			stmt.setString(31, user.getId_path());
			stmt.setString(32, user.getId_vehicleImgPath());
			stmt.setString(33, user.getAccountId());
			stmt.setString(34, user.getAccountPass());
			stmt.setString(35, user.getAccountToken());	
			stmt.setString(36, user.getAccountValue().toString());
			stmt.setInt(37,user.getUserId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new UserNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		//updateWatchList(user);
		//updateSocialList(user);
	}

	public static User getUserById(int id) throws UserNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser WHERE userId = ?";
		User user = null;
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				user = createUserByResultSet(rs);
			}else{
				throw new UserNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
//		user = addHistoryListToUser(user);
//		user = addWatchListToUser(user);
//		user = addSocialListToUser(user);
//		user = addTransactionListToUser(user);
//		user = addNotificationListToUser(user);
		return user;
	}

	public static User getUserByEmail(String email) throws UserNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser WHERE email = ?";
		User user = null;
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				user = createUserByResultSet(rs);
			}else{
				throw new UserNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
//		user = addHistoryListToUser(user);
//		user = addWatchListToUser(user);
//		user = addSocialListToUser(user);
//		user = addTransactionListToUser(user);
//		user = addNotificationListToUser(user);
		return user;
	}
	
	public static ArrayList<User> getUserWhoWatchedMessage(int messageId){
		String query = "SELECT * FROM carpoolDAOUser JOIN WatchList ON (User.userId = WatchList.User_userId AND WatchList.Message_messageId = ?)";
		ArrayList<User> users = new ArrayList<User>();
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, messageId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				users.add(createUserByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return users;
	}
	
	public static ArrayList<User> getUserWhoWatchedUser(int userId){
		String query = "SELECT * FROM carpoolDAOUser JOIN SocialList ON (User.userId = SocialList.mainUser AND SocialList.subUser = ?)";
		ArrayList<User> users = new ArrayList<User>();
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				users.add(createUserByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return users;
	}

	protected static User createUserByResultSet(ResultSet rs) throws SQLException {
		User user;
		 user = new User(rs.getInt("userId"),rs.getString("password"), rs.getString("name"),
				rs.getString("email"),rs.getString("phone"),rs.getString("qq"),rs.getInt("age"),Constants.gender.fromInt(rs.getInt("gender")),
				DateUtility.DateToCalendar(rs.getTimestamp("birthday")),rs.getString("imgPath"),new LocationRepresentation(rs.getString("user_primaryLocation"),rs.getString("user_customLocation"),rs.getInt("user_customDepthIndex")),
				DateUtility.DateToCalendar(rs.getTimestamp("lastLogin")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
				(ArrayList<String>)Parser.stringToList(rs.getString("verifications"),new String("")),
				rs.getBoolean("emailActivated"),rs.getBoolean("phoneActivated"),rs.getBoolean("emailNotice"),rs.getBoolean("phoneNotice"),
				Constants.userState.fromInt(rs.getInt("state")),Constants.userSearchState.fromInt(rs.getInt("searchState")),
				rs.getInt("level"),rs.getInt("averageScore"),rs.getInt("totalTranscations"),
				rs.getString("googleToken"),rs.getString("facebookToken"),rs.getString("twitterToken"),rs.getString("paypalToken"),
				rs.getString("id_docType"),rs.getString("id_docNum"),rs.getString("id_path"),rs.getString("id_vehicleImgPath"),
				rs.getString("accountId"),rs.getString("accountPass"),rs.getString("accountToken"),new BigDecimal(rs.getString("accountValue")));
		return user;
	}
//
//	private static User addHistoryListToUser(User user) throws UserNotFoundException {
//		ArrayList<Message> historyList = new ArrayList<Message>();
//		String query = "SELECT * from Message WHERE ownerId = ?";
//		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				historyList.add(carpoolDAOMessage.createMessageByResultSet(rs));
//			}
//		}catch(SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//		query = "SELECT * FROM Message JOIN Transaction ON ( Transaction.messageId = Message.messageId AND Transaction.initUserId = ?)";
//		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				historyList.add(carpoolDAOMessage.createMessageByResultSet(rs));
//			}
//		}catch(SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//		user.setHistoryList(historyList);
//		return user;
//	}
//
//	private static void updateWatchList(User user) {
//		String query = "DELETE from WatchList where User_userId = " + user.getUserId();
//		String query2 = "INSERT INTO WatchList (User_userId,Message_messageId) VALUES";
//		for(Message msg : user.getWatchList()){
//			query2 = query2 + "("+user.getUserId() + "," + msg.getMessageId() + "),";
//		}
//		query2 = query2.substring(0,query2.length()-1);
//		query2 = query2 + ";";
//		try(Statement stmt = carpoolDAOBasic.getSQLConnection().createStatement()){
//			stmt.addBatch(query);
//			if(user.getWatchList().size()!=0){
//				stmt.addBatch(query2);
//			}
//			stmt.executeBatch();
//		}catch(SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//	}
//
//	private static User addWatchListToUser(User user) throws UserNotFoundException {
//		ArrayList<Message> watchLsit = new ArrayList<Message>();
//		String query = "SELECT * FROM Message JOIN WatchList ON (Message.messageId = WatchList.Message_messageId AND WatchList.User_userId = ?);";
//		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				watchLsit.add(carpoolDAOMessage.createMessageByResultSet(rs));
//			}
//		} catch (SQLException e) {
//			DebugLog.d(e.getMessage());
//		}
//		user.setWatchList(watchLsit);
//		return user;
//	}
//
//	private static void updateSocialList(User user) {
//		String query = "DELETE FROM SocialList WHERE mainUser = " + user.getUserId();
//		String query2 = "INSERT INTO SocialList (mainUser,subUser) VALUES";
//		for(User u : user.getSocialList()){
//			query2 = query2 + "("+user.getUserId() + "," + u.getUserId() + "),";
//		}
//		query2 = query2.substring(0,query2.length()-1);
//		query2 = query2 + ";";
//		try(Statement stmt = carpoolDAOBasic.getSQLConnection().createStatement()){
//			stmt.addBatch(query);
//			if(user.getSocialList().size()!=0){
//				stmt.addBatch(query2);
//			}
//			stmt.executeBatch();
//		}catch(SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//	}
//
//	private static User addSocialListToUser(User user) {
//		ArrayList<User> socialLsit = new ArrayList<User>();
//		String query = "SELECT * FROM User JOIN SocialList ON (User.userId = SocialList.subUser AND SocialList.mainUser = ?);";
//		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				socialLsit.add(carpoolDAOUser.createUserByResultSet(rs));
//			}
//		} catch (SQLException e) {
//			DebugLog.d(e.getMessage());
//		}
//		user.setSocialList(socialLsit);
//		return user;
//	}	
//	
//	private static User addTransactionListToUser(User user){
//		ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
//		String query = "SELECT * FROM Transaction WHERE initUserId=? OR targetUserId = ?";
//		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			stmt.setInt(2, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				transactionList.add(DaoTransaction.createTransactionByResultSet(rs));
//			}
//		} catch (SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//		user.setTransactionList(transactionList);
//		return user;
//	}
//	
//	private static User addNotificationListToUser(User user){
//		ArrayList<Notification> notificationList = new ArrayList<Notification>();
//		String query = "SELECT * FROM Notification WHERE targetUserId = ?;";
//		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				notificationList.add(DaoNotification.createNotificationByResultSet(rs));
//			}
//		}catch(SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//		user.setNotificationList(notificationList);
//		return user;		
//	}

}
