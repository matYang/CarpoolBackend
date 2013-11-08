package serviceTest;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.NotificationDaoService;
import carpool.model.Notification;

public class NoticationDaoServiceTest {


	public void test() {
		Notification n1 = new Notification(Constants.NotificationEvent.watched, 1);
		NotificationDaoService.sendNotification(n1);
		
		try {
			Thread.sleep(1500l);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void addNotificationTest(){

        String query0 = "SET FOREIGN_KEY_CHECKS=0 ";       
        String query1 = "TRUNCATE TABLE carpoolDAONotification ";
        String query2 = "SET FOREIGN_KEY_CHECKS=1;";
        try(Statement stmt = CarpoolDaoBasic.getSQLConnection().createStatement()){
        	stmt.addBatch(query0);
        	stmt.addBatch(query1);
        	stmt.addBatch(query2);
       	
        	stmt.executeBatch();
        }catch(SQLException e){
        	DebugLog.d(e.getMessage());
        }
		Notification n1 = new Notification(Constants.NotificationEvent.watched, 1, 2, -1, -1);
		Notification n2 = new Notification(Constants.NotificationEvent.transactionCancelled, 1, 2, -1, -1);
		Notification n3 = new Notification(Constants.NotificationEvent.transactionInit, 1, 2, -1, -1);
		Notification n4 = new Notification(Constants.NotificationEvent.transactionEvaluated, 1, 2, -1, -1);
		Notification n5 = new Notification(Constants.NotificationEvent.transactionAboutToStart, 1, 2, -1, -1);
		
		ArrayList<Notification> ns = new ArrayList<Notification>();
		ns.add(n1);
		ns.add(n2);
		ns.add(n3);
		ns.add(n4);
		ns.add(n5);
		NotificationDaoService.sendNotification(ns);
	}

	
}
