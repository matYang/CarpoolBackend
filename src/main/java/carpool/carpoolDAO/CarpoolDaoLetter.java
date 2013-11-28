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
import carpool.constants.Constants.LetterState;
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
			DebugLog.d(e);
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
			DebugLog.d(e);			
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
			DebugLog.d(e);
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
			DebugLog.d(e);
		}

		return list;
	}

	public static  ArrayList<Letter> getUserLetters(int curUserId, int targetUserId, LetterType type, LetterDirection direction) throws UserNotFoundException{

		ArrayList<Letter> list = new ArrayList<Letter>();
		if(curUserId<=0 && targetUserId<=0){
			return list;
		}
		String query0 = "SELECT * from carpoolDAOLetter where (to_UserId = ? or from_UserId = ?) and letterType = ? ";
		String query =  "SELECT * from carpoolDAOLetter where to_UserId = ? and letterType = ?";
		String query1 = "SELECT * from carpoolDAOLetter where from_UserId = ? and letterType = ?";
		String query2 = "SELECT * from carpoolDAOLetter where from_UserId=? and to_UserId=? and letterType = ?";		
		String query3 = "SELECT * from carpoolDAOLetter where (from_UserId=? and to_UserId=?)OR(from_UserId=? and to_UserId=?) and letterType = ?";

		//System
		if(type.equals(Constants.LetterType.system)){	
			if(direction.equals(LetterDirection.inbound)){
				if(curUserId>0){
					//User send to System
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query1)){		
						stmt.setInt(1, curUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}else{//System send to User
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){		
						stmt.setInt(1, targetUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}
			}else if(direction.equals(LetterDirection.outbound)){
				if(curUserId>0){
					//System to User
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){		
						stmt.setInt(1, curUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}
				else{//User to System
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query1)){		
						stmt.setInt(1, targetUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}

			}else{
				//Both
				try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query0)){		
					stmt.setInt(1, curUserId > 0 ? curUserId : targetUserId);
					stmt.setInt(2, curUserId > 0 ? curUserId : targetUserId);
					stmt.setInt(3, type.code);
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						list.add(createLetterByResultSet(rs));
					}
				}catch(SQLException e){
					DebugLog.d(e);
				}
			}
		}
		else{
			//User
			if(direction.equals(Constants.LetterDirection.inbound)){
				if(curUserId>0 && targetUserId>0){
					//User to User
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query2)){
						stmt.setInt(1, curUserId);
						stmt.setInt(2, targetUserId);
						stmt.setInt(3, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}else if(curUserId<=0){
					//Users to User
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){						
						stmt.setInt(1, targetUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}else{
					//User to Users
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query1)){						
						stmt.setInt(1, curUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}

			}else if(direction.equals(Constants.LetterDirection.outbound)){
				if(curUserId>0 && targetUserId>0){
					//User to User
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query2)){
						stmt.setInt(1, targetUserId);
						stmt.setInt(2, curUserId);
						stmt.setInt(3, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}else if(curUserId>0){
					//Users to User
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){						
						stmt.setInt(1, curUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}else{
					//User to Users
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query1)){						
						stmt.setInt(1, targetUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}
			}else{
				//Both
				if(curUserId>0&&targetUserId>0){
					//User to User
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
						DebugLog.d(e);
					}
				}else{
					//Users to User or User to Users
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query0)){
						stmt.setInt(1, curUserId > 0 ? curUserId : targetUserId);
						stmt.setInt(2, curUserId > 0 ? curUserId : targetUserId);						
						stmt.setInt(3, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
							list.add(createLetterByResultSet(rs));
						}
					}catch(SQLException e){
						DebugLog.d(e);
					}
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
			DebugLog.d(e);
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

		Letter letter = new Letter(rs.getInt("letter_Id"),rs.getInt("from_UserId"),rs.getInt("to_UserId"),LetterType.fromInt(rs.getInt("letterType")),fromUser,toUser,
				rs.getString("content"),DateUtility.DateToCalendar(rs.getTimestamp("send_Time")),DateUtility.DateToCalendar(rs.getTimestamp("check_Time")),
				Constants.LetterState.fromInt(rs.getInt("letterState")),rs.getBoolean("historyDeleted"));
		return letter;
	}

	public static ArrayList<User> getLetterUsers(int userId) throws UserNotFoundException{
		ArrayList<User> list = new ArrayList<User>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query = "SELECT * from carpoolDAOLetter where (from_UserId = ? or to_UserId = ?) and letterType = ?";
		User user = null;

		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			stmt.setInt(3, LetterType.user.code);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				user = createUserByResultSet(rs,userId);
				if(!ilist.contains(user.getUserId())){
					list.add(user);
					ilist.add(user.getUserId());
				}
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}

		return list;
	}

	private static User createUserByResultSet(ResultSet rs,int userId) throws SQLException, UserNotFoundException {
		
		int fromUserId = rs.getInt("from_UserId");
		int toUserId = rs.getInt("to_UserId");
		
		
		if(fromUserId==userId&&toUserId==userId){
			//send letter to self
			return CarpoolDaoUser.getUserById(userId);
		}else if(fromUserId!=userId&&toUserId==userId&&fromUserId!=-1){
			return CarpoolDaoUser.getUserById(fromUserId);
		}else if(fromUserId==userId&&toUserId!=userId&&toUserId!=-1){
			return CarpoolDaoUser.getUserById(toUserId);
		}
		return null;
	}

	public static void checkLetter(int userId, int targetUserId){
		String query = "UPDATE carpoolDAOLetter set letterState = ? where ((from_UserId = ? and to_UserId=?)or(from_UserId = ? and to_UserId=?)) and letterState = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, LetterState.read.code);
			stmt.setInt(2, userId);
			stmt.setInt(3, targetUserId);
			stmt.setInt(4, targetUserId);
			stmt.setInt(5, userId);
			stmt.setInt(6, LetterState.unread.code);
			stmt.executeUpdate();			
		}catch(SQLException e){
			DebugLog.d(e);			
		}
	}
}
