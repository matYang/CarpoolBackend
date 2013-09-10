package badstudent.resources.transactionResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.model.*;
import badstudent.resources.userResource.UserCookieResource;
import badstudent.resources.userResource.UserResource;

public class TransactionResource extends ServerResource{

	//passes received json into message
	//note that this parseJSON
	private Transaction parseJSON(Representation entity, int userId){
		JSONObject jsonTransaction = null;
		try {
			jsonTransaction = (new JsonRepresentation(entity)).getJsonObject();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Common.d("@Post::receive jsonTransaction: " +  jsonTransaction.toString());

		Transaction transaction = null;
		try {
			transaction = new Transaction(jsonTransaction.getInt("initUserId"), jsonTransaction.getInt("targetUserId"), jsonTransaction.getInt("messageId"), Constants.paymentMethod.values()[jsonTransaction.getInt("paymentMethod")], 
					jsonTransaction.getInt("price"), jsonTransaction.getString("requestInfo"),  Common.parseDateString(jsonTransaction.getString("startTime")), 
					Common.parseDateString(jsonTransaction.getString("endTime")), new Location(jsonTransaction.getJSONObject("location").getString("province"), jsonTransaction.getJSONObject("location").getString("city"), jsonTransaction.getJSONObject("location").getString("region"),jsonTransaction.getJSONObject("location").getString("university")) );
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		  catch (NullPointerException e){
			e.printStackTrace();
			Common.d("likely invalid location string format");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return transaction;
	}
	
	
	@Get
	/**
	 * Retrieve all transactions from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllTransactions() {
		

		ArrayList<Transaction> allTransactions = TransactionDaoService.getAllTransactions();
		JSONArray jsonArray = new JSONArray();
		
		if (allTransactions == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allTransactions);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);

		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}

	

	@Post
	public Representation createTransaction(Representation entity) {
		
		int id = -1;
        boolean goOn = true;
        JSONObject newJsonTransaction = new JSONObject();
        
		try {
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_TransactionLength){
		        Transaction transaction = parseJSON(entity, id);
		        if (transaction != null){
		        	if (transaction.getInitUserId() == id){
			        	//check the state of the message, and if the transaction matches the message
		        		DMMessage message = DMMessageDaoService.getMessageById(transaction.getMessageId());
		        		if (message.isMessageValid() && message.getStartTime().compareTo(transaction.getStartTime()) == 0 && message.getEndTime().compareTo(transaction.getEndTime()) == 0){
		        			Transaction creationFeedBack = TransactionDaoService.createNewTransaction(transaction);
				            if (creationFeedBack != null){
				                newJsonTransaction = JSONFactory.toJSON(creationFeedBack);
				                setStatus(Status.SUCCESS_OK);
				            }
				            else{
				            	setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				            }
		        		}
		        		else{
		        			setStatus(Status.CLIENT_ERROR_CONFLICT);
			            }
		        	}
		        	else{
		        		setStatus(Status.CLIENT_ERROR_CONFLICT);
		        	}
		        }
		        else{
		        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		        }
	        }
	        else if (entity == null){
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
	        }
			
			
		} catch (DuplicateSessionCookieException e1){
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}catch(Exception e1){
			e1.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        
        Representation result =  new JsonRepresentation(newJsonTransaction);
        //set the response header
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        }  

        return result;
		
	}

	
	
	//needed here since backbone will try to send OPTIONS before POST
	@Options
	public Representation takeOptions(Representation entity) {
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 

		return new JsonRepresentation(new JSONObject());
	}


}
