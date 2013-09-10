package badstudent.dao.test;

import java.util.ArrayList;

import org.junit.Test;

import carpool.common.Common;
import carpool.common.EmailHandler;
import carpool.dbservice.UserDaoService;
import carpool.mappings.MappingManager;
import carpool.model.Location;


public class Simple {
	@Test
	public void test(){
		UserDaoService.sendActivationEmail(1, "shhyfz@hotmail.com");
	}
}
