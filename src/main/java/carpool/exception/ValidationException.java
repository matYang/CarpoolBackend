package carpool.exception;

/**
 * This Exception is thrown thenever there is a validation error in successfully-parsed data
 * meaning that the format of the data is good, but the context it presents contradicts our requirements
 * @author Matthew
 *
 */
public class ValidationException extends PseudoException{
	
	private static final long serialVersionUID = 1L;

	protected String exceptionType = "GeneralValidationException";

	private ValidationException(){
        super();
    }
	
	public ValidationException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 15;
    }
	
}
