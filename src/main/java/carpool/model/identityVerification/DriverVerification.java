package carpool.model.identityVerification;

import java.util.Calendar;

import org.json.JSONObject;

import carpool.configurations.EnumConfig.LicenseType;
import carpool.configurations.EnumConfig.VerificationState;
import carpool.configurations.EnumConfig.VerificationType;
import carpool.interfaces.PseudoModel;

public class DriverVerification extends IdentityVerification implements PseudoModel{

	private Calendar licenseIssueDate;
	private String licenseImgLink;
	
	public DriverVerification(int userId, String realName, String licenseNumber, LicenseType licenseType, Calendar licenseIssueDate, String licenseImgLink) {
		super(VerificationType.driver,  -1, userId, realName, licenseNumber, licenseType, Calendar.getInstance(), Calendar.getInstance(), VerificationState.pending, Calendar.getInstance(), -1, -1);
		this.licenseIssueDate = licenseIssueDate;
		this.licenseImgLink = licenseImgLink;
	}
	


	public DriverVerification(VerificationType type, long verificationId, int userId, String realName, String licenseNumber,
			LicenseType licenseType, Calendar submissionDate, Calendar expireDate, VerificationState state,
			Calendar reviewDateDate, int reviewerId, int recommenderId, Calendar licenseIssueDate, String licenseImgLink) {
		super(type, verificationId, userId, realName, licenseNumber, licenseType, submissionDate, expireDate, state, reviewDateDate, reviewerId, recommenderId);
		this.licenseIssueDate = licenseIssueDate;
		this.licenseImgLink = licenseImgLink;
	}

	public Calendar getLicenseIssueDate() {
		return licenseIssueDate;
	}


	public void setLicenseIssueDate(Calendar licenseIssueDate) {
		this.licenseIssueDate = licenseIssueDate;
	}
	

	public String getLicenseImgLink() {
		return licenseImgLink;
	}


	public void setLicenseImgLink(String licenseImgLink) {
		this.licenseImgLink = licenseImgLink;
	}





	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
