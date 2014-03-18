package carpool.cleanRoutineTask;

import java.util.ArrayList;

import java.util.Calendar;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoTransaction;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.TransactionState;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TransactionCleaner extends CarpoolDaoTransaction {

	public static void Clean() throws LocationNotFoundException{
		ArrayList<Notification> notificationQueue = new ArrayList<Notification>();
		Calendar currentDate = DateUtility.getCurTimeInstance();
		String ct=DateUtility.toSQLDateTime(currentDate);
		//System.out.println("currentTime: "+ct);
		Calendar DayLate = DateUtility.getCurTimeInstance();
		DayLate.set(Calendar.HOUR_OF_DAY, 23);
		DayLate.set(Calendar.MINUTE,59);
		DayLate.set(Calendar.SECOND,59);
		String late = DateUtility.toSQLDateTime(DayLate);
		//System.out.println("lateTime: "+late);
		Calendar Yesterday = DateUtility.getCurTimeInstance();
		Yesterday.add(Calendar.DAY_OF_YEAR, -1);
		String yesterday = DateUtility.toSQLDateTime(Yesterday);
		//System.out.println("yesterday: "+yesterday);
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM carpoolDAOTransaction where (transactionState = ? AND departure_Time > ? AND departure_Time <= ?) OR(transactionState = ? AND departure_Time >= ?);";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, EnumConfig.TransactionState.init.code);			
			stmt.setString(2, ct);							
			stmt.setString(3, late);	
			stmt.setInt(4, EnumConfig.TransactionState.aboutToStart.code);
			stmt.setString(5, yesterday);			
			rs = stmt.executeQuery();			
			while(rs.next()){	
				Transaction transaction = CarpoolDaoTransaction.createTransactionByResultSet(rs,conn);					
				if(transaction.getState() == EnumConfig.TransactionState.init){				    	
					transaction.setState(TransactionState.aboutToStart);				    	
				}else if(transaction.getState() == EnumConfig.TransactionState.aboutToStart){				    	
					transaction.setState(TransactionState.finished);				    	
				}
				tlist.add(transaction);
				ilist = CarpoolDaoTransaction.addIds(ilist, transaction.getProviderId());
				ilist = CarpoolDaoTransaction.addIds(ilist, transaction.getCustomerId());
				milist = CarpoolDaoTransaction.addIds(milist, transaction.getMessageId());
			}
			tlist = CarpoolDaoTransaction.fillTransactions(ilist, milist, tlist,conn);
			Transaction transaction = null;
			for(int i=0;i<tlist.size();i++){
				transaction = tlist.get(i);
				CarpoolDaoTransaction.updateTransactionInDatabase(transaction,conn);
				//use the queue from notification service here
				notificationQueue.add(new Notification(EnumConfig.NotificationEvent.transactionAboutToStart, transaction.getProviderId(), transaction.getCustomerId(), transaction.getMessageId(), transaction.getTransactionId()));
				notificationQueue.add(new Notification(EnumConfig.NotificationEvent.transactionAboutToStart, transaction.getCustomerId(), transaction.getProviderId(), transaction.getMessageId(), transaction.getTransactionId()));
			}
			NotificationDaoService.sendNotification(notificationQueue,conn);
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		} catch (UserNotFoundException e) {			
			e.printStackTrace();
		} catch (MessageNotFoundException e) {		
			e.printStackTrace();
		} catch (TransactionNotFoundException e) {		
			e.printStackTrace();
		} finally{
			CarpoolDaoBasic.closeResources(conn, stmt, rs, true);
		}
	}

}
