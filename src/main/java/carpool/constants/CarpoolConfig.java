package carpool.constants;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.EmailEvent;
import carpool.constants.Constants.MessageType;
import carpool.model.Location;
import carpool.model.representation.SearchRepresentation;

public class CarpoolConfig {
	private static final String ENV_VAR_KEY = "C_MAINSERVER_ENV";
	private static final String ENV_REMOTE = "REMOTE";
	public static final boolean isOnLocal;
	
	//used for emails,  Entry consists of subject and template
	public static final HashMap<EmailEvent, Entry<String, String>> emailEventMap;
	public static final String htmlTemplateURLTarget = "URLTARGET";
	public static final String htmlTemplateNameTarget = "NAMETARGET";

	
	static{
		String value = System.getenv(ENV_VAR_KEY);
		if (value != null && value.equals(ENV_REMOTE)){
			isOnLocal = false;
		} else{
			isOnLocal = true;
		}
		
		emailEventMap = new HashMap<EmailEvent, Entry<String, String>>();
		emailEventMap.put(EmailEvent.activeateAccount, new AbstractMap.SimpleEntry<String, String>("请激活您的账户","<!DOCTYPE html><html><head><meta charset='utf-8' /><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><meta name='HandheldFriendly' content='true'><title>routea.ca</title></head><body style='margin-bottom: 0px; margin-top: 0px; padding-bottom: 0px; padding-top: 0px;'><div style=' width:600px; font-size:12px; padding:0; margin:0 auto; font-family:Arial, '宋体'; overflow:hidden'><div style='border:1px solid #eee; verflow:hidden'><div style=' width:598px; height: 71px;border-bottom:2px solid #f5800a; padding:10px; padding-left:20px'><a href='http://routea.ca' target='_blank'><img src='https://routea.ca/emailImages/logo_email.jpg' border='0' alt='低碳生活，优质拼车'></a></div><div style='padding:30px 20px; font-size:14px; color:#000; line-height:24px'><p style='padding-bottom:10px'>&#24863;&#35874;&#24744;&#27880;&#20876;RouteA&#65292;&#35831;&#28857;&#20987;&#19979;&#38754;&#30340;&#38142;&#25509;&#23436;&#25104;&#27880;&#20876;&#65288;&#22914;&#26080;&#27861;&#25171;&#24320;&#35831;&#25226;&#27492;&#38142;&#25509;&#22797;&#21046;&#31896;&#36148;&#21040;&#27983;&#35272;&#22120;&#25171;&#24320;&#65289;&#12290;</p><p style='padding-bottom:10px'><a style='color:#0000cc' href='URLTARGET' target='_blank'>URLTARGET</a></p>RouteA&#37038;&#20214;&#20013;&#24515; <br></div><div style='padding:10px 20px; line-height:24px; color:#666; border-top:1px solid #eee; overflow:hidden'>&#9312; &#27492;&#37038;&#20214;&#20026;routea.ca&#31995;&#32479;&#21457;&#20986;&#65292;&#35831;&#21247;&#22238;&#22797;&#37038;&#20214;<br>&#9313; &#22914;&#26524;&#24744;&#26377;&#20219;&#20309;&#38382;&#39064;&#65292;&#21487;&#20197;&#38543;&#26102; <a style='color:#0000cc' href='http://routea.ca' target='_blank'>&#19982;RouteA&#23545;&#35805;</a></div></div><div style='background:#f7f7f7; height:3px; margin:0 3px; overflow:hidden;'></div></div></body></html>"));
		emailEventMap.put(EmailEvent.forgotPassword, new AbstractMap.SimpleEntry<String, String>("取回您的密码","<!DOCTYPE html><html><head><meta charset='utf-8' /><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><meta name='HandheldFriendly' content='true'><title>routea.ca</title></head><body style='margin-bottom: 0px; margin-top: 0px; padding-bottom: 0px; padding-top: 0px;'><div style=' width:600px; font-size:12px; padding:0; margin:0 auto; font-family:Arial, '宋体'; overflow:hidden'><div style='border:1px solid #eee; verflow:hidden'><div style=' width:598px; height: 71px;border-bottom:2px solid #f5800a; padding:10px; padding-left:20px'><a href='http://routea.ca' target='_blank'><img src='https://routea.ca/emailImages/logo_email.jpg' border='0' alt='低碳生活，优质拼车'></a></div><div style='padding:30px 20px; font-size:14px; color:#000; line-height:24px'><p style='padding-bottom:10px'>&#23562;&#25964;&#30340;RouteA&#29992;&#25143;&#65306;</p><p style='padding-bottom:10px'>&#35831;&#28857;&#20987;&#20197;&#19979;&#38142;&#25509;&#23436;&#25104;&#37325;&#32622;&#23494;&#30721;&#65288;&#22914;&#26080;&#27861;&#25171;&#24320;&#35831;&#25226;&#27492;&#38142;&#25509;&#22797;&#21046;&#31896;&#36148;&#21040;&#27983;&#35272;&#22120;&#25171;&#24320;&#65289;</p><p style='padding-bottom:10px'><a style='color:#0000cc' href='URLTARGET' target='_blank'>URLTARGET</a></p>RouteA&#37038;&#20214;&#20013;&#24515;<br></div><div style='padding:10px 20px; line-height:24px; color:#666; border-top:1px solid #eee; overflow:hidden'>&#9312; &#27492;&#37038;&#20214;&#20026;routea.ca&#31995;&#32479;&#21457;&#20986;&#65292;&#35831;&#21247;&#22238;&#22797;&#37038;&#20214;<br>&#9313; &#22914;&#26524;&#24744;&#26377;&#20219;&#20309;&#38382;&#39064;&#65292;&#21487;&#20197;&#38543;&#26102; <a style='color:#0000cc' href='http://routea.ca' target='_blank'>&#19982;RouteA&#23545;&#35805;</a></div></div><div style='background:#f7f7f7; height:3px; margin:0 3px; overflow:hidden;'></div></div></body></html>"));
		emailEventMap.put(EmailEvent.notification, new AbstractMap.SimpleEntry<String, String>("最新动态提示","TARGET"));

	}
	
