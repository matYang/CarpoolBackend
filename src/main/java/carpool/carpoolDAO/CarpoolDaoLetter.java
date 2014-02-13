package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
		Connection conn = CarpoolDaoBasic.getSQLConnection();

		if(letter.getFrom_userId()>0){
			User fromUser = CarpoolDaoUser.getUserById(letter.getFrom_userId(),conn);
			letter.setFrom_user(fromUser);
		}else{
			letter.setFrom_userId(-1);
		}
		if(letter.getTo_userId()>0){
			User toUser = CarpoolDaoUser.getUserById(letter.getTo_userId(),conn);
			letter.setTo_user(toUser);
		}else{
			letter.setTo_userId(-1);
		}

		String query = "INSERT INTO carpoolDAOLetter(from_UserId,to_UserId,letterType,content,send_Time,check_Time,letterState,historyDeleted,ownerId)"+
				"VALUES(?,?,?,?,?,?,?,?,?);";
		PreparedStatement stmt = null;			
		ResultSet rs = null;

		try{				
			int ownerId = -1;
			int round = letter.getFrom_userId()==letter.getTo_userId() ? 1 : 0;

			while(round<2){
				if(round==0){
					ownerId = letter.getFrom_userId();					
				}else{
					ownerId = letter.getTo_userId();
				}
				letter.setOwner_id(ownerId);

				stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

				stmt.setInt(1, letter.getFrom_userId());
				stmt.setInt(2, letter.getTo_userId());
				stmt.setInt(3, letter.getType().code);
				stmt.setString(4, letter.getContent());
				stmt.setString(5, DateUtility.toSQLDateTime(letter.getSend_time()));
				stmt.setString(6, DateUtility.toSQLDateTime(letter.getCheck_time()));
				stmt.setInt(7, letter.getState().code);
				stmt.setInt(8,letter.isHistoryDeleted() ? 1 : 0);
				stmt.setInt(9,ownerId);			
				stmt.executeUpdate();
				rs = stmt.getGeneratedKeys();
				rs.next();
				letter.setLetterId(rs.getInt(1));

				round++;
			}

		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 

		return letter;
	}

	public static void updateLetterInDatabases(Letter letter) throws LetterNotFoundException{
		String query = "UPDATE carpoolDAOLetter SET from_UserId=?,to_UserId=?,letterType=?,content=?,send_Time=?,check_Time=?,letterState=?,historyDeleted=?, ownerId=? where letter_Id=?";

		PreparedStatement stmt = null;
		Connection conn = null;				
		try{	
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, letter.getFrom_userId());
			stmt.setInt(2, letter.getTo_userId());
			stmt.setInt(3, letter.getType().code);
			stmt.setString(4, letter.getContent());
			stmt.setString(5, DateUtility.toSQLDateTime(letter.getSend_time()));
			stmt.setString(6, DateUtility.toSQLDateTime(letter.getCheck_time()));
			stmt.setInt(7, letter.getState().code);
			stmt.setInt(8,letter.isHistoryDeleted() ? 1 : 0);
			stmt.setInt(9, letter.getOwnder_id());
			stmt.setInt(10, letter.getLetterId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new LetterNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e);			
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 
	}

	public static Letter getLetterById(int letterId) throws LetterNotFoundException, UserNotFoundException, LocationNotFoundException{
		String query = "SELECT * from carpoolDAOLetter where letter_Id = ?";
		Letter letter = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, letterId);
			rs = stmt.executeQuery();
			if(rs.next()){
				letter = createLetterByResultSet(rs,conn);
			}else{
				throw new LetterNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 

		return letter;
	}

	public static ArrayList<Letter> getAllLetters() throws UserNotFoundException, LocationNotFoundException{
		ArrayList<Letter> list = new ArrayList<Letter>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query = "SELECT * from carpoolDAOLetter";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try{	
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("from_UserId"),rs.getInt("to_UserId"));
				list.add(createLettersByResultSetList(rs));
			}
			if(list.size()>0){
				list =  getUsersForLetters(ilist, list,conn);
			}

		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 

		return list;
	}

	public static  ArrayList<Letter> getUserLetters(int curUserId, int targetUserId, LetterType type, LetterDirection direction) throws UserNotFoundException, LocationNotFoundException, LetterNotFoundException{		
		ArrayList<Letter> list = new ArrayList<Letter>();
		if((curUserId<=0 && targetUserId<=0)||(curUserId<-1 || targetUserId<-1)||curUserId==0||targetUserId==0){
			return list;
		}
		ArrayList<Integer> ilist = new ArrayList<Integer>();		
		int fromUserId;
		int toUserId;
		String query0 = "SELECT * from carpoolDAOLetter where (to_UserId = ? or from_UserId = ?) and letterType = ? and ownerId= ?";
		String query =  "SELECT * from carpoolDAOLetter where to_UserId = ? and letterType = ? and ownerId= ?";
		String query1 = "SELECT * from carpoolDAOLetter where from_UserId = ? and letterType = ? and ownerId=?";
		String query2 = "SELECT * from carpoolDAOLetter where from_UserId=? and to_UserId=? and letterType = ? and ownerId = ?";		
		String query3 = "SELECT * from carpoolDAOLetter where (from_UserId=? and to_UserId=? and ownerId=?)OR(from_UserId=? and to_UserId=? and ownerId = ?) and letterType = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		int set1=-1;
		int set2=-1;
		int set3=-1;
		int set4=-1;
		int set5=-1;
		int set6=-1;
		int set7=-1;
		boolean both = false;
		boolean utu = false;
		boolean bothu = false;
		//System
		if(type.equals(Constants.LetterType.system)){
			try{
				conn = CarpoolDaoBasic.getSQLConnection();		
				set1 = curUserId > 0 ? curUserId : targetUserId;
				set2 = type.code;
				set3 = curUserId;
				if((direction.equals(LetterDirection.inbound)&&curUserId>0)
						||(direction.equals(LetterDirection.outbound)&&curUserId<=0)){
					//inbound and User send to System or outbound and User send to System														
					stmt = conn.prepareStatement(query1);
				}else if((direction.equals(LetterDirection.inbound)&&curUserId<=0)
						||(direction.equals(LetterDirection.outbound)&&curUserId>0)){
					//inbound and System send to User or outbound and System send to User
					stmt = conn.prepareStatement(query);													
				}else {
					//both
					stmt = conn.prepareStatement(query0);
					both = true;
					set2 = set1;
					set3 = type.code;
					set4 = curUserId;
				}					

				stmt.setInt(1, set1);
				stmt.setInt(2, set2);
				stmt.setInt(3, set3);
				if(both){
					stmt.setInt(4, set4);
				}
				rs = stmt.executeQuery();
				while(rs.next()){							
					fromUserId = rs.getInt("from_UserId");
					toUserId = rs.getInt("to_UserId");	
					ilist = addIds(ilist,fromUserId,toUserId);													
					list.add(createLettersByResultSetList(rs));
				}
				list =  getUsersForLetters(ilist, list,conn);
				setLettersRead(list,stmt,conn);
			}catch(SQLException e){
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
			} 
		}
		else{
			//User
			try{
				conn = CarpoolDaoBasic.getSQLConnection();
				if((direction.equals(Constants.LetterDirection.inbound)
						||(direction.equals(Constants.LetterDirection.outbound)))
						&&curUserId>0 && targetUserId>0){
					//inbound User to User or outbound User to User
					stmt = conn.prepareStatement(query2);
					set1 = direction.equals(Constants.LetterDirection.inbound) ? curUserId : targetUserId;
					set2 =direction.equals(Constants.LetterDirection.inbound) ? targetUserId : curUserId;
					set3 = type.code;
					set4 = curUserId;	
					utu = true;
				}else if((direction.equals(Constants.LetterDirection.inbound)&&curUserId<=0)
						||(direction.equals(Constants.LetterDirection.outbound)&&curUserId>0)){
					//inbound Users to User or outbound Users to User
					stmt = conn.prepareStatement(query);
					set1 = direction.equals(Constants.LetterDirection.inbound) ? targetUserId : curUserId;
					set2 = type.code;
					set3 = set1;							
				}else if(direction.equals(Constants.LetterDirection.inbound)
						||direction.equals(Constants.LetterDirection.outbound)){
					//inbound User to Users or outbound User to Users
					stmt = conn.prepareStatement(query1);
					set1 = direction.equals(Constants.LetterDirection.inbound) ? curUserId : targetUserId;
					set2 = type.code;
					set3 = set1;
				}else{
					//Both
					both = true;
					if(curUserId>0&&targetUserId>0){
						//User to User
						stmt = conn.prepareStatement(query3);
						set1 = curUserId;
						set2 = targetUserId;
						set3 = set1;
						set4 = set2;
						set5 = set1;
						set6 = set2;
						set7 = type.code;
						bothu = true;
					}else{
						//Users to User or User to Users
						stmt = conn.prepareStatement(query0);
						set1 = curUserId > 0 ? curUserId : targetUserId;
						set2 = set1;					
						set3 = type.code;
						set4 = set2;

					}
				}				

				stmt.setInt(1, set1);
				stmt.setInt(2, set2);
				stmt.setInt(3, set3);
				if(utu){
					stmt.setInt(4, set4);
				}
				if(both){
					stmt.setInt(4, set4);
					if(bothu){
						stmt.setInt(5, set5);
						stmt.setInt(6, set6);
						stmt.setInt(7, set7);
					}
				}
				rs = stmt.executeQuery();
				while(rs.next()){							
					fromUserId = rs.getInt("from_UserId");
					toUserId = rs.getInt("to_UserId");	
					ilist = addIds(ilist,fromUserId,toUserId);									
					list.add(createLettersByResultSetList(rs));
				}
				list = getUsersForLetters(ilist, list,conn);
				setLettersRead(list,stmt,conn);
			}catch(SQLException e){
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
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

	private static ArrayList<Letter> getUsersForLetters(ArrayList<Integer> ilist,ArrayList<Letter> letters,Connection...connections) throws LocationNotFoundException {
		HashMap<Integer,User> map = new HashMap<Integer,User>();
		map = getHashMap(ilist,connections);		
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
				Constants.LetterState.fromInt(rs.getInt("letterState")),rs.getBoolean("historyDeleted"),rs.getInt("ownerId"));


	}

	public static void deleteLetter(int letterId){
		String query = "DELETE from carpoolDAOLetter where letter_Id = ?";
		PreparedStatement stmt = null;
		Connection conn = null;

		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, letterId);
			stmt.executeUpdate();	
		}catch (SQLException e) {
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 
	}

	private static Letter createLetterByResultSet(ResultSet rs,Connection...connections) throws SQLException, UserNotFoundException, LocationNotFoundException {
		User fromUser = null;
		User toUser = null;

		if(rs.getInt("from_UserId")>0){
			fromUser = CarpoolDaoUser.getUserById(rs.getInt("from_UserId"),connections);
		}

		if(rs.getInt("to_UserId")>0){
			toUser =  CarpoolDaoUser.getUserById(rs.getInt("to_UserId"),connections);
		}

		Letter letter = new Letter(rs.getInt("letter_Id"),rs.getInt("from_UserId"),rs.getInt("to_UserId"),LetterType.fromInt(rs.getInt("letterType")),fromUser,toUser,
				rs.getString("content"),DateUtility.DateToCalendar(rs.getTimestamp("send_Time")),DateUtility.DateToCalendar(rs.getTimestamp("check_Time")),
				Constants.LetterState.fromInt(rs.getInt("letterState")),rs.getBoolean("historyDeleted"),rs.getInt("ownerId"));
		return letter;
	}

	public static ArrayList<User> getLetterUsers(int userId) throws UserNotFoundException, LocationNotFoundException{
		ArrayList<User> list = new ArrayList<User>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query = "SELECT * from carpoolDAOLetter where (from_UserId = ? or to_UserId = ?) and ownerId=? and letterType = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try{	
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			stmt.setInt(3, userId);
			stmt.setInt(4, LetterType.user.code);
			rs = stmt.executeQuery();			
			while(rs.next()){
				int tempId = checkUser(rs,userId);
				if(tempId!=-1 && !ilist.contains(tempId)){
					ilist.add(tempId);
				}
			}
			if(ilist.size()>0){
				list = getLetterUsersByIdList(ilist,conn);
			}			
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 

		return list;
	}

	private static ArrayList<User> getLetterUsersByIdList(ArrayList<Integer> list,Connection...connections) throws LocationNotFoundException{
		ArrayList<User> ulist = new ArrayList<User>();
		String query = "SELECT * FROM carpoolDAOUser where ";
		for(int i=0;i<list.size()-1;i++){
			query += "userId = ? OR ";
		}
		query += "userId = ?";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);

			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			rs = stmt.executeQuery();			
			while(rs.next()){
				ulist.add(CarpoolDaoUser.createUserByResultSet(rs,connections));
			}			
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,connections==null ? true : false);
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
		String query = "UPDATE carpoolDAOLetter set letterState = ? where ((from_UserId = ? and to_UserId=? and ownerId=? )or(from_UserId = ? and to_UserId=? and ownerId=?)) and letterState = ?";
		PreparedStatement stmt = null;
		Connection conn = null;

		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, LetterState.read.code);
			stmt.setInt(2, userId);
			stmt.setInt(3, targetUserId);
			stmt.setInt(4, targetUserId);

			stmt.setInt(5, targetUserId);
			stmt.setInt(6, userId);
			stmt.setInt(7, userId);
			stmt.setInt(8, LetterState.unread.code);
			stmt.executeUpdate();			
		}catch(SQLException e){
			DebugLog.d(e);			
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 
	}

	private static HashMap<Integer,User> getHashMap(ArrayList<Integer> list,Connection...connections) throws LocationNotFoundException{
		HashMap<Integer,User> map = new HashMap<Integer,User>();
		if(list.size()<=0){
			return map;
		}
		String query = "SELECT * FROM carpoolDAOUser where ";
		for(int i=0;i<list.size()-1;i++){
			query += "userId = ? OR ";
		}
		query += "userId = ?";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);		
			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			rs = stmt.executeQuery();
			int ind = 0;
			while(rs.next() && ind<list.size()){
				map.put(rs.getInt("userId"), CarpoolDaoUser.createUserByResultSet(rs,conn));
				ind++;
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,connections==null ? true : false);
		} 

		return map;
	}	

	public static ArrayList<Letter> getUncheckedLettersByUserId(int userId) throws UserNotFoundException, LocationNotFoundException{
		ArrayList<Letter> list = new ArrayList<Letter>();
		String query = "SELECT * from CarpoolDAOLetter where to_UserId =? and ownerId = ? and letterState = ?";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			stmt.setInt(3, LetterState.unread.code);				
			rs = stmt.executeQuery();
			while(rs.next()){
				list.add(createLetterByResultSet(rs,conn));
			}					
		}catch(SQLException e){
			DebugLog.d(e);			
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return list;
	}

	public static void setLettersRead(ArrayList<Letter>list,PreparedStatement stmt,Connection conn) throws SQLException, LetterNotFoundException{	

		String query = "UPDATE CarpoolDAOLetter SET letterState = ? where letter_Id=? ";
		stmt = conn.prepareStatement(query);
		for(int i=0;i<list.size();i++){
			list.get(i).setState(LetterState.read);
			stmt.setInt(1,LetterState.read.code);
			stmt.setInt(2, list.get(i).getLetterId());
			stmt.execute();
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new LetterNotFoundException();
			}
		}

	}

}
