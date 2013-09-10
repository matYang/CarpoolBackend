package carpool.exception.user;

import carpool.exception.PesudoException;

public class UserNotFoundException extends PesudoException {
	
	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "UserNotFound";

	public UserNotFoundException(){
        super();
    }
	
	public UserNotFoundException(String exceptionText){
        super(exceptionText);
    }

	@Override
    public int getCode() {
        return 1;
    }
	
}