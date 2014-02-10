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
import carpool.constants.CarpoolConfig;
import carpool.model.representation.SearchRepresentation;

public class StatisticAnalysisOfDataService {	

	public static HashMap<String,ArrayList<Entry<Long,Integer>>> GetTheEntireMap(){
		HashMap<String,HashMap> BigMap = new HashMap<String,HashMap>();
		HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();
		//Departure||Arrival-->UserSR||Databases
		BigMap.put(CarpoolConfig.UserSRDeparture, UserSRDeparture);
		BigMap.put(CarpoolConfig.UserSRArrival, UserSRArrival);
		BigMap.put(CarpoolConfig.DatabasesDeparture, DatabasesDeparture);
		BigMap.put(CarpoolConfig.DatabasesArrival, DatabasesArrival);
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
			CarpoolDaoBasic.CloseResources(conn, stmt, rs, false);
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
			CarpoolDaoBasic.CloseResources(conn, stmt, rs, true);
		}
		ArrayList<Entry<Long,Integer>> UserSRDepartureList = new ArrayList<Entry<Long,Integer>>();
		ArrayList<Entry<Long,Integer>> UserSRArrivalList = new ArrayList<Entry<Long,Integer>>();
		ArrayList<Entry<Long,Integer>> DatabasesDepartureList = new ArrayList<Entry<Long,Integer>>();
		ArrayList<Entry<Long,Integer>> DatabasesArrivalList = new ArrayList<Entry<Long,Integer>>();
		UserSRDepartureList = sortMap(BigMap.get(CarpoolConfig.UserSRDeparture));
		UserSRArrivalList = sortMap(BigMap.get(CarpoolConfig.UserSRArrival));
		DatabasesDepartureList = sortMap(BigMap.get(CarpoolConfig.DatabasesDeparture));
		DatabasesArrivalList = sortMap(BigMap.get(CarpoolConfig.DatabasesArrival));

