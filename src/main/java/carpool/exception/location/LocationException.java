package carpool.exception.location;

import carpool.exception.PseudoException;

public class LocationException extends PseudoException{

	private static final long serialVersionUID = 1L;

	protected String exceptionType = "LocationStructuralError";

	public LocationException(){
        super();
    }
	
	public LocationException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 17;
    }
}
