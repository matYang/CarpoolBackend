package carpool.exception.auth;

import carpool.exception.PseudoException;

public class UnexpectedCookieException extends PseudoException {

	private static final long serialVersionUID = 1L;

	protected String exceptionType = "UnexpectedCookieState";

	public UnexpectedCookieException(){
        super("server吃了坏掉的cookie肚子不好，请先登出或者清理cookie");
    }
	
	public UnexpectedCookieException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 13;
    }
	
}