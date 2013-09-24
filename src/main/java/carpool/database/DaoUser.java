package carpool.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.userSearchState;
import carpool.constants.Constants.userState;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;


public class DaoUser {

	public static ArrayList<User> searchUser(String name, String phone, String email, String qq){
		ArrayList<User> retVal = new ArrayList<User>();
		String query = "SELECT * FROM User WHERE name LIKE ? OR phone LIKE ? OR email LIKE ? OR qq LIKE ?;";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, name);
			stmt.setString(2, phone);
			stmt.setString(3, email);
			stmt.setString(4, qq);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				User user = createUserByResultSet(rs);
				user = addHistoryListToUser(user);
				user = addWatchListToUser(user);
				user = addSocialListToUser(user);
				user = addTransactionListToUser(user);
				user = addNotificationListToUser(user);
				retVal.add(user);
			}
		} catch (SQLException e) {
			DebugLog.d(e.getMessage());
		}
		return retVal;
	}

	public static User addUserToDatabase(User user) throws ValidationException{
		String query = "INSERT INTO User (password,name,level,averageScore," +
				"totalTranscations,universityGroup,age,gender,phone,email,qq," +
				"imgPath,location,emailActivated,phoneActivated,emailNotice," +
				"phoneNotice,state,searchState,lastLogin,creationTime,paypal) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setString(1, user.getPassword());
			stmt.setString(2, user.getName());
			stmt.setInt(3, user.getLevel());
			stmt.setInt(4, user.getAverageScore());
			stmt.setInt(5, user.getTotalTranscations());
			stmt.setString(6, user.getUniversityGroupString());
			stmt.setInt(7, user.getAge());
			stmt.setInt(8, user.getGender().code);
			stmt.setString(9, user.getPhone());
			stmt.setString(10, user.getEmail());
			stmt.setString(11, user.getQq());
			stmt.setString(12, user.getImgPath());
			stmt.setString(13, user.getLocation().toString());
			stmt.setInt(14, user.isEmailActivated() ? 1:0);
			stmt.setInt(15, user.isPhoneActivated() ? 1:0);
			stmt.setInt(16, user.isEmailNotice() ? 1:0);
			stmt.setInt(17, user.isPhoneNotice() ? 1:0);
			stmt.setInt(18, user.getState().code);
			stmt.setInt(19, user.getSearchState().code);
			stmt.setString(20, Validator.toSQLDateTime(user.getLastLogin()));
			stmt.setString(21, Validator.toSQLDateTime(user.getCreationTime()));
			stmt.setString(22, user.getPaypal());
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
		updateWatchList(user);
		updateSocialList(user);
		return user;
	}

	public static void deleteUserFromDatabase(int id) throws UserNotFoundException{
		String query = "DELETE from WatchList where User_userId = '" + id +"'";
		String query2 = "DELETE from User where userId = '" + id + "'";
		String query3 = "DELETE from Message where ownerId = '" + id +"'";
		String query4 = "DELETE from SocialList where mainUser = '" + id +"'";
		String query5 = "DELETE FROM Transaction WHERE initUserId="+id+" OR targetUserId = "+id;
		try(Statement stmt = DaoBasic.getSQLConnection().createStatement()){
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
		String query = "UPDATE User SET password=?,name=?,level=?,averageScore=?,totalTranscations=?," +
				"universityGroup = ?,age = ?,gender = ?,phone = ?,email = ?,qq = ?,imgPath = ?,location = ?," +
				"emailActivated = ?,phoneActivated = ?,emailNotice = ?,phoneNotice = ?,state = ?,searchState = ?," +
				"lastLogin = ?,creationTime = ?,paypal = ? WHERE userId = ?";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, user.getPassword());
			stmt.setString(2, user.getName());
			stmt.setInt(3, user.getLevel());
			stmt.setInt(4, user.getAverageScore());
			stmt.setInt(5, user.getTotalTranscations());
			stmt.setString(6, user.getUniversityGroupString());
			stmt.setInt(7, user.getAge());
			stmt.setInt(8, user.getGender().code);
			stmt.setString(9, user.getPhone());
			stmt.setString(10, user.getEmail());
			stmt.setString(11, user.getQq());
			stmt.setString(12, user.getImgPath());
			stmt.setString(13, user.getLocation().toString());
			stmt.setInt(14, user.isEmailActivated() ? 1:0);
			stmt.setInt(15, user.isPhoneActivated() ? 1:0);
			stmt.setInt(16, user.isEmailNotice() ? 1:0);
			stmt.setInt(17, user.isPhoneNotice() ? 1:0);
			stmt.setInt(18, user.getState().code);
			stmt.setInt(19, user.getSearchState().code);
			stmt.setString(20, Validator.toSQLDateTime(user.getLastLogin()));
			stmt.setString(21, Validator.toSQLDateTime(user.getCreationTime()));
			stmt.setString(22, user.getPaypal());
			stmt.setInt(23, user.getUserId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new UserNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		updateWatchList(user);
		updateSocialList(user);
	}

	public static User getUserById(int id) throws UserNotFoundException{
		String query = "SELECT * FROM User WHERE userId = ?";
		User user = null;
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
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
		user = addHistoryListToUser(user);
		user = addWatchListToUser(user);
		user = addSocialListToUser(user);
		user = addTransactionListToUser(user);
		user = addNotificationListToUser(user);
		return user;
	}

	public static User getUserByEmail(String email) throws UserNotFoundException{
		String query = "SELECT * FROM User WHERE email = ?";
		User user = null;
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
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
		user = addHistoryListToUser(user);
		user = addWatchListToUser(user);
		user = addSocialListToUser(user);
		user = addTransactionListToUser(user);
		user = addNotificationListToUser(user);
		return user;
	}
	
	public static ArrayList<User> getUserWhoWatchedMessage(int messageId){
		String query = "SELECT * FROM User JOIN WatchList ON (User.userId = WatchList.User_userId AND WatchList.Message_messageId = ?)";
		ArrayList<User> users = new ArrayList<User>();
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
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
		String query = "SELECT * FROM User JOIN SocialList ON (User.userId = SocialList.mainUser AND SocialList.subUser = ?)";
		ArrayList<User> users = new ArrayList<User>();
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
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
		String universityGroupString = rs.getString("universityGroup");
		ArrayList<String> universityGroup = new ArrayList<String>();
		if(!universityGroupString.equals("")){
			for(String each : universityGroupString.split("-")){
				universityGroup.add(each);
			}
		}
		String locationString = rs.getString("location");
		Location location = new Location(locationString);
		user = new User(rs.getInt("userId"),rs.getString("password"), rs.getString("name"),
				rs.getInt("level"), rs.getInt("averageScore"), rs.getInt("totalTranscations"),
				new ArrayList<Message>(),new ArrayList<Message>(),new ArrayList<User>(),
				new ArrayList<Transaction>(),new ArrayList<Notification>(),universityGroup,
				rs.getInt("age"), gender.fromInt(rs.getInt("gender")), rs.getString("phone"),
				rs.getString("email"),rs.getString("qq"), rs.getString("imgPath"),location,
				rs.getBoolean("emailActivated"),rs.getBoolean("phoneActivated"),
				rs.getBoolean("emailNotice"),rs.getBoolean("phoneNotice"),
				userState.fromInt(rs.getInt("state")),
				userSearchState.fromInt(rs.getInt("searchState")),
				DateUtility.DateToCalendar(rs.getTimestamp("lastLogin")),
				DateUtility.DateToCalendar(rs.getTimestamp("creationTime")), rs.getString("paypal"));
		return user;
	}

	private static User addHistoryListToUser(User user) {
		ArrayList<Message> historyList = new ArrayList<Message>();
		String query = "SELECT * from Message WHERE ownerId = ?";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user.getUserId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				historyList.add(DaoMessage.createMessageByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		query = "SELECT * FROM Message JOIN Transaction ON ( Transaction.messageId = Message.messageId AND Transaction.initUserId = ?)";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user.getUserId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				historyList.add(DaoMessage.createMessageByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		user.setHistoryList(historyList);
		return user;
	}

	private static void updateWatchList(User user) {
		String query = "DELETE from WatchList where User_userId = " + user.getUserId();
		String query2 = "INSERT INTO WatchList (User_userId,Message_messageId) VALUES";
		for(Message msg : user.getWatchList()){
			query2 = query2 + "("+user.getUserId() + "," + msg.getMessageId() + "),";
		}
		query2 = query2.substring(0,query2.length()-1);
		query2 = query2 + ";";
		try(Statement stmt = DaoBasic.getSQLConnection().createStatement()){
			stmt.addBatch(query);
			if(user.getWatchList().size()!=0){
				stmt.addBatch(query2);
			}
			stmt.executeBatch();
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
	}

	private static User addWatchListToUser(User user) {
		ArrayList<Message> watchLsit = new ArrayList<Message>();
		String query = "SELECT * FROM Message JOIN WatchList ON (Message.messageId = WatchList.Message_messageId AND WatchList.User_userId = ?);";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user.getUserId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				watchLsit.add(DaoMessage.createMessageByResultSet(rs));
			}
		} catch (SQLException e) {
			DebugLog.d(e.getMessage());
		}
		user.setWatchList(watchLsit);
		return user;
	}

	private static void updateSocialList(User user) {
		String query = "DELETE FROM SocialList WHERE mainUser = " + user.getUserId();
		String query2 = "INSERT INTO SocialList (mainUser,subUser) VALUES";
		for(User u : user.getSocialList()){
			query2 = query2 + "("+user.getUserId() + "," + u.getUserId() + "),";
		}
		query2 = query2.substring(0,query2.length()-1);
		query2 = query2 + ";";
		try(Statement stmt = DaoBasic.getSQLConnection().createStatement()){
			stmt.addBatch(query);
			if(user.getSocialList().size()!=0){
				stmt.addBatch(query2);
			}
			stmt.executeBatch();
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
	}

	private static User addSocialListToUser(User user) {
		ArrayList<User> socialLsit = new ArrayList<User>();
		String query = "SELECT * FROM User JOIN SocialList ON (User.userId = SocialList.subUser AND SocialList.mainUser = ?);";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user.getUserId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				socialLsit.add(DaoUser.createUserByResultSet(rs));
			}
		} catch (SQLException e) {
			DebugLog.d(e.getMessage());
		}
		user.setSocialList(socialLsit);
		return user;
	}	
	
	private static User addTransactionListToUser(User user){
		ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
		String query = "SELECT * FROM Transaction WHERE initUserId=? OR targetUserId = ?";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user.getUserId());
			stmt.setInt(2, user.getUserId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				transactionList.add(DaoTransaction.createTransactionByResultSet(rs));
			}
		} catch (SQLException e){
			DebugLog.d(e.getMessage());
		}
		user.setTransactionList(transactionList);
		return user;
	}
	
	private static User addNotificationListToUser(User user){
		ArrayList<Notification> notificationList = new ArrayList<Notification>();
		String query = "SELECT * FROM Notification WHERE targetUserId = ?;";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user.getUserId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				notificationList.add(DaoNotification.createNotificationByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		user.setNotificationList(notificationList);
		return user;		
	}

}
