package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.dbservice.LocationDaoService;
import carpool.exception.location.LocationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.locationService.CarpoolLocationLoader;
import carpool.model.Letter;
import carpool.model.Location;
import carpool.model.representation.DefaultLocationRepresentation;

public class CarpoolDaoLocation {

	public static boolean isLocationPoolEmpty(){
		String query = "SELECT COUNT(*) AS total FROM carpoolDAOLocation";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			if(rs.next()){
				if(rs.getInt("total")<=0){
					return true;
				}else{
					return false;
				}
			}
		} catch (SQLException e) {
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return false;
	}

	public static Location addLocationToDatabases(Location location,Connection...connections){
		PreparedStatement stmt = null;
		Connection conn =  CarpoolDaoBasic.getConnection(connections);
		ResultSet rs = null;

		String query = "INSERT INTO carpoolDAOLocation (province,city,region,pointName,pointAddress,lat,lng,match_Id)values(?,?,?,?,?,?,?,?)";
		try {		
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, location.getProvince());
			stmt.setString(2, location.getCity());
			stmt.setString(3, location.getRegion());
			stmt.setString(4, location.getPointName());
			stmt.setString(5, location.getPointAddress());
			stmt.setDouble(6, location.getLat());
			stmt.setDouble(7, location.getLng());
			stmt.setLong(8,location.getMatch());
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			location.setId(rs.getInt(1));
		} catch(SQLException e){
			DebugLog.d(e);
		} finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 

