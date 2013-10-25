package serviceTest;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.constants.Constants;
import carpool.dbservice.NotificationDaoService;
import carpool.model.Notification;

public class NoticationDaoServiceTest {

	@Test
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

	
}
