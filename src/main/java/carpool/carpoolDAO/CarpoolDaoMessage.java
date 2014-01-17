package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import carpool.common.Parser;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.constants.Constants.messageType;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;


public class CarpoolDaoMessage{

	public static ArrayList<Message> searchMessage(SearchRepresentation SR) throws UserNotFoundException, LocationNotFoundException {
		boolean isRoundTrip = SR.isRoundTrip();
		long departureMatch_Id = SR.getDepartureMatch_Id();		 
		long arrivalMatch_Id = SR.getArrivalMatch_Id();		 
		Calendar departureDate1 = (Calendar) SR.getDepartureDate().clone();
		departureDate1.set(Calendar.HOUR_OF_DAY, 0);
		departureDate1.set(Calendar.MINUTE,0);
		departureDate1.set(Calendar.SECOND,0);
		Calendar departureDate2 = (Calendar) SR.getDepartureDate().clone();
		departureDate2.set(Calendar.HOUR_OF_DAY, 23);
		departureDate2.set(Calendar.MINUTE,59);
		departureDate2.set(Calendar.SECOND,59);
		Calendar arrivalDate1 = (Calendar) SR.getArrivalDate().clone();
		arrivalDate1.set(Calendar.HOUR_OF_DAY, 0);
		arrivalDate1.set(Calendar.MINUTE,0);
		arrivalDate1.set(Calendar.SECOND,0);
		Calendar arrivalDate2 = (Calendar) SR.getArrivalDate().clone();
		arrivalDate2.set(Calendar.HOUR_OF_DAY, 23);
		arrivalDate2.set(Calendar.MINUTE,59);
		arrivalDate2.set(Calendar.SECOND,59);
		messageType targetType = SR.getTargetType();
		//		 System.out.println("dt1: "+DateUtility.toSQLDateTime(departureDate1));
		//		 System.out.println("dt2: "+DateUtility.toSQLDateTime(departureDate2));
		//		 System.out.println("at1: "+DateUtility.toSQLDateTime(arrivalDate1));
		//		 System.out.println("at2: "+DateUtility.toSQLDateTime(arrivalDate2));
		//		 DayTimeSlot departureTimeSlot = SR.getDepartureTimeSlot();
		//		 DayTimeSlot arrivalTimeSlot = SR.getArrivalTimeSlot();
		ArrayList<Message> retVal = new ArrayList<Message>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		//SR.isRoundTrip()==false
		String query = "SELECT * from carpoolDAOMessage WHERE((isRoundTrip NOT LIKE ? AND (departure_seatsNumber >= departure_seatsBooked)AND((departureMatch_Id = ?"+
				" AND arrivalMatch_Id = ? AND departure_Time >= ? AND departure_Time <= ?)OR(arrivalMatch_Id = ? AND departureMatch_Id = ? AND arrival_Time >= ? AND arrival_Time <= ?)))"+
				"OR(isRoundTrip LIKE ? AND (departure_seatsNumber >= departure_seatsBooked) AND departureMatch_Id = ? AND arrivalMatch_Id = ? AND departure_Time >= ?AND departure_Time <= ?)) AND messageState=2";
		//SR.isRoundTrip()==true		
		String query2="SELECT * from carpoolDAOMessage WHERE((isRoundTrip LIKE ? AND ((departureMatch_Id = ?"+
				" AND arrivalMatch_Id = ? AND ((departure_Time >= ? AND departure_Time <= ? AND (departure_seatsNumber >= departure_seatsBooked)) OR (arrival_Time >= ? AND arrival_Time <= ? AND (arrival_seatsNumber >= arrival_seatsBooked))))" +
				"OR(departureMatch_Id = ? AND arrivalMatch_Id = ? AND ((departure_Time >= ? AND departure_Time <= ? AND (departure_seatsNumber >= departure_seatsBooked))OR(arrival_Time >= ? AND arrival_Time <= ? AND (arrival_seatsNumber >= arrival_seatsBooked))))))" +
				"OR(isRoundTrip NOT LIKE ? AND (((departure_seatsNumber >= departure_seatsBooked) AND departureMatch_Id = ? AND arrivalMatch_Id = ? AND departure_Time >= ? AND departure_Time <= ?) OR ((departure_seatsNumber >= departure_seatsBooked) AND "+
				"arrivalMatch_Id = ? AND departureMatch_Id = ? AND departure_Time >=? AND departure_Time <= ?))))AND messageState=2";
		if(targetType.code==2){
			query+=" AND (messageType = 0 or messageType =1 or messageType=?)";
			query2+=" AND (messageType = 0 or messageType =1 or messageType=?)";
		}else{
			query+=" AND messageType = ?";
			query2+=" AND messageType = ?";
		}

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		if(!isRoundTrip){
			try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query);

				stmt.setInt(1, isRoundTrip ? 1 : 0);			
				stmt.setLong(2, departureMatch_Id);							
				stmt.setLong(3, arrivalMatch_Id);	
				stmt.setString(4, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(5, DateUtility.toSQLDateTime(departureDate2));			
				stmt.setLong(6, departureMatch_Id);							
				stmt.setLong(7, arrivalMatch_Id);			
				stmt.setString(8, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(9, DateUtility.toSQLDateTime(departureDate2));			
				stmt.setInt(10, isRoundTrip ? 1 :0);
				stmt.setLong(11, departureMatch_Id);							
				stmt.setLong(12, arrivalMatch_Id);		
				stmt.setString(13, DateUtility.toSQLDateTime(departureDate1));	
				stmt.setString(14, DateUtility.toSQLDateTime(departureDate2));
				stmt.setInt(15,targetType.code);
				rs = stmt.executeQuery();			
				while(rs.next()){	
					ilist = addIds(ilist,rs.getInt("ownerId"));
					retVal.add(createMessagesByResultSetList(rs));
				}
				retVal = getUsersForMessages(ilist,retVal);
			} catch (SQLException e) {
				e.printStackTrace();
				DebugLog.d(e);
			}finally  {
				try{
					if (stmt != null)  stmt.close();  
					if (conn != null)  conn.close(); 
					if (rs != null) rs.close();
				} catch (SQLException e){
					DebugLog.d("Exception when closing stmt, rs and conn");
					DebugLog.d(e);
				}
			} 
		}
		else{
			try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query2)){
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query2);

