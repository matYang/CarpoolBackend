package badstudent.dao.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import carpool.common.Common;
import carpool.database.DaoBasic;
import carpool.exception.user.UserNotFoundException;
import carpool.model.User;

public class SimleDatabase {
	
	private static Connection connection = null;
	

	private static void clearBothDatabase(){
        String query0 = "SET FOREIGN_KEY_CHECKS=0 ";
        String query1 = "TRUNCATE TABLE User ";
        String query2 = "TRUNCATE TABLE Message ";
        String query3 = "TRUNCATE TABLE Transaction ";
        String query4 = "TRUNCATE TABLE SocialList ";
        String query5 = "TRUNCATE TABLE WatchList ";
        String query6 = "TRUNCATE TABLE Notification ";
        String query7 = "SET FOREIGN_KEY_CHECKS=1;";
        try(Statement stmt = connection.createStatement()){
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
        	Common.d(e.getMessage());
        }
    }
	
	private static String testString = "Harry";
	private static String value = "Xiong";

	@Test
	public void simpleAdd() {
		String uri ="jdbc:mysql://localhost:3306/test?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(uri, "root", null);
        } catch (ClassNotFoundException e) {
            Common.d(e.getMessage());
        } catch (SQLException e) {
            Common.d(e.getMessage()); 
        }
        
        String query = "INSERT INTO Harry (simpleString, value) VALUES (?,?);";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			stmt.setString(1, testString);
			stmt.setString(2, value);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
		}catch(SQLException e){
			e.printStackTrace();
			fail();
		}
        
	}
	
	@Test
	public void simpleGet(){
		String query = "SELECT * FROM Harry WHERE value = ?";
		String response = "";
		try(PreparedStatement stmt = DaoBasic.getSQLConnection().prepareStatement(query)){
			stmt.setString(1, value);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				response = rs.getString("value");
				assertTrue(response.equals(value));
			}else{
				fail();
			}
		}catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}
	

}
