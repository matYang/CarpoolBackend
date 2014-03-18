package carpool.resources.transactionResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.configurations.EnumConfig;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.DuplicateSessionCookieException;
import carpool.exception.auth.SessionEncodingException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.*;
import carpool.resources.PseudoResource;
import carpool.resources.userResource.UserResource;
import carpool.resources.userResource.userAuthResource.UserAuthenticationResource;



public class TransactionResource extends PseudoResource{

	//passes received json into message
	//note that this parseJSON
	protected Transaction parseJSON(JSONObject jsonTransaction){

		Transaction transaction = null;
		try {
			transaction = new Transaction(jsonTransaction.getInt("providerId"), jsonTransaction.getInt("customerId"), jsonTransaction.getInt("messageId"), EnumConfig.PaymentMethod.values()[jsonTransaction.getInt("paymentMethod")], 
					jsonTransaction.getString("customerNote"), jsonTransaction.getString("providerNote"),DateUtility.castFromAPIFormat(jsonTransaction.getString("departure_time")), EnumConfig.DayTimeSlot.values()[jsonTransaction.getInt("departure_timeSlot")], jsonTransaction.getInt("departure_seatsBooked"),
					EnumConfig.TransactionType.values()[jsonTransaction.getInt("transactionType")]);
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
		
		ArrayList<Transaction> allTransactions;
		try {
			allTransactions = TransactionDaoService.getAllTransactions();
		} catch (PseudoException e) {
			this.addCORSHeader();
			return this.doPseudoException(e);
		}
		
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
			JSONObject jsonTransaction = (new JsonRepresentation(entity)).getJsonObject();
			id = jsonTransaction.getInt("userId");
			this.validateAuthentication(id);
			
	        Transaction transaction = parseJSON(jsonTransaction);
	        if (transaction != null){
	        	if (transaction.getProviderId() == id || transaction.getCustomerId() == id){
		        	//check the state of the message, and if the transaction matches the message
	        		Message message = MessageDaoService.getMessageById(transaction.getMessageId());
	        		if (message.validate() && message.isOpen()){
	        			Transaction creationFeedBack = TransactionDaoService.createNewTransaction(transaction);
			                newJsonTransaction = JSONFactory.toJSON(creationFeedBack);
			                setStatus(Status.SUCCESS_OK);

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
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(newJsonTransaction);
        this.addCORSHeader();
        return result;
		
	}


}
