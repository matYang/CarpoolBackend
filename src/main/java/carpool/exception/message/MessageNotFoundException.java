package carpool.exception.message;

import carpool.exception.PseudoException;

public class MessageNotFoundException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "MessageNotFound";

	public MessageNotFoundException(){
        super("对不起，您要找的信息不存在");
    }
	
	public MessageNotFoundException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 2;
    }
    
}