				stmt.setInt(1, isRoundTrip ? 1 : 0);
				stmt.setLong(2, departureMatch_Id);							
				stmt.setLong(3, arrivalMatch_Id);	
				stmt.setString(4, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(5, DateUtility.toSQLDateTime(departureDate2));
				stmt.setString(6, DateUtility.toSQLDateTime(arrivalDate1));
				stmt.setString(7, DateUtility.toSQLDateTime(arrivalDate2));				
				stmt.setLong(8, arrivalMatch_Id);
				stmt.setLong(9, departureMatch_Id);
				stmt.setString(10, DateUtility.toSQLDateTime(arrivalDate1));
				stmt.setString(11, DateUtility.toSQLDateTime(arrivalDate2));
				stmt.setString(12, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(13, DateUtility.toSQLDateTime(departureDate2));				
				stmt.setInt(14, isRoundTrip ? 1 : 0);
				stmt.setLong(15, departureMatch_Id);
				stmt.setLong(16, arrivalMatch_Id);			
				stmt.setString(17, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(18, DateUtility.toSQLDateTime(departureDate2));
				stmt.setLong(19, departureMatch_Id);
				stmt.setLong(20, arrivalMatch_Id);
				stmt.setString(21, DateUtility.toSQLDateTime(arrivalDate1));
				stmt.setString(22, DateUtility.toSQLDateTime(arrivalDate2));
				stmt.setInt(23,targetType.code);				
				rs = stmt.executeQuery();				
				while(rs.next()){									
					retVal.add(createMessageByResultSet(rs, false));
				}				
			} catch (SQLException e) {
				e.printStackTrace();
				DebugLog.d(e);
			}finally  {
				try{
					if (stmt != null)  stmt.close();  
					if (conn != null)  conn.close(); 
					if (rs != null) rs.close();
				} catch (SQLException e){
					DebugLog.d("Exception when closing stmt, rs and conn");
					DebugLog.d(e);
				}
			} 
		}
		return retVal;
	}


