package location;

import static org.junit.Assert.*;

import org.junit.Test;

import carpool.exception.ValidationException;
import carpool.exception.location.LocationException;
import carpool.locationService.LocationService;

public class LocationTest {

	@Test
	public void locationLoaderTest() {
		try {
			LocationService.init();
			LocationService.printParentNodeMap();
			LocationService.printLookupMap();
		} catch (LocationException | ValidationException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void locationRepresentationTest(){
		LocationRepresentation locRep = new LocationRepresentation("Canada_Ontario_Toronto_Convocation Hall_3");
	}

}
