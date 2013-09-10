package carpool.exception.auth;

import carpool.exception.PesudoException;

public class UnexpectedCookieException extends PesudoException {

	private static final long serialVersionUID = 1L;

	protected String exceptionType = "UnacceptableSearchState";

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