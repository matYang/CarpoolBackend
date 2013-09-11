package carpool.exception.auth;

import carpool.exception.PseudoException;

public class UnexpectedCookieException extends PseudoException {

	private static final long serialVersionUID = 1L;

	protected String exceptionType = "UnexpectedCookieState";

	public UnexpectedCookieException(){
        super();
    }
	
	public UnexpectedCookieException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 13;
    }
	
}