	public static Message addMessageToDatabase(Message msg) throws LocationNotFoundException{	
		msg.setDeparture_Location(CarpoolDaoLocation.addLocationToDatabases(msg.getDeparture_Location()));
		msg.setArrival_Location(CarpoolDaoLocation.addLocationToDatabases(msg.getArrival_Location()));
		msg.setDeparture_Id(msg.getDeparture_Location().getId());
		msg.setArrvial_Id(msg.getArrival_Location().getId());
		String query = "INSERT INTO carpoolDAOMessage (ownerId,isRoundTrip," +
				"departure_Id,departure_Time,departure_seatsNumber,departure_seatsBooked,departure_priceList,arrival_Id,arrival_Time," +
				"arrival_seatsNumber,arrival_seatsBooked,arrival_priceList,paymentMethod,note,messageType,gender,messageState,creationTime,editTime,historyDeleted,departure_timeSlot,arrival_timeSlot,departureMatch_Id,arrivalMatch_Id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, msg.getOwnerId());			
			stmt.setInt(2, msg.isRoundTrip() ? 1:0);
			stmt.setLong(3, msg.getDeparture_Id());
			stmt.setString(4, DateUtility.toSQLDateTime(msg.getDeparture_time()));
			stmt.setInt(5, msg.getDeparture_seatsNumber());
			stmt.setInt(6, msg.getDeparture_seatsBooked());
			stmt.setString(7, Parser.listToString(msg.getDeparture_priceList()));
			stmt.setLong(8, msg.getArrival_Id());
			stmt.setString(9, DateUtility.toSQLDateTime(msg.getArrival_time()));
			stmt.setInt(10, msg.getArrival_seatsNumber());
			stmt.setInt(11, msg.getArrival_seatsBooked());
			stmt.setString(12, Parser.listToString(msg.getArrival_priceList()));
			stmt.setInt(13, msg.getPaymentMethod().code);
			stmt.setString(14, msg.getNote());
			stmt.setInt(15, msg.getType().code);
			stmt.setInt(16, msg.getGenderRequirement().code);
			stmt.setInt(17, msg.getState().code);			
			stmt.setString(18, DateUtility.toSQLDateTime(msg.getCreationTime()));
			stmt.setString(19, DateUtility.toSQLDateTime(msg.getEditTime()));
			stmt.setInt(20, msg.isHistoryDeleted() ? 1:0);
			stmt.setInt(21, msg.getDeparture_timeSlot().code);
			stmt.setInt(22, msg.getArrival_timeSlot().code);
			stmt.setLong(23, msg.getDeparture_Location().getMatch());
			stmt.setLong(24, msg.getArrival_Location().getMatch());
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			msg.setMessageId(rs.getInt(1));							

		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 
				if (rs != null) rs.close();
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 
		return msg;
	}

