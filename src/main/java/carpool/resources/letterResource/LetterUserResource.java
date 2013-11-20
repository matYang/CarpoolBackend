package carpool.resources.letterResource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import carpool.common.DebugLog;
import carpool.constants.Constants;
import carpool.dbservice.LetterDaoService;
import carpool.exception.PseudoException;
import carpool.exception.message.MessageNotFoundException;
import carpool.exception.transaction.TransactionNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.factory.JSONFactory;
import carpool.model.Letter;
import carpool.model.User;
import carpool.resources.PseudoResource;

public class LetterUserResource extends PseudoResource{

        
    @Get 
    public Representation getLetterUsers() {
        int userId = -1;
        JSONArray jsonUsers = new JSONArray();
        
        try {
                userId = Integer.parseInt(this.getReqAttr("id"));
                this.validateAuthentication(userId);
                        
                ArrayList<User> users = LetterDaoService.getLetterUsers(userId);
                
                if (users != null){
                    jsonUsers  = JSONFactory.toJSON(users);
                    setStatus(Status.SUCCESS_OK);
                }
                else{
                        setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                }
                        
                } catch (PseudoException e){
                        this.addCORSHeader();
                        return new StringRepresentation(this.doPseudoException(e));
        } catch (Exception e){
                        this.doException(e);
                }
        
        Representation result = new JsonRepresentation(jsonUsers);
        this.addCORSHeader();
        return result;
    }
}
