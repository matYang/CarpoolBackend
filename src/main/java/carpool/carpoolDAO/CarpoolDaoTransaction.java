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
	"departure_primaryLocation,departure_customLocation,departure_customDepthIndex,arrival_primaryLocation,"+
	"arrival_customLocation,arrival_customDepthIndex,departure_seatsBooked,totalPrice,"
	+"transactionState,departure_timeSlot,creationTime,historyDeleted,paymentMethod,customerNote,providerNote,customerEvaluation,providerEvaluation,transactionType)"+
	"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	Message msg = CarpoolDaoMessage.getMessageById(transaction.getMessageId());
	User  provider = CarpoolDaoUser.getUserById(transaction.getProviderId());
	User  customer = CarpoolDaoUser.getUserById(transaction.getCustomerId());
	transaction.setCustomer(customer);
	transaction.setProvider(provider);
	transaction.setMessage(msg);		
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
	
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1,transaction.getProviderId());			
			stmt.setInt(2, transaction.getCustomerId());
			stmt.setInt(3, transaction.getMessageId());
			if(direction==0){
				stmt.setString(4, Parser.listToString(dplist));		
				stmt.setString(5, DateUtility.toSQLDateTime(msg.getDeparture_time()));
				stmt.setString(6, msg.getDeparture_location().getPrimaryLocationString());
				stmt.setString(7, msg.getDeparture_location().getCustomLocationString());
				stmt.setInt(8, msg.getDeparture_location().getCustomDepthIndex());		
				stmt.setString(9, msg.getArrival_location().getPrimaryLocationString());
			    stmt.setString(10, msg.getArrival_location().getCustomLocationString());
			    stmt.setInt(11, msg.getArrival_location().getCustomDepthIndex());		
			 }
			
			else
			{
				stmt.setString(4, Parser.listToString(aplist));				
				stmt.setString(5, DateUtility.toSQLDateTime(msg.getArrival_time()));
				stmt.setString(6, msg.getArrival_location().getPrimaryLocationString());
				stmt.setString(7, msg.getArrival_location().getCustomLocationString());
				stmt.setInt(8, msg.getArrival_location().getCustomDepthIndex());		
				stmt.setString(9, msg.getDeparture_location().getPrimaryLocationString());
			    stmt.setString(10, msg.getDeparture_location().getCustomLocationString());
			    stmt.setInt(11, msg.getDeparture_location().getCustomDepthIndex());			    
			}	
			stmt.setInt(12,transaction.getDeparture_seatsBooked());
			stmt.setInt(13, totalPrice);		
			stmt.setInt(14, transaction.getState().code);			
			stmt.setInt(15, transaction.getDeparture_timeSlot().code);		
			stmt.setString(16, DateUtility.toSQLDateTime(transaction.getCreationTime()));
			stmt.setInt(17,transaction.isHistoryDeleted() ? 1 : 0);
			stmt.setInt(18,msg.getPaymentMethod().code);
			stmt.setString(19,transaction.getCustomerNote());
			stmt.setString(20, transaction.getProviderNote());
			stmt.setInt(21,transaction.getCustomerEvaluation());
			stmt.setInt(22,transaction.getProviderEvaluation());
			stmt.setInt(23, direction);
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
	
	public static void updateTransactionInDatabase(Transaction transaction) throws TransactionNotFoundException, MessageNotFoundException, UserNotFoundException{
		String query="UPDATE carpoolDAOTransaction SET departure_priceList=?,departure_Time=?,"+
	"departure_primaryLocation=?,departure_customLocation=?,departure_customDepthIndex=?,arrival_primaryLocation=?,"+
	"arrival_customLocation=?,arrival_customDepthIndex=?,departure_seatsBooked=?,totalPrice=?,"+
	"transactionState=?,departure_timeSlot=?,creationTime=?,historyDeleted=?,paymentMethod=?,customerNote=?,providerNote=?,customerEvaluation=?,providerEvaluation=?, transactionType=? where transaction_Id=?";
		Message msg = CarpoolDaoMessage.getMessageById(transaction.getMessageId());		
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
		
			try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
				if(direction==0){
				stmt.setString(1, Parser.listToString(dplist));
				stmt.setString(2, DateUtility.toSQLDateTime(msg.getDeparture_time()));
				stmt.setString(3, msg.getDeparture_location().getPrimaryLocationString());
				stmt.setString(4, msg.getDeparture_location().getCustomLocationString());
				stmt.setInt(5, msg.getDeparture_location().getCustomDepthIndex());						
				stmt.setString(6, msg.getArrival_location().getPrimaryLocationString());
			    stmt.setString(7, msg.getArrival_location().getCustomLocationString());
			    stmt.setInt(8, msg.getArrival_location().getCustomDepthIndex());
				}else{
					stmt.setString(1, Parser.listToString(aplist));				
					stmt.setString(2, DateUtility.toSQLDateTime(msg.getArrival_time()));
					stmt.setString(3, msg.getArrival_location().getPrimaryLocationString());
					stmt.setString(4, msg.getArrival_location().getCustomLocationString());
					stmt.setInt(5, msg.getArrival_location().getCustomDepthIndex());		
					stmt.setString(6, msg.getDeparture_location().getPrimaryLocationString());
				    stmt.setString(7, msg.getDeparture_location().getCustomLocationString());
				    stmt.setInt(8, msg.getDeparture_location().getCustomDepthIndex());			
				}
				stmt.setInt(9,transaction.getDeparture_seatsBooked());					
				stmt.setInt(10, totalPrice);				
				stmt.setInt(11, transaction.getState().code);
				stmt.setInt(12, transaction.getDeparture_timeSlot().code);				
				stmt.setString(13, DateUtility.toSQLDateTime(transaction.getCreationTime()));
				stmt.setInt(14,transaction.isHistoryDeleted() ? 1 : 0);
				stmt.setInt(15,msg.getPaymentMethod().code);
				stmt.setString(16,transaction.getCustomerNote());
				stmt.setString(17, transaction.getProviderNote());
				stmt.setInt(18,transaction.getCustomerEvaluation());
				stmt.setInt(19,transaction.getProviderEvaluation());
				stmt.setInt(20, direction);
			    stmt.setInt(21,transaction.getTransactionId());			
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

	protected static Transaction createTransactionByResultSet(ResultSet rs) throws SQLException, UserNotFoundException, MessageNotFoundException {
		Transaction transaction = null;		
		User provider = CarpoolDaoUser.getUserById(rs.getInt("provider_Id"));
		User customer = CarpoolDaoUser.getUserById(rs.getInt("customer_Id"));
		Message msg = CarpoolDaoMessage.getMessageById(rs.getInt("message_Id"));
		transaction = new Transaction(rs.getInt("transaction_Id"),rs.getInt("provider_Id"),rs.getInt("customer_Id"),rs.getInt("message_Id"),
         Constants.paymentMethod.fromInt(rs.getInt("paymentMethod")),rs.getString("customerNote"),rs.getString("providerNote"),
         rs.getInt("customerEvaluation"),rs.getInt("providerEvaluation"),new LocationRepresentation(rs.getString("departure_primaryLocation"),rs.getString("departure_customLocation"),rs.getInt("departure_customDepthIndex")),
         new LocationRepresentation(rs.getString("arrival_primaryLocation"),rs.getString("arrival_customLocation"),rs.getInt("arrival_customDepthIndex")),DateUtility.DateToCalendar(rs.getTimestamp("departure_Time")),Constants.DayTimeSlot.fromInt(rs.getInt("departure_timeSlot")),rs.getInt("departure_seatsBooked"),
         (ArrayList<Integer>)Parser.stringToList(rs.getString("departure_priceList"), new Integer(0)),Constants.TransactionType.fromInt(rs.getInt("transactionType")),rs.getInt("totalPrice"),Constants.transactionState.fromInt(rs.getInt("transactionState")),
         DateUtility.DateToCalendar(rs.getTimestamp("creationTime")),rs.getBoolean("historyDeleted"));
		transaction.setCustomer(customer);
		transaction.setProvider(provider);
		transaction.setMessage(msg);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

