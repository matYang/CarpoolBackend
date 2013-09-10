package carpool.relayTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import carpool.common.Common;
import carpool.common.JSONFactory;
import carpool.interfaces.PesudoRelayTask;
import carpool.model.Notification;



public class NotificationRelayTask implements PesudoRelayTask{
	
	public static final String relay_notificationPushUrl = "http://localhost:8017/api/v1.0/notifications/push";

	private ArrayList<Notification> notificationQueue;
	
	public NotificationRelayTask(ArrayList<Notification> notificationQueue){
		this.notificationQueue = notificationQueue;
	}
	
	public boolean execute(){
		return sendNotificationToRelay();
	}
	
	public  boolean sendNotificationToRelay(){

	    HttpPost request = new HttpPost(relay_notificationPushUrl);
	    JSONArray json = JSONFactory.toJSON(this.notificationQueue);
	    
	    StringEntity entity;
	    HttpResponse response = null;
		try {
			entity = new StringEntity(json.toString());
			
			entity.setContentType("application/json;charset=UTF-8");
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
			
			request.setHeader("Accept", "application/json");
			request.setEntity(entity); 
			
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			//HttpConnectionParams.setSoTimeout(httpClient.getParams(), Constants.ANDROID_CONNECTION_TIMEOUT*1000); 
			
			response = httpClient.execute(request);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Common.d("NotificationRelayTask::sendNotification::sending encoutered Exception:" + e.toString());
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Common.d("NotificationRelayTask::sendNotification::sending encoutered Exception:" + e.toString());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Common.d("NotificationRelayTask::sendNotification::sending encoutered Exception:" + e.toString());
			return false;
		} 

		
		//parsing and printing the response
		StringBuilder sb = new StringBuilder();
	    InputStream in;
		try {
			in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    
			String line = null;
		    while((line = reader.readLine()) != null){
		        sb.append(line);
		    }
		    //TODO should be deleted later, just printing out the content to see how it's like
		    Common.d(sb.toString());
		    
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Common.d("NotificationRelayTask::sendNotification::parsing encoutered Exception:" + e.toString());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Common.d("NotificationRelayTask::sendNotification::parsing encoutered Exception:" + e.toString());
			return false;
		}
	   
	    return response.getStatusLine().getStatusCode() == 200 ? true : false;
    }
}
