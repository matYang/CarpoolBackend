package carpool.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.model.Notification;



public class DaoNotification {

	public static Notification addNotificationToDatabase(Notification n){
		String query = "INSERT INTO Notification (notificationType,notificationEvent,initUserId,initUserName,messageId,transcationId," +
				"targetUserId,summary,creationTime,checked,historyDeleted) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, n.getNotificationType().code);
			stmt.setInt(2, n.getNotificationEvent().code);
			stmt.setInt(3, n.getInitUserId());
			stmt.setString(4, n.getInitUserName());
			stmt.setInt(5, n.getMessageId());
			stmt.setInt(6, n.getTranscationId());
			stmt.setInt(7,n.getTargetUserId());
			stmt.setString(8, n.getSummary());
			stmt.setString(9,DateUtility.toSQLDateTime(n.getCreationTime()));
			stmt.setBoolean(10, n.isChecked());
			stmt.setBoolean(11, n.isHistoryDeleted());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			n.setNotificationId(rs.getInt(1));
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
			return null;
		}
		return n;
	}

	public static ArrayList<Notification> addNotificationToDatabase(ArrayList<Notification> notifications){
		String query = "INSERT INTO Notification (notificationType,notificationEvent,initUserId,initUserName,messageId,transcationId," +
				"targetUserId,summary,creationTime,checked,historyDeleted) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			for(Notification n : notifications){
				stmt.setInt(1, n.getNotificationType().code);
				stmt.setInt(2, n.getNotificationEvent().code);
				stmt.setInt(3, n.getInitUserId());
				stmt.setString(4, n.getInitUserName());
				stmt.setInt(5, n.getMessageId());
				stmt.setInt(6, n.getTranscationId());
				stmt.setInt(7,n.getTargetUserId());
				stmt.setString(8, n.getSummary());
				stmt.setString(9,DateUtility.toSQLDateTime(n.getCreationTime()));
				stmt.setBoolean(10, n.isChecked());
				stmt.setBoolean(11, n.isHistoryDeleted());
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys();
				rs.next();
				n.setNotificationId(rs.getInt(1));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
			return null;
		}
		return notifications;
	}

	public static void deleteNotificationFromDatabase(int id) throws NotificationNotFoundException{
		String query = "DELETE from Notification where notificationId = ?";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			if(stmt.executeUpdate()==0){
				throw new NotificationNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}		
	}

	public static void updateNotificationToDatabase(Notification n) throws NotificationNotFoundException{
		String query = "UPDATE Notification SET notificationType=?,notificationEvent=?,initUserId=?,initUserName=?,messageId=?,transcationId=?," +
				"targetUserId=?,summary=?,creationTime=?,checked=?,historyDeleted=? WHERE notificationId=?";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, n.getNotificationType().code);
			stmt.setInt(2, n.getNotificationEvent().code);
			stmt.setInt(3, n.getInitUserId());
			stmt.setString(4, n.getInitUserName());
			stmt.setInt(5, n.getMessageId());
			stmt.setInt(6, n.getTranscationId());
			stmt.setInt(7,n.getTargetUserId());
			stmt.setString(8, n.getSummary());
			stmt.setString(9,DateUtility.toSQLDateTime(n.getCreationTime()));
			stmt.setBoolean(10, n.isChecked());
			stmt.setBoolean(11, n.isHistoryDeleted());
			stmt.setInt(12, n.getNotificationId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new NotificationNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
	}

	public static ArrayList<Notification> getALL(){
		String query = "SELECT * FROM Notification;";
		ArrayList<Notification> retVal = new ArrayList<Notification>();
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				retVal.add(createNotificationByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return retVal;
	}

	public static Notification getNotificationById(int id) throws NotificationNotFoundException{
		String query = "SELECT * FROM Notification WHERE notificationId = ?;";
		Notification n = null;
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				n = createNotificationByResultSet(rs);
			}else{
				throw new NotificationNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return n;		
	}


	protected static Notification createNotificationByResultSet(ResultSet rs) throws SQLException {
		Notification n;
		n = new Notification(rs.getInt("notificationId"), Constants.notificationType.fromInt(rs.getInt("notificationType")),
				Constants.notificationEvent.fromInt(rs.getInt("notificationEvent")), rs.getInt("initUserId"),
				rs.getString("initUserName"), rs.getInt("messageId"), rs.getInt("transcationId"),
				rs.getInt("targetUserId"), rs.getString("summary"), DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),
				rs.getBoolean("checked"), rs.getBoolean("historyDeleted"));
		return n;
	}
}
