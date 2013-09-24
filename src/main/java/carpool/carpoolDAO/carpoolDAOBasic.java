package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import carpool.constants.Constants;
import carpool.common.DebugLog;


import redis.clients.jedis.Jedis;

public class carpoolDAOBasic {
    private static Jedis jedis = new Jedis("localhost");
    private static Connection connection = null;
    
    private static void connect(){
        String uri ="jdbc:mysql://localhost:3306/test?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(uri, "root", null);
        } catch (ClassNotFoundException e) {
            DebugLog.d(e.getMessage());
        } catch (SQLException e) {
        	DebugLog.d(e.getMessage()); 
        }
    }
    
    public static Jedis getJedis() {
        return jedis;
    }
    
    public static Connection getSQLConnection(){
        if(connection==null){
            connect();
        }
        return connection;
    }
    
    
    public static Long generateId(){
        return jedis.incr(Constants.key_idGenerator);
    }
    
    public static Set<String> getWholeDatabase(){
        return jedis.keys("*");  
    }

    public static void clearBothDatabase(){
        jedis.flushAll();
        String query0 = "SET FOREIGN_KEY_CHECKS=0 ";
        String query1 = "TRUNCATE TABLE User ";
        String query2 = "TRUNCATE TABLE Message ";
        String query3 = "TRUNCATE TABLE Transaction ";
        String query4 = "TRUNCATE TABLE SocialList ";
        String query5 = "TRUNCATE TABLE WatchList ";
        String query6 = "TRUNCATE TABLE Notification ";
        String query7 = "SET FOREIGN_KEY_CHECKS=1;";
        try(Statement stmt = getSQLConnection().createStatement()){
        	stmt.addBatch(query0);
        	stmt.addBatch(query1);
        	stmt.addBatch(query2);
        	stmt.addBatch(query3);
        	stmt.addBatch(query4);
        	stmt.addBatch(query5);
        	stmt.addBatch(query6);
        	stmt.addBatch(query7);
        	stmt.executeBatch();
        }catch(SQLException e){
        	DebugLog.d(e.getMessage());
        }
    }
}
