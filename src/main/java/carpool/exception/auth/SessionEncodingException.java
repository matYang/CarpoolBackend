package carpool.exception.auth;

import carpool.exception.PesudoException;

public class SessionEncodingException extends PesudoException {

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
