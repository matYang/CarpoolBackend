package carpool.asyncTask;
import carpool.interfaces.PseudoAsyncTask;


public class MyTestTask implements PseudoAsyncTask{
	private long waitTime;
	
	public MyTestTask(long t){
		this.waitTime = t;
	}
	
	@Override
	public boolean execute() {
		return MyRunTask();
	}
	
	public boolean MyRunTask(){
		System.out.println("Thread is running, and the wait time is "+waitTime);
		return true;
	}
}
