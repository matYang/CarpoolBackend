package carpool.resources.transactionResource;

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

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserCookieResource;
import carpool.resources.userResource.UserResource;



public class TransactionResource extends PseudoResource{

	//passes received json into message
	//note that this parseJSON
	protected Transaction parseJSON(Representation entity, int userId){
		JSONObject jsonTransaction = null;

		Transaction transaction = null;
		try {
			jsonTransaction = (new JsonRepresentation(entity)).getJsonObject();
			DebugLog.d("@Post::receive jsonTransaction: " +  jsonTransaction.toString());
			
			transaction = new Transaction(jsonTransaction.getInt("initUserId"), jsonTransaction.getInt("targetUserId"), jsonTransaction.getInt("messageId"), Constants.paymentMethod.values()[jsonTransaction.getInt("paymentMethod")], 
					jsonTransaction.getInt("price"), jsonTransaction.getString("requestInfo"),  DateUtility.parseDateString(jsonTransaction.getString("startTime")), 
					DateUtility.parseDateString(jsonTransaction.getString("endTime")), new Location(jsonTransaction.getJSONObject("location").getString("province"), jsonTransaction.getJSONObject("location").getString("city"), jsonTransaction.getJSONObject("location").getString("region"),jsonTransaction.getJSONObject("location").getString("university")) );
		} catch (Exception e) {
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
		this.addCORSHeader();
		return result;
	}

	

	@Post
	public Representation createTransaction(Representation entity) {
		
		int id = -1;
        JSONObject newJsonTransaction = new JSONObject();
        
		try {
			this.checkEntity(entity);
			
			id = Integer.parseInt(this.getQueryVal("userId"));
			this.validateAuthentication(id);
			
	        Transaction transaction = parseJSON(entity, id);
	        if (transaction != null){
	        	if (transaction.getInitUserId() == id){
		        	//check the state of the message, and if the transaction matches the message
	        		Message message = MessageDaoService.getMessageById(transaction.getMessageId());
	        		if (message.validate() && message.getStartTime().compareTo(transaction.getStartTime()) == 0 && message.getEndTime().compareTo(transaction.getEndTime()) == 0){
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
			
		} catch (PseudoException e){
        	this.doPseudoException(e);
        } catch(Exception e){
			this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(newJsonTransaction);
        this.addCORSHeader();
        return result;
		
	}


}
