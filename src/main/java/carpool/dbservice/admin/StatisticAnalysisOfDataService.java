package carpool.dbservice.admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import carpool.aws.AwsMain;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.DatabaseConfig;
import carpool.model.representation.SearchRepresentation;

public class StatisticAnalysisOfDataService {	

	public static HashMap<String,ArrayList<Entry<Long,Integer>>> GetTheEntireMap(){
		HashMap<String,HashMap> BigMap = new HashMap<String,HashMap>();
		HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();
		//Departure||Arrival-->UserSR||Databases
		BigMap.put(DatabaseConfig.UserSRDeparture, UserSRDeparture);
		BigMap.put(DatabaseConfig.UserSRArrival, UserSRArrival);
		BigMap.put(DatabaseConfig.DatabasesDeparture, DatabasesDeparture);
		BigMap.put(DatabaseConfig.DatabasesArrival, DatabasesArrival);
		//UserSR
		int total =0;
		String query = "SELECT COUNT(*) AS total FROM carpoolDAOUser";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{		
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();			
			if(rs.next()){									
				total = rs.getInt("total");
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs, false);
		}
		ArrayList<SearchRepresentation> srlist = new ArrayList<SearchRepresentation>();
		while(total>0){
			srlist = AwsMain.getUserSearchHistory(total);
			setUserSR(BigMap,srlist,"both");
			total--;
		}
		//Databases
		String query2 = "SELECT * from carpoolDAOMessage";
		try{			
			stmt = conn.prepareStatement(query2);
			rs = stmt.executeQuery();			
			while(rs.next()){
				setMessagePost(BigMap,rs.getLong("departureMatch_Id"),rs.getLong("arrivalMatch_Id"),"both");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs, true);
		}
		ArrayList<Entry<Long,Integer>> UserSRDepartureList = new ArrayList<Entry<Long,Integer>>();
		ArrayList<Entry<Long,Integer>> UserSRArrivalList = new ArrayList<Entry<Long,Integer>>();
		ArrayList<Entry<Long,Integer>> DatabasesDepartureList = new ArrayList<Entry<Long,Integer>>();
		ArrayList<Entry<Long,Integer>> DatabasesArrivalList = new ArrayList<Entry<Long,Integer>>();
		UserSRDepartureList = sortMap(BigMap.get(DatabaseConfig.UserSRDeparture));
		UserSRArrivalList = sortMap(BigMap.get(DatabaseConfig.UserSRArrival));
		DatabasesDepartureList = sortMap(BigMap.get(DatabaseConfig.DatabasesDeparture));
		DatabasesArrivalList = sortMap(BigMap.get(DatabaseConfig.DatabasesArrival));

		HashMap<String,ArrayList<Entry<Long,Integer>>> newMap = new HashMap<String,ArrayList<Entry<Long,Integer>>>();
		newMap.put(DatabaseConfig.UserSRDeparture, UserSRDepartureList);
		newMap.put(DatabaseConfig.UserSRArrival, UserSRArrivalList);
		newMap.put(DatabaseConfig.DatabasesDeparture, DatabasesDepartureList);
		newMap.put(DatabaseConfig.DatabasesArrival, DatabasesArrivalList);
		return newMap;
	}

