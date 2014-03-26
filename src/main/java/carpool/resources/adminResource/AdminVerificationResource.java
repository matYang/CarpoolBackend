package carpool.resources.adminResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.AdminRoutineAction;
import carpool.configurations.EnumConfig.VerificationType;
import carpool.dbservice.UserDaoService;
import carpool.dbservice.admin.AdminService;
import carpool.exception.PseudoException;
import carpool.factory.JSONFactory;
import carpool.model.Location;
import carpool.model.User;
import carpool.model.identityVerification.DriverVerification;
import carpool.model.identityVerification.PassengerVerification;
import carpool.resources.PseudoResource;

public class AdminVerificationResource extends PseudoResource {
	
	@Get
    public Representation updateTransaction(Representation entity) {
    	String access_admin = "";
    	int typeIndex = 0;
        JSONArray verificationArray = new JSONArray();
        
		try {
			access_admin = this.getQueryVal("access_admin");
			typeIndex = Integer.parseInt(this.getQueryVal("typeIndex"));
			
			if (!access_admin.equals(EnumConfig.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("Invalid authorization value");
				
			}
			
			VerificationType verificationType = VerificationType.fromInt(typeIndex);
			if (verificationType == VerificationType.driver){
				ArrayList<DriverVerification> verifications = AdminService.getPendingDriverVerification();
				verificationArray = JSONFactory.toJSON(verifications);
			}
			else if (verificationType == VerificationType.passenger){
				ArrayList<PassengerVerification> verifications = AdminService.getPendingPassengerVerification();
				verificationArray = JSONFactory.toJSON(verifications);
			}
			
		} catch(PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
		} catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(verificationArray);
        this.addCORSHeader();
        return result;
    }
	
	
	@Post
	public Representation changeLocation(Representation entity) {
		JSONObject response = new JSONObject();
		JSONObject request = new JSONObject();
		String access_admin = "";
		int typeIndex = -1;
		int verificationId = -1;
		boolean isVerified = false;

		try {
			this.checkEntity(entity);
			request = (new JsonRepresentation(entity)).getJsonObject();
			access_admin = request.getString("access_admin");
			typeIndex = request.getInt("typeIndex");
			verificationId = request.getInt("verificationId");
			isVerified = request.getBoolean("isVerified");
			
			if (!access_admin.equals(EnumConfig.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("Invalid authorization value");
			}
			
			VerificationType verificationType = VerificationType.fromInt(typeIndex);
			if (verificationType == VerificationType.driver){
				AdminService.decideDriverVerification(verificationId, isVerified);
			}
			else if (verificationType == VerificationType.passenger){
				AdminService.decidePassengerVerification(verificationId, isVerified);
			}

		} catch (PseudoException e){
			this.addCORSHeader();
			return this.doPseudoException(e);
        } catch (Exception e) {
			return this.doException(e);
		}
		
		setStatus(Status.SUCCESS_OK);
		Representation result = new StringRepresentation("SUCCESS", MediaType.TEXT_PLAIN);
		this.addCORSHeader();
		return result;
	}

}
