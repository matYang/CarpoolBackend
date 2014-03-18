package carpool.resources.adminResource;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import carpool.configurations.EnumConfig;
import carpool.configurations.EnumConfig.AdminRoutineAction;
import carpool.dbservice.admin.AdminService;
import carpool.resources.PseudoResource;

public class AdminRoutineResource extends PseudoResource{
	
    @Get
    public Representation updateTransaction(Representation entity) {
    	String access_admin = "";
    	int actionIndex = -1;
        
		try {
			
			access_admin = this.getQueryVal("access_admin");
			actionIndex = Integer.parseInt(this.getQueryVal("actionIndex"));
			
			if (!access_admin.equals(EnumConfig.access_admin)){
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				this.addCORSHeader();
		        return this.buildQuickResponse("invalid authorization value");
				
			}
			
			AdminRoutineAction action = AdminRoutineAction.fromInt(actionIndex);
			
			switch(action){
				case clearBothDatabase:
					AdminService.clearBothDatabase();
					break;
				case messageClean:
					AdminService.forceMessageClean();
					break;
				case transactionMonitor:
					AdminService.forceTransactionMonitoring();
					break;
				case cleanAndMonitor:
					AdminService.forceMessageClean();
					AdminService.forceTransactionMonitoring();
					break;
				case reloadLocation:
					AdminService.forceReloadLocation();
					break;
			}
			
		} catch(Exception e){
			return this.doException(e);
		}
        
        Representation result =  new JsonRepresentation(new JSONObject());
        this.addCORSHeader();
        return result;
    }
    
}
