package badstudent.dao.test;

import java.util.ArrayList;

import org.junit.Test;

import carpool.common.Validator;
import carpool.common.EmailHandler;
import carpool.dbservice.UserDaoService;
import carpool.mappings.MappingManager;
import carpool.model.Location;


public class SimpleTest {
	@Test
	public void test(){
		UserDaoService.sendActivationEmail(1, "shhyfz@hotmail.com");
	}
}