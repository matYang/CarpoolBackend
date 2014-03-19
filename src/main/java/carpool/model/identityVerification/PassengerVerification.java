package carpool.model.identityVerification;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.configurations.EnumConfig.LicenseType;
import carpool.configurations.EnumConfig.PassengerVerificationOrigin;
import carpool.configurations.EnumConfig.VerificationState;
import carpool.configurations.EnumConfig.VerificationType;
import carpool.interfaces.PseudoModel;

public class PassengerVerification extends IdentityVerification implements PseudoModel{
	
	private String frontImgLink;
	private String backImgLink;
	private PassengerVerificationOrigin origin;
	
	
	public PassengerVerification(int userId, String realName, String licenseNumber, String frontImgLink, String backImgLink, PassengerVerificationOrigin origin) {
		super(VerificationType.passenger, -1, userId, realName, licenseNumber, LicenseType.idCard, Calendar.getInstance(), Calendar.getInstance(), VerificationState.pending, Calendar.getInstance(), -1, -1);
		this.frontImgLink = frontImgLink;
		this.backImgLink = backImgLink;
		this.origin = origin;
	}
	
	
	
	public PassengerVerification(VerificationType type, int verificationId, int userId, String realName, String licenseNumber,
			LicenseType licenseType, Calendar submissionDate, Calendar expireDate, VerificationState state, Calendar reviewDate, 
			int reviewerId, int recommenderId, String frontImgLink, String backImgLink , PassengerVerificationOrigin origin) {
		super(type, verificationId, userId, realName, licenseNumber, licenseType, submissionDate, expireDate, state, reviewDate, reviewerId, recommenderId);
		this.frontImgLink = frontImgLink;
		this.backImgLink = backImgLink ;
		this.origin = origin;
	}

	
	public String getFrontImgLink() {
		return frontImgLink;
	}

	public void setFrontImgLink(String frontImgLink) {
		this.frontImgLink = frontImgLink;
	}


	public String getBackImgLink() {
		return backImgLink;
	}

	public void setBackImgLink(String backImgLink) {
		this.backImgLink = backImgLink;
	}


	public PassengerVerificationOrigin getOrigin() {
		return origin;
	}


	public void setOrigin(PassengerVerificationOrigin origin) {
		this.origin = origin;
	}


	@Override
	public JSONObject toJSON() {
		JSONObject jsonVerification = super.toJSON();
		try {
			jsonVerification .put("frontImgLink", this.getFrontImgLink());
			jsonVerification .put("backImgLink", this.getBackImgLink());
			jsonVerification .put("origin", this.getOrigin().code);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonVerification;

	}
	
	public boolean equals(PassengerVerification v){
		return super.equals(v) && this.frontImgLink.equals(v.frontImgLink) && this.backImgLink.equals(v.backImgLink) && this.origin == v.origin;
	}

}
