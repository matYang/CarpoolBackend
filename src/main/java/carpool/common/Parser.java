package carpool.common;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

public class Parser {
	//Pricelist: Numb-numb-
	public static String priceListToString(ArrayList<Integer> priceList){
		String Pricelist="";
		for(int i=0; i<priceList.size();i++){
			Pricelist += priceList.get(i) +"-";
		}
		return Pricelist;
	}
	
	public static ArrayList<Integer> stringToPriceList(String priceListString){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		String p="";
		int endind;
		int ptr;
		while(priceListString.length()>=2){
			ptr=0;
			endind=1;
			p=priceListString.substring(ptr,ptr+1);
			while(!p.equals("-")){
				endind++;
				ptr++;
				p=priceListString.substring(ptr,ptr+1);
				
			}		
			arrayList.add(Integer.parseInt(priceListString.substring(0,endind-1)));
			priceListString = priceListString.substring(endind,priceListString.length());
			
		}
		
		
		return arrayList;
	}
	
	@Test
	public void TestStringToPriceListOne(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(1);
		arrayList.add(2);
		arrayList.add(3);
		arrayList.add(4);
		arrayList.add(5);
		arrayList.add(6);
		if(!Parser.stringToPriceList("1-2-3-4-5-6-").equals(arrayList)){fail();}
	}
	@Test
	public void TestStringToPriceListTwo(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(1);
		arrayList.add(22);
		arrayList.add(333);
		arrayList.add(4444);
		arrayList.add(55555);
		arrayList.add(666666);
		if(!Parser.stringToPriceList("1-22-333-4444-55555-666666-").equals(arrayList)){fail();}
	}
	@Test
	public void TestStringToPriceListThree(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(1);
		arrayList.add(22);
		arrayList.add(333);
		arrayList.add(4444);
		arrayList.add(555);
		arrayList.add(66);
		if(!Parser.stringToPriceList("1-22-333-4444-555-66-").equals(arrayList)){fail();}
	}
	@Test
	public void TestPriceListToStringOne(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(2);
		if(!Parser.priceListToString(arrayList).equals("2-")){fail();}
	}
   @Test
   public void TestPriceListToStringTwo(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(1);
		arrayList.add(22);
		arrayList.add(333);
		arrayList.add(4444);
		arrayList.add(555);
		arrayList.add(66);
		if(!Parser.priceListToString(arrayList).equals("1-22-333-4444-555-66-")){fail();}
	}
}

