package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Parser;
import carpool.constants.Constants;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.Transaction;
import carpool.model.User;
import carpool.model.representation.LocationRepresentation;

public class CarpoolDaoTransaction {

	public static Transaction addTransactionToDatabase(Transaction transaction) throws MessageNotFoundException, UserNotFoundException{
		String query = "INSERT INTO carpoolDAOTransaction (provider_Id,customer_Id,message_Id,departure_priceList,departure_Time,"+
	"departure_primaryLocation,departure_customLocation,departure_customDepthIndex,arrival_priceList,arrival_Time,arrival_primaryLocation,"+
	"arrival_customLocation,arrival_customDepthIndex,departure_seatsBooked,arrival_seatsBooked,totalPrice,direction,"
	+"transactionState,departure_timeSlot,arrival_timeSlot,creationTime,historyDeleted,paymentMethod,customerNote,providerNote,customerEvaluation,providerEvaluation)"+
	"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	Message msg = CarpoolDaoMessage.getMessageById(transaction.getMessageId());
	User  provider = CarpoolDaoUser.getUserById(transaction.getProviderId());
	User  customer = CarpoolDaoUser.getUserById(transaction.getCustomerId());
	transaction.setCustomer(customer);
	transaction.setProvider(provider);
	transaction.setMessage(msg);
	
	int direction = transaction.getDirection().code;
	int totalPrice = 0;
	ArrayList<Integer> dplist = new ArrayList<Integer>();
	if(direction==0||direction==1){
	dplist = msg.getDeparture_priceList();
	for(int i=0; i<dplist.size();i++){
		totalPrice += dplist.get(i);
	 }
	}
	ArrayList<Integer> arlist = new ArrayList<Integer>();
	if(direction==0||direction==2){		
	    arlist = msg.getArrival_priceList();
		for(int i=0; i<arlist.size();i++){
			totalPrice += arlist.get(i);
		}
	}
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1,transaction.getProviderId());			
			stmt.setInt(2, transaction.getCustomerId());
			stmt.setInt(3, transaction.getMessageId());
			if(direction==0||direction==1)
			stmt.setString(4, Parser.listToString(dplist));
			else stmt.setString(4, null);
			stmt.setString(5, DateUtility.toSQLDateTime(msg.getDeparture_time()));
			stmt.setString(6, msg.getDeparture_location().getPrimaryLocationString());
			stmt.setString(7, msg.getDeparture_location().getCustomLocationString());
			stmt.setInt(8, msg.getDeparture_location().getCustomDepthIndex());
			if(direction==0||direction==2)
			stmt.setString(9, Parser.listToString(arlist));
			else stmt.setString(9, null);
			stmt.setString(10, DateUtility.toSQLDateTime(msg.getArrival_time()));
			stmt.setString(11, msg.getArrival_location().getPrimaryLocationString());
		    stmt.setString(12, msg.getArrival_location().getCustomLocationString());
		    stmt.setInt(13, msg.getArrival_location().getCustomDepthIndex());		
			if(direction==0||direction==1)
		    stmt.setInt(14,transaction.getDeparture_seatsBooked());	
			else  stmt.setInt(14,0);
		    if(direction==0||direction==2)
		    stmt.setInt(15,transaction.getArrival_seatsBooked());
		    else  stmt.setInt(15, 0);		    
			stmt.setInt(16, totalPrice);
			stmt.setInt(17, transaction.getDirection().code);
			stmt.setInt(18, transaction.getState().code);
			stmt.setInt(19, transaction.getDeparture_timeSlot().code);		
			stmt.setInt(20, transaction.getArrival_timeSlot().code);
			stmt.setString(21, DateUtility.toSQLDateTime(transaction.getCreationTime()));
			stmt.setInt(22,transaction.isHistoryDeleted() ? 1 : 0);
			stmt.setInt(23,msg.getPaymentMethod().code);
			stmt.setString(24,transaction.getCustomerNote());
			stmt.setString(25, transaction.getProviderNote());
			stmt.setInt(26,transaction.getCustomerEvaluation());
			stmt.setInt(27,transaction.getProviderEvaluation());
			stmt.executeUpdate();	 
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			transaction.setTransactionId(rs.getInt(1));
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
	 return transaction;
		
	}
	
	public static void UpdateTransactionInDatabase(Transaction transaction) throws TransactionNotFoundException, MessageNotFoundException, UserNotFoundException{
		String query="UPDATE carpoolDAOTransaction SET departure_priceList=?,departure_Time=?,"+
	"departure_primaryLocation=?,departure_customLocation=?,departure_customDepthIndex=?,arrival_priceList=?,arrival_Time=?,arrival_primaryLocation=?,"+
	"arrival_customLocation=?,arrival_customDepthIndex=?,departure_seatsBooked=?,arrival_seatsBooked=?,totalPrice=?,direction=?,"+
	"transactionState=?,departure_timeSlot=?,arrival_timeSlot=?,creationTime=?,historyDeleted=?,paymentMethod=?,customerNote=?,providerNote=?,customerEvaluation=?,providerEvaluation=? where transaction_Id=?";
		Message msg = CarpoolDaoMessage.getMessageById(transaction.getMessageId());
		int direction = transaction.getDirection().code;
		int totalPrice = 0;
		ArrayList<Integer> dplist = new ArrayList<Integer>();
		if(direction==0||direction==1){
		dplist = msg.getDeparture_priceList();
		for(int i=0; i<dplist.size();i++){
			totalPrice += dplist.get(i);
		 }
		}
		ArrayList<Integer> arlist = new ArrayList<Integer>();
		if(direction==0||direction==2){		
		    arlist = msg.getArrival_priceList();
			for(int i=0; i<arlist.size();i++){
				totalPrice += arlist.get(i);
			}
		}
			try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
				if(direction==0||direction==1)
				stmt.setString(1, Parser.listToString(dplist));
				else stmt.setString(1, null);
				stmt.setString(2, DateUtility.toSQLDateTime(msg.getDeparture_time()));
				stmt.setString(3, msg.getDeparture_location().getPrimaryLocationString());
				stmt.setString(4, msg.getDeparture_location().getCustomLocationString());
				stmt.setInt(5, msg.getDeparture_location().getCustomDepthIndex());						
				if(direction==0||direction==2)
				stmt.setString(6, Parser.listToString(arlist));
				else stmt.setString(6, null);
				stmt.setString(7, DateUtility.toSQLDateTime(msg.getArrival_time()));
				stmt.setString(8, msg.getArrival_location().getPrimaryLocationString());
			    stmt.setString(9, msg.getArrival_location().getCustomLocationString());
			    stmt.setInt(10, msg.getArrival_location().getCustomDepthIndex());			
			    if(direction==0||direction==1)
			    stmt.setInt(11,transaction.getDeparture_seatsBooked());	
				else  stmt.setInt(11,0);
			    if(direction==0||direction==2)
			    stmt.setInt(12,transaction.getArrival_seatsBooked());
			    else  stmt.setInt(12, 0);		    
				stmt.setInt(13, totalPrice);
				stmt.setInt(14, transaction.getDirection().code);
				stmt.setInt(15, transaction.getState().code);
				stmt.setInt(16, transaction.getDeparture_timeSlot().code);		
				stmt.setInt(17, transaction.getArrival_timeSlot().code);
				stmt.setString(18, DateUtility.toSQLDateTime(transaction.getCreationTime()));
				stmt.setInt(19,transaction.isHistoryDeleted() ? 1 : 0);
				stmt.setInt(20,msg.getPaymentMethod().code);
				stmt.setString(21,transaction.getCustomerNote());
				stmt.setString(22, transaction.getProviderNote());
				stmt.setInt(23,transaction.getCustomerEvaluation());
				stmt.setInt(24,transaction.getProviderEvaluation());
			    stmt.setInt(25,transaction.getTransactionId());			
			    int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new TransactionNotFoundException();
			} 
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
	}	
	
	public static Transaction getTransactionById(int transaction_id) throws TransactionNotFoundException, UserNotFoundException, MessageNotFoundException{
		String query="select * from carpoolDAOTransaction where transaction_Id=?;";
		Transaction transaction = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, transaction_id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				transaction = createTransactionByResultSet(rs);
			}else{
				throw new TransactionNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return transaction;
	}
	
	public static ArrayList<Transaction> getAllTranscations() throws TransactionNotFoundException, UserNotFoundException, MessageNotFoundException{
		String query="select * from carpoolDAOTransaction;";
		ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				tlist.add(createTransactionByResultSet(rs));
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return tlist;
	}

	private static Transaction createTransactionByResultSet(ResultSet rs) throws SQLException, UserNotFoundException, MessageNotFoundException {
		Transaction transaction = null;
		System.out.println(rs.getTimestamp("departure_Time"));
		System.out.println(rs.getTimestamp("arrival_Time"));
		User provider = CarpoolDaoUser.getUserById(rs.getInt("provider_Id"));
		User customer = CarpoolDaoUser.getUserById(rs.getInt("customer_Id"));
		Message msg = CarpoolDaoMessage.getMessageById(rs.getInt("message_Id"));
		transaction = new Transaction(rs.getInt("transaction_Id"),rs.getInt("provider_Id"),rs.getInt("customer_Id"),rs.getInt("message_Id"),
         provider,customer,msg,Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("customerNote"),rs.getString("providerNote"),
         rs.getInt("customerEvaluation"),rs.getInt("providerEvaluation"),Constants.TransactionDirection.fromInt(rs.getInt("direction")),new LocationRepresentation(rs.getString("departure_primaryLocation"),rs.getString("departure_customLocation"),rs.getInt("departure_customDepthIndex")),
         DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsBooked"),
         (ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"), new Integer(0)),new LocationRepresentation(rs.getString("arrival_primaryLocation"),rs.getString("arrival_customLocation"),rs.getInt("arrival_customDepthIndex")),
         DateUtility.DateToCalendar(rs.getTimestamp("arrival_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("arrival_timeSlot")),rs.getInt("arrival_seatsBooked"),
         (ArrayList<Integer>)Parser.stringToList(rs.getString("arrival_priceList"), new Integer(0)),rs.getInt("totalPrice"),Constants.transactionState.fromInt(rs.getInt("transactionState")),
         DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));
		
		return transaction;
	}
	
	public static void deleteTransactionFromDatabase(Transaction t) throws SQLException, TransactionNotFoundException{
		String query="delete from carpoolDAOTransaction where transaction_Id = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.executeQuery();
		}
	}
	
  public static ArrayList<Transaction> getAllTransactionByUserId(int userId) throws UserNotFoundException, MessageNotFoundException{	  
	  String query = "SELECT * FROM carpoolDAOTransaction where provider_Id = ? OR customer_Id = ?;";
	  ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){		
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				tlist.add(createTransactionByResultSet(rs));
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return tlist;	  
  }
	
  public static ArrayList<Transaction> getAllTransactionByMessageId(int msgId) throws UserNotFoundException, MessageNotFoundException{
	  String query = "SELECT * FROM carpoolDAOTransaction where message_Id = ?;";
	  ArrayList<Transaction> tlist = new ArrayList<Transaction>();
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){		
			stmt.setInt(1, msgId);			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				tlist.add(createTransactionByResultSet(rs));
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
		return tlist;
  }
	
	
	
	
	
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

