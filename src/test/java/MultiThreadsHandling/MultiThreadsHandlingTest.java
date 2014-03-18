package MultiThreadsHandling;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import carpool.asyncRelayExecutor.RelayTaskExecutableWrapper;
import carpool.asyncTask.MyTestTask;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DebugLog;
import carpool.configurations.EnumConfig.DayTimeSlot;
import carpool.configurations.EnumConfig.Gender;
import carpool.configurations.EnumConfig.MessageType;
import carpool.configurations.EnumConfig.PaymentMethod;
import carpool.exception.location.LocationNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import carpool.model.representation.SearchRepresentation;



public class MultiThreadsHandlingTest {
	private int threads = 70;	
	private ExecutorService testExecutor = Executors.newFixedThreadPool(threads);

	private boolean isCompleted (List<Future<?>> list){
		for(int i=0; i<list.size(); i++){
			try {
				if(list.get(i).get() !=null){
					return false;
				}
			} catch (InterruptedException | ExecutionException e) {				
				e.printStackTrace();
				DebugLog.d(e);
				throw new RuntimeException("Thread 挂了");
			}
		}
		return true;
	}

	@Test
	public void Test() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();		
		//Date
		Calendar dt = Calendar.getInstance();		
		Calendar at = Calendar.getInstance();
		at.add(Calendar.DAY_OF_YEAR, 1);		
		Calendar dt2 = Calendar.getInstance();	
		dt2.add(Calendar.DAY_OF_YEAR, -1);	
		Calendar dt3 = Calendar.getInstance();	
		dt3.add(Calendar.DAY_OF_YEAR, -2);

		//Location
		long departure_Id = 1L;
		long arrival_Id = 2L;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);		
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, Gender.both);

		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		PaymentMethod paymentMethod =null;
		paymentMethod = paymentMethod.fromInt(0);
		MessageType type = MessageType.fromInt(0);		
		MessageType type2 = MessageType.fromInt(2);
		Gender genderRequirement = Gender.fromInt(0);		
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);		
		int userId=user.getUserId();
		//These messages should pass the search	
		//Message	
		Message message=new Message(userId,false, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message);
		//Message2
		Message message2=new Message(userId,true, new Location(departureLocation),dt,timeSlot,1 , priceList,new Location(arrivalLocation),at,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		//Message3
		Message message3=new Message(userId,true, new Location(arrivalLocation),dt2,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		//Message4
		Message message4=new Message(userId,false,new Location(arrivalLocation),dt2,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message4);
		//Other messages
		Message message5=new Message(userId,false, new Location(arrivalLocation),dt3,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message5);
		Message message6=new Message(userId,true, new Location(arrivalLocation),dt3,timeSlot,1 , priceList,new Location(departureLocation),dt,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message6);
		Message message7=new Message(userId,false, new Location(departureLocation),dt3,timeSlot,1 , priceList,new Location(arrivalLocation),dt2,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message7);
		Message message8=new Message(userId,true, new Location(departureLocation),dt3,timeSlot,1 , priceList,new Location(arrivalLocation),dt2,timeSlot, 1,priceList,paymentMethod,"test",  type, genderRequirement);
		CarpoolDaoMessage.addMessageToDatabase(message8);
		//Seats adjust
		Message message9=new Message(userId,false, new Location(departureLocation),dt,timeSlot,10 , priceList,new Location(arrivalLocation),at,timeSlot, 0,priceList,paymentMethod,"test",  type, genderRequirement);
		message9.setDeparture_seatsBooked(11);
		CarpoolDaoMessage.addMessageToDatabase(message9);
		//SRs
		SearchRepresentation SR = new SearchRepresentation(false,new Location(departureLocation).getMatch(),new Location(arrivalLocation).getMatch(),dt,at,type,timeSlot,timeSlot);		
		SearchRepresentation SR2 = new SearchRepresentation(true,new Location(arrivalLocation).getMatch(),new Location(departureLocation).getMatch(),dt2,dt,type2,timeSlot,timeSlot);

		MyTestTask task = null;		
		RelayTaskExecutableWrapper executableTask = null;		
		ArrayList<Future<?>> futurelist=new ArrayList<Future<?>>();
		ArrayList<Message> mlist1 = new ArrayList<Message>();
		mlist1.add(message);
		mlist1.add(message2);
		mlist1.add(message3);
		mlist1.add(message6);
		ArrayList<Message> mlist2 = new ArrayList<Message>();
		mlist2.add(message);
		mlist2.add(message2);
		mlist2.add(message3);
		mlist2.add(message4);
		mlist2.add(message6);
		mlist2.add(message8);
		int numOfThreads = threads;		
		int testThreads = threads;
		while(numOfThreads > 0){			
			if(numOfThreads%2==0){
				task = new MyTestTask(SR2,mlist2);
			}else{
				task = new MyTestTask(SR,mlist1);
			}
			//System.out.println("Add Thread: "+(threads - numOfThreads+1));
			executableTask = new RelayTaskExecutableWrapper(task);				
			futurelist.add(testExecutor.submit(executableTask));
			numOfThreads--;
		}

		boolean isCompleted = isCompleted(futurelist);

		while(!isCompleted){
			//Wait
			isCompleted = isCompleted(futurelist);
		}

		System.out.println("Completed!!!");
		
		//Test for run again
		numOfThreads = testThreads;			
		while(numOfThreads > 0){			
			if(numOfThreads%2==0){
				task = new MyTestTask(SR2,mlist2);
			}else{
				task = new MyTestTask(SR,mlist1);
			}
			//System.out.println("Add Thread: "+(threads - numOfThreads+1));
			executableTask = new RelayTaskExecutableWrapper(task);				
			futurelist.add(testExecutor.submit(executableTask));
			numOfThreads--;
		}

		isCompleted = isCompleted(futurelist);

		while(!isCompleted){
			//Wait
			isCompleted = isCompleted(futurelist);
		}
		System.out.println("Test Completed!!!");

	} 


}
