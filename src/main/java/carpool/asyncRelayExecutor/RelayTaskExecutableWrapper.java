package carpool.asyncRelayExecutor;

import carpool.interfaces.PesudoRelayTask;

public class RelayTaskExecutableWrapper implements Runnable{
	
	private PesudoRelayTask task;
	
	public RelayTaskExecutableWrapper(PesudoRelayTask task){
		this.task = task;
	}
		 
	public void run(){
		this.task.execute();
	}

}
