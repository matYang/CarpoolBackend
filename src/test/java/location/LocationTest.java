package location;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import carpool.dbservice.LocationDaoService;
import carpool.exception.location.LocationException;
import carpool.exception.validation.ValidationException;
import carpool.model.representation.LocationRepresentation;

public class LocationTest {
	
	@Test
	public void testLoadDefaults(){
		try {
			LocationDaoService.init();
		} catch (LocationException e) {
			e.printStackTrace();
			fail();
		} catch (ValidationException e) {
			fail();
			e.printStackTrace();
		}
	}

}
