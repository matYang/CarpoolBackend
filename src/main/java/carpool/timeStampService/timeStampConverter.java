package carpool.timeStampService;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import carpool.common.DateUtility;
import carpool.constants.CarpoolConfig;

public class timeStampConverter {
	
	public static Calendar ConvertToStandard(Calendar old){
		//System.out.println("old time: "+DateUtility.castToAPIFormat(old));
		
		Calendar Standard = Calendar.getInstance(TimeZone.getTimeZone(CarpoolConfig.standardTimeZone));
		//System.out.println("standard time: "+DateUtility.castToAPIFormat(Standard));
		Calendar oldr = Calendar.getInstance(TimeZone.getTimeZone(old.getTimeZone().getID()));
		
		int hdif = Standard.get(Calendar.HOUR_OF_DAY) - oldr.get(Calendar.HOUR_OF_DAY);
		int mdif = Standard.get(Calendar.MINUTE) - oldr.get(Calendar.MINUTE);
		int sdif = Standard.get(Calendar.SECOND) - oldr.get(Calendar.SECOND);
		old.add(Calendar.SECOND,sdif);
		old.add(Calendar.MINUTE,mdif);
		old.add(Calendar.HOUR_OF_DAY,hdif);		
       
		
		return old;
		
       
	}
	
}
