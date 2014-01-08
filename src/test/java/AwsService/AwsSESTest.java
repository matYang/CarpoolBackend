package AwsService;
import static org.junit.Assert.fail;
import org.junit.Test;
import carpool.aws.awsSES;
import carpool.carpoolDAO.CarpoolDaoBasic;


public class AwsSESTest {
	
	@Test
	public void testSend(){
		CarpoolDaoBasic.clearBothDatabase();
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "lifecentric.o2o@gmail.com";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test1";
			awsSES.sendEmail(From,To,Body,Subject,"plain");			
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
			String To = "xiongchuhan@hotmail.com";
			String Body = "<h1>Click it to search using Google <p><div><a href=\"www.google.ca\">google</a></div></p></h1>";
			String Subject = "Test2";
			awsSES.sendEmail(From,To,Body,Subject,"html");
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
			awsSES.sendEmail(From,To,Body,Subject,"plain");
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		try{
			String From = "lifecentric.o2o@gmail.com";
			String To = "fyseason@163.com";
			String Body = "This email was sent through the Amazon SES SMTP interface by using Java.";
			String Subject = "Test2";
			awsSES.sendEmail(From,To,Body,Subject,"plain");
			//Passed;			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}		
		
	}

}
