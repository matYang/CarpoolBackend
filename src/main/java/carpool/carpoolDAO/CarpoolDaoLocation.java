package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DebugLog;
import carpool.exception.location.LocationNotFoundException;
import carpool.model.Location;

public class CarpoolDaoLocation {

	public static Location addLocationToDatabases(Location location){
		String query = "INSERT INTO carpoolDAOLocation (province,city,region,pointName,pointAddress,lat,lng,match_Id)values(?,?,?,?,?,?,?,?)";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){			
			stmt.setString(1, location.getProvince());
			stmt.setString(2, location.getCity());
			stmt.setString(3, location.getRegion());
			stmt.setString(4, location.getPointName());
			stmt.setString(5, location.getPointAddress());
			stmt.setDouble(6, location.getLat());
			stmt.setDouble(7, location.getLng());
			stmt.setLong(8,location.getMatch());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			location.setId(rs.getInt(1));
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}

		return location;
	}

	public static void updateLocationInDatabases(Location location) throws LocationNotFoundException{
		String query = "UPDATE carpoolDAOLocation SET province=?, city=?, region=?, pointName=?, pointAddress=?, lat=?, lng=?,match_Id=? where id = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, location.getProvince());
			stmt.setString(2, location.getCity());
			stmt.setString(3, location.getRegion());
			stmt.setString(4, location.getPointName());
			stmt.setString(5, location.getPointAddress());
			stmt.setDouble(6, location.getLat());
			stmt.setDouble(7, location.getLng());
			stmt.setLong(8, location.getMatch());
			stmt.setInt(9, location.getId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new LocationNotFoundException();
			} 
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}
	}

	public static void deleteLocation(int id){
		String query = "DELETE from carpoolDAOLocation where id = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, id);
			stmt.executeUpdate();	
		}catch (SQLException e) {
			DebugLog.d(e);
		}
	}

	public static Location getLocationById(int location_id)throws LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOLocation where id=?";
		Location location = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setInt(1, location_id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				location = createLocationByResultSet(rs);
			}else{
				throw new LocationNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}
		return location;
	}

	public static ArrayList<Location> getAllLocation(){
		ArrayList<Location> list = new ArrayList<Location>();
		String query = "select * from carpoolDAOLocation";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				list.add(createLocationByResultSet(rs));
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}
		return list;
	}

	private static Location createLocationByResultSet(ResultSet rs) throws SQLException {
		return new Location(rs.getInt("id"),rs.getString("province"),rs.getString("city"),rs.getString("region"),rs.getString("pointName"),rs.getString("pointAddress"),rs.getDouble("lat"),rs.getDouble("lng"),rs.getLong("match_Id"));
	}


}
