package carpool.asyncRelayExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import carpool.interfaces.PesudoRelayTask;
import carpool.relayTask.EmailRelayTask;
import carpool.relayTask.NotificationRelayTask;


public class ExecutorProvider {
	private static final int threadPool_max_notifiaction = 10;
	private static final int threadPool_max_email = 2;
	
	private static final ExecutorService notificationExecutor = Executors.newFixedThreadPool(threadPool_max_notifiaction);
	private static final ExecutorService emailExecutor = Executors.newFixedThreadPool(threadPool_max_email);
	
	public static void executeRelay (PesudoRelayTask task){
		RelayTaskExecutableWrapper executableTask = new RelayTaskExecutableWrapper(task);
		if (task instanceof NotificationRelayTask){
			notificationExecutor.submit(executableTask);
		}
		else if (task instanceof EmailRelayTask){
			emailExecutor.submit(executableTask);
		}
	}

}
