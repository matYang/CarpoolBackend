package carpool.exception.message;

import carpool.exception.PesudoException;

public class MessageOwnerNotMatchException extends PesudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "MessageOwnerNotMatchFound";

	public MessageOwnerNotMatchException(){
        super();
    }
	
	public MessageOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 3;
    }
    
}