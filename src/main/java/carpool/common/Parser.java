package carpool.common;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

public class Parser {
	
	//Pricelist: Numb-numb-
	public static String listToString(ArrayList<?> list){
		String serializedList = null;
		for(int i=0; i <list.size(); i++){
			if (serializedList == null){
				serializedList = "";
			}
			serializedList += list.get(i).toString() +"-";
		}
		return serializedList;
	}
	
	public static ArrayList<?> stringToList(String listString, Object optionFlag){
		String[] strArray = listString != null ? listString.split("-") : null;
		if (optionFlag instanceof Integer){
			ArrayList<Integer> intList = new ArrayList<Integer>();

			for (int i = 0; strArray != null && i < strArray.length; i++){
				intList.add(new Integer(strArray[i]));
			}
			return intList;
		}
		else if (optionFlag instanceof String){
			ArrayList<String> strList = new ArrayList<String>();
			for (int i = 0; strArray != null && i < strArray.length; i++){
				strList.add(strArray[i]);
			}
			return strList;
		}
		
		return null;
	}
	
	public static ArrayList<Integer> parseIntegerList(JSONArray jsonList){
		ArrayList<Integer> list = new ArrayList<Integer>();   
		try {
			if (jsonList != null) { 
				for (int i = 0; i < jsonList.length(); i++){ 
					list.add(new Integer(jsonList.getInt(i)));
				}  
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
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
		if(!Parser.stringToList("1-2-3-4-5-6-",new Integer(0)).equals(arrayList)){fail();}
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
		if(!Parser.stringToList("1-22-333-4444-55555-666666-",new Integer(0)).equals(arrayList)){fail();}
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
		if(!Parser.stringToList("1-22-333-4444-555-66-",new Integer(0)).equals(arrayList)){fail();}
	}
	@Test
	public void TestPriceListToStringOne(){
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		arrayList.add(2);
		if(!Parser.listToString(arrayList).equals("2-")){fail();}
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
		if(!Parser.listToString(arrayList).equals("1-22-333-4444-555-66-")){fail();}
	}
}

