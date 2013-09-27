package carpool.model.representation;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONObject;

import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.messageType;
import carpool.interfaces.PseudoRepresentation;

public class SearchRepresentation implements PseudoRepresentation{
	
	private boolean isRoundTrip;
	private LocationRepresentation departureLocation;
	private LocationRepresentation arrivalLocation;
	private Calendar departureDate;
	private Calendar arrivalDate;
	private messageType targetType;
	private DayTimeSlot departureTimeSlot;
	private DayTimeSlot arrivalTimeSlot;
	
	
	private SearchRepresentation(){}
	
	public SearchRepresentation(String serializedSearchString){
		
	}
	
	public SearchRepresentation(boolean isRoundTrip, ){
		
	}
	
	
	@Override
	public String toSerializedString(){
		
	}
	
	@Override
	public JSONObject toJSON(){
		
	}

}
