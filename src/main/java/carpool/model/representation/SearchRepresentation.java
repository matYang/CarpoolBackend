package carpool.model.representation;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.messageType;
import carpool.interfaces.PseudoRepresentation;
import carpool.model.representation.temp.LocationRepresentation;

public class SearchRepresentation implements PseudoRepresentation{
	
	private boolean isRoundTrip;
	private LocationRepresentation departureLocation;
	private LocationRepresentation arrivalLocation;
	private Calendar departureDate;
	private Calendar arrivalDate;
	private messageType targetType;
	private DayTimeSlot departureTimeSlot;
	private DayTimeSlot arrivalTimeSlot;
	
	
	@SuppressWarnings("unused")
	private SearchRepresentation(){}
	
	//used for sql
	public SearchRepresentation(boolean isRoundTrip,
			LocationRepresentation departureLocation,
			LocationRepresentation arrivalLocation, Calendar departureDate,
			Calendar arrivalDate, messageType targetType,
			DayTimeSlot departureTimeSlot, DayTimeSlot arrivalTimeSlot) {
		super();
		this.isRoundTrip = isRoundTrip;
		this.departureLocation = departureLocation;
		this.arrivalLocation = arrivalLocation;
		this.departureDate = departureDate;
		this.arrivalDate = arrivalDate;
		this.targetType = targetType;
		this.departureTimeSlot = departureTimeSlot;
		this.arrivalTimeSlot = arrivalTimeSlot;
	}

	//separated by "-"
	public SearchRepresentation(String serializedSearchString){
		String[] representationArray = serializedSearchString.split("-");
		this.isRoundTrip = Boolean.parseBoolean(representationArray[0]);
		this.departureLocation = new LocationRepresentation(representationArray[1]);
		this.arrivalLocation = new LocationRepresentation(representationArray[2]);
		this.departureDate = DateUtility.castFromAPIFormat(representationArray[3]);
		this.arrivalDate = DateUtility.castFromAPIFormat(representationArray[4]);
		this.targetType = Constants.messageType.values()[Integer.parseInt(representationArray[5])];
		this.departureTimeSlot = Constants.DayTimeSlot.values()[Integer.parseInt(representationArray[6])];
		this.arrivalTimeSlot = Constants.DayTimeSlot.values()[Integer.parseInt(representationArray[7])];
	}
	
	public SearchRepresentation(JSONObject jsonSearchRepresentation) throws JSONException{
		this.isRoundTrip = jsonSearchRepresentation.getBoolean("isRoundTrip");
		this.departureLocation = new LocationRepresentation(jsonSearchRepresentation.getJSONObject("departureLocation"));
		this.arrivalLocation = new LocationRepresentation(jsonSearchRepresentation.getJSONObject("arrivalLocation"));
		this.departureDate = DateUtility.castFromAPIFormat(jsonSearchRepresentation.getString("departureDate"));
		this.arrivalDate = DateUtility.castFromAPIFormat(jsonSearchRepresentation.getString("arrivalDate"));
		this.targetType = Constants.messageType.values()[jsonSearchRepresentation.getInt("targetType")];
		this.departureTimeSlot = Constants.DayTimeSlot.values()[jsonSearchRepresentation.getInt("departureTimeSlot")];
		this.arrivalTimeSlot = Constants.DayTimeSlot.values()[jsonSearchRepresentation.getInt("arrivalTimeSlot")];
	}
	
	
	
	
	public boolean isRoundTrip() {
		return isRoundTrip;
	}

	public void setRoundTrip(boolean isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}

	public LocationRepresentation getDepartureLocation() {
		return departureLocation;
	}

	public void setDepartureLocation(LocationRepresentation departureLocation) {
		this.departureLocation = departureLocation;
	}

	public LocationRepresentation getArrivalLocation() {
		return arrivalLocation;
	}

	public void setArrivalLocation(LocationRepresentation arrivalLocation) {
		this.arrivalLocation = arrivalLocation;
	}

	public Calendar getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Calendar departureDate) {
		this.departureDate = departureDate;
	}

	public Calendar getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Calendar arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public messageType getTargetType() {
		return targetType;
	}

	public void setTargetType(messageType targetType) {
		this.targetType = targetType;
	}

	public DayTimeSlot getDepartureTimeSlot() {
		return departureTimeSlot;
	}

	public void setDepartureTimeSlot(DayTimeSlot departureTimeSlot) {
		this.departureTimeSlot = departureTimeSlot;
	}

	public DayTimeSlot getArrivalTimeSlot() {
		return arrivalTimeSlot;
	}

	public void setArrivalTimeSlot(DayTimeSlot arrivalTimeSlot) {
		this.arrivalTimeSlot = arrivalTimeSlot;
	}
	

	@Override
	public String toSerializedString(){
		return this.isRoundTrip + "-" + this.departureLocation.toSerializedString()  + "-" + this.arrivalLocation.toSerializedString() + 
				DateUtility.castToAPIFormat(this.departureDate)  + "-" + DateUtility.castToAPIFormat(this.arrivalDate)  + "-" + this.targetType.code + this.departureTimeSlot.code + this.arrivalTimeSlot.code;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException{
		JSONObject jsonSearchRepresentation = new JSONObject();
		jsonSearchRepresentation.put("isRoundTrip", this.isRoundTrip);
		jsonSearchRepresentation.put("departureLocation", this.departureLocation.toJSON());
		jsonSearchRepresentation.put("arrivalLocation", this.arrivalLocation.toJSON());
		jsonSearchRepresentation.put("departureDate", DateUtility.castToAPIFormat(this.departureDate));
		jsonSearchRepresentation.put("arrivalDate", DateUtility.castToAPIFormat(this.arrivalDate));
		jsonSearchRepresentation.put("targetType", this.targetType.code);
		jsonSearchRepresentation.put("departureTimeSlot", this.departureTimeSlot.code);
		jsonSearchRepresentation.put("arrivalTimeSlot", this.arrivalTimeSlot.code);
		
		return jsonSearchRepresentation;
		
	}
	
	public boolean equals(SearchRepresentation s){
		return this.isRoundTrip == s.isRoundTrip() && this.departureLocation.equals(s.getDepartureLocation()) && this.arrivalLocation.equals(s.getArrivalLocation()) &&
				this.departureDate.equals(s.getDepartureDate()) && this.arrivalDate.equals(s.getArrivalDate()) && this.targetType == s.getTargetType() &&
				this.departureTimeSlot == s.getDepartureTimeSlot() && this.arrivalTimeSlot == s.getArrivalTimeSlot();
	}

}
