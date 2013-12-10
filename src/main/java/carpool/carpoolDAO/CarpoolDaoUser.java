package carpool.carpoolDAO;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import carpool.aws.awsMain;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Parser;
import carpool.common.Validator;
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.userSearchState;
import carpool.constants.Constants.userState;
import carpool.encryption.SessionCrypto;

import carpool.exception.validation.ValidationException;
import carpool.exception.location.LocationNotFoundException;

import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;
import carpool.model.representation.UserSearchRepresentation;


public class CarpoolDaoUser {

    public static ArrayList<User> searchForUser(UserSearchRepresentation usr){
    	ArrayList<User> ulist = new ArrayList<User>();
    	String name = usr.getName();
    	gender Gender = usr.getGender();
    	LocationRepresentation location = usr.getLocation();
    	
    	String query = "SELECT * FROM carpoolDAOUser WHERE name REGEXP ? AND gender LIKE ? AND user_primaryLocation LIKE ?;";
    	try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, name);
			stmt.setInt(2,Gender.code);
			stmt.setString(3, location.getPrimaryLocationString());
			ResultSet rs = stmt.executeQuery();			
				while(rs.next()){									
					ulist.add(createUserByResultSet(rs));
					}			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}
    	return ulist;
    	
    }
    
	public static User addUserToDatabase(User user) throws ValidationException{	
		
		String query = "INSERT INTO carpoolDAOUser (password,name,email,phone,qq,gender,birthday,"+
	            "imgPath,user_primaryLocation,user_customLocation,user_customDepthIndex,lastLogin,creationTime,"+
				"emailActivated,phoneActivated,emailNotice,phoneNotice,state,searchRepresentation,"+
	            "level,averageScore,totalTranscations,verifications,googleToken,facebookToken,twitterToken,"+
				"paypalToken,id_docType,id_docNum,id_path,id_vehicleImgPath,accountId,accountPass,accountToken,accountValue)"+
	            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setString(1, SessionCrypto.encrypt(user.getPassword()));
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPhone());
			stmt.setString(5, user.getQq());			
			stmt.setInt(6, user.getGender().code);
			stmt.setString(7,  DateUtility.toSQLDateTime(user.getBirthday()));
			stmt.setString(8, user.getImgPath());
			stmt.setString(9, user.getLocation().getPrimaryLocationString());
			stmt.setString(10, user.getLocation().getCustomLocationString());
			stmt.setInt(11, user.getLocation().getCustomDepthIndex());
			stmt.setString(12,  DateUtility.toSQLDateTime(user.getLastLogin()));
			stmt.setString(13, DateUtility.toSQLDateTime(user.getCreationTime()));
			stmt.setInt(14, user.isEmailActivated() ? 1:0);
			stmt.setInt(15, user.isPhoneActivated() ? 1:0);
			stmt.setInt(16, user.isEmailNotice() ? 1:0);
			stmt.setInt(17, user.isPhoneNotice() ? 1:0);
			stmt.setInt(18, user.getState().code);
			stmt.setString(19, user.getSearchRepresentation().toSerializedString());
			stmt.setInt(20, user.getLevel());
			stmt.setInt(21, user.getAverageScore());
			stmt.setInt(22, user.getTotalTranscations());
			stmt.setString(23, Parser.listToString(user.getVerifications()));
			stmt.setString(24,user.getGoogleToken());
			stmt.setString(25, user.getFacebookToken());
			stmt.setString(26, user.getTwitterToken());
			stmt.setString(27,user.getPaypalToken());
			stmt.setString(28, user.getId_docType());
			stmt.setString(29,user.getId_docNum());
			stmt.setString(30, user.getId_path());
			stmt.setString(31, user.getId_vehicleImgPath());
			stmt.setString(32, user.getAccountId());
			stmt.setString(33, user.getAccountPass());
			stmt.setString(34, user.getAccountToken());		
			stmt.setString(35, user.getAccountValue().toString());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			user.setUserId(rs.getInt(1));
		}catch(SQLException e){
			if(e.getMessage().contains("Duplicate")){
				throw new ValidationException("一部分账户内容与其他账户冲突");
			}else{
				DebugLog.d(e);
			}
		} catch (Exception e) {
			throw new ValidationException("创建用户失败，账户信息错误");
		} 
		return user;
	}

	public static void deleteUserFromDatabase(int id) throws UserNotFoundException{
		String query = "DELETE from WatchList where User_userId = '" + id +"'";
		String query2 = "DELETE from carpoolDAOUser where userId = '" + id + "'";
		String query3 = "DELETE from carpoolDAOMessage where ownerId = '" + id +"'";
		String query4 = "DELETE from SocialList where mainUser = '" + id +"'";
		String query5 = "DELETE FROM carpoolDAOTransaction WHERE provider_Id="+id+" OR customer_Id = "+id;
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
			DebugLog.d(e);
		}
	}

	public static void UpdateUserInDatabase(User user) throws UserNotFoundException, ValidationException{
		String query = "UPDATE carpoolDAOUser SET password=?,name=?,email=?,phone=?,qq=?,gender=?,birthday=?," +
	            "imgPath=?,user_primaryLocation=?,user_customLocation=?,user_customDepthIndex=?,lastLogin=?,"+
				"creationTime=?,emailActivated = ?,phoneActivated = ?,emailNotice = ?,phoneNotice = ?,state = ?,searchRepresentation = ?," +
				"level=?,averageScore=?,totalTranscations=?,verifications=?,googleToken=?,facebookToken=?,twitterToken=?,paypalToken=?,"+
				"id_docType=?,id_docNum=?,id_path=?,id_vehicleImgPath=?,accountId=?,accountPass=?,accountToken=?,accountValue=? WHERE userId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, SessionCrypto.encrypt(user.getPassword()));
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPhone());
			stmt.setString(5, user.getQq());			
			stmt.setInt(6, user.getGender().code);
			stmt.setString(7,  DateUtility.toSQLDateTime(user.getBirthday()));
			stmt.setString(8, user.getImgPath());
			stmt.setString(9, user.getLocation().getPrimaryLocationString());
			stmt.setString(10, user.getLocation().getCustomLocationString());
			stmt.setInt(11, user.getLocation().getCustomDepthIndex());
			stmt.setString(12,  DateUtility.toSQLDateTime(user.getLastLogin()));
			stmt.setString(13, DateUtility.toSQLDateTime(user.getCreationTime()));
			stmt.setInt(14, user.isEmailActivated() ? 1:0);
			stmt.setInt(15, user.isPhoneActivated() ? 1:0);
			stmt.setInt(16, user.isEmailNotice() ? 1:0);
			stmt.setInt(17, user.isPhoneNotice() ? 1:0);
			stmt.setInt(18, user.getState().code);
			stmt.setString(19, user.getSearchRepresentation().toSerializedString());
			stmt.setInt(20, user.getLevel());
			stmt.setInt(21, user.getAverageScore());
			stmt.setInt(22, user.getTotalTranscations());
			stmt.setString(23, Parser.listToString(user.getVerifications()));
			stmt.setString(24,user.getGoogleToken());
			stmt.setString(25, user.getFacebookToken());
			stmt.setString(26, user.getTwitterToken());
			stmt.setString(27,user.getPaypalToken());
			stmt.setString(28, user.getId_docType());
			stmt.setString(29,user.getId_docNum());
			stmt.setString(30, user.getId_path());
			stmt.setString(31, user.getId_vehicleImgPath());
			stmt.setString(32, user.getAccountId());
			stmt.setString(33, user.getAccountPass());
			stmt.setString(34, user.getAccountToken());	
			stmt.setString(35, user.getAccountValue().toString());
			stmt.setInt(36,user.getUserId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new UserNotFoundException();
			}
		} catch(SQLException e){
			DebugLog.d(e);
		} catch (Exception e) {
			throw new ValidationException("更改用户信息失败，账户信息错误");
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
			DebugLog.d(e);
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
			DebugLog.d(e);
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
			DebugLog.d(e);
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
			DebugLog.d(e);
		}
		return users;
	}

	protected static User createUserByResultSet(ResultSet rs) throws SQLException {
		User user = null;
		 try {
			user = new User(rs.getInt("userId"),SessionCrypto.decrypt(rs.getString("password")), rs.getString("name"),
					rs.getString("email"),rs.getString("phone"),rs.getString("qq"),Constants.gender.fromInt(rs.getInt("gender")),
					DateUtility.DateToCalendar(rs.getTimestamp("birthday")),rs.getString("imgPath"),new LocationRepresentation(rs.getString("user_primaryLocation"),rs.getString("user_customLocation"),rs.getInt("user_customDepthIndex")),
					DateUtility.DateToCalendar(rs.getTimestamp("lastLogin")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
					(ArrayList<String>)Parser.stringToList(rs.getString("verifications"),new String("")),
					rs.getBoolean("emailActivated"),rs.getBoolean("phoneActivated"),rs.getBoolean("emailNotice"),rs.getBoolean("phoneNotice"),
					Constants.userState.fromInt(rs.getInt("state")),new SearchRepresentation(rs.getString("searchRepresentation")),
					rs.getInt("level"),rs.getInt("averageScore"),rs.getInt("totalTranscations"),
					rs.getString("googleToken"),rs.getString("facebookToken"),rs.getString("twitterToken"),rs.getString("paypalToken"),
					rs.getString("id_docType"),rs.getString("id_docNum"),rs.getString("id_path"),rs.getString("id_vehicleImgPath"),
					rs.getString("accountId"),rs.getString("accountPass"),rs.getString("accountToken"),new BigDecimal(rs.getString("accountValue")));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
//			DebugLog.d(e);
//		}
//		query = "SELECT * FROM carpoolDAOMessage JOIN Transaction ON ( Transaction.messageId = carpoolDAOMessage.messageId AND Transaction.initUserId = ?)";
//		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
//			stmt.setInt(1, user.getUserId());
//			ResultSet rs = stmt.executeQuery();
//			while(rs.next()){
//				historyList.add(CarpoolDaoMessage.createMessageByResultSet(rs));
//			}
//		}catch(SQLException e){
//			DebugLog.d(e);
//		}
//		user.setHistoryList(historyList);
//		return user;
//	}

	
    public static boolean hasUserInSocialList(int mainUser, int subUser){
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
				DebugLog.d(e);
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
				DebugLog.d(e);
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
			DebugLog.d(e);
		}
	   return slist;
   }
   

	
	public static ArrayList<Message> getUserMessageHistory(int user) throws UserNotFoundException, LocationNotFoundException{
		ArrayList<Message> mlist = new ArrayList<Message>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query ="SELECT * FROM carpoolDAOMessage WHERE ownerId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, user);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				ilist = CarpoolDaoMessage.addIds(ilist,rs.getInt("ownerId"));
				mlist.add(CarpoolDaoMessage.createMessagesByResultSetList(rs));
			}
			mlist = CarpoolDaoMessage.getUsersForMessages(ilist, mlist);
		} catch (SQLException e) {
			DebugLog.d(e);
		}
		return mlist;
	}


}
