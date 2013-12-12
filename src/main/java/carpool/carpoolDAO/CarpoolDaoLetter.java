package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.LetterDirection;
import carpool.constants.Constants.LetterState;
import carpool.constants.Constants.LetterType;
import carpool.exception.letter.LetterNotFoundException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Letter;
import carpool.model.User;

public class CarpoolDaoLetter {

	public static Letter addLetterToDatabases(Letter letter) throws UserNotFoundException, LocationNotFoundException{
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

	public static Letter getLetterById(int letterId) throws LetterNotFoundException, UserNotFoundException, LocationNotFoundException{
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

	public static ArrayList<Letter> getAllLetters() throws UserNotFoundException, LocationNotFoundException{
		ArrayList<Letter> list = new ArrayList<Letter>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query = "SELECT * from carpoolDAOLetter";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("from_UserId"),rs.getInt("to_UserId"));
				list.add(createLettersByResultSetList(rs));
			}
			if(list.size()>0){
				list =  getUsersForLetters(ilist, list);
			}
			
		}catch(SQLException e){
			DebugLog.d(e);
		}

		return list;
	}

	public static  ArrayList<Letter> getUserLetters(int curUserId, int targetUserId, LetterType type, LetterDirection direction) throws UserNotFoundException, LocationNotFoundException{		
		ArrayList<Letter> list = new ArrayList<Letter>();
		if((curUserId<=0 && targetUserId<=0)||(curUserId<-1 || targetUserId<-1)||curUserId==0||targetUserId==0){
			return list;
		}
		ArrayList<Integer> ilist = new ArrayList<Integer>();		
		int fromUserId;
		int toUserId;
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);													
							list.add(createLettersByResultSetList(rs));
						}
						list =  getUsersForLetters(ilist, list);
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}else{//System send to User
					try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){		
						stmt.setInt(1, targetUserId);
						stmt.setInt(2, type.code);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){							
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);													
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);													
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);												
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
						fromUserId = rs.getInt("from_UserId");
						toUserId = rs.getInt("to_UserId");	
						ilist = addIds(ilist,fromUserId,toUserId);												
						list.add(createLettersByResultSetList(rs));
					}
					list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);									
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);												
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);												
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);												
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);										
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);									
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);											
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
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
							fromUserId = rs.getInt("from_UserId");
							toUserId = rs.getInt("to_UserId");	
							ilist = addIds(ilist,fromUserId,toUserId);
							list.add(createLettersByResultSetList(rs));
						}
						list = getUsersForLetters(ilist, list);
					}catch(SQLException e){
						DebugLog.d(e);
					}
				}
			}
		}

		return list;
	}

	private static ArrayList<Integer> addIds(ArrayList<Integer>ilist,int from,int to){
		if(from!=-1 && !ilist.contains(from)){
			ilist.add(from);
		}
		if(to!=-1 && !ilist.contains(to)){
			ilist.add(to);
		}
		return ilist;
	}
	private static ArrayList<Letter> getUsersForLetters(ArrayList<Integer> ilist,ArrayList<Letter> letters) throws LocationNotFoundException {
		HashMap<Integer,User> map = new HashMap<Integer,User>();
		map = getHashMap(ilist);		
		for(int i=0;i<letters.size();i++){
			if(letters.get(i).getFrom_userId()!=-1){
				letters.get(i).setFrom_user(map.get(letters.get(i).getFrom_userId()));
			}
			if(letters.get(i).getTo_userId()!=-1){
				letters.get(i).setTo_user(map.get(letters.get(i).getTo_userId()));
			}
		}

		return letters;

	}

	private static Letter createLettersByResultSetList(ResultSet rs) throws SQLException {
		return 	 new Letter(rs.getInt("letter_Id"),rs.getInt("from_UserId"),rs.getInt("to_UserId"),LetterType.fromInt(rs.getInt("letterType")),null,null,
				rs.getString("content"),DateUtility.DateToCalendar(rs.getTimestamp("send_Time")),DateUtility.DateToCalendar(rs.getTimestamp("check_Time")),
				Constants.LetterState.fromInt(rs.getInt("letterState")),rs.getBoolean("historyDeleted"));


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

	private static Letter createLetterByResultSet(ResultSet rs) throws SQLException, UserNotFoundException, LocationNotFoundException {
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

	public static ArrayList<User> getLetterUsers(int userId) throws UserNotFoundException, LocationNotFoundException{
		ArrayList<User> list = new ArrayList<User>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query = "SELECT * from carpoolDAOLetter where (from_UserId = ? or to_UserId = ?) and letterType = ?";

		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			stmt.setInt(3, LetterType.user.code);
			ResultSet rs = stmt.executeQuery();			
			while(rs.next()){
				int tempId = checkUser(rs,userId);
				if(tempId!=-1 && !ilist.contains(tempId)){
					ilist.add(tempId);
				}
			}
			if(ilist.size()>0){
				list = getLetterUsersByIdList(ilist);
			}			
		}catch(SQLException e){
			DebugLog.d(e);
		}

		return list;
	}

	private static ArrayList<User> getLetterUsersByIdList(ArrayList<Integer> list) throws LocationNotFoundException{
		ArrayList<User> ulist = new ArrayList<User>();
		String query = "SELECT * FROM carpoolDAOUser where ";
		for(int i=0;i<list.size()-1;i++){
			query += "userId = ? OR ";
		}
		query += "userId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			ResultSet rs = stmt.executeQuery();			
			while(rs.next()){
				ulist.add(CarpoolDaoUser.createUserByResultSet(rs));
			}			
		}catch(SQLException e){
			DebugLog.d(e);
		}
		return ulist;
	}	

	private static int checkUser(ResultSet rs,int userId) throws SQLException{
		int fromUserId = rs.getInt("from_UserId");
		int toUserId = rs.getInt("to_UserId");

		if(fromUserId==-1 || toUserId ==-1){
			return -1;
		}

		if(fromUserId==userId&&toUserId==userId){			
			return userId;
		}else if(fromUserId!=userId&&toUserId==userId){
			return fromUserId;
		}else if(fromUserId==userId&&toUserId!=userId){
			return toUserId;
		}
		return-1; 
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
	
	private static HashMap<Integer,User> getHashMap(ArrayList<Integer> list) throws LocationNotFoundException{
		HashMap<Integer,User> map = new HashMap<Integer,User>();
		String query = "SELECT * FROM carpoolDAOUser where ";
		for(int i=0;i<list.size()-1;i++){
			query += "userId = ? OR ";
		}
		query += "userId = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			ResultSet rs = stmt.executeQuery();
			int ind = 0;
			while(rs.next() && ind<list.size()){
				map.put(rs.getInt("userId"), CarpoolDaoUser.createUserByResultSet(rs));
				ind++;
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}

		return map;
	}	
}