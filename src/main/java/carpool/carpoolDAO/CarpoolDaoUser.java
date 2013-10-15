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
import carpool.database.DaoNotification;
import carpool.database.DaoTransaction;
import carpool.exception.ValidationException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;


public class CarpoolDaoUser {


	public static User addUserToDatabase(User user) throws ValidationException{
		String query = "INSERT INTO carpoolDAOUser (password,name,email,phone,qq,age,gender,birthday,"+
	            "imgPath,user_primaryLocation,user_customLocation,user_customDepthIndex,lastLogin,creationTime,"+
				"emailActivated,phoneActivated,emailNotice,phoneNotice,state,searchRepresentation,"+
	            "level,averageScore,totalTranscations,verifications,googleToken,facebookToken,twitterToken,"+
				"paypalToken,id_docType,id_docNum,id_path,id_vehicleImgPath,accountId,accountPass,accountToken,accountValue)"+
	            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
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
			stmt.setString(20, user.getSearchRepresentation().toSerializedString());
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
		return user;
	}

	public static void deleteUserFromDatabase(int id) throws UserNotFoundException{
		String query = "DELETE from WatchList where User_userId = '" + id +"'";
		String query2 = "DELETE from carpoolDAOUser where userId = '" + id + "'";
		String query3 = "DELETE from Message where ownerId = '" + id +"'";
		String query4 = "DELETE from SocialList where mainUser = '" + id +"'";
		String query5 = "DELETE FROM Transaction WHERE initUserId="+id+" OR targetUserId = "+id;
		try(Statement stmt = CarpoolDaoBasic.getSQLConnection().createStatement()){
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
				"creationTime=?,emailActivated = ?,phoneActivated = ?,emailNotice = ?,phoneNotice = ?,state = ?,searchRepresentation = ?," +
				"level=?,averageScore=?,totalTranscations=?,verifications=?,googleToken=?,facebookToken=?,twitterToken=?,paypalToken=?,"+
				"id_docType=?,id_docNum=?,id_path=?,id_vehicleImgPath=?,accountId=?,accountPass=?,accountToken=?,accountValue=? WHERE userId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
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
			stmt.setString(20, user.getSearchRepresentation().toSerializedString());
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
	}

	public static ArrayList<User> getAllUsers(){
		String query = "SELECT * FROM carpoolDAOUser";
		ArrayList<User> users = new ArrayList<User>();
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				users.add(createUserByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return users;
	}
	
	
	public static User getUserById(int id) throws UserNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser WHERE userId = ?";
		User user = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
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

		return user;
	}

	
	public static User getUserByEmail(String email) throws UserNotFoundException{
		String query = "SELECT * FROM carpoolDAOUser WHERE email = ?";
		User user = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
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

		return user;
	}
	

	
	public static ArrayList<User> getUserWhoWatchedUser(int userId){
		String query = "SELECT * FROM carpoolDAOUser JOIN SocialList ON (User.userId = SocialList.mainUser AND SocialList.subUser = ?)";
		ArrayList<User> users = new ArrayList<User>();
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
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
				Constants.userState.fromInt(rs.getInt("state")),new SearchRepresentation(rs.getString("searchRepresentation")),
				rs.getInt("level"),rs.getInt("averageScore"),rs.getInt("totalTranscations"),
				rs.getString("googleToken"),rs.getString("facebookToken"),rs.getString("twitterToken"),rs.getString("paypalToken"),
				rs.getString("id_docType"),rs.getString("id_docNum"),rs.getString("id_path"),rs.getString("id_vehicleImgPath"),
				rs.getString("accountId"),rs.getString("accountPass"),rs.getString("accountToken"),new BigDecimal(rs.getString("accountValue")));
		return user;
	}
	
//	//tranverse
//	private static User addHistoryListToUser(User user) throws UserNotFoundException {
//		ArrayList<Message> historyList = new ArrayList<Message>();
//		String query = "SELECT * from carpoolDAOMessage WHERE ownerId = ?";
//		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				historyList.add(CarpoolDaoMessage.createMessageByResultSet(rs, ));
//			}
//		}catch(SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//		query = "SELECT * FROM carpoolDAOMessage JOIN Transaction ON ( Transaction.messageId = carpoolDAOMessage.messageId AND Transaction.initUserId = ?)";
//		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				historyList.add(CarpoolDaoMessage.createMessageByResultSet(rs));
//			}
//		}catch(SQLException e){
//			DebugLog.d(e.getMessage());
//		}
//		user.setHistoryList(historyList);
//		return user;
//	}

	
    private static boolean hasUserInSocialList(int mainUser, int subUser){
    	String query = "SELECT COUNT(*) AS total FROM SocialList WHERE mainUser =" +mainUser+ " AND subUser ="+subUser+";";
    	try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
    		ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				if(rs.getInt("total")==1){
					return true;
				}else{
					return false;
				}
			}
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    	return false;
    }

    
    public static void addToSocialList(int user,int subUser) {
 		String query = "SET foreign_key_checks = 0;INSERT INTO SocialList (mainUser,subUser) VALUES(?,?);SET foreign_key_checks = 1;";
 		if(!hasUserInSocialList(user,subUser)){
	 		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
				stmt.setInt(1, user);
				stmt.setInt(2, subUser);
				stmt.executeUpdate();		
			}catch(SQLException e){
				DebugLog.d(e.getMessage());
			}
 		}
   }	

    public static void deleteFromSocialList(int user,int subUser){
 	   String query = "SET foreign_key_checks = 0;DELETE FROM SocialList WHERE mainUser =? AND subUser = ?;SET foreign_key_checks = 1;";
 	   if(hasUserInSocialList(user,subUser)){
 		   try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
				stmt.setInt(1, user);
				stmt.setInt(2, subUser);
				stmt.executeUpdate();	
				
			}catch(SQLException e){
				DebugLog.d(e.getMessage());
			}

	   }
	   
   }

    
   public static ArrayList<User> getSocialListOfUser(int user){
	   ArrayList<User> slist = new ArrayList<User>();
	   String query = "SELECT * FROM carpoolDAOUser JOIN SocialList ON (carpoolDAOUser.userId = SocialList.subUser AND SocialList.mainUser = ?);";
	   //TODO 
	   try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				slist.add(CarpoolDaoUser.createUserByResultSet(rs));
			}
		} catch (SQLException e) {
			DebugLog.d(e.getMessage());
		}
	   return slist;
   }
   

	
	public static ArrayList<Message> getUserMessageHistory(int user) throws UserNotFoundException{
		ArrayList<Message> mlist = new ArrayList<Message>();
		String query ="SELECT * FROM carpoolDAOMessage WHERE ownerId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				mlist.add(CarpoolDaoMessage.createMessageByResultSet(rs, true));
			}
		} catch (SQLException e) {
			DebugLog.d(e.getMessage());
		}
		return mlist;
	}
	
	private static User addTransactionListToUser(User user){
		ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
		String query = "SELECT * FROM Transaction WHERE initUserId=? OR targetUserId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
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
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
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
