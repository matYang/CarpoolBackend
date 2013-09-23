package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.Constants.messageType;
import carpool.common.Constants.userSearchState;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Location;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;


public class carpoolDAOMessage{
	
	public static ArrayList<Message> searchMessageSingle(String location, String date,String type) {
		date = date.split(" ")[0];
		ArrayList<Message> retVal = new ArrayList<Message>();
		String query = "SELECT * from Message WHERE location LIKE ? AND (startTime <= ? OR endTime >= ?) AND type LIKE ?;";
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
			Common.d(e.getMessage());
		}
		return retVal;
	}
	
	public static ArrayList<Message> searchMessageRegion(String location,String date, String type) {
		date = date.split(" ")[0];
		String[] locations = location.split(" ");
		location = locations[0]+" "+locations[1]+" "+locations[2]+" %";
		return searchMessageSingle(location, date, type);
	}
	
	public static Message addMessageToDatabase(Message msg){
		String query = "INSERT INTO Message (messageId,ownerId,isRoundTrip," +
				"departure_Location,departure_Time,departure_seatsNumber,departure_seatsBooked,departure_priceList,arrival_Location,arrival_Time," +
				"arrival_seatsNumber,arrival_seatsBooked,arrival_priceList,paymentMethod,note,messageType,gender,messageState,creationTime,editTime,historyDeleted) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = carpoolDAOBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, msg.getMessageId());
			stmt.setInt(2, msg.ownerId());
			stmt.setInt(3, msg.isRoundTrip() ? 1:0);
			stmt.setString(4, msg.getDeparture_Location().toString());
			stmt.setString(5, Common.toSQLDateTime(msg.getDeparture_Time()));
			stmt.setInt(6, msg.getDeparture_seatsNumber());
			stmt.setInt(7, msg.getDeparture_seatsBooked());
			stmt.setString(8, msg.getDeparture_priceList());
			stmt.setString(9, msg.getArrival_Location().toString());
			stmt.setString(10, Common.toSQLDateTime(msg.getArrival_Time()));
			stmt.setInt(11, msg.getArrival_seatsNumber());
			stmt.setInt(12, msg.getArrival_seatsBooked());
			stmt.setString(13, msg.getArrival_priceList());
			stmt.setInt(14, msg.getPaymentMethod().code);
			stmt.setString(15, msg.getNote());
			stmt.setInt(16, msg.getMessageType().code);
			stmt.setInt(17, msg.getGender().code);
			stmt.setInt(18, msg.getMessageState().code);			
			stmt.setString(19, Common.toSQLDateTime(msg.getCreationTime()));
			stmt.setString(20, Common.toSQLDateTime(msg.getEditTime()));
			stmt.setInt(21, msg.isHistoryDeleted() ? 1:0);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			//msg.setMessageId(rs.getInt(1));
		}catch(SQLException e){
			Common.d(e.getMessage());
		}
		return msg;
	}
	
	public static void deleteMessageFromDatabase(int id) throws MessageNotFoundException{
		String query = "DELETE from WatchList where Message_messageId = '" + id +"'";
		String query2 = "DELETE from Message where messageId = '" + id + "'";
		try(Statement stmt = DaoBasic.getSQLConnection().createStatement()){
			stmt.addBatch(query);
			stmt.addBatch(query2);
			int resultSet[] = stmt.executeBatch();
			if(resultSet[1]==0){
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			Common.d(e.getMessage());
		}
	}
	
	public static void UpdateMessageInDatabase(Message msg) throws MessageNotFoundException{
		String query = "UPDATE Message SET ownerImgPath=?,ownerName=?,ownerLevel=?,ownerAverageScore=?,ownerPhone=?,ownerEmail=?,ownerQq=?," +
				"paymentMethod=?,location=?,startTime=?,endTime=?,note=?,type=?,genderRequirement=?,state=?,price=?,active=?,historyDeleted=?," +
				"creationTime=? WHERE messageId=?";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, msg.getOwnerImgPath());
			stmt.setString(2, msg.getOwnerName());
			stmt.setInt(3, msg.getOwnerLevel());
			stmt.setInt(4, msg.getOwnerAverageScore());
			stmt.setString(5, msg.getOwnerPhone());
			stmt.setString(6, msg.getOwnerEmail());
			stmt.setString(7, msg.getOwnerQq());
			stmt.setInt(8, msg.getPaymentMethod().code);
			stmt.setString(9, msg.getLocation().toString());
			stmt.setString(10, Common.toSQLDateTime(msg.getStartTime()));
			stmt.setString(11, Common.toSQLDateTime(msg.getEndTime()));
			stmt.setString(12, msg.getNote());
			stmt.setInt(13, msg.getType().code);
			stmt.setInt(14, msg.getGenderRequirement().code);
			stmt.setInt(15, msg.getState().code);
			stmt.setInt(16, msg.getPrice());
			stmt.setInt(17, msg.isActive() ? 1:0);
			stmt.setInt(18, msg.isHistoryDeleted() ? 1:0);
			stmt.setString(19, Common.toSQLDateTime(msg.getCreationTime()));
			stmt.setInt(20, msg.getMessageId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			Common.d(e.getMessage());
		}
	}
	
	public static Message getMessageById(int id) throws MessageNotFoundException{
		String query = "SELECT * FROM Message WHERE messageId = ?;";
		Message message = null;
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				message = createMessageByResultSet(rs);
			}else{
				throw new MessageNotFoundException();
			}
		}catch(SQLException e){
			Common.d(e.getMessage());
		}
		return message;
	}

	protected static Message createMessageByResultSet(ResultSet rs) throws SQLException {
		Message message;
		message = new Message(rs.getInt("messageId"),rs.getInt("ownerId"),rs.getString("ownerImgPath"),rs.getString("ownerName"),
				rs.getInt("ownerLevel"),rs.getInt("ownerAverageScore"),rs.getString("ownerPhone"),rs.getString("ownerEmail"),
				rs.getString("ownerQq"),Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),new Location(rs.getString("location")),
				Common.DateToCalendar(rs.getTimestamp("startTime")),Common.DateToCalendar(rs.getTimestamp("endTime")),rs.getString("note"),
				Constants.messageType.fromInt(rs.getInt("type")),Constants.gender.fromInt(rs.getInt("genderRequirement")),
				Constants.messageState.fromInt(rs.getInt("state")),rs.getInt("price"),rs.getBoolean("active"),
				rs.getBoolean("historyDeleted"),new ArrayList<Transaction>(),Common.DateToCalendar(rs.getTimestamp("creationTime")));
		return message;
	}
	
	public static ArrayList<Message> getAll() {
		ArrayList<Message> retVal = new ArrayList<Message>();
		String query = "SELECT * from Message;";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				retVal.add(createMessageByResultSet(rs));
			}
		} catch (SQLException e) {
			Common.d(e.getMessage());
		}
		return retVal;
	}

}