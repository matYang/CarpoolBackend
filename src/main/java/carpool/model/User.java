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
import carpool.constants.Constants;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.userSearchState;
import carpool.constants.Constants.userState;
import carpool.exception.validation.ValidationException;
import carpool.factory.JSONFactory;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;
import carpool.model.representation.SearchRepresentation;


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
    private gender gender;
    private Calendar birthday;
    private String imgPath;
    private long location_Id;
    private Location location;
    private long match_Id;
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
    

    /*****
     * the followings are user's state information
     *****/
    private boolean emailActivated;
    private boolean phoneActivated;
    private boolean emailNotice;
    private boolean phoneNotice;
    private userState state;
    private SearchRepresentation searchRepresentation;

    
    /*****
     * the following stores user's statistics
     *****/
    private int level;
    private int averageScore;
    private int totalTranscations;
    
    /*****
     * the followings are user's authorizations
     *****/
    private ArrayList<String> verifications;
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
    
    
    /*****
     * protected constructor to disallow raw initialization and serialization, but allow easier testing
     *****/
    protected User(){}
 

    /*****
     * Constructor for user registration
	 *****/
	public User(String password, String email, Location location, gender g) {
		super();
		this.userId = -1;
		this.password = password;
		this.email = email;
		this.location = location;
		this.location_Id = location.getId();
		this.match_Id = location.getMatch();
		//dummy fills
		this.name = "无名氏";
		this.phone = "";
		this.qq = "";
	    this.gender = g;
	    this.birthday = Calendar.getInstance();
	    this.imgPath = "res/personal/default-avatar.jpg";
	    this.lastLogin = Calendar.getInstance();
	    this.creationTime = Calendar.getInstance();
	    
	    this.historyList = new ArrayList<Message>();
	    this.watchList = new ArrayList<Message>();
	    this.socialList = new ArrayList<User>();
	    this.transactionList = new ArrayList<Transaction>();
	    this.notificationList = new ArrayList<Notification>();
  
	    this.verifications = new ArrayList<String>();
	    this.emailActivated = false;
	    this.phoneActivated = false;
	    this.emailNotice = false;
	    this.phoneNotice = false;
	    this.state = Constants.userState.normal;
	    this.searchRepresentation = CarpoolConfig.getDefaultSearchRepresentation();
	    
	    this.level = 0;
	    this.averageScore = 0;
	    this.totalTranscations = 0;

	    this.googleToken = "";
	    this.facebookToken = "";
	    this.twitterToken = "";
	    this.paypalToken = "";
	    this.id_docType = "";
	    this.id_docNum = "";
	    this.id_path = "";
	    this.id_vehicleImgPath = "";
	    
	    this.accountId = "";
	    this.accountPass = "";
	    this.accountToken = "";
	    this.accountValue = BigDecimal.valueOf(0l);
	    
	}


	/*****
	 * full constructor used for SQL retrieval
	 *****/
	public User(int userId, String password, String name, String email,
			String phone, String qq, carpool.constants.Constants.gender gender, Calendar birthday,
			String imgPath, Location location, Calendar lastLogin,
			Calendar creationTime, ArrayList<String> verifications, boolean emailActivated,
			boolean phoneActivated, boolean emailNotice, boolean phoneNotice,
			userState state, SearchRepresentation searchRepresentation, int level,
			int averageScore, int totalTranscations, String googleToken,
			String facebookToken, String twitterToken, String paypalToken,
			String id_docType, String id_docNum, String id_path,
			String id_vehicleImgPath, String accountId, String accountPass,
			String accountToken, BigDecimal accountValue,long match_Id) {
		super();
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.qq = qq;
		this.gender = gender;
		this.birthday = birthday;
		this.imgPath = imgPath;
		this.location = location;
		this.location_Id = location.getId();
		this.match_Id = match_Id;
		this.lastLogin = lastLogin;
		this.creationTime = creationTime;
		this.verifications = verifications;
		this.emailActivated = emailActivated;
		this.phoneActivated = phoneActivated;
		this.emailNotice = emailNotice;
		this.phoneNotice = phoneNotice;
		this.state = state;
		this.searchRepresentation = searchRepresentation;
		this.level = level;
		this.averageScore = averageScore;
		this.totalTranscations = totalTranscations;
		this.googleToken = googleToken;
		this.facebookToken = facebookToken;
		this.twitterToken = twitterToken;
		this.paypalToken = paypalToken;
		this.id_docType = id_docType;
		this.id_docNum = id_docNum;
		this.id_path = id_path;
		this.id_vehicleImgPath = id_vehicleImgPath;
		this.accountId = accountId;
		this.accountPass = accountPass;
		this.accountToken = accountToken;
		this.accountValue = accountValue;
		
		this.historyList = new ArrayList<Message>();
	    this.watchList = new ArrayList<Message>();
	    this.socialList = new ArrayList<User>();
	    this.transactionList = new ArrayList<Transaction>();
	    this.notificationList = new ArrayList<Notification>();
	}

	
	

	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getQq() {
		return qq;
	}


	public void setQq(String qq) {
		this.qq = qq;
	}


	public int getAge() {
		Calendar c_age = Calendar.getInstance();
		return c_age.get(Calendar.YEAR) - this.birthday.get(Calendar.YEAR);
	}


	public gender getGender() {
		return gender;
	}


	public void setGender(gender gender) {
		this.gender = gender;
	}


	public Calendar getBirthday() {
		return birthday;
	}


	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}


	public String getImgPath() {
		return imgPath;
	}


	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
		this.location_Id = location.getId();
	}
		
	public Calendar getLastLogin() {
		return lastLogin;
	}


	public void setLastLogin(Calendar lastLogin) {
		this.lastLogin = lastLogin;
	}


	public Calendar getCreationTime() {
		return creationTime;
	}



	public ArrayList<Message> getHistoryList() {
		return historyList;
	}


	public void setHistoryList(ArrayList<Message> historyList) {
		this.historyList = historyList;
	}


	public ArrayList<Message> getWatchList() {
		return watchList;
	}


	public void setWatchList(ArrayList<Message> watchList) {
		this.watchList = watchList;
	}


	public ArrayList<User> getSocialList() {
		return socialList;
	}


	public void setSocialList(ArrayList<User> socialList) {
		this.socialList = socialList;
	}


	public ArrayList<Transaction> getTransactionList() {
		return transactionList;
	}


	public void setTransactionList(ArrayList<Transaction> transactionList) {
		this.transactionList = transactionList;
	}


	public ArrayList<Notification> getNotificationList() {
		return notificationList;
	}


	public void setNotificationList(ArrayList<Notification> notificationList) {
		this.notificationList = notificationList;
	}

	
	public ArrayList<String> getVerifications(){
		return this.verifications;
	}
	
	public void setVerifications(ArrayList<String> verifications){
		this.verifications = verifications;
	}
	
	public boolean isEmailActivated() {
		return emailActivated;
	}


	public void setEmailActivated(boolean emailActivated) {
		this.emailActivated = emailActivated;
	}


	public boolean isPhoneActivated() {
		return phoneActivated;
	}


	public void setPhoneActivated(boolean phoneActivated) {
		this.phoneActivated = phoneActivated;
	}


	public boolean isEmailNotice() {
		return emailNotice;
	}


	public void setEmailNotice(boolean emailNotice) {
		this.emailNotice = emailNotice;
	}


	public boolean isPhoneNotice() {
		return phoneNotice;
	}


	public void setPhoneNotice(boolean phoneNotice) {
		this.phoneNotice = phoneNotice;
	}


	public userState getState() {
		return state;
	}


	public void setState(userState state) {
		this.state = state;
	}


	public SearchRepresentation getSearchRepresentation() {
		return searchRepresentation;
	}


	public void setSearchRepresentation(SearchRepresentation searchRepresentation) {
		this.searchRepresentation = searchRepresentation;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getAverageScore() {
		return averageScore;
	}


	public void setAverageScore(int averageScore) {
		this.averageScore = averageScore;
	}


	public int getTotalTranscations() {
		return totalTranscations;
	}


	public void setTotalTranscations(int totalTranscations) {
		this.totalTranscations = totalTranscations;
	}


	public String getGoogleToken() {
		return googleToken;
	}


	public void setGoogleToken(String googleToken) {
		this.googleToken = googleToken;
	}


	public String getFacebookToken() {
		return facebookToken;
	}


	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}


	public String getTwitterToken() {
		return twitterToken;
	}


	public void setTwitterToken(String twitterToken) {
		this.twitterToken = twitterToken;
	}


	public String getPaypalToken() {
		return paypalToken;
	}


	public void setPaypalToken(String paypalToken) {
		this.paypalToken = paypalToken;
	}


	public String getId_docType() {
		return id_docType;
	}


	public void setId_docType(String id_docType) {
		this.id_docType = id_docType;
	}


	public String getId_docNum() {
		return id_docNum;
	}


	public void setId_docNum(String id_docNum) {
		this.id_docNum = id_docNum;
	}


	public String getId_path() {
		return id_path;
	}


	public void setId_path(String id_path) {
		this.id_path = id_path;
	}


	public String getId_vehicleImgPath() {
		return id_vehicleImgPath;
	}


	public void setId_vehicleImgPath(String id_vehicleImgPath) {
		this.id_vehicleImgPath = id_vehicleImgPath;
	}


	public String getAccountId() {
		return accountId;
	}


	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}


	public String getAccountPass() {
		return accountPass;
	}


	public void setAccountPass(String accountPass) {
		this.accountPass = accountPass;
	}


	public String getAccountToken() {
		return accountToken;
	}


	public void setAccountToken(String accountToken) {
		this.accountToken = accountToken;
	}

	public long getLocation_Id() {
		return location_Id;
	}

	public void setLocation_Id(long location_Id) {
		this.location_Id = location_Id;
	}

	public long getMatch_Id() {
		return match_Id;
	}

	public void setMatch_Id(long match_Id) {
		this.match_Id = match_Id;
	}

	public BigDecimal getAccountValue() {
		return accountValue;
	}


	public void setAccountValue(BigDecimal accountValue) {
		this.accountValue = accountValue;
	}
	

	public boolean isPasswordCorrect(String password) {
		return this.password.equals(password);
	}


	public void setPassword(String oldPassword, String newPassword) throws ValidationException{
		if (isPasswordCorrect(oldPassword)){
			this.password = newPassword;
		}
		else{
			throw new ValidationException("Password Incorrect");
		}
	}


	/** 
     * check if user can be logged in
     * Precondition: email has been activated, and state is in normal
     */
    public boolean isAbleToLogin(){
        return this.isEmailActivated() && this.state == Constants.userState.normal;
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
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", name="
				+ name + ", email=" + email + ", phone=" + phone + ", qq=" + qq
				+ ", age=" + this.getAge() + ", gender=" + gender + ", birthday="
				+ birthday + ", imgPath=" + imgPath + ", location=" + location
				+ ", lastLogin=" + lastLogin + ", creationTime=" + creationTime
				+ ", historyList=" + historyList + ", watchList=" + watchList
				+ ", socialList=" + socialList + ", transactionList="
				+ transactionList + ", notificationList=" + notificationList
				+ ", verifications=" + verifications + ", emailActivated="
				+ emailActivated + ", phoneActivated=" + phoneActivated
				+ ", emailNotice=" + emailNotice + ", phoneNotice="
				+ phoneNotice + ", state=" + state + ", searchRepresentation="
				+ searchRepresentation.toSerializedString() + ", level=" + level + ", averageScore="
				+ averageScore + ", totalTranscations=" + totalTranscations
				+ ", googleToken=" + googleToken + ", facebookToken="
				+ facebookToken + ", twitterToken=" + twitterToken
				+ ", paypalToken=" + paypalToken + ", id_docType=" + id_docType
				+ ", id_docNum=" + id_docNum + ", id_path=" + id_path
				+ ", id_vehicleImgPath=" + id_vehicleImgPath + ", accountId="
				+ accountId + ", accountPass=" + accountPass
				+ ", accountToken=" + accountToken + ", accountValue="
				+ accountValue + "]";
	}

	//override Comparator, by default users will be sorted in alphabetical order of their names
	@Override
	public int compareTo(User anotherUser) {
		return this.getName().compareTo(anotherUser.getName());
	}

    public boolean equals(User newUser) throws ValidationException{       	
    	return newUser !=null && this.userId == newUser.getUserId() 
    			              && this.name.equals(newUser.getName())
    			              && this.gender.equals(newUser.getGender())
    			              && this.location.equals(newUser.getLocation())
    			              && this.emailActivated==newUser.emailActivated
    			              && this.emailNotice==newUser.emailNotice
    			              && this.phoneActivated==newUser.phoneActivated
    			              && this.phoneNotice== newUser.phoneNotice
    			              && this.password.equals(newUser.getPassword())
    			              && this.isAbleToLogin()==newUser.isAbleToLogin()
    			              && this.validate()==newUser.validate()
    			              && this.accountToken.equals(newUser.getAccountToken())
    			              && this.accountValue.toString().equals(newUser.getAccountValue().toString())
    			              && this.getAge()==newUser.getAge()
    			              && this.averageScore==newUser.getAverageScore()
    			              && this.birthday.getTime().toString().equals(newUser.getBirthday().getTime().toString())
    			              && this.creationTime.getTime().toString().equals(newUser.getCreationTime().getTime().toString())
    			              && this.facebookToken.equals(newUser.getFacebookToken())
    			              && this.googleToken.equals(newUser.getGoogleToken())
    			              && this.gender.code==newUser.getGender().code
    			              && HelperOperator.isArrayListEqual(this.historyList, newUser.getHistoryList())
    			              && this.id_docNum.equals(newUser.getId_docNum())
    			              && this.id_docType.equals(newUser.getId_docType())
    			              && this.id_path.equals(newUser.getId_path())
    			              && this.id_vehicleImgPath.equals(newUser.getId_vehicleImgPath())
    			              && this.imgPath.equals(newUser.getImgPath())
    			              && this.lastLogin.getTime().toString().equals(newUser.getLastLogin().getTime().toString())
    			              && this.level==newUser.getLevel()
    			              && this.paypalToken.equals(newUser.getPaypalToken())
    			              && this.phone.equals(newUser.getPhone())
    			              && this.qq.equals(newUser.getQq())
    			              && this.state.code==newUser.getState().code
    			              && this.twitterToken.equals(newUser.getTwitterToken())
    			              && HelperOperator.isArrayListEqual(this.watchList, newUser.getWatchList())
    			              && HelperOperator.isArrayListEqual(this.socialList, newUser.getSocialList())
    			              && HelperOperator.isArrayListEqual(this.verifications, newUser.getVerifications());
    	                      
    	
    }
	@Override
	public boolean validate() throws ValidationException{
		//TODO remove true
		return true || Validator.isEmailFormatValid(this.email) && Validator.isNameFormatValid(this.name) && Validator.isPasswordFormatValid(this.password) && Validator.isPhoneFormatValid(this.phone) && Validator.isQqFormatValid(this.qq);
	}

	public String getPassword() {
		return  this.password;
	}

}

