package carpool.clean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.constants.Constants.messageType;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.model.Message;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;
public class MessageCleaner extends CarpoolDaoMessage {

	
	public static void Clean(){
		
    Calendar currentDate = Calendar.getInstance();
    String ct=DateUtility.toSQLDateTime(currentDate);
    
	String query = "SELECT * from carpoolDAOMessage WHERE((isRoundTrip LIKE ? AND arrival_Time < ?) OR(isRoundTrip LIKE ? AND departure_Time < ?))AND messageState LIKE ?;";
	try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
		stmt.setInt(1, 1);			
		stmt.setString(2, ct);							
		stmt.setInt(3, 0);	
		stmt.setString(4, ct);
		stmt.setInt(5, 2);			
		ResultSet rs = stmt.executeQuery();			
			while(rs.next()){	
				Message msg = CarpoolDaoMessage.createMessageByResultSet(rs, false);
			    msg.setState(Constants.messageState.fromInt(1));
			    CarpoolDaoMessage.UpdateMessageInDatabase(msg);
				}			
	} catch (SQLException e) {
		e.printStackTrace();
		DebugLog.d(e.getMessage());
	} catch (UserNotFoundException e) {
		e.printStackTrace();
	} catch (MessageNotFoundException e) {		
		e.printStackTrace();
	}
  }
	
}
