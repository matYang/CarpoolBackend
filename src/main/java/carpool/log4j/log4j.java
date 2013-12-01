package carpool.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class log4j {
		
	public static void configure(){
		PropertyConfigurator.configure("src/log4j.properties");
	}
	
}
