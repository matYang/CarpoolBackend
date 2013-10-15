package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.Parser;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;

public class CarpoolDaoTransaction {

	public static void addTransactionToDatabase(int providerId, int customerId,int messageId,int seatsNumber) throws MessageNotFoundException, UserNotFoundException{
		String query = "INSERT INTO carpoolDAOTransaction (provider_Id,customer_Id,message_Id,departure_priceList,departure_Time,"+
	"departure_primaryLocation,departure_customLocation,departure_customDepthIndex,arrival_priceList,arrival_Time,arrival_primaryLocation,"+
	"arrival_customLocation,arrival_customDepthIndex,seatsNumber,departure_seatsBooked,arrival_seatsBooked,totalPrice,isRoundTrip,"
	+"messageType,messageState)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	Message msg = CarpoolDaoMessage.getMessageById(messageId);
	boolean isRoundTrip = msg.isRoundTrip();
	int totalPrice = 0;
	ArrayList<Integer> dplist = new ArrayList<Integer>();
	dplist = msg.getDeparture_priceList();
	for(int i=0; i<dplist.size();i++){
		totalPrice += dplist.get(i);
	}
	ArrayList<Integer> arlist = new ArrayList<Integer>();
	if(isRoundTrip){
		
	    arlist = msg.getArrival_priceList();
		for(int i=0; i<arlist.size();i++){
			totalPrice += arlist.get(i);
		}
	}
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setInt(1,providerId);			
			stmt.setInt(2, customerId);
			stmt.setInt(3, messageId);
			stmt.setString(4, Parser.listToString(dplist));
			stmt.setString(5, DateUtility.toSQLDateTime(msg.getDeparture_time()));
			stmt.setString(6, msg.getDeparture_location().getPrimaryLocationString());
			stmt.setString(7, msg.getDeparture_location().getCustomLocationString());
			stmt.setInt(8, msg.getDeparture_location().getCustomDepthIndex());	
			if(isRoundTrip){
			stmt.setString(9, Parser.listToString(arlist));
			stmt.setString(10, DateUtility.toSQLDateTime(msg.getArrival_time()));
			stmt.setString(11, msg.getArrival_location().getPrimaryLocationString());
		    stmt.setString(12, msg.getArrival_location().getCustomLocationString());
		    stmt.setInt(13, msg.getArrival_location().getCustomDepthIndex());		
			}else{
		    stmt.setString(9, null);
			stmt.setString(10, null);
			stmt.setString(11, null);
			stmt.setString(12, null);
			stmt.setInt(13, -1);	
			}
		    stmt.setInt(14,seatsNumber);	
		    stmt.setInt(15, msg.getDeparture_seatsBooked());
		    if(isRoundTrip){
		    stmt.setInt(16, msg.getArrival_seatsBooked());
		    }
		    else{
		    stmt.setInt(16, -1);
		    }
			stmt.setInt(17, totalPrice);
			stmt.setInt(18, isRoundTrip ? 1 : 0);
			stmt.setInt(19, msg.getType().code);
			stmt.setInt(20, msg.getState().code);		
			stmt.executeUpdate();	      			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e.getMessage());
		}
	 
		
	}
	
}
