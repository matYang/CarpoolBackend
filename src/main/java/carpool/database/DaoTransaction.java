package carpool.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.model.Message;
import carpool.model.Transaction;
import carpool.model.representation.LocationRepresentation;



public class DaoTransaction {
	
	public static Transaction addTransactionToDatabase(Transaction t){
		String query = "INSERT INTO Transaction (initUserId,targetUserId,initUserImgPath,initUserName,initUserLevel,targetUserImgPath,targetUserName,"+
				"targetUserLevel,initUserEval,targetUserEval,messageId,messageNote,paymentMethod,price,requestInfo,responseInfo,startTime,endTime," +
				"location,established,success,state,historyDeleted,creationTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = carpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1, t.getInitUserId());
			stmt.setInt(2, t.getTargetUserId());
			stmt.setString(3, t.getInitUserImgPath());
			stmt.setString(4, t.getInitUserName());
			stmt.setInt(5, t.getInitUserLevel());
			stmt.setString(6, t.getTargetUserImgPath());
			stmt.setString(7, t.getTargetUserName());
			stmt.setInt(8, t.getTargetUserLevel());
			stmt.setInt(9, t.getInitUserEval());
			stmt.setInt(10, t.getTargetUserEval());
			stmt.setInt(11, t.getMessageId());
			stmt.setString(12, t.getMessageNote());
			stmt.setInt(13, t.getPaymentMethod().code);
			stmt.setInt(14, t.getPrice());
			stmt.setString(15, t.getRequestInfo());
			stmt.setString(16, t.getResponseInfo());
			stmt.setString(17, DateUtility.toSQLDateTime(t.getStartTime()));
			stmt.setString(18, DateUtility.toSQLDateTime(t.getEndTime()));
			stmt.setString(19, t.getLocation().toString());
			stmt.setInt(20, t.isEstablished() ? 1:0);
			stmt.setInt(21, t.isSuccess() ? 1:0);
			stmt.setInt(22, t.getState().code);
			stmt.setInt(23, t.isHistoryDeleted() ? 1:0);
			stmt.setString(24, DateUtility.toSQLDateTime(t.getCreationTime()));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			t.setTransactionId(rs.getInt(1));
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
			return null;
		}
		return t;
	}
	
	public static void deleteTransactionFromDatabase(int id) throws TransactionNotFoundException{
		String query = "DELETE from Transaction where transactionId = ?";
		try(PreparedStatement stmt = carpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			if(stmt.executeUpdate()==0){
				throw new TransactionNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
	}
	
	public static void UpdateTransactionInDatabase(Transaction t) throws TransactionNotFoundException{
		String query = "UPDATE Transaction SET initUserId=?,targetUserId=?,initUserImgPath=?,initUserName=?,initUserLevel = ?," +
				"targetUserImgPath=?,targetUserName=?,targetUserLevel=?,initUserEval=?,targetUserEval=?,messageId=?," +
				"messageNote=?,paymentMethod=?,price=?,requestInfo=?,responseInfo=?,startTime=?,endTime=?,location=?," +
				"established=?,success=?,state=?,historyDeleted=?,creationTime=? WHERE transactionId=?";
		try(PreparedStatement stmt = carpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, t.getInitUserId());
			stmt.setInt(2, t.getTargetUserId());
			stmt.setString(3, t.getInitUserImgPath());
			stmt.setString(4, t.getInitUserName());
			stmt.setInt(5, t.getInitUserLevel());
			stmt.setString(6, t.getTargetUserImgPath());
			stmt.setString(7, t.getTargetUserName());
			stmt.setInt(8, t.getTargetUserLevel());
			stmt.setInt(9, t.getInitUserEval());
			stmt.setInt(10, t.getTargetUserEval());
			stmt.setInt(11, t.getMessageId());
			stmt.setString(12, t.getMessageNote());
			stmt.setInt(13, t.getPaymentMethod().code);
			stmt.setInt(14, t.getPrice());
			stmt.setString(15, t.getRequestInfo());
			stmt.setString(16, t.getResponseInfo());
			stmt.setString(17, DateUtility.toSQLDateTime(t.getStartTime()));
			stmt.setString(18, DateUtility.toSQLDateTime(t.getEndTime()));
			stmt.setString(19, t.getLocation().toString());
			stmt.setInt(20, t.isEstablished() ? 1:0);
			stmt.setInt(21, t.isSuccess() ? 1:0);
			stmt.setInt(22, t.getState().code);
			stmt.setInt(23, t.isHistoryDeleted() ? 1:0);
			stmt.setString(24, DateUtility.toSQLDateTime(t.getCreationTime()));
			stmt.setInt(25, t.getTransactionId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new TransactionNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
	}
	
	public static ArrayList<Transaction> getALL(){
		String query = "SELECT * FROM Transaction;";
		ArrayList<Transaction> retVal = new ArrayList<Transaction>();
		try(PreparedStatement stmt = carpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				retVal.add(createTransactionByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return retVal;
	}
	
	public static Transaction getTransactionById(int id) throws TransactionNotFoundException{
		String query = "SELECT * FROM Transaction WHERE transactionId = ?;";
		Transaction t = null;
		try(PreparedStatement stmt = carpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				t = createTransactionByResultSet(rs);
			}else{
				throw new TransactionNotFoundException();
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return t;		
	}
	
	public static ArrayList<Transaction> getTransactionByMessage(int id){
		String query = "SELECT * FROM Transaction WHERE messageId = ?;";
		ArrayList<Transaction> retVal = new ArrayList<Transaction>();
		try(PreparedStatement stmt = carpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				retVal.add(createTransactionByResultSet(rs));
			}
		}catch(SQLException e){
			DebugLog.d(e.getMessage());
		}
		return retVal;
	}

	public static Transaction createTransactionByResultSet(ResultSet rs) throws SQLException {
		Transaction t;
		t = new Transaction(rs.getInt("transactionId"), rs.getInt("initUserId"), rs.getInt("targetUserId"), rs.getString("initUserImgPath"),
				rs.getString("initUserName"), rs.getInt("initUserLevel"),rs.getString("targetUserImgPath"), rs.getString("targetUserName"),
				rs.getInt("targetUserLevel"),rs.getInt("initUserEval"),rs.getInt("targetUserEval"), rs.getInt("messageId"), rs.getString("messageNote"),
				Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")), rs.getInt("price"),rs.getString("requestInfo"),
				rs.getString("responseInfo"), DateUtility.DateToCalendar(rs.getTimestamp("startTime")),DateUtility.DateToCalendar(rs.getTimestamp("endTime")),
				new LocationRepresentation(rs.getString("location")), rs.getBoolean("established"), rs.getBoolean("success"),
				Constants.transactionState.fromInt(rs.getShort("state")), rs.getBoolean("historyDeleted"), DateUtility.DateToCalendar(rs.getTimestamp("creationTime")));
		return t;
	}
}
