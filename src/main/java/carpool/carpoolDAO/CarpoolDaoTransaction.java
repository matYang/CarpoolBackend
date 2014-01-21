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
import carpool.common.Parser;
import carpool.constants.Constants;
import carpool.constants.Constants.TransactionType;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.Transaction;
import carpool.model.User;

public class CarpoolDaoTransaction {

	public static Transaction addTransactionToDatabase(Transaction transaction) throws MessageNotFoundException, UserNotFoundException, LocationNotFoundException{
		String query = "INSERT INTO carpoolDAOTransaction (provider_Id,customer_Id,message_Id,departure_priceList,departure_Time,"+
				"departure_Id,arrival_Id,departure_seatsBooked,totalPrice,"
				+"transactionState,departure_timeSlot,creationTime,historyDeleted,paymentMethod,customerNote,providerNote,customerEvaluation,providerEvaluation,transactionType)"+
				"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Message msg = CarpoolDaoMessage.getMessageById(transaction.getMessageId());
		User  provider = CarpoolDaoUser.getUserById(transaction.getProviderId());
		User  customer = CarpoolDaoUser.getUserById(transaction.getCustomerId());
		transaction.setCustomer(customer);
		transaction.setProvider(provider);
		transaction.setMessage(msg);
		transaction.setDeparture_location(msg.getDeparture_Location());		
		transaction.setDeparture_Id(msg.getDeparture_Location().getId());
		transaction.setArrival_location(msg.getArrival_Location());
		transaction.setArrival_Id(msg.getArrival_Location().getId());
		int totalPrice = 0;
		TransactionType direction = transaction.getType();
		ArrayList<Integer> dplist = new ArrayList<Integer>();
		ArrayList<Integer> aplist = new ArrayList<Integer>();
		if(direction == TransactionType.departure){
			dplist = msg.getDeparture_priceList();
			for(int i=0; i<dplist.size();i++){
				totalPrice += dplist.get(i);
			}
		}else if(direction == TransactionType.arrival){
			aplist = msg.getArrival_priceList();
			for(int i=0; i<aplist.size();i++){
				totalPrice += aplist.get(i);
			}
		}

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
		try{	
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			stmt.setInt(1,transaction.getProviderId());			
			stmt.setInt(2, transaction.getCustomerId());
			stmt.setInt(3, transaction.getMessageId());
			if(direction == TransactionType.departure){
				stmt.setString(4, Parser.listToString(dplist));		
				stmt.setString(5, DateUtility.toSQLDateTime(msg.getDeparture_time()));
				stmt.setLong(6, msg.getDeparture_Id());						
				stmt.setLong(7, msg.getArrival_Id());						
			}

			else
			{
				stmt.setString(4, Parser.listToString(aplist));				
				stmt.setString(5, DateUtility.toSQLDateTime(msg.getArrival_time()));
				stmt.setLong(6, msg.getArrival_Id());
				stmt.setLong(7, msg.getDeparture_Id());		

			}	
			stmt.setInt(8,transaction.getDeparture_seatsBooked());
			stmt.setInt(9, totalPrice);		
			stmt.setInt(10, transaction.getState().code);			
			stmt.setInt(11, transaction.getDeparture_timeSlot().code);		
			stmt.setString(12, DateUtility.toSQLDateTime(transaction.getCreationTime()));
			stmt.setInt(13,transaction.isHistoryDeleted() ? 1 : 0);
			stmt.setInt(14,msg.getPaymentMethod().code);
			stmt.setString(15,transaction.getCustomerNote());
			stmt.setString(16, transaction.getProviderNote());
			stmt.setInt(17,transaction.getCustomerEvaluation());
			stmt.setInt(18,transaction.getProviderEvaluation());
			stmt.setInt(19, direction.code);
			stmt.executeUpdate();	 
			rs = stmt.getGeneratedKeys();
			rs.next();
			transaction.setTransactionId(rs.getInt(1));
		}catch(SQLException e){
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
		return transaction;

	}

	public static void updateTransactionInDatabase(Transaction transaction) throws TransactionNotFoundException, MessageNotFoundException, UserNotFoundException, LocationNotFoundException{
		String query="UPDATE carpoolDAOTransaction SET departure_priceList=?,departure_Time=?,"+
				"departure_Id=?,arrival_Id=?,departure_seatsBooked=?,totalPrice=?,"+
				"transactionState=?,departure_timeSlot=?,creationTime=?,historyDeleted=?,paymentMethod=?,customerNote=?,providerNote=?,customerEvaluation=?,providerEvaluation=?, transactionType=? where transaction_Id=?";
		Message msg = null;
		if(transaction.getMessage()==null){
			msg = CarpoolDaoMessage.getMessageById(transaction.getMessageId());
		}
		else msg = transaction.getMessage();

		transaction.setDeparture_location(msg.getDeparture_Location());		
		transaction.setDeparture_Id(msg.getDeparture_Location().getId());
		transaction.setArrival_location(msg.getArrival_Location());
		transaction.setArrival_Id(msg.getArrival_Location().getId());

		int totalPrice = 0;
		int direction = transaction.getType().code;
		ArrayList<Integer> dplist = new ArrayList<Integer>();
		ArrayList<Integer> aplist = new ArrayList<Integer>();
		if(direction == 0){
			dplist = msg.getDeparture_priceList();
			for(int i=0; i<dplist.size();i++){
				totalPrice += dplist.get(i);
			}
		}else if(direction == 1){
			aplist = msg.getArrival_priceList();
			for(int i=0; i<aplist.size();i++){
				totalPrice += aplist.get(i);
			}
		}	

		PreparedStatement stmt = null;
		Connection conn = null;

		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			if(direction==0){
				stmt.setString(1, Parser.listToString(dplist));
				stmt.setString(2, DateUtility.toSQLDateTime(msg.getDeparture_time()));				
				stmt.setLong(3, msg.getDeparture_Id());							
				stmt.setLong(4, msg.getArrival_Id());				
			}else{
				stmt.setString(1, Parser.listToString(aplist));				
				stmt.setString(2, DateUtility.toSQLDateTime(msg.getArrival_time()));
				stmt.setLong(3, msg.getArrival_Id());						
				stmt.setLong(4, msg.getDeparture_Id());							
			}
			stmt.setInt(5,transaction.getDeparture_seatsBooked());					
			stmt.setInt(6, totalPrice);				
			stmt.setInt(7, transaction.getState().code);
			stmt.setInt(8, transaction.getDeparture_timeSlot().code);				
			stmt.setString(9, DateUtility.toSQLDateTime(transaction.getCreationTime()));
			stmt.setInt(10,transaction.isHistoryDeleted() ? 1 : 0);
			stmt.setInt(11,msg.getPaymentMethod().code);
			stmt.setString(12,transaction.getCustomerNote());
			stmt.setString(13, transaction.getProviderNote());
			stmt.setInt(14,transaction.getCustomerEvaluation());
			stmt.setInt(15,transaction.getProviderEvaluation());
			stmt.setInt(16, direction);
			stmt.setInt(17,transaction.getTransactionId());			
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new TransactionNotFoundException();
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

	public static Transaction getTransactionById(int transaction_id) throws TransactionNotFoundException, UserNotFoundException, MessageNotFoundException, LocationNotFoundException{
		String query="select * from carpoolDAOTransaction where transaction_Id=?;";
		Transaction transaction = null;

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, transaction_id);
			rs = stmt.executeQuery();
			if(rs.next()){
				transaction = createTransactionByResultSet(rs,"ForOneTransaction");
			}else{
				throw new TransactionNotFoundException();
			}
		}catch(SQLException e){
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
		return transaction;
	}

	protected static Transaction createTransactionByResultSet(ResultSet rs,String str) throws SQLException, UserNotFoundException, MessageNotFoundException, LocationNotFoundException {
		User provider = CarpoolDaoUser.getUserById(rs.getInt("provider_Id"));
		User customer = CarpoolDaoUser.getUserById(rs.getInt("customer_Id"));
		Message msg = CarpoolDaoMessage.getMessageById(rs.getInt("message_Id"));		
		Location departure_location = msg.getDeparture_Location();
		Location arrival_location = msg.getArrival_Location();		
		Transaction transaction = null;
		transaction = new Transaction(rs.getInt("transaction_Id"),rs.getInt("provider_Id"),rs.getInt("customer_Id"),rs.getInt("message_Id"),
				Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("customerNote"),rs.getString("providerNote"),
				rs.getInt("customerEvaluation"),rs.getInt("providerEvaluation"),departure_location,arrival_location,DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsBooked"),
				(ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"), new Integer(0)),Constants.TransactionType.fromInt(rs.getInt("transactionType")),rs.getInt("totalPrice"),Constants.transactionState.fromInt(rs.getInt("transactionState")),
				DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));
		transaction.setProvider(provider);
		transaction.setCustomer(customer);
		transaction.setMessage(msg);
		return transaction;
	}

	public static ArrayList<Transaction> getAllTranscations() throws TransactionNotFoundException, UserNotFoundException, MessageNotFoundException, LocationNotFoundException{
		String query="select * from carpoolDAOTransaction;";
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){			
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("provider_Id"));	
				ilist = addIds(ilist,rs.getInt("customer_Id"));
				milist = addIds(milist,rs.getInt("message_Id"));
				tlist.add(createTransactionByResultSet(rs));
			}
			tlist = fillTransactions(ilist,milist,tlist);
		}catch(SQLException e){
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
		return tlist;
	}

	protected static Transaction createTransactionByResultSet(ResultSet rs) throws SQLException, LocationNotFoundException{
		Location departure_location = CarpoolDaoLocation.getLocationById(rs.getLong("departure_Id"));
		Location arrival_location = CarpoolDaoLocation.getLocationById(rs.getLong("arrival_Id"));
		return new Transaction(rs.getInt("transaction_Id"),rs.getInt("provider_Id"),rs.getInt("customer_Id"),rs.getInt("message_Id"),
				Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("customerNote"),rs.getString("providerNote"),
				rs.getInt("customerEvaluation"),rs.getInt("providerEvaluation"),departure_location,arrival_location,DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsBooked"),
				(ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"), new Integer(0)),Constants.TransactionType.fromInt(rs.getInt("transactionType")),rs.getInt("totalPrice"),Constants.transactionState.fromInt(rs.getInt("transactionState")),
				DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));

	}

	public static void deleteTransactionFromDatabase(Transaction t) throws SQLException, TransactionNotFoundException{
		String query="delete from carpoolDAOTransaction where transaction_Id = ?";
		PreparedStatement stmt = null;
		Connection conn = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.executeQuery();
		}catch(SQLException e){
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

	public static ArrayList<Transaction> getAllTransactionByUserId(int userId) throws UserNotFoundException, MessageNotFoundException, LocationNotFoundException{	  
		String query = "SELECT * FROM carpoolDAOTransaction where provider_Id = ? OR customer_Id = ?;";
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){		
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("provider_Id"));	
				ilist = addIds(ilist,rs.getInt("customer_Id"));
				milist = addIds(milist,rs.getInt("message_Id"));
				tlist.add(createTransactionByResultSet(rs));
			}
			tlist = fillTransactions(ilist,milist,tlist);
		}catch(SQLException e){
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
		return tlist;	  
	}

	public static ArrayList<Transaction> getAllTransactionByMessageId(int msgId) throws UserNotFoundException, MessageNotFoundException, LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOTransaction where message_Id = ?;";
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Integer> milist = new ArrayList<Integer>();

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){		
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, msgId);			
			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("provider_Id"));	
				ilist = addIds(ilist,rs.getInt("customer_Id"));
				milist = addIds(milist,rs.getInt("message_Id"));
				tlist.add(createTransactionByResultSet(rs));
			}
			tlist = fillTransactions(ilist,milist,tlist);
		}catch(SQLException e){
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
		return tlist;
	}

	public static ArrayList<Transaction> fillTransactions(ArrayList<Integer> ilist, ArrayList<Integer> milist,	ArrayList<Transaction> tlist) throws LocationNotFoundException {

		HashMap<Integer,User> usersMap = new HashMap<Integer,User>();

		if(ilist.size()>0){
			usersMap = getUsersHashMap(ilist);
		}

		HashMap<Integer,Message> msgMap = new HashMap<Integer,Message>();

		if(milist.size()>0){
			msgMap = getMsgHashMap(milist);
		}

		for(int i=0;i<tlist.size();i++){
			tlist.get(i).setProvider(usersMap.get(tlist.get(i).getProviderId()));
			tlist.get(i).setCustomer(usersMap.get(tlist.get(i).getCustomerId()));
			tlist.get(i).setMessage(msgMap.get(tlist.get(i).getMessageId()));
		}
		return tlist;
	}

	public static HashMap<Integer,Message> getMsgHashMap(ArrayList<Integer> list) throws LocationNotFoundException {
		ArrayList<Integer> ilist = new ArrayList<Integer>();
		ArrayList<Message> mlist = new ArrayList<Message>();
		HashMap<Integer,Message> map = new HashMap<Integer,Message>();
		//Already checked list size, but for the consistency of the code we still check list's size
		if(list.size()<=0){
			return map;
		}
		String query = "SELECT * FROM carpoolDAOMessage where ";
		for(int i=0;i<list.size()-1;i++){
			query += "messageId = ? OR ";
		}
		query += "messageId = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			rs = stmt.executeQuery();
			while(rs.next()){
				ilist = addIds(ilist,rs.getInt("ownerId"));
				mlist.add(CarpoolDaoMessage.createMessagesByResultSetList(rs));
			}
			mlist = CarpoolDaoMessage.getUsersForMessages(ilist, mlist);
			int ind = 0;
			while(ind<mlist.size()){
				map.put(mlist.get(ind).getMessageId(), mlist.get(ind));
				ind++;
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

	public static ArrayList<Integer> addIds(ArrayList<Integer> ilist, int id) {
		if(!ilist.contains(id)){
			ilist.add(id);
		}
		return ilist;
	}	

	public static HashMap<Integer,User> getUsersHashMap(ArrayList<Integer> list) throws LocationNotFoundException{
		HashMap<Integer,User> map = new HashMap<Integer,User>();
		if(list.size()<=0){
			return map;
		}
		String query = "SELECT * FROM carpoolDAOUser where ";
		for(int i=0;i<list.size()-1;i++){
			query += "userId = ? OR ";
		}
		query += "userId = ?";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		//try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			for(int i=0;i<list.size();i++){
				stmt.setInt(i+1, list.get(i));
			}
			rs = stmt.executeQuery();
			int ind = 0;
			while(rs.next() && ind<list.size()){
				map.put(rs.getInt("userId"), CarpoolDaoUser.createUserByResultSet(rs));
				ind++;
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

}