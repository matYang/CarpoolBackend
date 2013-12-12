package representation;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.constants.CarpoolConfig;
import carpool.dbservice.LocationDaoService;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;

public class SearchRepresentationTest {

	@Test
	public void test() {
		CarpoolDaoBasic.clearBothDatabase();
		try{
			ArrayList<String> lv1 = new ArrayList<String>();
			ArrayList<String> lv2 = new ArrayList<String>();
			ArrayList<String> lv3 = new ArrayList<String>();
			ArrayList<String> lv4 = new ArrayList<String>();
			
			lv1.add("Canada");
			lv2.add("Ontario");
			//LinkedHashMap is reverse insertion order
			lv3.add("Mississauga");
			lv3.add("Toronto");
			lv3.add("Waterloo");
			
			lv4.add("Square One");
			lv4.add("Pearson Airport - terminal 3");
			lv4.add("Pearson Airport - terminal 1");
			lv4.add("York Dale");
			lv4.add("Eaton Center");
			lv4.add("CN Tower");
			lv4.add("Convocation Hall");
			lv4.add("DC Library");
			lv4.add("Matthew's Sweet Little Home");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr =  sdf.format(Calendar.getInstance().getTime());
			
			LocationRepresentation validLocRepA = new LocationRepresentation(lv1.get(0)+"_"+lv2.get(0)+"_"+lv3.get(1)+"_"+lv4.get(3)+"_"+3);
			LocationRepresentation validLocRepB = new LocationRepresentation(lv1.get(0)+"_"+lv2.get(0)+"_"+lv3.get(0)+"_"+lv4.get(1)+"_"+3);

			SearchRepresentation sr1 = new SearchRepresentation("false" + CarpoolConfig.urlSeperator + validLocRepA.toSerializedString() + CarpoolConfig.urlSeperator + validLocRepB.toSerializedString() + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator  + "0" + CarpoolConfig.urlSeperator + "0" + CarpoolConfig.urlSeperator + "0");
			SearchRepresentation sr2 = new SearchRepresentation(sr1.toSerializedString());
			assertTrue(sr1.equals(sr2));
			
			assertTrue(sr1.getDepartureLocation().equals(sr2.getDepartureLocation()));
			assertTrue(sr1.getArrivalLocation().equals(sr2.getArrivalLocation()));
			assertTrue(sr1.equals(sr2));
			SearchRepresentation sr3 = new SearchRepresentation(sr1.isRoundTrip(), sr2.getDepartureLocation(), sr1.getArrivalLocation(), sr1.getDepartureDate(), sr2.getArrivalDate(), sr1.getTargetType(), sr1.getDepartureTimeSlot(),sr2.getArrivalTimeSlot());
			assertTrue(sr3.equals(sr2));
			
		} catch (Exception e){
			e.printStackTrace();
			fail();
		}
	}

}
