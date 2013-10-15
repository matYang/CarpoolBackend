package carpool.asyncRelayExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import carpool.asyncTask.StoreSearchHistoryTask;
import carpool.asyncTask.relayTask.EmailRelayTask;
import carpool.asyncTask.relayTask.NotificationRelayTask;
import carpool.interfaces.PseudoAsyncTask;


public class ExecutorProvider {
	private static final int threadPool_max_notifiaction = 10;
	private static final int threadPool_max_email = 2;
	private static final int threadPool_max_sr = 2;
	private static final ExecutorService notificationExecutor = Executors.newFixedThreadPool(threadPool_max_notifiaction);
	private static final ExecutorService emailExecutor = Executors.newFixedThreadPool(threadPool_max_email);
	private static final ExecutorService srExecutor = Executors.newFixedThreadPool(threadPool_max_sr);
	
	public static void executeRelay (PseudoAsyncTask task){
		RelayTaskExecutableWrapper executableTask = new RelayTaskExecutableWrapper(task);
		if (task instanceof NotificationRelayTask){
			notificationExecutor.submit(executableTask);
		}
		else if (task instanceof EmailRelayTask){
			emailExecutor.submit(executableTask);
		}
		else if(task instanceof StoreSearchHistoryTask){
			srExecutor.submit(executableTask);
		}
	}

}
