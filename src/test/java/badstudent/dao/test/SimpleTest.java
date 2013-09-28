package badstudent.dao.test;

import java.util.ArrayList;

import org.junit.Test;

import carpool.common.Validator;
import carpool.common.EmailHandler;
import carpool.dbservice.EmailDaoService;
import carpool.mappings.MappingManager;
import carpool.model.representation.LocationRepresentation;


public class SimpleTest {
	@Test
	public void test(){
		EmailDaoService.sendActivationEmail(1, "shhyfz@hotmail.com");
	}
}
