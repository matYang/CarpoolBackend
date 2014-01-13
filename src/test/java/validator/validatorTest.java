package validator;
import static org.junit.Assert.*;

import org.junit.Test;

import carpool.common.Validator;


public class validatorTest {

	@Test
	public void testPhoneFormat(){
		String myphone = "9892263974";
		if(Validator.isPhoneFormatValid(myphone)){
			//Passed;
		}else{
			fail();
		}
		
		myphone = "sdf";
		if(!Validator.isPhoneFormatValid(myphone)){
			//Passed;
		}else{
			fail();
		}
		
		myphone = "1sdf";
		if(!Validator.isPhoneFormatValid(myphone)){
			//Passed;
		}else{
			fail();
		}
		
		myphone = "1@$sdf";
		if(!Validator.isPhoneFormatValid(myphone)){
			//Passed;
		}else{
			fail();
		}
	}
	
	@Test
	public void testEmailFormat(){
		String myemail = "xiongchuhan@hotmail.com";
		if(Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "lifecentric.o2o@gmail.com";
		if(Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "lifecentri-c.o2o@gmail.com";
		if(Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "sdf";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "lifecentric.o2o@.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "@lifecentric.o2o@gmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "lifecentric.o2o@g.com";
		if(Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "lifecentric.o2ogmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "asdfsdfsdrewrfdgdfgergtertrewtretertretertertretertreter@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1!@#$%#$%@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1!#$%^&*()-+=qw2@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1sdf..@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1sdf.3@hotmail.com";
		if(Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1sdf.erw-3-3-1@hotmail.com";
		if(Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1sdf.erw-3-3-1&@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1sdf.erw-3-3-1&(@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = "-1sdf.erw-3-3-1&#2@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
		myemail = ".sdf.erw-3-3-1@hotmail.com";
		if(!Validator.isEmailFormatValid(myemail)){
			//Passed;
		}else{
			fail();
		}
		
	}
	
	@Test
	public void testQQFormat(){
		String qq="1234";
		if(!Validator.isQqFormatValid(qq)){
			//Passed;
		}else{
			fail();
		}
		
		qq="12345";
		if(Validator.isQqFormatValid(qq)){
			//Passed;
		}else{
			fail();
		}
		
		qq="1234512345";
		if(Validator.isQqFormatValid(qq)){
			//Passed;
		}else{
			fail();
		}
		
		qq="12345123451";
		if(!Validator.isQqFormatValid(qq)){
			//Passed;
		}else{
			fail();
		}
		
		qq="12345#$45";
		if(!Validator.isQqFormatValid(qq)){
			//Passed;
		}else{
			fail();
		}
	}
	
	@Test
	public void testNameFormat(){
		String name = "熊处寒";
		if(Validator.isNameFormatValid(name)){
			//Passed;
		}else{
			fail();
		}
		
		name="12345#$45";
		if(!Validator.isNameFormatValid(name)){
			//Passed;
		}else{
			fail();
		}
		
		name="";
		if(!Validator.isNameFormatValid(name)){
			//Passed;
		}else{
			fail();
		}
		
		name="1";
		if(!Validator.isNameFormatValid(name)){
			//Passed;
		}else{
			fail();
		}
		
		name="いっぽん";
		if(!Validator.isNameFormatValid(name)){
			//Passed;
		}else{
			fail();
		}
		
		name="马修羊isYangChunkai";
		if(Validator.isNameFormatValid(name)){
			//Passed;
		}else{
			fail();
		}
		
		name="yck马xch熊";
		if(Validator.isNameFormatValid(name)){
			//Passed;
		}else{
			fail();
		}
	}
	
	@Test
	public void testPasswordFormat(){
		String pw="12345";
		if(!Validator.isPasswordFormatValid(pw)){
			//Passed;
		}else{
			fail();
		}
		
		pw="1234512345123451234512345123451";
		if(!Validator.isPasswordFormatValid(pw)){
			//Passed;
		}else{
			fail();
		}
		
		pw="qAD#$1234";
		if(Validator.isPasswordFormatValid(pw)){
			//Passed;
		}else{
			fail();
		}
		pw="ycg1990$";
		if(Validator.isPasswordFormatValid(pw)){
			//Passed;
		}else{
			fail();
		}
		
		pw="qAD#$123我4";
		if(!Validator.isPasswordFormatValid(pw)){
			//Passed;
		}else{
			fail();
		}
		
		pw="qAD#$123い4";
		if(!Validator.isPasswordFormatValid(pw)){
			//Passed;
		}else{
			fail();
		}
	}	
	
}
