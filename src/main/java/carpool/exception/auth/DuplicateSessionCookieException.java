package carpool.exception.auth;

import carpool.exception.PseudoException;

public class DuplicateSessionCookieException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "DuplicateSessionCookie";

	public DuplicateSessionCookieException(){
        super("session重复，请先登出");
    }
	
	public DuplicateSessionCookieException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 10;
    }
	
}