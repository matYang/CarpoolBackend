package carpool.carpoolDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import carpool.common.DebugLog;
import carpool.exception.location.LocationNotFoundException;
import carpool.model.Location;
import carpool.model.representation.DefaultLocationRepresentation;

public class CarpoolDaoLocation {

	public static boolean isLocationPoolEmpty(){
		String query = "SELECT COUNT(*) AS total FROM carpoolDAOLocation";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				if(rs.getInt("total")<=0){
					return true;
				}else{
					return false;
				}
			}
		} catch (SQLException e) {
			DebugLog.d(e);
		}
		return false;
	}

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
			DebugLog.d(e);
		}

		return location;
	}

	public static Location getLocationById(long l)throws LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOLocation where id=?";
		Location location = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setLong(1, l);
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


	public static ArrayList<DefaultLocationRepresentation> getDefaultLocationRepresentations(){
		ArrayList<DefaultLocationRepresentation> list = new ArrayList<DefaultLocationRepresentation>();
		Location location = null;
		String query = "SELECT * FROM defaultLocations JOIN carpoolDAOLocation ON (carpoolDAOLocation.match_Id = defaultLocations.id and carpoolDAOLocation.id = defaultLocations.referenceNum);";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				location = createLocationByResultSet(rs);
				if(location !=null){
					list.add(new DefaultLocationRepresentation(location.getMatch(),location.getId(),location,rs.getInt("radius"), rs.getString("synonyms")));
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}
		return list;
	}	


	public static DefaultLocationRepresentation getDefaultLocationRepresentationById(long id){
		String query = "SELECT * FROM defaultLocations JOIN carpoolDAOLocation ON (carpoolDAOLocation.match_Id = ? and carpoolDAOLocation.id = defaultLocations.referenceNum);";		
		Location location = null;
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				location = createLocationByResultSet(rs);
				if(location !=null){	
					return new DefaultLocationRepresentation(location.getMatch(),rs.getLong("referenceNum"),location,rs.getInt("radius"), rs.getString("synonyms"));

				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}
		return null;
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
			stmt.setLong(9, location.getId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new LocationNotFoundException();
			} 
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}
	}

	public static void deleteLocation(long l){
		String query = "DELETE from carpoolDAOLocation where id = ?";
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setLong(1, l);
			stmt.executeUpdate();	
		}catch (SQLException e) {
			DebugLog.d(e);
		}
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
		return new Location(rs.getLong("id"),rs.getString("province"),rs.getString("city"),rs.getString("region"),rs.getString("pointName"),rs.getString("pointAddress"),rs.getDouble("lat"),rs.getDouble("lng"),rs.getLong("match_Id"));
	}


	public static DefaultLocationRepresentation addDefaultLocation(DefaultLocationRepresentation defaultLocationRep) throws LocationNotFoundException{
		String query = "INSERT INTO defaultLocations (referenceNum,radius,synonyms) values (?,?,?)";
		Location location = null;		
		location = CarpoolDaoLocation.addLocationToDatabases(defaultLocationRep.getLocation());
		defaultLocationRep.setReferenceId(location.getId());
		try(PreparedStatement stmt = CarpoolDaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){			
			stmt.setLong(1, defaultLocationRep.getReferenceId());
			stmt.setInt(2, defaultLocationRep.getRadius());
			stmt.setString(3, defaultLocationRep.getSynonyms());			
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			defaultLocationRep.setId(rs.getInt(1));
			location.setMatch(defaultLocationRep.getId());
			CarpoolDaoLocation.updateLocationInDatabases(location);			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}
		return defaultLocationRep;

	}

}
