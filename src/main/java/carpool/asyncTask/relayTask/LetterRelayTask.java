package carpool.asyncTask.relayTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.factory.JSONFactory;
import carpool.interfaces.PseudoAsyncTask;
import carpool.model.Letter;

public class LetterRelayTask implements PseudoAsyncTask{
	
	public static final String relay_letterPushUrl = "http://localhost:8017/api/v1.0/letter/push";

	private Letter letter;

	
	public LetterRelayTask(Letter letter){
		this.letter = letter;
	}
	
	public boolean execute(){
		return sendNotificationToRelay();
	}
	
	public  boolean sendNotificationToRelay(){

	    HttpPost request = new HttpPost(relay_letterPushUrl);
	    JSONObject json = JSONFactory.toJSON(this.letter);
	    
	    StringEntity entity;
	    HttpResponse response = null;
		try {
			
			entity = new StringEntity(json.toString());
			
			entity.setContentType("application/json;charset=UTF-8");
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
			
			request.setHeader("Accept", "application/json");
			request.setEntity(entity); 
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			response = httpClient.execute(request);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			DebugLog.d(e);
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			DebugLog.d(e);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			DebugLog.d(e);
			return false;
		} 

		if (response.getStatusLine().getStatusCode() != 200){
			DebugLog.d(Constants.log_errKeyword + " sending letter failed with status: " + response.getStatusLine().getStatusCode());
		}
	    return response.getStatusLine().getStatusCode() == 200 ? true : false;
	}
	
}
