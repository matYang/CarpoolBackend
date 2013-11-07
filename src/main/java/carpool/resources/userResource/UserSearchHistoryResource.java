package carpool.resources.userResource;

import java.util.ArrayList;
import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import carpool.aws.awsMain;
import carpool.factory.JSONFactory;
import carpool.model.representation.SearchRepresentation;
import carpool.resources.PseudoResource;

public class UserSearchHistoryResource extends PseudoResource{

	@Get
	public Representation getUserSearchHistory(){
		int userId = -1;
		ArrayList<SearchRepresentation> searchHistory = new ArrayList<SearchRepresentation>();
		JSONArray resultArr = new JSONArray();

		try{
			userId = Integer.parseInt(this.getAttribute("id"));			
			searchHistory = awsMain.getUserSearchHistory(userId);
			resultArr = JSONFactory.toJSON(searchHistory);

		}  catch (Exception e) {
			this.doException(e);
		}		

		Representation response = new JsonRepresentation(resultArr);
		addCORSHeader();
		return response;
	}
}
