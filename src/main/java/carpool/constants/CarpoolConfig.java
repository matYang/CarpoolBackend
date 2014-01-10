package carpool.constants;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import carpool.common.DebugLog;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.EmailEvent;
import carpool.constants.Constants.messageType;
import carpool.model.Location;
import carpool.model.representation.SearchRepresentation;

public class CarpoolConfig {
	private static final String ENV_VAR_KEY = "C_MAINSERVER_ENV";
	private static final String ENV_REMOTE = "REMOTE";
	public static final boolean isOnLocal;
	
	//used for emails,  Entry consists of subject and template
	public static final HashMap<EmailEvent, Entry<String, String>> emailEventMap;
	public static final String htmlTemplateTarget = "TARGET";

	
	static{
		String value = System.getenv(ENV_VAR_KEY);
		if (value != null && value.equals(ENV_REMOTE)){
			isOnLocal = false;
		} else{
			isOnLocal = true;
		}
		
		emailEventMap = new HashMap<EmailEvent, Entry<String, String>>();
		emailEventMap.put(EmailEvent.activeateAccount, new AbstractMap.SimpleEntry<String, String>("请激活您的账户","TARGET"));
		emailEventMap.put(EmailEvent.forgotPassword, new AbstractMap.SimpleEntry<String, String>("取回您的密码","TARGET"));
		emailEventMap.put(EmailEvent.notification, new AbstractMap.SimpleEntry<String, String>("最新动态提示","TARGET"));

	}
	
	public static final int max_recents = 10;
	public static final long max_feedBackLength = 200000l;
	public static final long max_PostLength = 819200l;
	public static final long max_FileLength = 81920000l;
	
	//redis keys
	public static final String key_emailActivationAuth = "ea";
	public static final String key_forgetPasswordAuth = "fp";
	public static final long session_updateThreshould = 259200000l;		//3 days
	public static final long session_expireThreshould = 604800000l;		//7 days
    public static final String redisSearchHistoryPrefix = "redis/userSearchHistory-";;
    public static final int redisSearchHistoryUpbound = isOnLocal ? 6 : 50;
	
	
	public static final String domainName = isOnLocal ? "localhost:8015" : "www.routea.ca";
	public static final String sqlPass = isOnLocal ? null : "badstudent";
	
	
	public static final boolean cookieEnabled = false;
	public static final String cookie_userSession = "userSessionCookie";
	public static final int cookie_maxAge = 5184000; //2 month
	
	
	public static final String urlSeperator = "+";
	public static final String urlSeperatorRegx = "\\+";
	

	public static final String pathToSearchHistoryFolder = "srHistory/";
	public static final String searchHistoryFileSufix = "_sr.txt";
	
	
	//image
    public static final String profileImgPrefix = "userprofile-";
    public static final String imgSize_xs = "xs-8-";
    public static final String imgSize_s = "s-16-";
    public static final String imgSize_m = "m-32-";
    public static final String imgSize_l = "l-64-";
    public static final String imgSize_xl = "xl-128-";
    
    
	//log
    public static final String log4jBasicPatternLayout = "%d [%t] %-5p %c - %m%n";
    public static final String log4LogFileFolder = "log4j/";
    public static final String log4jLogFileSuffix = ".info.log";    
    public static final String debugLogChinesePrefix = "_chinese";
    
    /*database configurations*/	
	public static final String UserSRDeparture = "UserSRDeparture";
	public static final String UserSRArrival = "UserSRArrival";
	public static final String DatabasesDeparture = "DatabasesDeparture";
	public static final String DatabasesArrival = "DatabasesArrival";
	public static final String RDSDBInstanceUri = "badstudent.cunzg2tyzsud.us-west-2.rds.amazonaws.com";
	public static final String RedisEndPoint = "redisserver.ppomgu.0001.usw2.cache.amazonaws.com";
	public static final String localhostUri = "localhost";
	public static final String jdbcUri = isOnLocal ? localhostUri : RDSDBInstanceUri;
	public static final String redisUri = isOnLocal ? localhostUri : RedisEndPoint;
	
	public static final String AccessKeyID="AKIAIE53WCAFSYLUGH2A";
	public static final String SecretKey="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";
	
	public static final String standardTimeZone = "UTC";
	
	//email
	public static final String SMTP_USERNAME = "AKIAIK5KV62M7VTBOJDQ"; 
	public static final String SMTP_PASSWORD = "AklHWYPbI4LBZcQZB3BDqj9KQgl20FyUZj296ru6aRD+";
	public static final String SMTP_FROM = "info@routea.ca";
	public static final String SMTP_HOST = "email-smtp.us-east-1.amazonaws.com";
	public static final int SMTP_PORT = 587;	
	
	/*API level constants*/
	public static final int category_DM = 0;
	public static final String applicationPrefix = "/api";
	public static final String versionPrefix = "/v1.0";
	public static final int maxEmailLength = 50;
	public static final int maxQqLength = 15;
	public static final int maxUserNameLength = 18;
	public static final int minPasswordLength = 6;
	public static final int maxPasswordLength = 30;
	public static final String log_errKeyword = "ERROR!:";
	
	public static final String RegexEmailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	public static final String RegexNamePattern = "[\u4e00-\u9fa5|A-za-z]*";
	public static final String RegexPwPattern = "[A-za-z0-9|!@#\\\\$%^&*()_+|-=|[]{}|\\|:;|\"\']";
	public static final int qqMinLength = 5;
	public static final int qqMaxLength = 10;
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