	public static ArrayList<Entry<Long,Integer>> getSpecificList(String str){
		HashMap<String,HashMap> BigMap = new HashMap<String,HashMap>();
		HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();
		//Departure||Arrival-->UserSR||Databases
		BigMap.put(DatabaseConfig.UserSRDeparture, UserSRDeparture);
		BigMap.put(DatabaseConfig.UserSRArrival, UserSRArrival);
		BigMap.put(DatabaseConfig.DatabasesDeparture, DatabasesDeparture);
		BigMap.put(DatabaseConfig.DatabasesArrival, DatabasesArrival);

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		if(str.equals(DatabaseConfig.UserSRDeparture)||str.equals(DatabaseConfig.UserSRArrival)){
			int total =0;
			String query = "SELECT COUNT(*) AS total FROM carpoolDAOUser";
			try{	
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query);
				rs = stmt.executeQuery();			
				if(rs.next()){									
					total = rs.getInt("total");
				}			
			} catch (SQLException e) {
				e.printStackTrace();
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, rs, true);
			}
			ArrayList<SearchRepresentation> srlist = new ArrayList<SearchRepresentation>();
			while(total>0){
				srlist = AwsMain.getUserSearchHistory(total);
				setUserSR(BigMap,srlist,str.equals(DatabaseConfig.UserSRDeparture) ? "departure" : "arrival");
				total--;
			}
			if(str.equals(DatabaseConfig.UserSRDeparture)){
				ArrayList<Entry<Long,Integer>> UserSRDepartureList = new ArrayList<Entry<Long,Integer>>();
				UserSRDepartureList = sortMap(BigMap.get(DatabaseConfig.UserSRDeparture));
				return UserSRDepartureList;
			}else{
				ArrayList<Entry<Long,Integer>> UserSRArrivalList = new ArrayList<Entry<Long,Integer>>();
				UserSRArrivalList = sortMap(BigMap.get(DatabaseConfig.UserSRArrival));
				return UserSRArrivalList;
			}

		}else if(str.equals(DatabaseConfig.DatabasesDeparture)||str.equals(DatabaseConfig.DatabasesArrival)){
			String query2 = "SELECT * from carpoolDAOMessage";
			try{
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query2);
				rs = stmt.executeQuery();
				while(rs.next()){
					setMessagePost(BigMap,rs.getLong("departureMatch_Id"),rs.getLong("arrivalMatch_Id"),str.equals(DatabaseConfig.DatabasesDeparture) ? "departure" : "arrival");
				}
			}catch (SQLException e) {
				e.printStackTrace();
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.closeResources(conn, stmt, rs, true);
			}
			if(str.equals(DatabaseConfig.DatabasesDeparture)){
				ArrayList<Entry<Long,Integer>> DatabasesDepartureList = new ArrayList<Entry<Long,Integer>>();
				DatabasesDepartureList = sortMap(BigMap.get(DatabaseConfig.DatabasesDeparture));
				return DatabasesDepartureList;
			}else{
				ArrayList<Entry<Long,Integer>> DatabasesArrivalList = new ArrayList<Entry<Long,Integer>>();
				DatabasesArrivalList = sortMap(BigMap.get(DatabaseConfig.DatabasesArrival));
				return DatabasesArrivalList;
			}
		}
		return null;
	}



	public static void setUserSR(HashMap<String,HashMap> map,ArrayList<SearchRepresentation> srlist,String waydirection){			
		if(waydirection.equals("departure")){		
			for(int i=0; i<srlist.size(); i++){
				if(map.get(DatabaseConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id())==null){
					map.get(DatabaseConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),1);
				}else{
					int tempCount = (int) map.get(DatabaseConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id());
					map.get(DatabaseConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),tempCount+1);
				}

			}
		}else if(waydirection.equals("arrival")){

			for(int i=0; i<srlist.size(); i++){
				if(map.get(DatabaseConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id())==null){
					map.get(DatabaseConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),1);
				}else{
					int tempCount = (int) map.get(DatabaseConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id());
					map.get(DatabaseConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),tempCount+1);
				}

			}
		}else{		
			for(int i=0; i<srlist.size(); i++){
				if(map.get(DatabaseConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id())==null){
					map.get(DatabaseConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),1);
				}else{					
					int tempCount = (int) map.get(DatabaseConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id());
					map.get(DatabaseConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),tempCount+1);
				}

				if(map.get(DatabaseConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id())==null){
					map.get(DatabaseConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),1);
				}else{
					int tempCount = (int) map.get(DatabaseConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id());
					map.get(DatabaseConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),tempCount+1);
				}

			}
		}

	}

	public static void setMessagePost(HashMap<String,HashMap> map,Long departureId, Long arrivalId, String waydirection){
		if(waydirection.equals("departure")){
			if(map.get(DatabaseConfig.DatabasesDeparture).get(departureId)==null){
				map.get(DatabaseConfig.DatabasesDeparture).put(departureId, 1);
			}else{
				int tempCount = (int) map.get(DatabaseConfig.DatabasesDeparture).get(departureId);
				map.get(DatabaseConfig.DatabasesDeparture).put(departureId, tempCount+1);
			}
		}else if(waydirection.equals("arrival")){
			if(map.get(DatabaseConfig.DatabasesArrival).get(arrivalId)==null){
				map.get(DatabaseConfig.DatabasesArrival).put(arrivalId, 1);
			}else{
				int tempCount = (int) map.get(DatabaseConfig.DatabasesArrival).get(arrivalId);
				map.get(DatabaseConfig.DatabasesArrival).put(arrivalId, tempCount+1);
			}
		}else{
			if(map.get(DatabaseConfig.DatabasesDeparture).get(departureId)==null){
				map.get(DatabaseConfig.DatabasesDeparture).put(departureId, 1);
			}else{
				int tempCount = (int) map.get(DatabaseConfig.DatabasesDeparture).get(departureId);
				map.get(DatabaseConfig.DatabasesDeparture).put(departureId, tempCount+1);
			}

			if(map.get(DatabaseConfig.DatabasesArrival).get(arrivalId)==null){
				map.get(DatabaseConfig.DatabasesArrival).put(arrivalId, 1);
			}else{
				int tempCount = (int) map.get(DatabaseConfig.DatabasesArrival).get(arrivalId);
				map.get(DatabaseConfig.DatabasesArrival).put(arrivalId, tempCount+1);
			}
		}
	} 

	public static ArrayList<Entry<Long,Integer>> sortMap(HashMap<Long,Integer> map){
		ArrayList<Entry<Long,Integer>> list = new ArrayList<Entry<Long,Integer>>();		
		Entry<Long,Integer> maxEntry = null;
		Entry<Long,Integer> pre = null;
		while(map.size()>0){
			for(Entry<Long,Integer> entry : map.entrySet()) {
				if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
					maxEntry = entry;
				}
			}
			if(!list.contains(maxEntry))list.add(maxEntry);
			map.remove(maxEntry.getKey());
			if(pre!=null && pre.equals(maxEntry)){
				maxEntry=null;
				pre = null;
			}
			else pre = maxEntry;
		}

		return list;

	}

	private static boolean qualifiedSR(SearchRepresentation sr, Calendar low, Calendar high){
		String lowbound = DateUtility.castToAPIFormat(low);
		String highbound = DateUtility.castToAPIFormat(high);
		String srTimeStamp = DateUtility.castToAPIFormat(sr.getTimeStamp());
		return lowbound.compareTo(srTimeStamp)<=0 && highbound.compareTo(srTimeStamp)>=0;
	}

	public static ArrayList<SearchRepresentation> getUserSRsBasedOnTimeStamps(Calendar low, Calendar high){
		int total =0;
		String query = "SELECT COUNT(*) AS total FROM carpoolDAOUser";
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try{		
			conn = CarpoolDaoBasic.getSQLConnection();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();			
			if(rs.next()){									
				total = rs.getInt("total");
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}finally  {
			CarpoolDaoBasic.closeResources(conn, stmt, rs, true);
		}
		ArrayList<SearchRepresentation> srlist = new ArrayList<SearchRepresentation>();
		ArrayList<SearchRepresentation> finallist = new ArrayList<SearchRepresentation>();
		while(total>0){
			srlist = AwsMain.getUserSearchHistory(total);
			for(int i=0; i<srlist.size(); i++){
				if(qualifiedSR(srlist.get(i),low,high)){
					finallist.add(srlist.get(i));
				}				
			}
			total--;
		}
		return finallist;
	}


}