		return location;
	}

	public static Location getLocationById(long l,Connection...connections)throws LocationNotFoundException{
		String query = "SELECT * FROM carpoolDAOLocation where id=?";
		Location location = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);

			stmt.setLong(1, l);
			rs = stmt.executeQuery();
			if(rs.next()){
				location = createLocationByResultSet(rs);
			}else{
				throw new LocationNotFoundException();
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 
		return location;
	}


	public static ArrayList<DefaultLocationRepresentation> getDefaultLocationRepresentations(){
		ArrayList<DefaultLocationRepresentation> list = new ArrayList<DefaultLocationRepresentation>();
		Location location = null;
		String query = "SELECT * FROM defaultLocations JOIN carpoolDAOLocation ON (carpoolDAOLocation.match_Id = defaultLocations.id and carpoolDAOLocation.id = defaultLocations.referenceNum);";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{			
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();
			while(rs.next()){
				location = createLocationByResultSet(rs);
				if(location !=null){
					list.add(new DefaultLocationRepresentation(location.getMatch(),location.getId(),location,rs.getInt("radius"), rs.getString("synonyms")));
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return list;
	}	


	public static DefaultLocationRepresentation getDefaultLocationRepresentationById(long id){
		String query = "SELECT * FROM defaultLocations JOIN carpoolDAOLocation ON (carpoolDAOLocation.match_Id = ? and carpoolDAOLocation.id = defaultLocations.referenceNum);";		
		Location location = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			if(rs.next()){
				location = createLocationByResultSet(rs);
				if(location !=null){	
					return new DefaultLocationRepresentation(location.getMatch(),rs.getLong("referenceNum"),location,rs.getInt("radius"), rs.getString("synonyms"));

				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return null;
	}



	public static void updateLocationInDatabases(Location location,Connection...connections) throws LocationNotFoundException{
		String query = "UPDATE carpoolDAOLocation SET province=?, city=?, region=?, pointName=?, pointAddress=?, lat=?, lng=?,match_Id=? where id = ?";
		PreparedStatement stmt = null;
		Connection conn = null;
		try{
			conn = CarpoolDaoBasic.getConnection(connections);
			stmt = conn.prepareStatement(query);

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
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 
	}

	public static void deleteLocation(long l){
		String query = "DELETE from carpoolDAOLocation where id = ?";
		PreparedStatement stmt = null;
		Connection conn = null;
		try{
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);

			stmt.setLong(1, l);
			stmt.executeUpdate();	
		}catch (SQLException e) {
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, null,true);
		} 
	}



	public static ArrayList<Location> getAllLocation(){
		ArrayList<Location> list = new ArrayList<Location>();
		String query = "select * from carpoolDAOLocation";

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{			
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			while(rs.next()){
				list.add(createLocationByResultSet(rs));
			}
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs,true);
		} 
		return list;
	}

	private static Location createLocationByResultSet(ResultSet rs) throws SQLException {
		return new Location(rs.getLong("id"),rs.getString("province"),rs.getString("city"),rs.getString("region"),rs.getString("pointName"),rs.getString("pointAddress"),rs.getDouble("lat"),rs.getDouble("lng"),rs.getLong("match_Id"));
	}

	private static void updateDefaultLocation(DefaultLocationRepresentation defaultLocationRep,Connection...connections) throws LocationNotFoundException{
		String query = "UPDATE defaultLocations SET radius=?,synonyms = ? where id=?";	
		//May consider not updating location later...
		Connection conn =CarpoolDaoBasic.getConnection(connections);
		CarpoolDaoLocation.updateLocationInDatabases(defaultLocationRep.getLocation(),conn);		
		PreparedStatement stmt = null;

		try{		
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, defaultLocationRep.getRadius());
			stmt.setString(2, defaultLocationRep.getSynonyms());
			stmt.setLong(3, defaultLocationRep.getId());
			int recordsAffected = stmt.executeUpdate();
			if(recordsAffected==0){
				throw new LocationNotFoundException();
			} 
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources( conn,  stmt,  null,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 
	}
	public static DefaultLocationRepresentation addDefaultLocation(DefaultLocationRepresentation defaultLocationRep,Connection...connections) throws LocationNotFoundException{
		String query = "INSERT INTO defaultLocations (referenceNum,radius,synonyms) values (?,?,?)";
		Location location = null;		
		Connection conn = CarpoolDaoBasic.getConnection(connections);
		location = CarpoolDaoLocation.addLocationToDatabases(defaultLocationRep.getLocation(),conn);
		defaultLocationRep.setReferenceId(location.getId());
		PreparedStatement stmt = null;	
		ResultSet rs = null;

		try{		
			stmt = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, defaultLocationRep.getReferenceId());
			stmt.setInt(2, defaultLocationRep.getRadius());
			stmt.setString(3, defaultLocationRep.getSynonyms());			
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			defaultLocationRep.setId(rs.getInt(1));
			location.setMatch(defaultLocationRep.getId());
			CarpoolDaoLocation.updateLocationInDatabases(location,conn);			
		}catch(SQLException e){
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources( conn,  stmt,  rs,CarpoolDaoBasic.shouldConnectionClose(connections));
		} 
		return defaultLocationRep;

	}

	private static DefaultLocationRepresentation defaultLocationCopy(DefaultLocationRepresentation d1,
			DefaultLocationRepresentation d2){
		d1.setId(d2.getId());		
		d1.setReferenceId(d2.getReferenceId());
		d1.setLocation(d2.getLocation());
		return d1;

	}

	private static DefaultLocationRepresentation getOldLocation(DefaultLocationRepresentation dr, 
			ArrayList<DefaultLocationRepresentation>list){
		for(DefaultLocationRepresentation dlr: list){
			if(dlr.getLocation().getLat().equals(dr.getLocation().getLat())&&dlr.getLocation().getLng().equals(dr.getLocation().getLng())){
				if(dlr.getRadius()!=dr.getRadius()||!dlr.getSynonyms().equals(dr.getSynonyms())||!dlr.getLocation().getCity().equals(dr.getLocation().getCity())
						||!dlr.getLocation().getPointAddress().equals(dr.getLocation().getPointAddress())||!dlr.getLocation().getPointName().equals(dr.getLocation().getPointName())
						||!dlr.getLocation().getProvince().equals(dr.getLocation().getProvince())||!dlr.getLocation().getRegion().equals(dr.getLocation().getRegion())){
					return defaultLocationCopy(dr,dlr);
				}else{
					return dr;
				}

			}
		}
		return null;

	}

	public static void reloadDefaultLocations() throws LocationException, ValidationException, LocationNotFoundException {

		int defaultLocationsNum = 0;

		ArrayList<DefaultLocationRepresentation> dlist = new ArrayList<DefaultLocationRepresentation>();		
		dlist = getDefaultLocationRepresentations();

		ArrayList<HashMap<String, String>> bufferList = CarpoolLocationLoader.loadLocationFromFile("LocationData.txt");
		defaultLocationsNum = bufferList.size();

		Connection conn = CarpoolDaoBasic.getSQLConnection();

		for (HashMap<String, String> bufferMap : bufferList){
			Location location = new Location(bufferMap.get("province"),bufferMap.get("city"),bufferMap.get("region"),bufferMap.get("name"),bufferMap.get("address"),Double.parseDouble(bufferMap.get("lat")),Double.parseDouble(bufferMap.get("lng")),-1l);
			DefaultLocationRepresentation defaultLocationRep = new DefaultLocationRepresentation(location, Integer.parseInt(bufferMap.get("radius")), bufferMap.get("synonyms"));
			DefaultLocationRepresentation tempdlr = getOldLocation(defaultLocationRep,dlist);
			if(tempdlr!=null){
				//Update old
				if(!tempdlr.equals(defaultLocationRep)){
					updateDefaultLocation(tempdlr,conn);
				}				
			}else{
				//Add new
				addDefaultLocation(defaultLocationRep,conn);
			}
		}
		CarpoolDaoBasic.closeResources(conn, null, null, true);


	}

}
