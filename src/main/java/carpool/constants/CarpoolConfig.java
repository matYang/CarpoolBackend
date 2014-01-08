package carpool.constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import carpool.common.DebugLog;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.messageType;
import carpool.model.Location;
import carpool.model.representation.SearchRepresentation;

public class CarpoolConfig {
	private static final String ENV_VAR_KEY = "C_MAINSERVER_ENV";
	//private static final String ENV_VAR_KEY = "REMOTE";
	private static final String ENV_REMOTE = "REMOTE";
	private static final boolean isOnLocal;
	
	static{
		String value = System.getenv(ENV_VAR_KEY);
		if (value != null && value.equals(ENV_REMOTE)){
			isOnLocal = false;
		} else{
			isOnLocal = true;
		}
	}
	
	public static final int max_recents = 10;
	public static final long max_feedBackLength = 200000l;
	public static final long max_PostLength = 819200l;
	public static final long max_FileLength = 81920000l;

	public static final String key_emailActivationAuth = "ea";
	public static final String key_forgetPasswordAuth = "fp";
	
	
	public static final String domainName = isOnLocal ? "localhost:8015" : "www.huaixuesheng.com";
	public static final String sqlPass = isOnLocal ? null : "badstudent";
	
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
	
    public static final String log4jBasicPatternLayout = "%d [%t] %-5p %c - %m%n";
    public static final String log4LogFileFolder = "log4j/";
    public static final String log4jLogFileSuffix = ".info.log";    
    public static final String debugLogChinesePrefix = "_chinese";
	public static final Location getDefaultLocationRepresentation(){
		return null;
	}
	public static final String UserSRDeparture = "UserSRDeparture";
	public static final String UserSRArrival = "UserSRArrival";
	public static final String DatabasesDeparture = "DatabasesDeparture";
	public static final String DatabasesArrival = "DatabasesArrival";
	
	public static final String standardTimeZone = "UTC";
	
	public static final String AccessKeyID="AKIAJAU3ADUWK7CKFPZQ";
	public static final String SecretKey="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";
	public static final String SMTP_USERNAME = "AKIAIK5KV62M7VTBOJDQ"; 
	public static final String SMTP_PASSWORD = "AklHWYPbI4LBZcQZB3BDqj9KQgl20FyUZj296ru6aRD+";  
	public static final String HOST = "email-smtp.us-east-1.amazonaws.com";
	public static final int PORT = 587;
	
	public static final String RDSreplicaUri = "replica.cunzg2tyzsud.us-west-2.rds.amazonaws.com";
	public static final String RDSDBInstanceUri = "badstudent.cunzg2tyzsud.us-west-2.rds.amazonaws.com";
	public static final String localhostUri = "localhost";
	public static final String jdbcUri = isOnLocal ? localhostUri : RDSreplicaUri;
	
	public static final SearchRepresentation getDefaultSearchRepresentation(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr =  sdf.format(Calendar.getInstance().getTime());		
		long departureMatch_Id = 1;
		long arrivalMatch_Id = 2;		
		return new SearchRepresentation("false" + CarpoolConfig.urlSeperator + departureMatch_Id + CarpoolConfig.urlSeperator + arrivalMatch_Id + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator  + "0" + CarpoolConfig.urlSeperator + "0" + CarpoolConfig.urlSeperator + "0"+CarpoolConfig.urlSeperator + dateStr);
	}

	public static final void initConfig(){
		//do nothing, force static block initialization at start of server
	}
}
