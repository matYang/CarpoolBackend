package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.notification.NotificationNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;
import carpool.model.User;

public class CarpoolDaoNotification {

	public static Notification addNotificationToDatabase(Notification notification){
		String query="INSERT INTO carpoolDAONotification(target_UserId,origin_UserId,origin_MessageId,origin_TransactionId,notificationState,historyDeleted,creationTime,notificationEvent)values(?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, notification.getTargetUserId());
			stmt.setInt(2, notification.getInitUserId());
			stmt.setInt(3, notification.getMessageId());
			stmt.setInt(4, notification.getTransactionId());
			stmt.setInt(5, notification.getState().code);
			stmt.setInt(6, notification.isHistoryDeleted() ? 1 : 0);
			stmt.setString(7, DateUtility.toSQLDateTime(notification.getCreationTime()));
			stmt.setInt(8, notification.getNotificationEvent().code);
			stmt.executeUpdate();	 
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			notification.setNotificationId(rs.getInt(1));
		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return notification;
	}
	
	public static void updateNotificationInDatabase(Notification notification) throws NotificationNotFoundException{
		String query = "UPDATE carpoolDAONotification SET target_UserId=?,origin_UserId=?,origin_MessageId=?,origin_TransactionId=?,notificationState = ?,historyDeleted = ?,creationTime = ?,notificationEvent=? where notification_Id =?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){	
			stmt.setInt(1, notification.getTargetUserId());
			stmt.setInt(2, notification.getInitUserId());
			stmt.setInt(3, notification.getMessageId());
			stmt.setInt(4, notification.getTransactionId());
			stmt.setInt(5, notification.getState().code);
			stmt.setInt(6, notification.isHistoryDeleted() ? 1 : 0);
			stmt.setString(7, DateUtility.toSQLDateTime(notification.getCreationTime()));
			stmt.setInt(8, notification.getNotificationEvent().code);
			stmt.setInt(9, notification.getNotificationId());
			stmt.executeUpdate();	 			
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new NotificationNotFoundException();
			} 
			
		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}		
		
	}
	
	public static void deleteNotification(Notification notification) throws NotificationNotFoundException{
		String query = "UPDATE carpoolDAONotification SET historyDeleted = 1 where notification_Id =?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){				
			stmt.setInt(1, notification.getNotificationId());
			stmt.executeUpdate();	 			
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new NotificationNotFoundException();
			} 
			
		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}	
		
	}
	
  public static ArrayList<Notification> getAllNotifications() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException{
	  ArrayList<Notification> list = new ArrayList<Notification>();
	  String query = "select * from carpoolDAONotification";
	  try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				list.add(createNotificationByResultSet(rs));
			}
	  }catch(SQLException e){
		  e.printStackTrace();
	  }
	  	  
	  return list;	
}
  
  public static ArrayList<Notification> getByUserId(int userId) throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException{
	  ArrayList<Notification> list = new ArrayList<Notification>();
	  String query = "select * from carpoolDAONotification where target_UserId = ? AND historyDeleted = 0;";
	  try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
		   stmt.setInt(1,userId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				list.add(createNotificationByResultSet(rs));
			}
	  }catch(SQLException e){
		  e.printStackTrace();
	  }
	  return list;
	  
  }

private static Notification createNotificationByResultSet(ResultSet rs) throws SQLException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException {
	Message msg = rs.getInt("origin_MessageId")==-1 ? null : CarpoolDaoMessage.getMessageById(rs.getInt("origin_MessageId"));
	User oriusr =  rs.getInt("origin_UserId")==-1 ? null : CarpoolDaoUser.getUserById(rs.getInt("origin_UserId"));
	Transaction transaction = rs.getInt("origin_TransactionId")==-1 ? null : CarpoolDaoTransaction.getTransactionById(rs.getInt("origin_TransactionId"));
	Notification notification = new Notification(rs.getInt("notification_Id"), Constants.NotificationEvent.fromInt(rs.getInt("notificationEvent")),rs.getInt("target_UserId"),
			rs.getInt("origin_UserId"),rs.getInt("origin_MessageId"),rs.getInt("origin_TransactionId"),Constants.NotificationState.fromInt(rs.getInt("notificationState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));
   notification.setMessage(msg);
   notification.setInitUser(oriusr);
   notification.setTransaction(transaction);
   return notification;
}




}
