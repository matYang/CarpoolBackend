package carpool.model.representation;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.MessageType;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoRepresentation;
import carpool.model.Location;

public class SearchRepresentation implements PseudoRepresentation{
	
	private boolean isRoundTrip;
	private long departureMatch_Id;
	private long arrivalMatch_Id;
	private Calendar departureDate;
	private Calendar arrivalDate;
	private MessageType targetType;
	private DayTimeSlot departureTimeSlot;
	private DayTimeSlot arrivalTimeSlot;
	private Calendar timeStamp;
	
	@SuppressWarnings("unused")
	private SearchRepresentation(){}
	
	public SearchRepresentation(boolean isRoundTrip,
			long departureMatch_Id,long arrivalMatch_Id,
			Calendar departureDate,	Calendar arrivalDate, MessageType targetType,
			DayTimeSlot departureTimeSlot, DayTimeSlot arrivalTimeSlot) {
		super();
		this.isRoundTrip = isRoundTrip;
		this.departureMatch_Id = departureMatch_Id;
		this.arrivalMatch_Id = arrivalMatch_Id;
		this.departureDate = departureDate;
		this.arrivalDate = arrivalDate;
		this.targetType = targetType;
		this.departureTimeSlot = departureTimeSlot;
		this.arrivalTimeSlot = arrivalTimeSlot;
		this.timeStamp = DateUtility.convertToStandard(DateUtility.getCurTimeInstance());
	}

	//separated by "+"
	public SearchRepresentation(String serializedSearchString){
		
		String[] representationArray = serializedSearchString.split(CarpoolConfig.urlSeperatorRegx);		
		this.isRoundTrip = Boolean.parseBoolean(representationArray[0]);
		this.departureMatch_Id = Long.parseLong(representationArray[1],10);
		this.arrivalMatch_Id = Long.parseLong(representationArray[2],10);
		this.departureDate = DateUtility.castFromAPIFormat(representationArray[3]);
		this.arrivalDate = DateUtility.castFromAPIFormat(representationArray[4]);
		this.targetType = Constants.MessageType.values()[Integer.parseInt(representationArray[5])];
		this.departureTimeSlot = Constants.DayTimeSlot.values()[Integer.parseInt(representationArray[6])];
		this.arrivalTimeSlot = Constants.DayTimeSlot.values()[Integer.parseInt(representationArray[7])];
		this.timeStamp = DateUtility.castFromAPIFormat(representationArray[8]);
	}
	
	public SearchRepresentation(JSONObject jsonSearchRepresentation) throws JSONException{
		this.isRoundTrip = jsonSearchRepresentation.getBoolean("isRoundTrip");
		this.departureMatch_Id = jsonSearchRepresentation.getLong("departureMatch_Id");
		this.departureMatch_Id = jsonSearchRepresentation.getLong("arrivalMatch_Id");
		this.departureDate = DateUtility.castFromAPIFormat(jsonSearchRepresentation.getString("departureDate"));
		this.arrivalDate = DateUtility.castFromAPIFormat(jsonSearchRepresentation.getString("arrivalDate"));
		this.targetType = Constants.MessageType.values()[jsonSearchRepresentation.getInt("targetType")];
		this.departureTimeSlot = Constants.DayTimeSlot.values()[jsonSearchRepresentation.getInt("departureTimeSlot")];
		this.arrivalTimeSlot = Constants.DayTimeSlot.values()[jsonSearchRepresentation.getInt("arrivalTimeSlot")];
		this.timeStamp = DateUtility.castFromAPIFormat(jsonSearchRepresentation.getString("timeStamp"));
	}
	
	
	public Calendar getTimeStamp(){
		return timeStamp;
	}
	
	public boolean isRoundTrip() {
		return isRoundTrip;
	}

	public void setRoundTrip(boolean isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}

	public long getDepartureMatch_Id() {
		return departureMatch_Id;
	}

	public void setDepartureMatch_Id(long id) {
		this.departureMatch_Id = id;
	}

	public long getArrivalMatch_Id() {
		return arrivalMatch_Id;
	}

	public void setArrivalMatch_Id(long id) {
		this.arrivalMatch_Id = id;
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

	public MessageType getTargetType() {
		return targetType;
	}

	public void setTargetType(MessageType targetType) {
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
		return this.isRoundTrip + CarpoolConfig.urlSeperator + this.departureMatch_Id  + CarpoolConfig.urlSeperator + this.arrivalMatch_Id + CarpoolConfig.urlSeperator + 
				DateUtility.castToAPIFormat(this.departureDate)  + CarpoolConfig.urlSeperator + DateUtility.castToAPIFormat(this.arrivalDate)  + CarpoolConfig.urlSeperator + this.targetType.code + CarpoolConfig.urlSeperator + this.departureTimeSlot.code + CarpoolConfig.urlSeperator + this.arrivalTimeSlot.code+ CarpoolConfig.urlSeperator+DateUtility.castToAPIFormat(this.timeStamp);
	}
	
	@Override
	public JSONObject toJSON(){
		JSONObject jsonSearchRepresentation = new JSONObject();
		try{
			jsonSearchRepresentation.put("isRoundTrip", this.isRoundTrip);
			jsonSearchRepresentation.put("departureMatch_Id", this.departureMatch_Id);
			jsonSearchRepresentation.put("arrivalMatch_Id", this.arrivalMatch_Id);
			jsonSearchRepresentation.put("departureDate", DateUtility.castToAPIFormat(this.departureDate));
			jsonSearchRepresentation.put("arrivalDate", DateUtility.castToAPIFormat(this.arrivalDate));
			jsonSearchRepresentation.put("targetType", this.targetType.code);
			jsonSearchRepresentation.put("departureTimeSlot", this.departureTimeSlot.code);
			jsonSearchRepresentation.put("arrivalTimeSlot", this.arrivalTimeSlot.code);
			jsonSearchRepresentation.put("timeStamp", DateUtility.castToAPIFormat(this.timeStamp));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonSearchRepresentation;
		
	}
	
	public boolean equals(SearchRepresentation s){
		return this.isRoundTrip == s.isRoundTrip() && this.departureMatch_Id==s.getDepartureMatch_Id() && this.arrivalMatch_Id==s.getArrivalMatch_Id() &&
				this.departureDate.equals(s.getDepartureDate()) && this.arrivalDate.equals(s.getArrivalDate()) && this.targetType == s.getTargetType() &&
				this.departureTimeSlot == s.getDepartureTimeSlot() && this.arrivalTimeSlot == s.getArrivalTimeSlot() && this.timeStamp.equals(s.getTimeStamp());
	}

}
