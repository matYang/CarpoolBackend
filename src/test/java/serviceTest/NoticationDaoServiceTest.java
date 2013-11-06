package serviceTest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

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
