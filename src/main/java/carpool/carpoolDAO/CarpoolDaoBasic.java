package carpool.carpoolDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import carpool.constants.CarpoolConfig;
import carpool.common.DebugLog;
import carpool.dbservice.LocationDaoService;
import carpool.exception.location.LocationException;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CarpoolDaoBasic {
	
	private static JedisPool jedisPool; 
	private static HikariDataSource ds = null;
	
	static {
		JedisPoolConfig jedisConfig = new JedisPoolConfig();
		jedisConfig.setTestOnBorrow(false);
		jedisConfig.setMinIdle(5);
		jedisConfig.setMaxWait(4000l);
		jedisPool = new JedisPool(jedisConfig, CarpoolConfig.redisUri, 6379);
		
		
		HikariConfig sqlConfig = new HikariConfig();
		sqlConfig.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		sqlConfig.addDataSourceProperty("url", "jdbc:mysql://"+CarpoolConfig.jdbcUri+":3306/test?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes");
		sqlConfig.addDataSourceProperty("user", "root");
		sqlConfig.addDataSourceProperty("password", CarpoolConfig.sqlPass);
		sqlConfig.setPoolName("SQLPool");
		sqlConfig.setMaxLifetime(1800000l);
		sqlConfig.setAutoCommit(true);
		sqlConfig.setMinimumPoolSize(10);
		sqlConfig.setMaximumPoolSize(100);
		sqlConfig.setConnectionTimeout(10000l);
		ds = new HikariDataSource(sqlConfig);
		
	}	

    
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
    
    public static void returnJedis(Jedis jedis){
    	jedisPool.returnResource(jedis);
    }
    
    public static Connection getSQLConnection(){
    	Connection connection;
    	try {
			connection = ds.getConnection();
		} catch (SQLException e) {
			DebugLog.d(e);
			throw new RuntimeException(e.getMessage(), e); 
		} 
		return connection;

    }
    
    public static void closeResource( Connection conn, PreparedStatement stmt, ResultSet rs){
		try{
			if (stmt != null)  stmt.close();  
			if (conn != null)  conn.close(); 
			if (rs != null) rs.close();
		} catch (SQLException e){
			DebugLog.d("Exception when closing stmt, rs and conn");
			DebugLog.d(e);
		}
    }
    

    public static void clearBothDatabase(){
    	Jedis jedis = getJedis();
        jedis.flushAll();
        returnJedis(jedis);
        
        Statement stmt = null;
		Connection conn = null;
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
        try{
        	conn = getSQLConnection();
        	stmt = conn.createStatement();
        			
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
        } catch(SQLException e) {
        	DebugLog.d(e);
        } finally {
			try{
				if (stmt != null)  stmt.close();  
	            if (conn != null)  conn.close(); 
			} catch (SQLException e){
				DebugLog.d("Exception when closing stmt, rs and conn");
				DebugLog.d(e);
			}
        }
        
		try {
			LocationDaoService.init();
		} catch (LocationException | ValidationException | LocationNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

    }
    
    
}
