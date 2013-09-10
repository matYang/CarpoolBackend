package carpool.exception.message;

import carpool.exception.PesudoException;

public class MessageNotFoundException extends PesudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "MessageNotFound";

	public MessageNotFoundException(){
        super();
    }
	
	public MessageNotFoundException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 2;
    }
    
}