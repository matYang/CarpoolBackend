package carpool.exception.location;

import carpool.exception.PseudoException;

public class LocationNotFoundException extends PseudoException {
	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "LocationNotFound";

	public LocationNotFoundException(){
        super();
    }
	
	public LocationNotFoundException(String exceptionText){
		super(exceptionText);
	}
	@Override
	public int getCode() {
        return 19;
    }
}
