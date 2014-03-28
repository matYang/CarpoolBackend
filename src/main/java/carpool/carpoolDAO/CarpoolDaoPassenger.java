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
import carpool.model.identityVerification.PassengerVerification;

public class CarpoolDaoPassenger {

	public static ArrayList<PassengerVerification> getPassengerVerificationsByUserId(int userId){

		ArrayList<PassengerVerification> plist = new ArrayList<PassengerVerification>();

		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "SELECT * FROM carpoolDAOPassenger where user_Id = ?";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, userId);
			rs = stmt.executeQuery();
			while(rs.next()){				
				plist.add(createPassengerVerificationByResultSet(rs));
			}			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}  


		return plist;
	}

	public static PassengerVerification addPassengerToDatabases(PassengerVerification passenger){

		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "INSERT INTO carpoolDAOPassenger(user_Id,realName,licenseNum,licenseType,submissionDate," +
				"expireDate,v_state,reviewDate,reviewer_Id,recommender_Id,frontImgLink,backImgLink,originType,verificationType)"+"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, passenger.getUserId());
			stmt.setString(2, passenger.getRealName());
			stmt.setString(3, passenger.getLicenseNumber());
			stmt.setInt(4, passenger.getLicenseType().code);
			stmt.setString(5, DateUtility.toSQLDateTime(passenger.getSubmissionDate()));
			stmt.setString(6, DateUtility.toSQLDateTime(passenger.getExpireDate()));
			stmt.setInt(7, passenger.getState().code);
			stmt.setString(8,DateUtility.toSQLDateTime(passenger.getReviewDateDate()));
			stmt.setInt(9, passenger.getReviewerId());
			stmt.setInt(10, passenger.getRecommenderId());
			stmt.setString(11, passenger.getFrontImgLink());
			stmt.setString(12, passenger.getBackImgLink());
			stmt.setInt(13, passenger.getOrigin().code);
			stmt.setInt(14, passenger.getType().code);
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			passenger.setVerificationId(rs.getInt(1));
		} catch(SQLException e){
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return passenger;
	}

	public static void updatePassengerVerificationInDatabases(PassengerVerification passenger) throws identityVerificationNotFound{
		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		String query = "UPDATE carpoolDAOPassenger SET user_Id=?,realName=?,licenseNum=?,licenseType=?,submissionDate=?," +
				"expireDate=?,v_state=?,reviewDate=?,reviewer_Id=?,recommender_Id=?,frontImgLink=?,backImgLink=?,originType=?,verificationType=?"+
				" where v_Id = ?";
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, passenger.getUserId());
			stmt.setString(2, passenger.getRealName());
			stmt.setString(3, passenger.getLicenseNumber());
			stmt.setInt(4, passenger.getLicenseType().code);
			stmt.setString(5, DateUtility.toSQLDateTime(passenger.getSubmissionDate()));
			stmt.setString(6, DateUtility.toSQLDateTime(passenger.getExpireDate()));
			stmt.setInt(7, passenger.getState().code);
			stmt.setString(8,DateUtility.toSQLDateTime(passenger.getReviewDateDate()));
			stmt.setInt(9, passenger.getReviewerId());
			stmt.setInt(10, passenger.getRecommenderId());
			stmt.setString(11, passenger.getFrontImgLink());
			stmt.setString(12, passenger.getBackImgLink());
			stmt.setInt(13, passenger.getOrigin().code);
			stmt.setInt(14, passenger.getType().code);
			stmt.setInt(15, passenger.getVerificationId());
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

	public static void deletePassengerVerificationInDatabase(long v_id){
		String query = "DELETE from carpoolDAOPassenger where v_Id = ?";
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

	public static ArrayList<PassengerVerification> getPassengerVerifications(VerificationState... states){
		String query = "SELECT * FROM carpoolDAOPassenger;";
		ArrayList<PassengerVerification> plist = new ArrayList<PassengerVerification>();

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
				plist.add(createPassengerVerificationByResultSet(rs));
			}			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		}  
		return plist;
	}

	public static PassengerVerification getPassengerVerificationById(int l,Connection...connections) throws identityVerificationNotFound{
		String query = "SELECT * FROM carpoolDAOPassenger where v_Id=?";
		PassengerVerification passenger = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);

			stmt.setLong(1, l);
			rs = stmt.executeQuery();
			if(rs.next()){
				passenger = createPassengerVerificationByResultSet(rs);
				if (passenger.getState() == VerificationState.verified && passenger.hasExpired()){
					passenger.setState(VerificationState.expired);
					CarpoolDaoPassenger.updatePassengerVerificationInDatabases(passenger);
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
		return passenger;
	}

	private static PassengerVerification createPassengerVerificationByResultSet(ResultSet rs) throws SQLException {
		return new PassengerVerification(EnumConfig.VerificationType.fromInt(rs.getInt("verificationType")),rs.getInt("v_Id"),rs.getInt("user_Id"),
				rs.getString("realName"),rs.getString("licenseNum"),EnumConfig.LicenseType.fromInt(rs.getInt("licenseType")),
				DateUtility.DateToCalendar(rs.getTimestamp("submissionDate")),DateUtility.DateToCalendar(rs.getTimestamp("expireDate")),
				EnumConfig.VerificationState.fromInt(rs.getInt("v_state")),DateUtility.DateToCalendar(rs.getTimestamp("reviewDate")),
				rs.getInt("reviewer_Id"),rs.getInt("recommender_Id"),rs.getString("frontImgLink"),rs.getString("backImgLink"),EnumConfig.PassengerVerificationOrigin.frontInt(rs.getInt("originType")));
	}
}
