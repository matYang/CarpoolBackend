package clearDatabase;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;

public class ClearDatabase {

	@Test
	public void test() {
		CarpoolDaoBasic.clearBothDatabase();
	}

}
