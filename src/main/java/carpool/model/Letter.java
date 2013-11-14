package carpool.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.HelperOperator;
import carpool.common.Validator;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.*;
import carpool.exception.ValidationException;
import carpool.factory.JSONFactory;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;


public class Letter implements PseudoModel, PseudoValidatable, Comparable<User>{
        
    private int letterId;
    private int from_userId;
    private int to_userId;
    private LetterType type;
    
    private User from_user;
    private User to_user;
    
    private String title;
    private String content;
    private Calendar send_time;
    private Calendar check_time;
    
    private LetterState state;
    private boolean historyDeleted;
  
   
    /*****
     * protected constructor to disallow raw initialization and serialization, but allow easier testing
     *****/
    protected Letter(){}
 

    /*****
     * Constructor for letter sending
         *****/
        public Letter(int from_userId, int to_userId, LetterType type, String title, String content) {
                super();

        }



 

    @Override
        public JSONObject toJSON(){
                JSONObject jsonUser = new JSONObject();
                try {
                        
                        jsonUser.put("userId", this.getUserId());
                        jsonUser.put("name", this.getName());
                        jsonUser.put("email", this.getEmail());
                        jsonUser.put("phone", this.getPhone());
                        jsonUser.put("qq", this.getQq());
                        jsonUser.put("age", this.getAge());
                        jsonUser.put("gender", this.getGender().code);
                        jsonUser.put("birthday", DateUtility.castToAPIFormat(this.getBirthday()));
                        jsonUser.put("imgPath", this.getImgPath());
                        jsonUser.put("location", this.location.toJSON());
                        jsonUser.put("lastLogin", DateUtility.castToAPIFormat(this.getLastLogin()));
                        jsonUser.put("creationTime", DateUtility.castToAPIFormat(this.getCreationTime()));

                        jsonUser.put("historyList", JSONFactory.toJSON(this.getHistoryList()));
                        jsonUser.put("watchList", JSONFactory.toJSON(this.getWatchList()));
                        jsonUser.put("socialList", JSONFactory.toJSON(this.getSocialList()));
                        jsonUser.put("transactionList", JSONFactory.toJSON(this.getTransactionList()));
                        jsonUser.put("notificationList", JSONFactory.toJSON(this.getNotificationList()));
                        
                        jsonUser.put("verifications", new JSONArray(this.verifications));
                        jsonUser.put("emailActivated", this.isEmailActivated());
                        jsonUser.put("phoneActivated", this.isPhoneActivated());
                        jsonUser.put("emailNotice", this.isEmailNotice());
                        jsonUser.put("phoneNotice", this.isPhoneNotice());
                        
                        jsonUser.put("state", this.getState().code);
                        jsonUser.put("searchRepresentation", this.getSearchRepresentation().toJSON());
                        jsonUser.put("level", this.getLevel());
                        jsonUser.put("averageScore", this.getAverageScore());
                        jsonUser.put("totalTranscations", this.getTotalTranscations());
                        
                        jsonUser.put("googleToken", this.getGoogleToken());
                        jsonUser.put("facebookToken", this.getFacebookToken());
                        jsonUser.put("twitterToken", this.getTwitterToken());
                        jsonUser.put("paypalToken", this.getPaypalToken());
                        jsonUser.put("id_docType", this.getId_docType());
                        jsonUser.put("id_docNum", this.getId_docNum());
                        jsonUser.put("id_path", this.getId_path());
                        jsonUser.put("id_vehicleImgPath", this.getId_vehicleImgPath());
                        
                        jsonUser.put("accountId", this.getAccountId());
                        jsonUser.put("accountPass", this.getAccountPass());
                        jsonUser.put("accountToken", this.getAccountToken());
                        jsonUser.put("accountValue", this.getAccountValue());

                } catch (JSONException e) {
                        e.printStackTrace();
                }
                
                return jsonUser;
        }





        @Override
        public int compareTo(Letter anotherLetter) {
                return this.getSend_time().compareTo(anotherLetter.getSend_time());
        }


        @Override
        public boolean validate() throws ValidationException{
                // TODO 
                
                
                return true;
        }
        
}
