package carpool.constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;

public class CarpoolConfig {
	
	public static final int max_recents = 10;
	public static final long max_feedBackLength = 200000l;
	public static final long max_PostLength = 819200l;
	public static final long max_FileLength = 81920000l;

	public static final String key_emailActivationAuth = "ea";
	public static final String key_forgetPasswordAuth = "fp";
	
	
	//public static final String domainName = "www.huaixuesheng.com";
	public static final String domainName = "localhost:8015";
	public static final boolean cookieEnabled = false;
	//time stamp on the session will be updated if it is 3 days old
	public static final long session_updateThreshould = 259200000l;
	//time stamp 7 days old would result in failure of login
	public static final long session_expireThreshould = 604800000l;
	public static final String cookie_userSession = "userSessionCookie";
	public static final int cookie_maxAge = 5184000; //2 month
	
	
	
	public static final int customDepthIndex = 3;
	public static final String locationRepresentationSeperator = "_";
	public static final String urlSeperator = "+";
	public static final String urlSeperatorRegx = "\\+";
	
	public static final String default_locationRepresentationString = "Canada_Ontario_Waterloo_undetermined_3";
	public static final String default_searchRepresentationString = "false+Canada_Ontario_Waterloo_undetermined_3+Canada_Ontario_Waterloo_undetermined_3+2013-10-08 22:20:11+2013-10-08 22:20:11+0+0+0";
	

	public static final String pathToSearchHistoryFolder = "srHistory/";
	public static final String searchHistoryFileSufix = "_sr.txt";
    
    public static final String profileImgPrefix = "userprofile-";
    public static final String imgSize_xs = "xs-8-";
    public static final String imgSize_s = "s-16-";
    
    public static final String imgSize_m = "m-32-";
    public static final String imgSize_l = "l-64-";
    public static final String imgSize_xl = "xl-128-";
    
    public static final String imgSize_raw = "raw-0-";
    
    public static final String redisSearchHistoryPrefix = "redis/userSearchHistory-";;
    public static final int redisSearchHistoryUpbound = 50;
	
	public static final LocationRepresentation getDefaultLocationRepresentation(){
		return new LocationRepresentation("Canada_Ontario_Waterloo_undetermined_3");
	}
	
	public static final SearchRepresentation getDefaultSearchRepresentation(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr =  sdf.format(Calendar.getInstance().getTime());
		return new SearchRepresentation("false" + CarpoolConfig.urlSeperator + getDefaultLocationRepresentation().toSerializedString() + CarpoolConfig.urlSeperator + getDefaultLocationRepresentation().toSerializedString() + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator  + "0" + CarpoolConfig.urlSeperator + "0" + CarpoolConfig.urlSeperator + "0");
	}

	
}
