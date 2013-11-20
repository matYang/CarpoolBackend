package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.LetterDirection;
import carpool.constants.Constants.LetterType;
import carpool.exception.letter.LetterNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Letter;
import carpool.model.User;

public class CarpoolDaoLetter {

	public static Letter addLetterToDatabases(Letter letter) throws UserNotFoundException{
		if(letter.getFrom_userId()>0){
			User fromUser = CarpoolDaoUser.getUserById(letter.getFrom_userId());
			letter.setFrom_user(fromUser);
		}
		if(letter.getTo_userId()>0){
			User toUser = CarpoolDaoUser.getUserById(letter.getTo_userId());
			letter.setTo_user(toUser);
		}

		String query = "INSERT INTO carpoolDAOLetter(from_UserId,to_UserId,letterType,content,send_Time,check_Time,letterState,historyDeleted)"+
				"VALUES(?,?,?,?,?,?,?,?);";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, letter.getFrom_userId());
			stmt.setInt(2, letter.getTo_userId());
			stmt.setInt(3, letter.getType().code);
			stmt.setString(4, letter.getContent());
			stmt.setString(5, DateUtility.toSQLDateTime(letter.getSend_time()));
			stmt.setString(6, DateUtility.toSQLDateTime(letter.getCheck_time()));
			stmt.setInt(7, letter.getState().code);
			stmt.setInt(8,letter.isHistoryDeleted() ? 1 : 0);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			letter.setLetterId(rs.getInt(1));
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}

		return letter;
	}

	public static void updateLetterInDatabases(Letter letter) throws LetterNotFoundException{
		String query = "UPDATE carpoolDAOLetter SET from_UserId=?,to_UserId=?,letterType=?,content=?,send_Time=?,check_Time=?,letterState=?,historyDeleted=? where letter_Id=?";

		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, letter.getFrom_userId());
			stmt.setInt(2, letter.getTo_userId());
			stmt.setInt(3, letter.getType().code);
			stmt.setString(4, letter.getContent());
			stmt.setString(5, DateUtility.toSQLDateTime(letter.getSend_time()));
			stmt.setString(6, DateUtility.toSQLDateTime(letter.getCheck_time()));
			stmt.setInt(7, letter.getState().code);
			stmt.setInt(8,letter.isHistoryDeleted() ? 1 : 0);
			stmt.setInt(9, letter.getLetterId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new LetterNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());			
		}
	}

	public static Letter getLetterById(int letterId) throws LetterNotFoundException, UserNotFoundException{
		String query = "SELECT * from carpoolDAOLetter where letter_Id = ?";
		Letter letter = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, letterId);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				letter = createLetterByResultSet(rs);
			}else{
				throw new LetterNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}

		return letter;
	}

	public static ArrayList<Letter> getAllLetters() throws UserNotFoundException{
		ArrayList<Letter> list = new ArrayList<Letter>();
		String query = "SELECT * from carpoolDAOLetter";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				list.add(createLetterByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}

		return list;
	}

	public static  ArrayList<Letter> getUserLetter(int curUserId, int targetUserId, LetterType type, LetterDirection direction) throws UserNotFoundException{
		ArrayList<Letter> list = new ArrayList<Letter>();
		String query =  "SELECT * from carpoolDAOLetter where to_UserId = ? and letterType = ?";		
		String query1 = "SELECT * from carpoolDAOLetter where (to_UserId = ? or from_UserId = ?)and letterType = ?";
		String query2 = "SELECT * from carpoolDAOLetter where from_UserId=? and to_UserId=? and letterType = ?";		
		String query3 = "SELECT * from carpoolDAOLetter where (from_UserId=? and to_UserId=?)OR(from_UserId=? and to_UserId=?) and letterType = ?";

		//System
		if(type.equals(Constants.LetterType.system)){
			//Out
			if(direction.equals(Constants.LetterDirection.outbound)){
				try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
					stmt.setInt(1, targetUserId);
					stmt.setInt(2, type.code);
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						list.add(createLetterByResultSet(rs));
					}
				}catch(SQLException e){
					DebugLog.d(e.getMessage());
				}
			}
		}else{
			//User
			if(direction.equals(Constants.LetterDirection.inbound)){
				//In
				try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query2)){
					stmt.setInt(1, curUserId);
					stmt.setInt(2, targetUserId);
					stmt.setInt(3, type.code);
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						list.add(createLetterByResultSet(rs));
					}
				}catch(SQLException e){
					DebugLog.d(e.getMessage());
				}

			}else if(direction.equals(Constants.LetterDirection.outbound)){
				//Out
				try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query2)){
					stmt.setInt(1, targetUserId);
					stmt.setInt(2, curUserId);
					stmt.setInt(3, type.code);
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						list.add(createLetterByResultSet(rs));
					}
				}catch(SQLException e){
					DebugLog.d(e.getMessage());
				}

			}else{
				//Both
				try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query3)){
					stmt.setInt(1, curUserId);
					stmt.setInt(2, targetUserId);
					stmt.setInt(3, targetUserId);
					stmt.setInt(4, curUserId);
					stmt.setInt(5, type.code);
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						list.add(createLetterByResultSet(rs));
					}
				}catch(SQLException e){
					DebugLog.d(e.getMessage());
				}
			}
		}

		return list;
	}

	public static void deleteLetter(int letterId){
		String query = "DELETE from carpoolDAOLetter where letter_Id = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, letterId);
			stmt.executeUpdate();	
		}catch (SQLException e) {
			DebugLog.d(e.getMessage());
		}
	}

	private static Letter createLetterByResultSet(ResultSet rs) throws SQLException, UserNotFoundException {
		User fromUser = null;
		User toUser = null;

		if(rs.getInt("from_UserId")>0){
			fromUser = CarpoolDaoUser.getUserById(rs.getInt("from_UserId"));
		}

		if(rs.getInt("to_UserId")>0){
			toUser =  CarpoolDaoUser.getUserById(rs.getInt("to_UserId"));
		}

		Letter letter = new Letter(rs.getInt("letter_Id"),rs.getInt("from_UserId"),rs.getInt("to_UserId"),Constants.LetterType.fromInt(rs.getInt("letterType")),fromUser,toUser,
				rs.getString("content"),DateUtility.DateToCalendar(rs.getTimestamp("send_Time")),DateUtility.DateToCalendar(rs.getTimestamp("check_Time")),
				Constants.LetterState.fromInt(rs.getInt("letterState")),rs.getBoolean("historyDeleted"));
		return letter;
	}

}
