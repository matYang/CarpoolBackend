package carpool.cleanRoutineTask;

import java.util.Calendar;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoTransaction;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.transactionState;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class TransactionCleaner extends CarpoolDaoTransaction {

	public static void Clean(){
		Calendar currentDate = Calendar.getInstance();
	    String ct=DateUtility.toSQLDateTime(currentDate);
	    
	    Calendar DayLate = Calendar.getInstance();
	    DayLate.set(Calendar.HOUR_OF_DAY, 23);
	    DayLate.set(Calendar.MINUTE,59);
	    DayLate.set(Calendar.SECOND,59);
	    String late = DateUtility.toSQLDateTime(DayLate);
	    
	    Calendar Yesterday = Calendar.getInstance();
	    Yesterday.add(Calendar.DAY_OF_YEAR, -1);
	    String yesterday = DateUtility.toSQLDateTime(Yesterday);
	    
	    String query = "SELECT * FROM carpoolDAOTransaction where (transactionState = ? AND departure_Time > ? AND departure_Time < ?) OR(transactionState = ? AND departure_Time > ?);";
	    try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, 0);			
			stmt.setString(2, ct);							
			stmt.setString(3, late);	
			stmt.setInt(4, 2);
			stmt.setString(5, yesterday);			
			ResultSet rs = stmt.executeQuery();			
				while(rs.next()){	
					Transaction transaction = CarpoolDaoTransaction.createTransactionByResultSet(rs);
				    if(transaction.getState().code==0){				    	
				    	transaction.setState(transactionState.aboutToStart);
				    }else if(transaction.getState().code==2){
				    	transaction.setState(transactionState.finished);
				    }
				    CarpoolDaoTransaction.updateTransactionInDatabase(transaction);
					}			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		} catch (UserNotFoundException e) {			
			e.printStackTrace();
		} catch (MessageNotFoundException e) {		
			e.printStackTrace();
		} catch (TransactionNotFoundException e) {		
			e.printStackTrace();
		}
	}
	
}
