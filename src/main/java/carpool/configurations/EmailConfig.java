package carpool.configurations;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

import carpool.configurations.EnumConfig.EmailEvent;

public class EmailConfig {
	
	//used for emails,  Entry consists of subject and template
	public static final HashMap<EmailEvent, Entry<String, String>> emailEventMap;
	
	static{
		emailEventMap = new HashMap<EmailEvent, Entry<String, String>>();
		//请激活您的账户	&#35831;&#28608;&#27963;&#24744;&#30340;&#36134;&#25143;
		emailEventMap.put(EmailEvent.activeateAccount, new AbstractMap.SimpleEntry<String, String>("请激活您的账户","<!DOCTYPE html><html><head><meta charset='utf-8' /><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><meta name='HandheldFriendly' content='true'><title>routea.ca</title></head><body style='margin-bottom: 0px; margin-top: 0px; padding-bottom: 0px; padding-top: 0px;'><div style=' width:600px; font-size:12px; padding:0; margin:0 auto; font-family:Arial, '宋体'; overflow:hidden'><div style='border:1px solid #eee; verflow:hidden'><div style=' width:598px; height: 71px;border-bottom:2px solid #f5800a; padding:10px; padding-left:20px'><a href='http://routea.ca' target='_blank'><img src='https://routea.ca/emailImages/logo_email.jpg' border='0' alt='&#20302;&#30899;&#29983;&#27963;&#65292;&#20248;&#36136;&#25340;&#36710;'></a></div><div style='padding:30px 20px; font-size:14px; color:#000; line-height:24px'><p style='padding-bottom:10px'>&#24863;&#35874;&#24744;&#27880;&#20876;RouteA&#65292;&#35831;&#28857;&#20987;&#19979;&#38754;&#30340;&#38142;&#25509;&#23436;&#25104;&#27880;&#20876;&#65288;&#22914;&#26080;&#27861;&#25171;&#24320;&#35831;&#25226;&#27492;&#38142;&#25509;&#22797;&#21046;&#31896;&#36148;&#21040;&#27983;&#35272;&#22120;&#25171;&#24320;&#65289;&#12290;</p><p style='padding-bottom:10px'><a style='color:#0000cc' href='URLTARGET' target='_blank'>URLTARGET</a></p>RouteA&#37038;&#20214;&#20013;&#24515; <br></div><div style='padding:10px 20px; line-height:24px; color:#666; border-top:1px solid #eee; overflow:hidden'>&#9312; &#27492;&#37038;&#20214;&#20026;routea.ca&#31995;&#32479;&#21457;&#20986;&#65292;&#35831;&#21247;&#22238;&#22797;&#37038;&#20214;<br>&#9313; &#22914;&#26524;&#24744;&#26377;&#20219;&#20309;&#38382;&#39064;&#65292;&#21487;&#20197;&#38543;&#26102; <a style='color:#0000cc' href='http://routea.ca' target='_blank'>&#19982;RouteA&#23545;&#35805;</a></div></div><div style='background:#f7f7f7; height:3px; margin:0 3px; overflow:hidden;'></div></div></body></html>"));
		//取回您的密码		&#21462;&#22238;&#24744;&#30340;&#23494;&#30721;
		emailEventMap.put(EmailEvent.forgotPassword, new AbstractMap.SimpleEntry<String, String>("取回您的密码","<!DOCTYPE html><html><head><meta charset='utf-8' /><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><meta name='HandheldFriendly' content='true'><title>routea.ca</title></head><body style='margin-bottom: 0px; margin-top: 0px; padding-bottom: 0px; padding-top: 0px;'><div style=' width:600px; font-size:12px; padding:0; margin:0 auto; font-family:Arial, '宋体'; overflow:hidden'><div style='border:1px solid #eee; verflow:hidden'><div style=' width:598px; height: 71px;border-bottom:2px solid #f5800a; padding:10px; padding-left:20px'><a href='http://routea.ca' target='_blank'><img src='https://routea.ca/emailImages/logo_email.jpg' border='0' alt='&#20302;&#30899;&#29983;&#27963;&#65292;&#20248;&#36136;&#25340;&#36710;'></a></div><div style='padding:30px 20px; font-size:14px; color:#000; line-height:24px'><p style='padding-bottom:10px'>&#23562;&#25964;&#30340;RouteA&#29992;&#25143;&#65306;</p><p style='padding-bottom:10px'>&#35831;&#28857;&#20987;&#20197;&#19979;&#38142;&#25509;&#23436;&#25104;&#37325;&#32622;&#23494;&#30721;&#65288;&#22914;&#26080;&#27861;&#25171;&#24320;&#35831;&#25226;&#27492;&#38142;&#25509;&#22797;&#21046;&#31896;&#36148;&#21040;&#27983;&#35272;&#22120;&#25171;&#24320;&#65289;</p><p style='padding-bottom:10px'><a style='color:#0000cc' href='URLTARGET' target='_blank'>URLTARGET</a></p>RouteA&#37038;&#20214;&#20013;&#24515;<br></div><div style='padding:10px 20px; line-height:24px; color:#666; border-top:1px solid #eee; overflow:hidden'>&#9312; &#27492;&#37038;&#20214;&#20026;routea.ca&#31995;&#32479;&#21457;&#20986;&#65292;&#35831;&#21247;&#22238;&#22797;&#37038;&#20214;<br>&#9313; &#22914;&#26524;&#24744;&#26377;&#20219;&#20309;&#38382;&#39064;&#65292;&#21487;&#20197;&#38543;&#26102; <a style='color:#0000cc' href='http://routea.ca' target='_blank'>&#19982;RouteA&#23545;&#35805;</a></div></div><div style='background:#f7f7f7; height:3px; margin:0 3px; overflow:hidden;'></div></div></body></html>"));
		//最新动态提示		&#26368;&#26032;&#21160;&#24577;&#25552;&#31034;
		emailEventMap.put(EmailEvent.notification, new AbstractMap.SimpleEntry<String, String>("最新动态提示","TARGET"));
		
	}

	//email
	public static final String SMTP_USERNAME = "AKIAIK5KV62M7VTBOJDQ";
	public static final String SMTP_PASSWORD = "AklHWYPbI4LBZcQZB3BDqj9KQgl20FyUZj296ru6aRD+";
	public static final String SMTP_FROM = "info@routea.ca";
	public static final String SMTP_HOST = "email-smtp.us-east-1.amazonaws.com";
	public static final int SMTP_PORT = 587;
	public static final String htmlTemplateURLTarget = "URLTARGET";
	public static final String htmlTemplateNameTarget = "NAMETARGET";

}