	public static void deleteMessageFromDatabase(int id) throws MessageNotFoundException{
		String query = "DELETE from WatchList where Message_messageId = '" + id +"'";
		String query2 = "DELETE from carpoolDAOMessage where messageId = '" + id + "'";

		Statement stmt = null;
		Connection conn = null;

		try{//(Statement stmt = CarpoolDaoBasic.getSQLConnection().createStatement()){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.createStatement();

			stmt.addBatch(query);
			stmt.addBatch(query2);
			int resultSet[] = stmt.executeBatch();
			if(resultSet[1]==0){
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 				
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 
	}

	public static void UpdateMessageInDatabase(Message msg) throws MessageNotFoundException{
		msg.setDeparture_Location(CarpoolDaoLocation.addLocationToDatabases(msg.getDeparture_Location()));
		msg.setArrival_Location(CarpoolDaoLocation.addLocationToDatabases(msg.getArrival_Location()));
		msg.setDeparture_Id(msg.getDeparture_Location().getId());
		msg.setArrvial_Id(msg.getArrival_Location().getId());
		String query = "UPDATE carpoolDAOMessage SET isRoundTrip=?,departure_Id=?,departure_Time=?," +
				"departure_seatsNumber=?,departure_seatsBooked=?,departure_priceList=?,arrival_Id=?,arrival_Time=?," +
				"arrival_seatsNumber=?,arrival_seatsBooked=?,arrival_priceList=?,paymentMethod=?,note=?,messageType=?,gender=?,messageState=?,creationTime=?,editTime=?,historyDeleted=?,departure_timeSlot=?,arrival_timeSlot=?,departureMatch_Id=?,arrivalMatch_Id=? WHERE messageId=?";

		PreparedStatement stmt = null;
		Connection conn = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, msg.isRoundTrip() ? 1:0);
			stmt.setLong(2, msg.getDeparture_Id());			
			stmt.setString(3, DateUtility.toSQLDateTime(msg.getDeparture_time()));
			stmt.setInt(4, msg.getDeparture_seatsNumber());
			stmt.setInt(5, msg.getDeparture_seatsBooked());
			stmt.setString(6, Parser.listToString(msg.getDeparture_priceList()));
			stmt.setLong(7, msg.getArrival_Id());			
			stmt.setString(8, DateUtility.toSQLDateTime(msg.getArrival_time()));
			stmt.setInt(9, msg.getArrival_seatsNumber());
			stmt.setInt(10, msg.getArrival_seatsBooked());
			stmt.setString(11, Parser.listToString(msg.getArrival_priceList()));
			stmt.setInt(12, msg.getPaymentMethod().code);
			stmt.setString(13, msg.getNote());
			stmt.setInt(14, msg.getType().code);
			stmt.setInt(15, msg.getGenderRequirement().code);
			stmt.setInt(16, msg.getState().code);			
			stmt.setString(17, DateUtility.toSQLDateTime(msg.getCreationTime()));
			stmt.setString(18, DateUtility.toSQLDateTime(msg.getEditTime()));
			stmt.setInt(19, msg.isHistoryDeleted() ? 1:0);
			stmt.setInt(20, msg.getDeparture_timeSlot().code);
			stmt.setInt(21, msg.getArrival_timeSlot().code);			
			stmt.setLong(22, msg.getDeparture_Location().getMatch());
			stmt.setLong(23, msg.getArrival_Location().getMatch());
			stmt.setInt(24, msg.getMessageId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new MessageNotFoundException();
			} 
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 				
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 
	}

	public static ArrayList<Message> getAllMessages() throws LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOMessage;";
		ArrayList<Message> mlist = new ArrayList<Message>();

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			while(rs.next()){				
				mlist.add(createMessageByResultSet(rs,false));
			}			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 
				if (rs != null) rs.close();
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		}  
		return mlist;
	}

	public static ArrayList<Message> getUserPublishedMessages(int userId){
		ArrayList<Message> mlist = new ArrayList<Message>();
		String query = "SELECT * FROM carpoolDAOMessage where ownerId = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, userId);
			rs = stmt.executeQuery();
			while(rs.next()){
				mlist.add(createMessagesByResultSetList(rs));
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		} catch (LocationNotFoundException e) {			
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 
				if (rs != null) rs.close();
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 

		return mlist;
	}

	protected static ArrayList<Message> getUsersForMessages(ArrayList<Integer> ilist, ArrayList<Message> mlist) throws LocationNotFoundException {
		HashMap<Integer,User> map = new HashMap<Integer,User>();
		map = getUsersHashMap(ilist);		
		for(int i=0;i<mlist.size();i++){
			mlist.get(i).setOwner(map.get(mlist.get(i).getOwnerId()));		
		}
		return mlist;
	}

	public static ArrayList<Integer> addIds(ArrayList<Integer> ilist, int ownerId) {
		if(!ilist.contains(ownerId)){
			ilist.add(ownerId);
		}		
		return ilist;
	}


	public static Message getMessageById(int id) throws MessageNotFoundException,UserNotFoundException, LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOMessage WHERE messageId = ?;";
		Message message = null;

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if(rs.next()){
				message = createMessageByResultSet(rs, true);
			}else{
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 
				if (rs != null) rs.close();
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 
		return message;
	}


	protected static Message createMessageByResultSet(ResultSet rs, boolean shouldAddUser) throws SQLException, UserNotFoundException, LocationNotFoundException {
		User owner = shouldAddUser ? owner = CarpoolDaoUser.getUserById(rs.getInt("ownerId")) : null;
		Location departure_Location = CarpoolDaoLocation.getLocationById(rs.getLong("departure_Id"));
		Location arrival_Location = CarpoolDaoLocation.getLocationById(rs.getLong("arrival_Id"));
		Message message = new Message(rs.getInt("messageId"),rs.getInt("ownerId"),owner,rs.getBoolean("isRoundTrip"),departure_Location,
				DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsNumber"),rs.getInt("departure_seatsBooked"),(ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"),new Integer(0)),
				arrival_Location,DateUtility.DateToCalendar(rs.getTimestamp("arrival_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("arrival_timeSlot")),rs.getInt("arrival_seatsNumber"),rs.getInt("arrival_seatsBooked"),
				(ArrayList<Integer>)Parser.stringToList(rs.getString("arrival_priceList"),new Integer(0)),Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("note"),
				Constants.messageType.fromInt(rs.getInt("messageType")),Constants.gender.fromInt(rs.getInt("gender")),
				Constants.messageState.fromInt(rs.getInt("messageState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
				DateUtility.DateToCalendar(rs.getTimestamp("editTime")),rs.getBoolean("historyDeleted"));


		return message;
	}

	protected static Message createMessagesByResultSetList(ResultSet rs) throws SQLException, LocationNotFoundException{	
		Location departure_Location = CarpoolDaoLocation.getLocationById(rs.getLong("departure_Id"));
		Location arrival_Location = CarpoolDaoLocation.getLocationById(rs.getLong("arrival_Id"));
		Message message = new Message(rs.getInt("messageId"),rs.getInt("ownerId"),null,rs.getBoolean("isRoundTrip"),departure_Location,
				DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsNumber"),rs.getInt("departure_seatsBooked"),(ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"),new Integer(0)),
				arrival_Location,DateUtility.DateToCalendar(rs.getTimestamp("arrival_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("arrival_timeSlot")),rs.getInt("arrival_seatsNumber"),rs.getInt("arrival_seatsBooked"),
				(ArrayList<Integer>)Parser.stringToList(rs.getString("arrival_priceList"),new Integer(0)),Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("note"),
				Constants.messageType.fromInt(rs.getInt("messageType")),Constants.gender.fromInt(rs.getInt("gender")),
				Constants.messageState.fromInt(rs.getInt("messageState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
				DateUtility.DateToCalendar(rs.getTimestamp("editTime")),rs.getBoolean("historyDeleted"));


		return message;
	}	

	public static ArrayList<Message> getRecentMessages() throws LocationNotFoundException{
		ArrayList<Message> retVal = new ArrayList<Message>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		String query = "SELECT * from carpoolDAOMessage ORDER BY creationTime DESC LIMIT " + CarpoolConfig.max_recents + ";";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("ownerId"));
				retVal.add(createMessagesByResultSetList(rs));
			}
			//if(retVal.size()>0){
			retVal = getUsersForMessages(ilist,retVal);
			//}

		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 
				if (rs != null) rs.close();
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 
		//TODO
		for(int i = retVal.size()-1; i>= 0; i--){
			if (retVal.get(i) == null){
				retVal.remove(i);
			}
		}
		return retVal;
	}

	private static HashMap<Integer,User> getUsersHashMap(ArrayList<Integer> list) throws LocationNotFoundException{
		HashMap<Integer,User> map = new HashMap<Integer,User>();
		String query = "SELECT * FROM carpoolDAOUser where ";
		for(int i=0;i<list.size()-1;i++){
			query += "userId = ? OR ";
		}
		query += "userId = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			rs = stmt.executeQuery();
			int ind = 0;
			while(rs.next() && ind<list.size()){
				map.put(rs.getInt("userId"), CarpoolDaoUser.createUserByResultSet(rs));
				ind++;
			}
		}catch(SQLException e){
			DebugLog.d(e);
		}finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 
				if (rs != null) rs.close();
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 

		return map;
	}	

	public static boolean isOpen(Message msg){		
		String ct = DateUtility.toSQLDateTime(DateUtility.getCurTimeInstance());
		String dt = DateUtility.toSQLDateTime(msg.getDeparture_time());
		return (msg.getState().code==carpool.constants.Constants.messageState.open.code && ct.compareTo(dt)<0);
	}	

}
