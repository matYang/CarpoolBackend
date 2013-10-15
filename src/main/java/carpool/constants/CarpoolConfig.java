package carpool.constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;

public class CarpoolConfig {
	
	public static final int customDepthIndex = 3;
	
	public static final String urlSeperator = "+";
	public static final String urlSeperatorRegx = "\\+";
	
	public static final String default_locationRepresentationString = "Canada_Ontario_Waterloo_undetermined_3";
	public static final String default_searchRepresentationString = "false+Canada_Ontario_Waterloo_undetermined_3+Canada_Ontario_Waterloo_undetermined_3+2013-10-08 22:20:11+2013-10-08 22:20:11+0+0+0";
	

	public static final String pathToSearchHistoryFolder = "srHistory/";
	public static final String searchHistoryFileSufix = "_sr.txt";
    
	//public static final String domainName = "www.huaixuesheng.com";
	public static final String domainName = "localhost:8015";
	public static final boolean cookieEnabled = false;
	
	
	public static final LocationRepresentation getDefaultLocationRepresentation(){
		return new LocationRepresentation("Canada_Ontario_Waterloo_undetermined_3");
	}
	
	public static final SearchRepresentation getDefaultSearchRepresentation(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr =  sdf.format(Calendar.getInstance().getTime());
		return new SearchRepresentation("false" + CarpoolConfig.urlSeperator + getDefaultLocationRepresentation().toSerializedString() + CarpoolConfig.urlSeperator + getDefaultLocationRepresentation().toSerializedString() + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator  + "0" + CarpoolConfig.urlSeperator + "0" + CarpoolConfig.urlSeperator + "0");
	}
	
}
