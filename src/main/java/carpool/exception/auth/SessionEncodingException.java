package carpool.exception.auth;

import carpool.exception.PseudoException;

public class SessionEncodingException extends PseudoException {

	private static final long serialVersionUID = 1L;

	protected String exceptionType = "SessionEncoding";

	public SessionEncodingException(){
        super();
    }
	
	public SessionEncodingException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 11;
    }
	
}