		HashMap<String,ArrayList<Entry<Long,Integer>>> newMap = new HashMap<String,ArrayList<Entry<Long,Integer>>>();
		newMap.put(CarpoolConfig.UserSRDeparture, UserSRDepartureList);
		newMap.put(CarpoolConfig.UserSRArrival, UserSRArrivalList);
		newMap.put(CarpoolConfig.DatabasesDeparture, DatabasesDepartureList);
		newMap.put(CarpoolConfig.DatabasesArrival, DatabasesArrivalList);
		return newMap;
	}

	public static ArrayList<Entry<Long,Integer>> getSpecificList(String str){
		HashMap<String,HashMap> BigMap = new HashMap<String,HashMap>();
		HashMap<Long,Integer> UserSRDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> UserSRArrival = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesDeparture = new HashMap<Long,Integer>();
		HashMap<Long,Integer> DatabasesArrival = new HashMap<Long,Integer>();
		//Departure||Arrival-->UserSR||Databases
		BigMap.put(CarpoolConfig.UserSRDeparture, UserSRDeparture);
		BigMap.put(CarpoolConfig.UserSRArrival, UserSRArrival);
		BigMap.put(CarpoolConfig.DatabasesDeparture, DatabasesDeparture);
		BigMap.put(CarpoolConfig.DatabasesArrival, DatabasesArrival);

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		if(str.equals(CarpoolConfig.UserSRDeparture)||str.equals(CarpoolConfig.UserSRArrival)){
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
				CarpoolDaoBasic.CloseResources(conn, stmt, rs, true);
			}
			ArrayList<SearchRepresentation> srlist = new ArrayList<SearchRepresentation>();
			while(total>0){
				srlist = AwsMain.getUserSearchHistory(total);
				setUserSR(BigMap,srlist,str.equals(CarpoolConfig.UserSRDeparture) ? "departure" : "arrival");
				total--;
			}
			if(str.equals(CarpoolConfig.UserSRDeparture)){
				ArrayList<Entry<Long,Integer>> UserSRDepartureList = new ArrayList<Entry<Long,Integer>>();
				UserSRDepartureList = sortMap(BigMap.get(CarpoolConfig.UserSRDeparture));
				return UserSRDepartureList;
			}else{
				ArrayList<Entry<Long,Integer>> UserSRArrivalList = new ArrayList<Entry<Long,Integer>>();
				UserSRArrivalList = sortMap(BigMap.get(CarpoolConfig.UserSRArrival));
				return UserSRArrivalList;
			}

		}else if(str.equals(CarpoolConfig.DatabasesDeparture)||str.equals(CarpoolConfig.DatabasesArrival)){
			String query2 = "SELECT * from carpoolDAOMessage";
			try{
				conn = CarpoolDaoBasic.getSQLConnection();
				stmt = conn.prepareStatement(query2);
				rs = stmt.executeQuery();
				while(rs.next()){
					setMessagePost(BigMap,rs.getLong("departureMatch_Id"),rs.getLong("arrivalMatch_Id"),str.equals(CarpoolConfig.DatabasesDeparture) ? "departure" : "arrival");
				}
			}catch (SQLException e) {
				e.printStackTrace();
				DebugLog.d(e);
			}finally  {
				CarpoolDaoBasic.CloseResources(conn, stmt, rs, true);
			}
			if(str.equals(CarpoolConfig.DatabasesDeparture)){
				ArrayList<Entry<Long,Integer>> DatabasesDepartureList = new ArrayList<Entry<Long,Integer>>();
				DatabasesDepartureList = sortMap(BigMap.get(CarpoolConfig.DatabasesDeparture));
				return DatabasesDepartureList;
			}else{
				ArrayList<Entry<Long,Integer>> DatabasesArrivalList = new ArrayList<Entry<Long,Integer>>();
				DatabasesArrivalList = sortMap(BigMap.get(CarpoolConfig.DatabasesArrival));
				return DatabasesArrivalList;
			}
		}
		return null;
	}



	public static void setUserSR(HashMap<String,HashMap> map,ArrayList<SearchRepresentation> srlist,String waydirection){			
		if(waydirection.equals("departure")){		
			for(int i=0; i<srlist.size(); i++){
				if(map.get(CarpoolConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id())==null){
					map.get(CarpoolConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),1);
				}else{
					int tempCount = (int) map.get(CarpoolConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id());
					map.get(CarpoolConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),tempCount+1);
				}

			}
		}else if(waydirection.equals("arrival")){

			for(int i=0; i<srlist.size(); i++){
				if(map.get(CarpoolConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id())==null){
					map.get(CarpoolConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),1);
				}else{
					int tempCount = (int) map.get(CarpoolConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id());
					map.get(CarpoolConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),tempCount+1);
				}

			}
		}else{		
			for(int i=0; i<srlist.size(); i++){
				if(map.get(CarpoolConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id())==null){
					map.get(CarpoolConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),1);
				}else{					
					int tempCount = (int) map.get(CarpoolConfig.UserSRDeparture).get(srlist.get(i).getDepartureMatch_Id());
					map.get(CarpoolConfig.UserSRDeparture).put(srlist.get(i).getDepartureMatch_Id(),tempCount+1);
				}

				if(map.get(CarpoolConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id())==null){
					map.get(CarpoolConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),1);
				}else{
					int tempCount = (int) map.get(CarpoolConfig.UserSRArrival).get(srlist.get(i).getArrivalMatch_Id());
					map.get(CarpoolConfig.UserSRArrival).put(srlist.get(i).getArrivalMatch_Id(),tempCount+1);
				}

			}
		}

	}

	public static void setMessagePost(HashMap<String,HashMap> map,Long departureId, Long arrivalId, String waydirection){
		if(waydirection.equals("departure")){
			if(map.get(CarpoolConfig.DatabasesDeparture).get(departureId)==null){
				map.get(CarpoolConfig.DatabasesDeparture).put(departureId, 1);
			}else{
				int tempCount = (int) map.get(CarpoolConfig.DatabasesDeparture).get(departureId);
				map.get(CarpoolConfig.DatabasesDeparture).put(departureId, tempCount+1);
			}
		}else if(waydirection.equals("arrival")){
			if(map.get(CarpoolConfig.DatabasesArrival).get(arrivalId)==null){
				map.get(CarpoolConfig.DatabasesArrival).put(arrivalId, 1);
			}else{
				int tempCount = (int) map.get(CarpoolConfig.DatabasesArrival).get(arrivalId);
				map.get(CarpoolConfig.DatabasesArrival).put(arrivalId, tempCount+1);
			}
		}else{
			if(map.get(CarpoolConfig.DatabasesDeparture).get(departureId)==null){
				map.get(CarpoolConfig.DatabasesDeparture).put(departureId, 1);
			}else{
				int tempCount = (int) map.get(CarpoolConfig.DatabasesDeparture).get(departureId);
				map.get(CarpoolConfig.DatabasesDeparture).put(departureId, tempCount+1);
			}

			if(map.get(CarpoolConfig.DatabasesArrival).get(arrivalId)==null){
				map.get(CarpoolConfig.DatabasesArrival).put(arrivalId, 1);
			}else{
				int tempCount = (int) map.get(CarpoolConfig.DatabasesArrival).get(arrivalId);
				map.get(CarpoolConfig.DatabasesArrival).put(arrivalId, tempCount+1);
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
			CarpoolDaoBasic.CloseResources(conn, stmt, rs, true);
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
