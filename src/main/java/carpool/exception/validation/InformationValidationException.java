package carpool.exception.validation;

import carpool.exception.PseudoException;


public class InformationValidationException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "InformationValidation";

	public InformationValidationException(){
        super();
    }
	
	public InformationValidationException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 16;
    }
	
}