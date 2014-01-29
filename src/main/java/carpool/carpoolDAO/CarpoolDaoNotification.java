package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.NotificationState;
import carpool.constants.Constants.NotificationStateChangeActon;
import carpool.exception.location.LocationNotFoundException;
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
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String query="INSERT INTO carpoolDAONotification(target_UserId,origin_UserId,origin_MessageId,origin_TransactionId,notificationState,historyDeleted,creationTime,notificationEvent)values(?,?,?,?,?,?,?,?)";
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, notification.getTargetUserId());
			stmt.setInt(2, notification.getInitUserId());
			stmt.setInt(3, notification.getMessageId());
			stmt.setInt(4, notification.getTransactionId());
			stmt.setInt(5, notification.getState().code);
			stmt.setInt(6, notification.isHistoryDeleted() ? 1 : 0);
			stmt.setString(7, DateUtility.toSQLDateTime(notification.getCreationTime()));
			stmt.setInt(8, notification.getNotificationEvent().code);
			stmt.executeUpdate();	 
			rs = stmt.getGeneratedKeys();
			rs.next();
			notification.setNotificationId(rs.getInt(1));
		}
		catch(SQLException e){
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
		return notification;
	}

	public static ArrayList<Notification> addNotificationsToDatabase(ArrayList<Notification> notifications){
		String query="INSERT INTO carpoolDAONotification(target_UserId,origin_UserId,origin_MessageId,origin_TransactionId,notificationState,historyDeleted,creationTime,notificationEvent)values(?,?,?,?,?,?,?,?)";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			for(Notification n:notifications){
				stmt.setInt(1, n.getTargetUserId());
				stmt.setInt(2, n.getInitUserId());
				stmt.setInt(3, n.getMessageId());
				stmt.setInt(4, n.getTransactionId());
				stmt.setInt(5, n.getState().code);
				stmt.setInt(6, n.isHistoryDeleted() ? 1 : 0);
				stmt.setString(7, DateUtility.toSQLDateTime(n.getCreationTime()));
				stmt.setInt(8, n.getNotificationEvent().code);
				stmt.executeUpdate();	 
				rs = stmt.getGeneratedKeys();
				rs.next();
				n.setNotificationId(rs.getInt(1));
			}
		}
		catch(SQLException e){
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
		return notifications;

	}
	public static void updateNotificationInDatabase(Notification notification) throws NotificationNotFoundException{
		String query = "UPDATE carpoolDAONotification SET target_UserId=?,origin_UserId=?,origin_MessageId=?,origin_TransactionId=?,notificationState = ?,historyDeleted = ?,creationTime = ?,notificationEvent=? where notification_Id =?";
		PreparedStatement stmt = null;
		Connection conn = null;

		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){	
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, notification.getTargetUserId());
			stmt.setInt(2, notification.getInitUserId());
			stmt.setInt(3, notification.getMessageId());
			stmt.setInt(4, notification.getTransactionId());
			stmt.setInt(5, notification.getState().code);
			stmt.setInt(6, notification.isHistoryDeleted() ? 1 : 0);
			stmt.setString(7, DateUtility.toSQLDateTime(notification.getCreationTime()));
			stmt.setInt(8, notification.getNotificationEvent().code);
			stmt.setInt(9, notification.getNotificationId());			 			
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new NotificationNotFoundException();
			} 

		}
		catch(SQLException e){
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

	public static Notification getNotificationById(int notificationId) throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		String query="select * from carpoolDAONotification where notification_Id=?";
		Notification notification = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, notificationId);
			rs = stmt.executeQuery();
			if(rs.next()){
				notification = createNotificationByResultSet(rs,"ForOneNotification");
			}
		}catch(SQLException e){
			e.printStackTrace();
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
		return notification;
	}

	public static void deleteNotification(int notificationId) throws NotificationNotFoundException{
		String query = "UPDATE carpoolDAONotification SET historyDeleted = 1 where notification_Id =?";
		PreparedStatement stmt = null;
		Connection conn = null;

		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){				
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, notificationId);				 			
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new NotificationNotFoundException();
			} 

		}
		catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}	finally  {
			try{
				if (stmt != null)  stmt.close();  
				if (conn != null)  conn.close(); 				
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
		} 

	}

	public static ArrayList<Notification> getAllNotifications() throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		ArrayList<Notification> list = new ArrayList<Notification>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();
		ArrayList<Integer> tlist = new ArrayList<Integer>();
		String query = "select * from carpoolDAONotification";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("origin_UserId"));
				milist = addIds(milist,rs.getInt("origin_MessageId"));
				tlist = addIds(tlist,rs.getInt("origin_TransactionId"));
				list.add(createNotificationByResultSet(rs));
			}
			list = FillNotification(ilist,milist,tlist,list);
		}catch(SQLException e){
			e.printStackTrace();
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

		return list;	
	}

	private static ArrayList<Notification> FillNotification(
			ArrayList<Integer> ilist, ArrayList<Integer> milist,
			ArrayList<Integer> tlist, ArrayList<Notification> list) throws LocationNotFoundException {
		HashMap<Integer,User> userMap = new HashMap<Integer,User>();
		HashMap<Integer,Message> msgMap = new HashMap<Integer,Message>();
		HashMap<Integer,Transaction> tranMap = new HashMap<Integer,Transaction>();

		if(ilist.size()>0){
			userMap = CarpoolDaoTransaction.getUsersHashMap(ilist);
		}

		if(milist.size()>0){
			msgMap = CarpoolDaoTransaction.getMsgHashMap(milist);
		}		

		if(tlist.size()>0){
			tranMap = getTranMap(tlist);
		}

		for(int i=0;i<list.size();i++){
			list.get(i).setInitUser(userMap.get(list.get(i).getInitUserId()));
			list.get(i).setMessage(msgMap.get(list.get(i).getMessageId()));
			list.get(i).setTransaction(tranMap.get(list.get(i).getTransactionId()));
		}
		return list;	

	}	

	private static HashMap<Integer,Transaction> getTranMap(ArrayList<Integer> list) throws LocationNotFoundException{
		HashMap<Integer,Transaction> map = new HashMap<Integer,Transaction>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		String query = "SELECT * FROM carpoolDAOTransaction where ";
		for(int i=0;i<list.size()-1;i++){
			query += "transaction_Id = ? OR ";
		}
		query += "transaction_Id = ?";

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

			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("provider_Id"));
				milist = addIds(milist,rs.getInt("customer_Id"));
				tlist.add(CarpoolDaoTransaction.createTransactionByResultSet(rs));
			}
			CarpoolDaoTransaction.fillTransactions(ilist, milist, tlist);
			int ind=0;
			while(ind<tlist.size()){
				map.put(tlist.get(ind).getTransactionId(), tlist.get(ind));
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


	public static void modifyNotificationByIdList(ArrayList<Integer> idList, int userId, NotificationStateChangeActon action) throws NotificationNotFoundException{
		String query;
		PreparedStatement stmt = null;
		Connection conn = null;

		if(idList.size()<=0)return;

		if(action == NotificationStateChangeActon.check){
			query = "UPDATE carpoolDAONotification SET notificationState = ? where (target_UserId = ? and notification_Id = ?)";
		}else if(action == NotificationStateChangeActon.delete){
			query = "DELETE from carpoolDAONotification  where (target_UserId = ? and notification_Id = ?) ";
		}else return;

		for(int i = 1;i < idList.size();i++){
			query += " or (target_UserId = ? and notification_Id = ?)";
		}

		if(action == NotificationStateChangeActon.check){
			try {		
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, NotificationState.read.code);
				for(int k = 0;k < idList.size()*2;k += 2){
					stmt.setInt(k+2, userId);
					stmt.setInt(k+3, idList.get(k/2));
				}
				int recordsAffected = stmt.executeUpdate();
				if(recordsAffected==0){
					throw new NotificationNotFoundException();
				} 
			} catch (SQLException e) {				
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

		}else{
			try {		
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query);				
				for(int k = 0;k < idList.size()*2;k += 2){
					stmt.setInt(k+1, userId);
					stmt.setInt(k+2, idList.get(k/2));
				}
				int recordsAffected = stmt.executeUpdate();
				if(recordsAffected==0){
					throw new NotificationNotFoundException();
				} 
			} catch (SQLException e) {				
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

	}

	private static ArrayList<Integer> addIds(ArrayList<Integer> ilist, int id) {		
		if(id !=-1 && !ilist.contains(id)){
			ilist.add(id);
		}
		return ilist;
	}

	public static ArrayList<Notification> getByUserId(int userId, boolean onlyGetUnchecked) throws MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException{
		ArrayList<Notification> list = new ArrayList<Notification>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();
		ArrayList<Integer> tlist = new ArrayList<Integer>();
		String query = onlyGetUnchecked ? "select * from carpoolDAONotification where target_UserId = ? AND notificationState = 0 AND historyDeleted = 0;" : "select * from carpoolDAONotification where target_UserId = ? AND historyDeleted = 0;";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{//(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1,userId);
			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("origin_UserId"));
				milist = addIds(milist,rs.getInt("origin_MessageId"));
				tlist = addIds(tlist,rs.getInt("origin_TransactionId"));
				list.add(createNotificationByResultSet(rs));
			}

			list = FillNotification(ilist,milist,tlist,list);

		}catch(SQLException e){
			e.printStackTrace();
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
		return list;

	}

	private static Notification createNotificationByResultSet(ResultSet rs,String str) throws SQLException, MessageNotFoundException, UserNotFoundException, TransactionNotFoundException, LocationNotFoundException {
		User origin = rs.getInt("origin_UserId")==-1 ? null : CarpoolDaoUser.getUserById(rs.getInt("origin_UserId"));
		Message msg = rs.getInt("origin_MessageId")==-1 ? null : CarpoolDaoMessage.getMessageById(rs.getInt("origin_MessageId"));
		Transaction transaction = rs.getInt("origin_TransactionId")==-1 ? null : CarpoolDaoTransaction.getTransactionById(rs.getInt("origin_TransactionId"));
		Notification notification = null;
		notification = new Notification(rs.getInt("notification_Id"), Constants.NotificationEvent.fromInt(rs.getInt("notificationEvent")),rs.getInt("target_UserId"),
				rs.getInt("origin_UserId"),rs.getInt("origin_MessageId"),rs.getInt("origin_TransactionId"),Constants.NotificationState.fromInt(rs.getInt("notificationState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));
		notification.setInitUser(origin);
		notification.setMessage(msg);
		notification.setTransaction(transaction);
		return notification;
	}

	private static Notification createNotificationByResultSet(ResultSet rs) throws SQLException{
		return new Notification(rs.getInt("notification_Id"), Constants.NotificationEvent.fromInt(rs.getInt("notificationEvent")),rs.getInt("target_UserId"),
				rs.getInt("origin_UserId"),rs.getInt("origin_MessageId"),rs.getInt("origin_TransactionId"),Constants.NotificationState.fromInt(rs.getInt("notificationState")),DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));

	}

}
