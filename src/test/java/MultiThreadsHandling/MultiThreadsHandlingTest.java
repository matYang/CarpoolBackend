package MultiThreadsHandling;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import carpool.asyncRelayExecutor.RelayTaskExecutableWrapper;
import carpool.asyncTask.MyTestTask;
import carpool.common.DateUtility;
import carpool.common.DebugLog;



public class MultiThreadsHandlingTest {
	private int threads = 1000;
	private ExecutorService testExecutor = Executors.newFixedThreadPool(threads);
	
	@Test
	public void Test(){
		long time = DateUtility.getCurTime();
		MyTestTask task = new MyTestTask(time);
		
		RelayTaskExecutableWrapper executableTask = new RelayTaskExecutableWrapper(task);
		int numOfThreads = threads;
		Future<?> FutureObject=null;
		boolean completed = true;
		
		while(numOfThreads > 0){			
			try {
				if(completed){
					FutureObject = testExecutor.submit(executableTask);
				}
				if(FutureObject.get()==null){
					//Success
					completed = true;
					System.out.println("Successfully complete the Thread: "+(threads-numOfThreads+1));
					numOfThreads--;					
				}else{
					//Wait
					completed = false;
					System.out.println("SSSSSSSSSSS wait the Thread: "+(threads-numOfThreads+1));
					FutureObject.wait();
				}
			} catch (InterruptedException | ExecutionException e) {		
				//Fail
				e.printStackTrace();
				DebugLog.d(e);				
				fail();
				break;
			}					
		}		
		
	} 
	
		
}
