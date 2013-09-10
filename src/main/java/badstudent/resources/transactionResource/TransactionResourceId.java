package badstudent.resources.transactionResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.common.Constants.messageState;
import badstudent.common.Constants.transactionStateChangeAction;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.exception.message.MessageNotFoundException;
import badstudent.exception.message.MessageOwnerNotMatchException;
import badstudent.exception.transaction.TransactionNotFoundException;
import badstudent.exception.transaction.TransactionOwnerNotMatchException;
import badstudent.exception.transaction.TransactionStateViolationException;
import badstudent.model.*;
import badstudent.mappings.*;
import badstudent.resources.userResource.UserCookieResource;
import badstudent.resources.userResource.UserResource;

public class TransactionResourceId extends ServerResource{
	

    @Get 
    /**
     * @return  get the transaction by its transaction id
     */
    public Representation getTransactionById() {
    	int id = -1;
    	int transactionId = -1;
        boolean goOn = false;
        JSONObject jsonObject = new JSONObject();
        
        try {
			transactionId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn){
	        	Transaction transaction = TransactionDaoService.getUserTransactionById(transactionId, id);
	        	if (transaction != null){
	                jsonObject = JSONFactory.toJSON(transaction);
	                setStatus(Status.SUCCESS_OK);
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	        	}
	        }
			
		} catch (TransactionOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (TransactionNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        
        
        Representation result = new JsonRepresentation(jsonObject);
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return result;
    }
    
    //if authentication passed, local model should have the correct password field, thus checking both password and authCode here, please note under other situations password on the front end would be goofypassword
    //authCode must not equal to initial authCode -1
    @Put 
    public Representation updateTransaction(Representation entity) {
        int userId = -1;
        int transactionId = -1;
        int stateIndex = -1;
        boolean goOn = true;
        JSONObject newJsonTransaction = new JSONObject();
        Transaction transaction = new Transaction();
        
		try {
			transactionId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			userId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			stateIndex = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("stateIndex"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn && entity!= null && entity.getSize() < Constants.max_TransactionLength){
				transactionStateChangeAction stateChangeAction = transactionStateChangeAction.fromInt(stateIndex);
				
		        if (stateChangeAction != null){
		        	switch(stateChangeAction){
		        		case confirm:
		        			transaction = TransactionDaoService.confirmTransaction(transactionId, userId);
		        			break;
		        			
		        		case refuse:
		        			transaction = TransactionDaoService.refuseTransaction(transactionId, userId);
		        			break;
		        			
		        		case cancel:
		        			transaction = TransactionDaoService.cancelTransaction(transactionId, userId);
		        			break;
		        			
		        		case report:
		        			transaction = TransactionDaoService.reportTransaction(transactionId, userId);
		        			break;
		        			
		        		case evaluate:
		        			int score = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("score"),"utf-8"));
		        			transaction = TransactionDaoService.evaluateTransaction(transactionId, userId, score);
		        			break;
		        			
		        		default:
		        			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		        			break;
		        	}
		        }
		        else{
		        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		        }
		        
		        newJsonTransaction = JSONFactory.toJSON(transaction);
	        }
	        else if (entity == null){
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	        else{
	        	setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
	        }
			
		} catch (TransactionOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (TransactionNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (TransactionStateViolationException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
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
    
    
    //now front end sending delete must expose authCode as a parameter, must not equal to initial authCode -1
    @Delete
    public Representation deleteTransaction() {
    	boolean deleted = false;
    	boolean goOn = true;
    	
    	int id = -1;
    	int transactionId = -1;
		try {
			transactionId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"), "utf-8"));
			id = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("userId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			//not full user here
			if (goOn){
		   		 deleted = TransactionDaoService.deleteTransaction(transactionId, id);
		   		 if (deleted){
			       	 setStatus(Status.SUCCESS_OK);
			       	 Common.d("@Delete with id: " + transactionId);
			     }
			     else{
			       	 setStatus(Status.CLIENT_ERROR_CONFLICT);
			     }
			}
			
        } catch (TransactionOwnerNotMatchException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		} catch (TransactionNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (TransactionStateViolationException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
		} catch (DuplicateSessionCookieException e1){
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch(NumberFormatException e){
        	e.printStackTrace();
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        } catch(UnsupportedEncodingException e1){
			e1.printStackTrace();
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch(Exception e2){
			e2.printStackTrace();
        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
	      
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return null;
    }



    //needed here since backbone will try to send OPTIONS to /id before PUT or DELETE
    @Options
    public Representation takeOptions(Representation entity) {
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 
        //send anything back will be fine, browser just expects a response
        return new JsonRepresentation(new JSONObject());
    }

}
