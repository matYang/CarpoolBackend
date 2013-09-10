package badstudent.dao.test;

import java.util.ArrayList;

import org.junit.Test;

import badstudent.common.Common;
import badstudent.common.EmailHandler;
import badstudent.dbservice.UserDaoService;
import badstudent.mappings.MappingManager;
import badstudent.model.Location;

public class Simple {
	@Test
	public void test(){
		UserDaoService.sendActivationEmail(1, "shhyfz@hotmail.com");
	}
}
