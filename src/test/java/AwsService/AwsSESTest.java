package AwsService;
import static org.junit.Assert.fail;
import org.junit.Test;
import carpool.aws.awsSES;
import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.constants.CarpoolConfig;

public class AwsSESTest {
	
	@Test
	public void testSend(){
		CarpoolDaoBasic.clearBothDatabase();
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "lifecentric.o2o@gmail.com";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test1";
			awsSES.send(From,To,Body,Subject);
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testSpecialCases(){
		CarpoolDaoBasic.clearBothDatabase();
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test2";
			awsSES.send(From,To,Body,Subject);
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "x";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test2";
			awsSES.send(From,To,Body,Subject);
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "xiongchuhanplace@hotmail.com";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test2";
			awsSES.send(From,To,Body,Subject);
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		
		
	}

}
