package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.VerificationState;
import carpool.exception.identityVerification.identityVerificationNotFound;
import carpool.model.identityVerification.DriverVerification;
import carpool.model.identityVerification.PassengerVerification;



public class CarpoolDaoDriver {

	public static ArrayList<DriverVerification> getDriverVerificationsByUserId(int userId){

		ArrayList<DriverVerification> dlist = new ArrayList<DriverVerification>();

		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "SELECT * FROM carpoolDAODriver where user_Id = ?";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, userId);
			rs = stmt.executeQuery();
			while(rs.next()){				
				dlist.add(createDriverVerificationByResultSet(rs));
			}			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}  


		return dlist;
	}

	public static DriverVerification addDriverToDatabases(DriverVerification driver){

		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "INSERT INTO carpoolDAODriver(user_Id,realName,licenseNum,licenseType,submissionDate," +
				"expireDate,v_state,reviewDate,reviewer_Id,recommender_Id,licenseIssueDate,licenseImgLink,verificationType)"+"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, driver.getUserId());
			stmt.setString(2, driver.getRealName());
			stmt.setString(3, driver.getLicenseNumber());
			stmt.setInt(4, driver.getLicenseType().code);
			stmt.setString(5, DateUtility.toSQLDateTime(driver.getSubmissionDate()));
			stmt.setString(6, DateUtility.toSQLDateTime(driver.getExpireDate()));
			stmt.setInt(7, driver.getState().code);
			stmt.setString(8,DateUtility.toSQLDateTime(driver.getReviewDate()));
			stmt.setInt(9, driver.getReviewerId());
			stmt.setInt(10, driver.getRecommenderId());
			stmt.setString(11, DateUtility.toSQLDateTime(driver.getLicenseIssueDate()));
			stmt.setString(12, driver.getLicenseImgLink());
			stmt.setInt(13, driver.getType().code);
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			driver.setVerificationId(rs.getInt(1));
		} catch(SQLException e){
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return driver;
	}

	public static void updateDriverVerificationInDatabases(DriverVerification driver) throws identityVerificationNotFound{
		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "UPDATE carpoolDAODriver SET user_Id=?,realName=?,licenseNum=?,licenseType=?,submissionDate=?," +
				"expireDate=?,v_state=?,reviewDate=?,reviewer_Id=?,recommender_Id=?,licenseIssueDate=?,licenseImgLink=?,verificationType=?"+
				" WHERE v_Id = ?";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, driver.getUserId());
			stmt.setString(2, driver.getRealName());
			stmt.setString(3, driver.getLicenseNumber());
			stmt.setInt(4, driver.getLicenseType().code);
			stmt.setString(5, DateUtility.toSQLDateTime(driver.getSubmissionDate()));
			stmt.setString(6, DateUtility.toSQLDateTime(driver.getExpireDate()));
			stmt.setInt(7, driver.getState().code);
			stmt.setString(8,DateUtility.toSQLDateTime(driver.getReviewDate()));
			stmt.setInt(9, driver.getReviewerId());
			stmt.setInt(10, driver.getRecommenderId());
			stmt.setString(11, DateUtility.toSQLDateTime(driver.getLicenseIssueDate()));
			stmt.setString(12, driver.getLicenseImgLink());
			stmt.setInt(13, driver.getType().code);
			stmt.setInt(14, driver.getVerificationId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new identityVerificationNotFound();
			}
		}
		catch(SQLException e){
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 

	}

	public static void deleteDriverVerificationInDatabase(long v_id){
		String query = "DELETE from carpoolDAODriver where v_Id = ?";
		PreparedStatement stmt = null;
		Connection conn = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setLong(1, v_id);
			stmt.executeUpdate();	
		}catch (SQLException e) {
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 
	}

	public static ArrayList<DriverVerification> getDriverVerifications(VerificationState... states){
		String query = "SELECT * from carpoolDAODriver";
		ArrayList<DriverVerification> dlist = new ArrayList<DriverVerification>();

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			
			if(states.length > 0){
				query += " where v_state =" + states[0].code;
				for(int i = 1; i < states.length; i++){
					query += " or v_state =" + states[i].code;
				}
			}
			
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			
			while(rs.next()){				
				dlist.add(createDriverVerificationByResultSet(rs));		
			}			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}  
		return dlist;
	}	

	public static DriverVerification getDriverVerificationById(int l,Connection...connections) throws identityVerificationNotFound{
		String query = "SELECT * FROM carpoolDAODriver where v_Id = ?";
		DriverVerification driver = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);

			stmt.setLong(1, l);
			rs = stmt.executeQuery();
			if(rs.next()){
				driver = createDriverVerificationByResultSet(rs);
				if (driver.getState() == VerificationState.verified && driver.hasExpired()){
					driver.setState(VerificationState.expired);
					CarpoolDaoDriver.updateDriverVerificationInDatabases(driver);
					PassengerVerification passengerVerification = CarpoolDaoPassenger.getPassengerVerificationById(driver.getAssociatedPassengerVerificationId(), connections);
					passengerVerification.setState(VerificationState.expired);
					CarpoolDaoPassenger.updatePassengerVerificationInDatabases(passengerVerification);
				}
			}else{
				throw new identityVerificationNotFound();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		}

		return driver;
	}

	private static DriverVerification createDriverVerificationByResultSet(ResultSet rs) throws SQLException {
		return new DriverVerification(EnumConfig.VerificationType.fromInt(rs.getInt("verificationType")),rs.getInt("v_Id"),rs.getInt("user_Id"),
				rs.getString("realName"),rs.getString("licenseNum"),EnumConfig.LicenseType.fromInt(rs.getInt("licenseType")),
				DateUtility.DateToCalendar(rs.getTimestamp("submissionDate")),DateUtility.DateToCalendar(rs.getTimestamp("expireDate")),
				EnumConfig.VerificationState.fromInt(rs.getInt("v_state")),DateUtility.DateToCalendar(rs.getTimestamp("reviewDate")),
				rs.getInt("reviewer_Id"),rs.getInt("recommender_Id"),DateUtility.DateToCalendar(rs.getTimestamp("licenseIssueDate")),rs.getString("licenseImgLink"));
	}

}
