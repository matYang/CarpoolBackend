package carpool.common;

import java.util.ArrayList;

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
		ArrayList<Integer> arrayList = null;
		String sub="";
		int e;
		while(priceListString.length()>=2){
			sub=priceListString.substring(0,1);
		    e=Integer.parseInt(sub);
		    arrayList.add(e);
		    
		 if(priceListString.length()>2){
		    priceListString = priceListString.substring(2,priceListString.length());
		}else{
			break;
			}
		}
		
		return arrayList;
	}

}
