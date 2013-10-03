package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import carpool.common.Parser;
import carpool.constants.Constants;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.userSearchState;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.database.DaoUser;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;


public class carpoolDAOMessage{
	
	public static ArrayList<Message> searchMessageSingle(String location, String date,String type) throws UserNotFoundException {
		date = date.split(" ")[0];
		ArrayList<Message> retVal = new ArrayList<Message>();
		String query = "SELECT * from carpoolDAOMessage WHERE location LIKE ? AND (startTime <= ? OR endTime >= ?) AND type LIKE ?;";
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, location);
			stmt.setString(2, date);
			stmt.setString(3, date);
			stmt.setString(4, type);
			ResultSet rs = stmt.executeQuery();
			
				while(rs.next()){
					retVal.add(createMessageByResultSet(rs));
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return retVal;
	}
	
	public static ArrayList<Message> searchMessageRegion(String location,String date, String type) throws UserNotFoundException {
		date = date.split(" ")[0];
		String[] locations = location.split(" ");
		location = locations[0]+" "+locations[1]+" "+locations[2]+" %";
		return searchMessageSingle(location, date, type);
	}
	
	public static Message addMessageToDatabase(Message msg){
		String query = "INSERT INTO carpoolDAOMessage (ownerId,isRoundTrip," +
				"departure_primaryLocation,departure_customLocation,departure_customDepthIndex,departure_Time,departure_seatsNumber,departure_seatsBooked,departure_priceList,arrival_primaryLocation,arrival_customLocation,arrival_customDepthIndex,arrival_Time," +
				"arrival_seatsNumber,arrival_seatsBooked,arrival_priceList,paymentMethod,note,messageType,gender,messageState,creationTime,editTime,historyDeleted,departure_timeSlot,arrival_timeSlot) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
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
		try(Statement stmt = carpoolDAOBasic.getSQLConnection().createStatement()){
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
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
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
	
	public static Message getMessageById(int id) throws MessageNotFoundException,UserNotFoundException{
		String query = "SELECT * FROM carpoolDAOMessage WHERE messageId = ?;";
		Message message = null;
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				message = createMessageByResultSet(rs);
			}else{
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return message;
	}

	protected static Message createMessageByResultSet(ResultSet rs) throws SQLException, UserNotFoundException {
		//User owner;
		//owner = DaoUser.getUserById(rs.getInt("ownerId"));
		Message message = new Message(rs.getInt("messageId"),rs.getInt("ownerId"),null,rs.getBoolean("isRoundTrip"),new LocationRepresentation(rs.getString("departure_primaryLocation"),rs.getString("departure_customLocation"),rs.getInt("departure_customDepthIndex")),
				DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsNumber"),rs.getInt("departure_seatsBooked"),(ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"),new Integer(0)),
				new LocationRepresentation(rs.getString("arrival_primaryLocation"),rs.getString("arrival_customLocation"),rs.getInt("arrival_customDepthIndex")),	DateUtility.DateToCalendar(rs.getTimestamp("arrival_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("arrival_timeSlot")),rs.getInt("arrival_seatsNumber"),rs.getInt("arrival_seatsBooked"),
				(ArrayList<Integer>)Parser.stringToList(rs.getString("arrival_priceList"),new Integer(0)),Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("note"),
				Constants.messageType.fromInt(rs.getInt("messageType")),Constants.gender.fromInt(rs.getInt("gender")),
				Constants.messageState.fromInt(rs.getInt("messageState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
				DateUtility.DateToCalendar(rs.getTimestamp("editTime")),rs.getBoolean("historyDeleted"));
		
		
		return message;
	}
	
	public static ArrayList<Message> getAll() throws UserNotFoundException{
		ArrayList<Message> retVal = new ArrayList<Message>();
		String query = "SELECT * from carpoolDAOMessage;";
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				retVal.add(createMessageByResultSet(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return retVal;
	}

}