package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import carpool.common.Parser;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.userSearchState;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;


public class CarpoolDaoMessage{
	
	public static ArrayList<Message> searchMessage(SearchRepresentation SR) throws UserNotFoundException {
		 boolean isRoundTrip = SR.isRoundTrip();
		 LocationRepresentation departureLocation = SR.getDepartureLocation();		 
		 LocationRepresentation arrivalLocation = SR.getArrivalLocation();		 
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
		//SR.isRoundTrip()==false
		String query = "SELECT * from carpoolDAOMessage WHERE((isRoundTrip NOT LIKE ? AND (departure_seatsNumber >= departure_seatsBooked)AND((departure_primaryLocation LIKE ?"+
		"AND arrival_primaryLocation LIKE ? AND departure_Time >= ? AND departure_Time <= ?)OR(arrival_primaryLocation LIKE ? AND departure_primaryLocation LIKE ? AND arrival_Time >= ? AND arrival_Time <= ?)))"+
		"OR(isRoundTrip LIKE ? AND (departure_seatsNumber >= departure_seatsBooked) AND departure_primaryLocation LIKE ? AND arrival_primaryLocation LIKE ? AND departure_Time >= ?AND departure_Time <= ?)) AND messageState=2";
		//SR.isRoundTrip()==true		
		String query2="SELECT * from carpoolDAOMessage WHERE((isRoundTrip LIKE ? AND ((departure_primaryLocation LIKE ?"+
				"AND arrival_primaryLocation LIKE ? AND ((departure_Time >= ? AND departure_Time <= ? AND (departure_seatsNumber >= departure_seatsBooked)) OR (arrival_Time >= ? AND arrival_Time <= ? AND (arrival_seatsNumber >= arrival_seatsBooked))))" +
				"OR(departure_primaryLocation LIKE ? AND arrival_primaryLocation LIKE ? AND ((departure_Time >= ? AND departure_Time <= ? AND (departure_seatsNumber >= departure_seatsBooked))OR(arrival_Time >= ? AND arrival_Time <= ? AND (arrival_seatsNumber >= arrival_seatsBooked))))))" +
				"OR(isRoundTrip NOT LIKE ? AND (((departure_seatsNumber >= departure_seatsBooked) AND departure_primaryLocation LIKE ? AND arrival_primaryLocation LIKE ? AND departure_Time >= ? AND departure_Time <= ?) OR ((departure_seatsNumber >= departure_seatsBooked) AND "+
				"arrival_primaryLocation LIKE ? AND departure_primaryLocation LIKE ? AND departure_Time >=? AND departure_Time <= ?))))AND messageState=2";
		if(targetType.code==2){
			query+=" AND (messageType = 0 or messageType =1 or messageType=?)";
			query2+=" AND (messageType = 0 or messageType =1 or messageType=?)";
		}else{
			query+=" AND messageType = ?";
			query2+=" AND messageType = ?";
		}
		if(!isRoundTrip){
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, isRoundTrip ? 1 : 0);			
			stmt.setString(2, departureLocation.getPrimaryLocationString());							
			stmt.setString(3, arrivalLocation.getPrimaryLocationString());	
			stmt.setString(4, DateUtility.toSQLDateTime(departureDate1));
			stmt.setString(5, DateUtility.toSQLDateTime(departureDate2));			
			stmt.setString(6, departureLocation.getPrimaryLocationString());							
			stmt.setString(7, arrivalLocation.getPrimaryLocationString());				
			stmt.setString(8, DateUtility.toSQLDateTime(departureDate1));
			stmt.setString(9, DateUtility.toSQLDateTime(departureDate2));			
			stmt.setInt(10, isRoundTrip ? 1 :0);
			stmt.setString(11, departureLocation.getPrimaryLocationString());			
			stmt.setString(12, arrivalLocation.getPrimaryLocationString());		
			stmt.setString(13, DateUtility.toSQLDateTime(departureDate1));	
			stmt.setString(14, DateUtility.toSQLDateTime(departureDate2));
			stmt.setInt(15,targetType.code);
			ResultSet rs = stmt.executeQuery();			
				while(rs.next()){									
					retVal.add(createMessageByResultSet(rs, true));
					}			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		}
		else{
			try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query2)){
				stmt.setInt(1, isRoundTrip ? 1 : 0);
				stmt.setString(2, departureLocation.getPrimaryLocationString());		
				stmt.setString(3, arrivalLocation.getPrimaryLocationString());	
				stmt.setString(4, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(5, DateUtility.toSQLDateTime(departureDate2));
				stmt.setString(6, DateUtility.toSQLDateTime(arrivalDate1));
				stmt.setString(7, DateUtility.toSQLDateTime(arrivalDate2));				
				stmt.setString(8, arrivalLocation.getPrimaryLocationString());
				stmt.setString(9, departureLocation.getPrimaryLocationString());
				stmt.setString(10, DateUtility.toSQLDateTime(arrivalDate1));
				stmt.setString(11, DateUtility.toSQLDateTime(arrivalDate2));
				stmt.setString(12, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(13, DateUtility.toSQLDateTime(departureDate2));				
				stmt.setInt(14, isRoundTrip ? 1 : 0);
				stmt.setString(15, departureLocation.getPrimaryLocationString());
				stmt.setString(16, arrivalLocation.getPrimaryLocationString());			
				stmt.setString(17, DateUtility.toSQLDateTime(departureDate1));
				stmt.setString(18, DateUtility.toSQLDateTime(departureDate2));
				stmt.setString(19, departureLocation.getPrimaryLocationString());
				stmt.setString(20, arrivalLocation.getPrimaryLocationString());
				stmt.setString(21, DateUtility.toSQLDateTime(arrivalDate1));
				stmt.setString(22, DateUtility.toSQLDateTime(arrivalDate2));
				stmt.setInt(23,targetType.code);				
				ResultSet rs = stmt.executeQuery();				
					while(rs.next()){									
						retVal.add(createMessageByResultSet(rs, false));
					}				
			} catch (SQLException e) {
				e.printStackTrace();
				DebugLog.d(e.getMessage());
			}
		}
		return retVal;
	}
	
	
	public static Message addMessageToDatabase(Message msg){
		String query = "INSERT INTO carpoolDAOMessage (ownerId,isRoundTrip," +
				"departure_primaryLocation,departure_customLocation,departure_customDepthIndex,departure_Time,departure_seatsNumber,departure_seatsBooked,departure_priceList,arrival_primaryLocation,arrival_customLocation,arrival_customDepthIndex,arrival_Time," +
				"arrival_seatsNumber,arrival_seatsBooked,arrival_priceList,paymentMethod,note,messageType,gender,messageState,creationTime,editTime,historyDeleted,departure_timeSlot,arrival_timeSlot) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, msg.getOwnerId());			
			stmt.setInt(2, msg.isRoundTrip() ? 1:0);
			stmt.setString(3, msg.getDeparture_location().getPrimaryLocationString());
			stmt.setString(4, msg.getDeparture_location().getCustomLocationString());
			stmt.setInt(5, msg.getDeparture_location().getCustomDepthIndex());
			stmt.setString(6, DateUtility.toSQLDateTime(msg.getDeparture_time()));
			stmt.setInt(7, msg.getDeparture_seatsNumber());
			stmt.setInt(8, msg.getDeparture_seatsBooked());
			stmt.setString(9, Parser.listToString(msg.getDeparture_priceList()));
			stmt.setString(10, msg.getArrival_location().getPrimaryLocationString());
		    stmt.setString(11, msg.getArrival_location().getCustomLocationString());
		    stmt.setInt(12, msg.getArrival_location().getCustomDepthIndex());
			stmt.setString(13, DateUtility.toSQLDateTime(msg.getArrival_time()));
			stmt.setInt(14, msg.getArrival_seatsNumber());
			stmt.setInt(15, msg.getArrival_seatsBooked());
			stmt.setString(16, Parser.listToString(msg.getArrival_priceList()));
			stmt.setInt(17, msg.getPaymentMethod().code);
			stmt.setString(18, msg.getNote());
			stmt.setInt(19, msg.getType().code);
			stmt.setInt(20, msg.getGenderRequirement().code);
			stmt.setInt(21, msg.getState().code);			
			stmt.setString(22, DateUtility.toSQLDateTime(msg.getCreationTime()));
			stmt.setString(23, DateUtility.toSQLDateTime(msg.getEditTime()));
			stmt.setInt(24, msg.isHistoryDeleted() ? 1:0);
			stmt.setInt(25, msg.getDeparture_timeSlot().code);
			stmt.setInt(26, msg.getArrival_timeSlot().code);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			msg.setMessageId(rs.getInt(1));
			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return msg;
	}
	
	public static void deleteMessageFromDatabase(int id) throws MessageNotFoundException{
		String query = "DELETE from WatchList where Message_messageId = '" + id +"'";
		String query2 = "DELETE from carpoolDAOMessage where messageId = '" + id + "'";
		try(Statement stmt = CarpoolDaoBasic.getSQLConnection().createStatement()){
			stmt.addBatch(query);
			stmt.addBatch(query2);
			int resultSet[] = stmt.executeBatch();
			if(resultSet[1]==0){
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
	}
	
	public static void UpdateMessageInDatabase(Message msg) throws MessageNotFoundException{
		String query = "UPDATE carpoolDAOMessage SET isRoundTrip=?,departure_primaryLocation=?,departure_customLocation=?,departure_customDepthIndex=?,departure_Time=?," +
				"departure_seatsNumber=?,departure_seatsBooked=?,departure_priceList=?,arrival_primaryLocation=?,arrival_customLocation=?,arrival_customDepthIndex=?,arrival_Time=?," +
				 "arrival_seatsNumber=?,arrival_seatsBooked=?,arrival_priceList=?,paymentMethod=?,note=?,messageType=?,gender=?,messageState=?,creationTime=?,editTime=?,historyDeleted=?,departure_timeSlot=?,arrival_timeSlot=? WHERE messageId=?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, msg.isRoundTrip() ? 1:0);
			stmt.setString(2, msg.getDeparture_location().getPrimaryLocationString());
			stmt.setString(3, msg.getDeparture_location().getCustomLocationString());
			stmt.setInt(4, msg.getDeparture_location().getCustomDepthIndex());
			stmt.setString(5, DateUtility.toSQLDateTime(msg.getDeparture_time()));
			stmt.setInt(6, msg.getDeparture_seatsNumber());
			stmt.setInt(7, msg.getDeparture_seatsBooked());
			stmt.setString(8, Parser.listToString(msg.getDeparture_priceList()));
			stmt.setString(9, msg.getArrival_location().getPrimaryLocationString());
			stmt.setString(10, msg.getArrival_location().getCustomLocationString());
			stmt.setInt(11, msg.getArrival_location().getCustomDepthIndex());
			stmt.setString(12, DateUtility.toSQLDateTime(msg.getArrival_time()));
			stmt.setInt(13, msg.getArrival_seatsNumber());
			stmt.setInt(14, msg.getArrival_seatsBooked());
			stmt.setString(15, Parser.listToString(msg.getArrival_priceList()));
			stmt.setInt(16, msg.getPaymentMethod().code);
			stmt.setString(17, msg.getNote());
			stmt.setInt(18, msg.getType().code);
			stmt.setInt(19, msg.getGenderRequirement().code);
			stmt.setInt(20, msg.getState().code);			
			stmt.setString(21, DateUtility.toSQLDateTime(msg.getCreationTime()));
			stmt.setString(22, DateUtility.toSQLDateTime(msg.getEditTime()));
			stmt.setInt(23, msg.isHistoryDeleted() ? 1:0);
			stmt.setInt(24, msg.getDeparture_timeSlot().code);
			stmt.setInt(25, msg.getArrival_timeSlot().code);
			stmt.setInt(26, msg.getMessageId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new MessageNotFoundException();
			} 
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
	}
	

	public static ArrayList<Message> getAllMessages(){
		String query = "SELECT * FROM carpoolDAOMessage;";
		ArrayList<Message> mlist = new ArrayList<Message>();
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				mlist.add(createMessageByResultSet(rs, false));
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return mlist;
	}
	
	public static Message getMessageById(int id) throws MessageNotFoundException,UserNotFoundException{
		String query = "SELECT * FROM carpoolDAOMessage WHERE messageId = ?;";
		Message message = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				message = createMessageByResultSet(rs, true);
			}else{
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return message;
	}

	
	protected static Message createMessageByResultSet(ResultSet rs, boolean shouldAddUser) throws SQLException, UserNotFoundException {
		User owner = shouldAddUser ? owner = CarpoolDaoUser.getUserById(rs.getInt("ownerId")) : null;
		Message message = new Message(rs.getInt("messageId"),rs.getInt("ownerId"),owner,rs.getBoolean("isRoundTrip"),new LocationRepresentation(rs.getString("departure_primaryLocation"),rs.getString("departure_customLocation"),rs.getInt("departure_customDepthIndex")),
				DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsNumber"),rs.getInt("departure_seatsBooked"),(ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"),new Integer(0)),
				new LocationRepresentation(rs.getString("arrival_primaryLocation"),rs.getString("arrival_customLocation"),rs.getInt("arrival_customDepthIndex")),	DateUtility.DateToCalendar(rs.getTimestamp("arrival_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("arrival_timeSlot")),rs.getInt("arrival_seatsNumber"),rs.getInt("arrival_seatsBooked"),
				(ArrayList<Integer>)Parser.stringToList(rs.getString("arrival_priceList"),new Integer(0)),Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("note"),
				Constants.messageType.fromInt(rs.getInt("messageType")),Constants.gender.fromInt(rs.getInt("gender")),
				Constants.messageState.fromInt(rs.getInt("messageState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
				DateUtility.DateToCalendar(rs.getTimestamp("editTime")),rs.getBoolean("historyDeleted"));
		
		
		return message;
	}
	
	
	public static ArrayList<Message> getRecentMessages(){
		ArrayList<Message> retVal = new ArrayList<Message>();
		String query = "SELECT * from carpoolDAOMessage ORDER BY creationTime DESC LIMIT 10;";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				retVal.add(createMessageByResultSet(rs, true));
			}
		} catch (SQLException | UserNotFoundException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		//TODO
		for(int i = retVal.size()-1; i>= 0; i--){
			if (retVal.get(i) == null){
				retVal.remove(i);
			}
		}
		return retVal;
	}

}
