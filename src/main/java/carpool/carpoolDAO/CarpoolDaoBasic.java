package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.common.DebugLog;
import carpool.dbservice.LocationDaoService;
import carpool.exception.location.LocationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;


import redis.clients.jedis.Jedis;

public class CarpoolDaoBasic {
    private static Jedis jedis = new Jedis("localhost");
    private static Connection connection = null;
    
    private static void connect(){
        String uri ="jdbc:mysql://"+CarpoolConfig.jdbcUri+":3306/test?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(uri, "root", CarpoolConfig.sqlPass);
        } catch (ClassNotFoundException e) {
            DebugLog.d(e);
        } catch (SQLException e) {
        	DebugLog.d(e); 
        }
    }
    
    public static Jedis getJedis() {
        return jedis;
    }
    
    public static Connection getSQLConnection(){
        try {
			if(connection==null || connection.isClosed()){
				connect();
			}
		} catch (SQLException e) {
			DebugLog.d(e);
			DebugLog.d("getSQLConnection:: SQL Connection error, trying to re-establish connection to sql");
			connect();
		}
        return connection;
    }
    
    
    public static Set<String> getWholeDatabase(){
        return jedis.keys("*");  
    }

    public static void clearBothDatabase(){
        jedis.flushAll();
        String query0 = "SET FOREIGN_KEY_CHECKS=0 ";       
        String query1 = "TRUNCATE TABLE SocialList ";
        String query2 = "TRUNCATE TABLE WatchList ";
        String query3 = "TRUNCATE TABLE carpoolDAONotification ";
        String query4 = "TRUNCATE TABLE carpoolDAOMessage";
        String query5 = "TRUNCATE TABLE carpoolDAOUser";
        String query6 = "TRUNCATE TABLE carpoolDAOTransaction";
        String query7 = "TRUNCATE TABLE carpoolDAOLetter";
        String query8 = "TRUNCATE TABLE carpoolDAOLocation";
        String query9 = "TRUNCATE TABLE defaultLocations";
        String query10 = "SET FOREIGN_KEY_CHECKS=1;";
        try(Statement stmt = getSQLConnection().createStatement()){
        	stmt.addBatch(query0);
        	stmt.addBatch(query1);
        	stmt.addBatch(query2);
        	stmt.addBatch(query3);
        	stmt.addBatch(query4);
        	stmt.addBatch(query5);
        	stmt.addBatch(query6);
        	stmt.addBatch(query7);
        	stmt.addBatch(query8);
        	stmt.addBatch(query9);
        	stmt.addBatch(query10);
        	stmt.executeBatch();
        }catch(SQLException e){
        	DebugLog.d(e);
        }
        
		try {
			LocationDaoService.init();
		} catch (LocationException | ValidationException | LocationNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

    }
}
