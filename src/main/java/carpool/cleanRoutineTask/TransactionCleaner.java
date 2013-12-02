package carpool.cleanRoutineTask;

import java.util.ArrayList;
import java.util.Calendar;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoTransaction;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.transactionState;
import carpool.dbservice.NotificationDaoService;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Notification;
import carpool.model.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class TransactionCleaner extends CarpoolDaoTransaction {

	public static void Clean(){
		Calendar currentDate = Calendar.getInstance();
		String ct=DateUtility.toSQLDateTime(currentDate);
		//System.out.println("currentTime: "+ct);
		Calendar DayLate = Calendar.getInstance();
		DayLate.set(Calendar.HOUR_OF_DAY, 23);
		DayLate.set(Calendar.MINUTE,59);
		DayLate.set(Calendar.SECOND,59);
		String late = DateUtility.toSQLDateTime(DayLate);
		//System.out.println("lateTime: "+late);
		Calendar Yesterday = Calendar.getInstance();
		Yesterday.add(Calendar.DAY_OF_YEAR, -1);
		String yesterday = DateUtility.toSQLDateTime(Yesterday);
		//System.out.println("yesterday: "+yesterday);
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();

		String query = "SELECT * FROM carpoolDAOTransaction where (transactionState = ? AND departure_Time > ? AND departure_Time <= ?) OR(transactionState = ? AND departure_Time >= ?);";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, Constants.transactionState.init.code);			
			stmt.setString(2, ct);							
			stmt.setString(3, late);	
			stmt.setInt(4, Constants.transactionState.aboutToStart.code);
			stmt.setString(5, yesterday);			
			ResultSet rs = stmt.executeQuery();			
			while(rs.next()){	
				Transaction transaction = CarpoolDaoTransaction.createTransactionByResultSet(rs);					
				if(transaction.getState() == Constants.transactionState.init){				    	
					transaction.setState(transactionState.aboutToStart);				    	
				}else if(transaction.getState() == Constants.transactionState.aboutToStart){				    	
					transaction.setState(transactionState.finished);				    	
				}
				tlist.add(transaction);
				ilist = CarpoolDaoTransaction.addIds(ilist, transaction.getProviderId());
				ilist = CarpoolDaoTransaction.addIds(ilist, transaction.getCustomerId());
				milist = CarpoolDaoTransaction.addIds(milist, transaction.getMessageId());
			}
			tlist = CarpoolDaoTransaction.fillTransactions(ilist, milist, tlist);
			Transaction transaction = null;
			for(int i=0;i<tlist.size();i++){
				transaction = tlist.get(i);
				CarpoolDaoTransaction.updateTransactionInDatabase(transaction);
				//use the queue from notification service here
				NotificationDaoService.addToNotificationQueue(new Notification(Constants.NotificationEvent.transactionAboutToStart, transaction.getProviderId(), transaction.getCustomerId(), transaction.getMessageId(), transaction.getTransactionId()));
				NotificationDaoService.addToNotificationQueue(new Notification(Constants.NotificationEvent.transactionAboutToStart, transaction.getCustomerId(), transaction.getProviderId(), transaction.getMessageId(), transaction.getTransactionId()));
			}
			NotificationDaoService.dispatchNotificationQueue();
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		} catch (UserNotFoundException e) {			
			e.printStackTrace();
		} catch (MessageNotFoundException e) {		
			e.printStackTrace();
		} catch (TransactionNotFoundException e) {		
			e.printStackTrace();
		}
	}

}
