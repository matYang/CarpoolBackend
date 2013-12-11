package carpool.exception.auth;

import carpool.exception.PseudoException;

public class AccountAuthenticationException extends PseudoException{

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "Authentication";

	public AccountAuthenticationException(){
        super("账户验证失败");
    }
	
	public AccountAuthenticationException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 14;
    }
	
}
