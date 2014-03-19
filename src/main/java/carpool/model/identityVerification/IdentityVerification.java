package carpool.model.identityVerification;

import java.util.Calendar;

import carpool.configurations.EnumConfig.LicenseType;
import carpool.configurations.EnumConfig.VerificationState;
import carpool.configurations.EnumConfig.VerificationType;

public abstract class IdentityVerification {
	
	private VerificationType type;
	
	private long verificationId;
	private int userId;
	private String realName;
	private String licenseNumber;
	private LicenseType licenseType;

	
	private Calendar submissionDate;
	private Calendar expireDate;
	private VerificationState state;
	
	private Calendar reviewDate;
	private int reviewerId;
	private int recommenderId;
	
	
	
	
	public IdentityVerification(VerificationType type, long verificationId,
			int userId, String realName, String licenseNumber,
			LicenseType licenseType, Calendar submissionDate,
			Calendar expireDate, VerificationState state,
			Calendar reviewDateDate, int reviewerId, int recommenderId) {
		super();
		this.type = type;
		this.verificationId = verificationId;
		this.userId = userId;
		this.realName = realName;
		this.licenseNumber = licenseNumber;
		this.licenseType = licenseType;
		this.submissionDate = submissionDate;
		this.expireDate = expireDate;
		this.state = state;
		this.reviewDate = reviewDateDate;
		this.reviewerId = reviewerId;
		this.recommenderId = recommenderId;
	}
	
	public VerificationType getType(){
		return this.type;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
	public LicenseType getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(LicenseType licenseType) {
		this.licenseType = licenseType;
	}
	public Calendar getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Calendar submissionDate) {
		this.submissionDate = submissionDate;
	}
	public Calendar getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Calendar expireDate) {
		this.expireDate = expireDate;
	}
	public VerificationState getState() {
		return state;
	}
	public void setState(VerificationState state) {
		this.state = state;
	}
	public Calendar getReviewDateDate() {
		return reviewDate;
	}
	public void setReviewDateDate(Calendar reviewDateDate) {
		this.reviewDate = reviewDateDate;
	}
	public int getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(int reviewerId) {
		this.reviewerId = reviewerId;
	}
	public int getRecommenderId() {
		return recommenderId;
	}
	public void setRecommenderId(int recommenderId) {
		this.recommenderId = recommenderId;
	}
	public long getVerificationId() {
		return verificationId;
	}	
	public void setVerificationId(long verificationId) {
		this.verificationId = verificationId;
	}
	

}
