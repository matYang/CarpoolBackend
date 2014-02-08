package commonTest;

import static org.junit.Assert.*;
import java.sql.Connection;


import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;

public class VaragrsTest {

	@Test
	public void test() {
		Connection conn = CarpoolDaoBasic.getSQLConnection();
		assertTrue(testVarargs() == 0);
		assertTrue(testVarargs(null) == -1);
		assertTrue(testVarargs(conn) == 1);
		assertTrue(testVarargs(conn, conn) == 2);
		
		assertTrue(testVarargsB() == 0);
		assertTrue(testVarargsB(conn) == 1);
		assertTrue(testVarargsB(conn, conn) == 2);
		
		
	}
	
	
	public static int testVarargs(Connection... conns){
		int count = 0;
		if (conns == null){
			return -1;
		}
		else{
			for (Connection conn : conns){
				count++;
			}
		}
		return count;
	}
	
	public static int testVarargsB(Connection... conns){
		if (conns.length > 0 && ! (conns[0] instanceof java.sql.Connection)){
			return -1;
		}
		return conns.length;
	}
	
	public static void lalala(Connection... conns){
		Connection conn = null;
		if (conns.length == 0){
			conn =  CarpoolDaoBasic.getSQLConnection();
		}
		else if (conns.length == 1){
			conn = conns[0];
		}
		
		
		CarpoolDaoBasic.closeResource(conns.length == 0 ? conn : null, null, null);
	}
	
}
