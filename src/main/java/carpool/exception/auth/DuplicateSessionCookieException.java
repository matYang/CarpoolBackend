package carpool.exception.auth;

import carpool.exception.PesudoException;

public class DuplicateSessionCookieException extends PesudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "DuplicateSessionCookie";

	public DuplicateSessionCookieException(){
        super();
    }
	
	public DuplicateSessionCookieException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 10;
    }
	
}