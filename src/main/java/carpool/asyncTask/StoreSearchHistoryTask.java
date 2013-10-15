package carpool.asyncTask;
import java.util.concurrent.locks.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import carpool.constants.CarpoolConfig;
import carpool.interfaces.PseudoAsyncTask;
import carpool.model.representation.SearchRepresentation;
import flexjson.JSONSerializer;
public class StoreSearchHistoryTask implements PseudoAsyncTask {
	
	private int userId;
	private SearchRepresentation sr;
	private Lock lock;
	public StoreSearchHistoryTask(SearchRepresentation sr, int userId){
		this.userId = userId;
		this.sr = sr;
	}
	
	
	@Override
	public boolean execute() {
		return storeSearchHistoryTask();
	}
	
	
	public boolean storeSearchHistoryTask(){
		this.lock = new Lock();
		BufferedWriter bw = null;
        if(userId !=0 && sr !=null){
        try {
			this.lock.lock();
		} catch (InterruptedException ie) {			
			ie.printStackTrace();
		}
		try {
		    String srString = sr.toSerializedString();
		    String fileName = CarpoolConfig.pathToSearchHistoryFolder + userId + CarpoolConfig.searchHistoryFileSufix;
		    bw = new BufferedWriter(new FileWriter(fileName, true));
		    bw.write(srString);
		    bw.newLine();
		    bw.flush();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		} finally { // always close the file as well as unlock
		    if (bw != null) {
		        try {
		            bw.close();
		            lock.unlock();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		  }	
        }
				
		return true;
	}

}
