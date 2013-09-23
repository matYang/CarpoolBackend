package carpool.clean;


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import carpool.common.Common;
import carpool.dbservice.*;
import carpool.model.*;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class Clean{
	//private static DaoService daoService = new DaoService();
	public static final String timeZoneId = "asia/shanghai";
	public static final String fileName = "messageHistory.txt";

	public static Calendar dateToCalendar(Date date){ 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static void writeMessageToFile(Message message){
		BufferedWriter bw = null;

		try {
		    String jsonMessage = new JSONSerializer().serialize(message);
		    
		    bw = new BufferedWriter(new FileWriter(fileName, true));
		    bw.write(jsonMessage);
		    bw.newLine();
		    bw.flush();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		} finally { // always close the file
		    if (bw != null) {
		        try {
		            bw.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}

	
	public void cleanSchedules(){
		/*
		Calendar currentCal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		Set<String> keys = DaoService.getIds();

		//go through each Message and clears outdated data, write them to a txt file
		for (String key : keys){
			Message message = DaoService.getMessageById(key);
			Calendar messageEndCal = dateToCalendar(message.getEndDate());
			//messageEndCal.add(Calendar.DAY_OF_YEAR, 1);    		//add an offset of 1 day since date from front end always indicates 00:00AM on the last day
			if (messageEndCal.before(currentCal) || messageEndCal.equals(currentCal)){
				DaoService.deleteMessage(message.getId());
				writeMessageToFile(message);
			}
			if (!message.isMessageVaild()){
				DaoService.deleteMessage(message.getId());
				writeMessageToFile(message);
			}
			
		}
		Message message = new Message();
		writeMessageToFile(message);
		Common.d("clean thread triggered with message: " + message.toString());
		*/
	}
	




}


