package carpool.resources.adminResource;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.constants.Constants;
import carpool.constants.Constants.AdminRoutineAction;
import carpool.dbservice.admin.AdminService;
import carpool.resources.PseudoResource;

public class AdminStateResource extends PseudoResource{
	
    @Get
    public Representation updateTransaction(Representation entity) {
    	String access_admin = "";
    	int actionIndex = -1;
        
		try {
			
			access_admin = this.getQueryVal("access_admin");
			actionIndex = Integer.parseInt(this.getQueryVal("actionIndex"));
			
			if (!access_admin.equals(Constants.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("invalid authorization value");
				
			}
			
			
			//TODO put the functional code here
			
			
		} catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(new JSONObject());
        this.addCORSHeader();
        return result;
    }
    
}
