package carpool.asyncTask;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import carpool.common.DebugLog;
import carpool.configurations.ServerConfig;
import carpool.dbservice.FileService;
import carpool.interfaces.PseudoAsyncTask;
import carpool.model.representation.SearchRepresentation;


public class StoreSearchHistoryTask implements PseudoAsyncTask {
	
	private int userId;
	private SearchRepresentation sr;
	
	public StoreSearchHistoryTask(SearchRepresentation sr, int userId){
		this.userId = userId;
		this.sr = sr;
	}
	
	
	@Override
	public boolean execute() {
		return storeSearchHistoryTask();
	}
	
	
	public boolean storeSearchHistoryTask(){
		try {
			FileService.storeSearchRepresentation(sr, userId);
		} catch (IOException e) {
			e.printStackTrace();
			DebugLog.d(e);
		}	
		return true;
	}

}
