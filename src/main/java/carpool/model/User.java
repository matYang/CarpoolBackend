package carpool.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.Common;
import carpool.common.Constants;
import carpool.common.JSONFactory;
import carpool.common.Constants.gender;
import carpool.common.Constants.userSearchState;
import carpool.common.Constants.userState;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;


public class User implements PseudoModel, PseudoValidatable, Comparable<User>{
	
	/******
	 *  the following stores user's account informations
	 ******/
    private int userId;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String qq;
    private int age;
    private gender gender;
    private Calendar birthday;
    private String imgPath;
    private Location location;

    private Calendar lastLogin;
    private Calendar creationTime;
    
    
    /*****
     * the followings are for user's relations
     ******/
    private ArrayList<Message> historyList;
    private ArrayList<Message> watchList;
    private ArrayList<User> socialList;
    private ArrayList<Transaction> transactionList;
    private ArrayList<Notification> notificationList;
    @Deprecated
    private ArrayList<String> universityGroup;
    

    /*****
     * the followings are user's state information
     *****/
    private boolean emailActivated;
    private boolean phoneActivated;
    private boolean emailNotice;
    private boolean phoneNotice;
    private userState state;
    private userSearchState searchState;

    
    /*****
     * the following stores user's statistics
     *****/
    private int level;
    private int averageScore;
    private int totalTranscations;
    
    /*****
     * the followings are user's authorizations
     *****/
    private String googleToken;
    private String facebookToken;
    private String twitterToken;
    private String paypalToken;
    private String id_docType;
    private String id_docNum;
    private String id_path;
    private String id_vehicleImgPath;
    
    /*****
     * the follows are user's account information
     *****/
    private String accountId;
    private String accountPass;
    private String accountToken;
    private BigDecimal accountValue;
    
    
    private User(){
    	
    }
    
    

    /** 
     * check if user can be logged in
     * @return  if the user is able to be logged in, etc, state == normal, emailActivated == true
     */
    public boolean isAbleToLogin(){
        //TODO
        return this.isEmailActivated();
    }

    /**
     * check if existing user is valid, potentially has to look into database
     * @param user
     * @return
     */
    public boolean isUserValid(){
        //TODO
        return true;
    }

    /**
     * check if newUser is valid, potentially has to look into database
     * checks if the newly registered is valid
     */
    public boolean isNewUserValid(){
        //isEmailUserNameAvailable(newUser.getEmail(), newUser.getUserName());
        //TODO	
        return true;
    }



    /**
     * check if password is in a valid format
     * @param password
     * @return true if password is valid
     */
    public static boolean isPasswordFormatValid(String password){
        if (password == null || password.length() == 0 || password.length() > Constants.maxPasswordLength){
            return false;
        }
        return true;
    }

    /**
     * check if user's name is in a valid format
     * @param userName
     * @return true if name is valid
     */
    public static boolean isNameFormatValid(String userName){
        if (userName == null || userName.length() == 0 || userName.length() > Constants.maxUserNameLength){
            return false;
        }
        //check for @
        if (userName.indexOf("@") >= 0){
            return false;
        }
        //check for all-number userName
        for (int i = 0; i < userName.length(); i++) {
            if (Character.isLetter(userName.charAt(i)) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if age is valid
     * @param age
     * @return true if age is valid
     */
    public static boolean isAgeValid(int age){
        if(age>5 && age<99){
            return true;
        }
        return false;
    }

	public static boolean isGenderValid(gender gender) {
		// TODO
		return true;
	}

	/**
	 * if the searchState is valid for currently user, currently not null, between 0-5 is good
	 * @param searchState
	 * @return
	 */
	public static boolean isSearchStateValid(userSearchState searchState) {
		// TODO 
		return true;
	}
	

	@Override
	public JSONObject toJSON(){
		JSONObject jsonUser = new JSONObject(this);
		try {
			jsonUser.put("lastLogin", Common.CalendarToUTCString(this.getLastLogin()));
			jsonUser.put("creationTime", Common.CalendarToUTCString(this.getCreationTime()));
			
			jsonUser.put("location", this.location.toJSON());
			jsonUser.put("historyList", JSONFactory.toJSON(this.getHistoryList()));
			jsonUser.put("watchList", JSONFactory.toJSON(this.getWatchList()));
			jsonUser.put("socialList", JSONFactory.toJSON(this.getSocialList()));
			jsonUser.put("transactionList", JSONFactory.toJSON(this.getTransactionList()));
			jsonUser.put("notificationList", JSONFactory.toJSON(this.getNotificationList()));
			
			jsonUser.put("universityGroup", JSONFactory.toJSON_arr_str(this.getUniversityGroup()));
			
			jsonUser.put("gender", this.getGender());
			jsonUser.put("state", this.getState());
			jsonUser.put("searchState", this.getSearchState());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonUser;
	}
	
	
}

