package carpool.exception.message;

import carpool.exception.PseudoException;

public class MessageOwnerNotMatchException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "MessageOwnerNotMatchFound";

	public MessageOwnerNotMatchException(){
        super("对不起，您没有对该信息执行该操作的权限");
    }
	
	public MessageOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 3;
    }
    
}