	public static final int max_recents = 10;
	public static final long max_feedBackLength = 200000l;
	public static final long max_PostLength = 819200l;
	public static final long max_FileLength = 81920000l;
	
	//redis related
	public static final String key_emailActivationAuth = "ea";
	public static final String key_forgetPasswordAuth = "fp";
	public static final String redisSeperator = "+";
	public static final String redisSeperatorRegex = "\\+";
	public static final long session_updateThreshold = 259200000l;		//3 days
	public static final long session_expireThreshold = 604800000l;		//7 days
	public static final long emailActivation_expireThreshold = 604800000l;		//7 days
	public static final long forgetPassword_expireThreshold = 604800000l;		//7 days
	public static final int session_sequenceLength = 15;
	public static final int emailActivation_sequenceLength = 15;
	public static final int forgetPassword_sequenceLength = 30;
    public static final String redisSearchHistoryPrefix = "usrSRH";;
    public static final int redisSearchHistoryUpbound = isOnLocal ? 6 : 50;
	
	
	public static final String domainName = isOnLocal ? "localhost:8015" : "www.routea.ca";
	public static final String sqlPass = isOnLocal ? "" : "badstudent";
	
	
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
	public static final int maxUserNameLength = 18;
	public static final int minPasswordLength = 6;
	public static final int maxPasswordLength = 30;
	public static final String log_errKeyword = "ERROR!:";
	
	public static final String RegexEmailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	public static final String RegexNamePattern = "[\u4e00-\u9fa5|A-za-z]*";
	public static final String RegexNameWhiteSpacePattern = "\\S*\\s\\S*";
	public static final String RegexPwPattern = "[A-Za-z0-9!@#$%^&*?-_+=]*";
	public static final int qqMinLength = 5;
	public static final int qqMaxLength = 10;
	public static final SearchRepresentation getDefaultSearchRepresentation(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr =  sdf.format(DateUtility.getCurTimeInstance().getTime());		
		long departureMatch_Id = 1;
		long arrivalMatch_Id = 2;		
		return new SearchRepresentation("false" + CarpoolConfig.urlSeperator + departureMatch_Id + CarpoolConfig.urlSeperator + arrivalMatch_Id + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator + dateStr + CarpoolConfig.urlSeperator  + "0" + CarpoolConfig.urlSeperator + "0" + CarpoolConfig.urlSeperator + "0"+CarpoolConfig.urlSeperator + dateStr);
	}

	public static final void initConfig(){
		//do nothing, force static block initialization at start of server
	}

	public static final String timeZoneIdNY = "America/New_York";
	public static final String timeZoneIdCH = "asia/shanghai";
	public static final String timeZoneStandard = "UTC";
	
}
